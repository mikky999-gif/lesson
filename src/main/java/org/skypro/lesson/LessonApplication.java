package org.skypro.lesson;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.skypro.lesson.bot.TelegramBot;

@SpringBootApplication
public class LessonApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(LessonApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Application started successfully!");
    }

}