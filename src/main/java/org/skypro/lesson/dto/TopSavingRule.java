package org.skypro.lesson.dto;

import org.skypro.lesson.model.Product;
import org.skypro.lesson.model.Recommendation;
import org.skypro.lesson.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TopSavingRule implements Ruleset {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TopSavingRule(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Recommendation check(UUID userId) {

        boolean hasDebit = transactionRepository.hasAnyTransactions(userId, "DEBIT");
        long sumDebitDeposits = transactionRepository.sumDepositsByType(userId, "DEBIT");
        long sumSavingDeposits = transactionRepository.sumDepositsByType(userId, "SAVING");
        long debitBalance = transactionRepository.calculateBalance(userId, "DEBIT");

        if (hasDebit &&
                ((sumDebitDeposits >= 50000L) || (sumSavingDeposits >= 50000L)) &&
                (debitBalance > 0L)) {

            Product topSavingProduct = new Product(
                    "Top Saving",
                    "59efc529-2fff-41af-baff-90ccd7402925",
                    "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, " +
                            "который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных " +
                            "квитанций — всё под контролем!\n" +
                            "Преимущества «Копилки»:\n" +
                            "* Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически " +
                            "переводить определенную сумму на ваш счет.\n" +
                            "* Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и " +
                            "корректируйте стратегию при необходимости.\n" +
                            "Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только " +
                            "через мобильное приложение или интернет-банкинг.\n" +
                            "Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!"
            );
            return new Recommendation(topSavingProduct.getName(), topSavingProduct.getId(), topSavingProduct.getDescription());
        }

        return null;
    }
}
