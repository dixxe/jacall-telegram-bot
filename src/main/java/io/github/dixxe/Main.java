package io.github.dixxe;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

class JacallBot extends Bot {
    public JacallBot(String botToken) {
        super(botToken, new OkHttpTelegramClient(botToken));

    }

    @Override
    public void botSendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            botSendError(chatId, e);
            e.printStackTrace();
        }
    }

    @Override
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

    private enum Commands {
        start("/start", "Command to start bot"),
        help("/help", "FAQ how to use bot"),
        remind("/remind", "remind [time] - reminds you after specified time");

        private final String description;
        private final String name;
        Commands(String name, String description) {
            this.description = description;
            this.name = name;
        }

        public String getDescription() {
            return String.format("%s - %s\n", this.name, this.description);
        }
    }

    @Override
    // Method that runs on every update
    public void consume(Update update) {
        // Everything will be caught.
        try {
            if (!update.hasMessage() && !update.getMessage().getText().contains("/")) {
                return;
            }
            Message recievedMessage = update.getMessage();
            String chatID = recievedMessage.getChatId().toString();
            String messageContent = recievedMessage.getText().replace("/", "");

            handleCommands(messageContent, chatID);

        } catch (Exception e) {
            botSendError(update.getMessage().getChatId().toString(), e);
        }

    }

    private void handleCommands(String messageContent, String chatID) {
        switch (Commands.valueOf(messageContent)) {
            case start -> botSendMessage(chatID, "Greetings!");
            case help -> {
                StringBuilder helpMessage = new StringBuilder();
                Arrays.stream(Commands.values()).forEach(com -> helpMessage.append(com.getDescription()));
                botSendMessage(chatID, helpMessage.toString());
            }
            case remind -> botSendMessage(chatID, "Ещё не сделано!");
        }
    }

}