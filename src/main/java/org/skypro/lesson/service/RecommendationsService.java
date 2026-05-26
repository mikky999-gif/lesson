package org.skypro.lesson.service;

import org.skypro.lesson.dto.Ruleset;
import org.skypro.lesson.model.Recommendation;
import org.skypro.lesson.model.DynamicRule;
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

    @Autowired
    public RecommendationsService(
            DynamicRuleJpaRepository jpaRepo,
            TransactionRepository transactionRepository,
            List<Ruleset> rulesets,
            @Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jpaRepo = jpaRepo;
        this.transactionRepository = transactionRepository;
        this.rulesets = rulesets;
        this.jdbcTemplate = jdbcTemplate;
    }


    private long getSumByType(UUID userId, String productType, String transactionType) {
        String sql = """
            SELECT COALESCE(SUM(CASE WHEN t.type = ? THEN t.amount ELSE -t.amount END), 0)
            FROM transactions t
            JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ? AND p.type = ?
            """;
        return jdbcTemplate.queryForObject(sql, Long.class, transactionType, userId.toString(), productType);
    }

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
        for (DynamicRule.QueryDefinition query : rule.getRuleDefinition()) {
            boolean result = executeQuery(query, userId);
            if (query.isNegate() != null && query.isNegate()) {
                result = !result;
            }
            if (!result) {
                return false;
            }
        }
        return true;
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

                long sumValue = getSumByType(userId, prodTypeSum, transTypeSum);
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