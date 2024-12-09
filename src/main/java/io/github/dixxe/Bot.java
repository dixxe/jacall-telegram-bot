package io.github.dixxe;

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

// Bot class the father of all bots.
// Will grow bigger in future!
abstract class Bot implements LongPollingSingleThreadUpdateConsumer {
    protected final String token;
    protected final TelegramClient telegramClient;
    protected enum Commands {};

    Bot(String token, TelegramClient telegramClient) {
        this.token = token;
        this.telegramClient = telegramClient;
    }

    public void botSendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            botSendError(chatId, e);
            e.printStackTrace();
        }
    }

    // Method for user reporting system! Help to find nasty bugs :)
    public void botSendError(String chatId, Exception er) {
        String errorText = String.format("Ух!! Тут случился JavaException! Зарепорти меня @d1xxe!\n```\nat %s\n%s\n```",
                er.getStackTrace()[0], er);
        SendMessage sendMessage = new SendMessage(chatId, errorText);
        er.printStackTrace();
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
