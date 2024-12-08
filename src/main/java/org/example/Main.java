package org.example;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;

public class Main {
    public static void main(String[] Args) {
        try {
            String botToken = System.getenv("TOKEN");
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(botToken, new JacallBot(botToken));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}

class JacallBot implements LongPollingSingleThreadUpdateConsumer {
    private final String token;
    private TelegramClient telegramClient;
    public JacallBot(String botToken) {
        token = botToken;
        telegramClient = new OkHttpTelegramClient(token);
    }

    @Override
    // Method that runs on every update
    public void consume(Update update) {
        // Everything will be caught.
        try {
            if (update.hasMessage() && update.getMessage().getText().equals("/start")) {
                String chatId = update.getMessage().getChatId().toString();
                botSendMessage(chatId, "Приветствую! Я бот на джаве от @d1xxe!\nЗа фичами или с репортами к нему");
            }
        } catch (Exception e) {
            botSendError(update.getMessage().getChatId().toString(), e);
        }

    }
    // Method to easily write messages.
    // Move it in BotUtils class later. *TODO
    private void botSendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            botSendError(chatId, e);
            e.printStackTrace();
        }
    }
    // Method for user reporting system! Help to find nasty bugs :)
    private void botSendError(String chatId, Exception er) {
        // Need to rewrite it in future
        SendMessage sendMessage = new SendMessage(chatId,
                "Ух!! Тут случился JavaException! Зарепорти меня @d1xxe!\n```\nat " + er.getStackTrace()[0] + "\n" + er + "\n```");
        er.printStackTrace();
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}