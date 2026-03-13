package org.skypro.lesson;

import org.skypro.lesson.model.User;
import org.skypro.lesson.repository.TransactionRepository;
import org.skypro.lesson.service.RecommendationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class LessonApplication implements CommandLineRunner {

    private final RecommendationsService recommendationsService;
    private final TransactionRepository transactionRepository;

    @Autowired
    public LessonApplication(RecommendationsService recommendationsService,
                             TransactionRepository transactionRepository) {
        this.recommendationsService = recommendationsService;
        this.transactionRepository = transactionRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(LessonApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        printUsersGroupedByCategoriesFromDatabase();
    }

    private void printUsersGroupedByCategoriesFromDatabase() {
        System.out.println("\n*** Пользователи из базы данных, сгруппированные по категориям рекомендаций ***");

        List<UUID> allUserIds = transactionRepository.getAllUserIds();

        if (allUserIds.isEmpty()) {
            System.out.println("В базе данных нет пользователей с транзакциями.");
            return;
        }

        System.out.println("Обрабатываем " + allUserIds.size() + " пользователей из базы данных...");

        Map<String, Set<User>> groupedUsers = recommendationsService.groupUsersByCategories(allUserIds);

        if (groupedUsers.isEmpty()) {
            System.out.println("Нет пользователей, подходящих под условия рекомендаций.");
            return;
        }

        groupedUsers.forEach((category, users) -> {
            System.out.println("Категория '" + category + "':");
            if (users.isEmpty()) {
                System.out.println("  Нет пользователей в этой категории");
            } else {
                users.forEach(user ->
                        System.out.println("  - Пользователь ID: " + user.getId())
                );
                System.out.println("    Всего пользователей в категории: " + users.size());
            }
            System.out.println();
        });
    }
}