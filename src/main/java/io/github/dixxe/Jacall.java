package io.github.dixxe;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Arrays;

class JacallBot extends Bot {
    public JacallBot(String botToken) {
        super(botToken, new OkHttpTelegramClient(botToken));
        registerCommand("/start", "Command that starts bot");
        registerCommand("/help", "FAQ command");
        registerCommand("/huba-buba", "Rofl command!");
    }

    @Override
    // Method that runs on every update (message edited, send, deleted, etc.)
    public void consume(Update update) {
        // Everything will be caught.
        try {
            if (!((update.hasMessage()) && (update.getMessage().getText().contains("/")))) {
                return;
            }
            Message recievedMessage = update.getMessage();
            String chatID = recievedMessage.getChatId().toString();
            String messageContent = recievedMessage.getText();

            handleCommands(messageContent, chatID);

        } catch (Exception e) {
            botSendError(update.getMessage().getChatId().toString(), e);
        }

    }
    @Override
    protected void handleCommands(String content, String chatID) {
        // Here I seperate command from *arguments* in future I *probably* move it to Command class.
        String commandName = content.split(" ")[0];
        String requestedCommand = null;
        // I need this try-catch block, because if user types wrong command it will try to .getName() on null.
        // Probably there is better way to do it. *TODO
        try {
            requestedCommand = findCommand(commandName).getName();
        } catch (NullPointerException e) {
            botSendMessage(chatID, "Такой команды нет!");
            return;
        }
        // Don't forget java switch are NOT EXHAUSTIVE!!
        switch (requestedCommand) {
            case "/start" -> botSendMessage(chatID, String.format("Вас приветствует бот написанный на джаве!\n"));
            case "/help" -> botSendMessage(chatID, getHelpMessage());
            case "/huba-buba" -> botSendMessage(chatID, "Почему у телеграмма команды после черточки ломаются?");
        }
    }

}
