package org.skypro.lesson.service;

import org.skypro.lesson.dto.Ruleset;
import org.skypro.lesson.model.Recommendation;
import org.skypro.lesson.model.DynamicRule;
import org.skypro.lesson.model.RuleStats;
import org.skypro.lesson.repository.RuleStatsRepository;
import org.skypro.lesson.repository.jpa.DynamicRuleJpaRepository;
import org.skypro.lesson.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RecommendationsService {

    private final DynamicRuleJpaRepository jpaRepo;
    private final TransactionRepository transactionRepository;
    private final List<Ruleset> rulesets;
    private final JdbcTemplate jdbcTemplate;
    private final RuleStatsRepository ruleStatsRepository;
    private final DynamicRuleJpaRepository dynamicRuleJpaRepository;

    @Autowired
    public RecommendationsService(
            DynamicRuleJpaRepository jpaRepo,
            TransactionRepository transactionRepository,
            List<Ruleset> rulesets,
            @Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate,
            RuleStatsRepository ruleStatsRepository,
            DynamicRuleJpaRepository dynamicRuleJpaRepository) {
        this.jpaRepo = jpaRepo;
        this.transactionRepository = transactionRepository;
        this.rulesets = rulesets;
        this.jdbcTemplate = jdbcTemplate;
        this.ruleStatsRepository = ruleStatsRepository;
        this.dynamicRuleJpaRepository = dynamicRuleJpaRepository;
    }

    /**
     * Получает список рекомендаций для пользователя.
     *
     * @param userId UUID пользователя
     * @return Список рекомендаций
     */

    public List<Recommendation> getRecommendations(UUID userId) {
        List<Recommendation> recommendations = new ArrayList<>();

        for (Ruleset rule : rulesets) {
            Recommendation rec = rule.check(userId);
            if (rec != null) {
                recommendations.add(rec);
            }
        }

        List<DynamicRule> dynamicRules = jpaRepo.findAll();

        for (DynamicRule rule : dynamicRules) {
            if (checkDynamicRuleConditions(rule, userId)) {
                recommendations.add(new Recommendation(
                        rule.getProductName(),
                        rule.getProductId().toString(),
                        rule.getProductText()
                ));
            }
        }

        return recommendations;
    }

    private boolean checkDynamicRuleConditions(DynamicRule rule, UUID userId) {
        boolean result = true;

        for (DynamicRule.QueryDefinition query : rule.getRuleDefinition()) {
            // Проверка выполнения условий правила
            boolean queryResult = executeQuery(query, userId);
            if (query.isNegate() != null && query.isNegate()) {
                queryResult = !queryResult;
            }
            result &= queryResult;
        }

        if (result) {
            incrementRuleCounter(rule.getId());
        }
        return result;
    }

    private void incrementRuleCounter(UUID ruleId) {
        RuleStats stats = ruleStatsRepository.findById(ruleId).orElseGet(() -> {
            RuleStats newStats = new RuleStats();
            newStats.setRule(dynamicRuleJpaRepository.findById(ruleId).orElseThrow());
            newStats.setCounter(0);
            return newStats;
        });

        stats.setCounter(stats.getCounter() + 1);
        ruleStatsRepository.save(stats);
    }

    private boolean executeQuery(DynamicRule.QueryDefinition query, UUID userId) {
        String queryType = query.getQuery();
        List<Object> args = query.getArguments();

        switch (queryType) {
            case "USER_OF":
                String productTypeUserOf = (String) args.get(0);
                return transactionRepository.hasAnyTransactions(userId, productTypeUserOf);

            case "ACTIVE_USER_OF":
                String productTypeActive = (String) args.get(0);
                long countTrans = transactionRepository.countTransactionsByType(userId, productTypeActive);
                return countTrans >= 5;

            case "TRANSACTION_SUM_COMPARE":
                String prodTypeSum = (String) args.get(0);
                String transTypeSum = (String) args.get(1);
                String operator = (String) args.get(2);
                long constant = Long.parseLong((String) args.get(3));
                long sumValue = transactionRepository.calculateBalance(userId, prodTypeSum);
                return compare(sumValue, operator, constant);

            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW":
                String prodTypeCompare = (String) args.get(0);
                String operatorCompare = (String) args.get(1);

                long sumDeposit = transactionRepository.sumDepositsByType(userId, prodTypeCompare);
                long sumWithdrawal = transactionRepository.sumWithdrawalsByType(userId, prodTypeCompare);

                return compare(sumDeposit, operatorCompare, sumWithdrawal);

            default:
                throw new IllegalArgumentException("Unknown query type: " + queryType);
        }
    }

    private boolean compare(long value1, String operator, long value2) {
        return switch (operator) {
            case ">" -> value1 > value2;
            case "<" -> value1 < value2;
            case ">=" -> value1 >= value2;
            case "<=" -> value1 <= value2;
            case "=" -> value1 == value2;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }
}