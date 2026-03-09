package org.skypro.lesson.dto;

import org.skypro.lesson.model.Product;
import org.skypro.lesson.model.Recommendation;
import org.skypro.lesson.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SimpleCreditRule implements Ruleset {

    private final TransactionRepository transactionRepository;

    @Autowired
    public SimpleCreditRule(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Recommendation check(UUID userId) {

        boolean hasCredit = transactionRepository.hasAnyTransactions(userId, "CREDIT");
        long debitBalance = transactionRepository.calculateBalance(userId, "DEBIT");
        long totalDebitSpending = transactionRepository.sumWithdrawalsByType(userId, "DEBIT");

        if (!hasCredit && (debitBalance > 0L) && (totalDebitSpending > 100000L)) {

            Product simpleCreditProduct = new Product(
                    "Простой кредит",
                    "ab138afb-f3ba-4a93-b74f-0fcee86d447f",
                    "Откройте мир выгодных кредитов с нами!\n" +
                    "Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, " +
                    "что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.\n" +
                    "Почему выбирают нас:\n" +
                    "* Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.\n" +
                    "* Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.\n" +
                    "* Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.\n" +
                    "Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!"
            );
            return new Recommendation(simpleCreditProduct.getName(), simpleCreditProduct.getId(), simpleCreditProduct.getDescription());
        }

        return null;
    }
}