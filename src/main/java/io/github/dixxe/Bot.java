package io.github.dixxe;

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
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

    public abstract void botSendMessage(String chatId, String message);

    // Method for user reporting system! Help to find nasty bugs :)
    public abstract void botSendError(String chatId, Exception er);
}
