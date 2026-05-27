package org.skypro.lesson.bot;

import org.skypro.lesson.model.Recommendation;
import org.skypro.lesson.model.User;
import org.skypro.lesson.repository.UserRepository;
import org.skypro.lesson.service.RecommendationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final UserRepository userRepository;
    private final RecommendationsService recommendationsService;

    @Autowired
    public TelegramBot(UserRepository userRepository, RecommendationsService recommendationsService) {
        this.botToken = "8873358490:AAE7_mPij3WPO6ZTUUdAZADu8e8-UND4r2I";
        this.botUsername = "lesson20260526_bot";
        this.userRepository = userRepository;
        this.recommendationsService = recommendationsService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleCommand(update.getMessage().getText(), update.getMessage().getChatId());
        }
    }

    private void handleCommand(String command, long chatId) {
        if (command.startsWith("/recommend")) {
            String username = extractUsername(command);
            sendRecommendations(username, chatId);
        } else {
            sendWelcomeMessage(chatId);
        }
    }

    private String extractUsername(String command) {
        return command.substring("/recommend ".length());
    }

    private void sendRecommendations(String username, long chatId) {
        List<User> users = userRepository.findByFullNameContainingIgnoreCase(username);

        if (users.size() != 1) {
            SendMessage message = new SendMessage()
                    .setChatId(chatId)
                    .setText("Пользователь не найден");
            sendMessage(message);
            return;
        }

        User user = users.get(0);
        List<String> recommendations = recommendationsService.getRecommendations(user.getId())
                .stream()
                .map(Recommendation::getText)
                .collect(Collectors.joining("\n• "));

        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setParseMode("Markdown")
                .setText("Здравствуйте " + user.getFirstName() + " " + user.getLastName() + "\n" +
                        "Новые продукты для вас:\n• " + recommendations);
        sendMessage(message);
    }

    private void sendWelcomeMessage(long chatId) {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText("Привет! Я бот для выдачи рекомендаций. Напишите /recommend <username>, чтобы получить рекомендации.");
        sendMessage(message);
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}