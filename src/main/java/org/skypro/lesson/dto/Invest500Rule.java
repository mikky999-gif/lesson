package org.skypro.lesson.dto;

import org.skypro.lesson.model.Product;
import org.skypro.lesson.model.Recommendation;
import org.skypro.lesson.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Invest500Rule implements Ruleset {

    private final TransactionRepository transactionRepository;

    @Autowired
    public Invest500Rule(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Recommendation check(UUID userId) {

        boolean hasDebit = transactionRepository.hasAnyTransactions(userId, "DEBIT");
        boolean hasInvest = transactionRepository.hasAnyTransactions(userId, "INVEST");
        long sumSavingsDeposits = transactionRepository.sumDepositsByType(userId, "SAVING");

        if (!hasInvest && hasDebit && sumSavingsDeposits > 1000L) {
            Product investProduct = new Product(
                    "Invest 500",
                    "147f6a0f-3b91-413b-ab99-87f081d60d5a",
                    "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) " +
                    "от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет " +
                    "до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. " +
                    "Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными " +
                    "рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!"
            );
            return new Recommendation(investProduct.getName(), investProduct.getId(), investProduct.getDescription());
        }

        return null;
    }
}