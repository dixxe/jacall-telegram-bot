package io.github.dixxe;

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Bot class the father of all bots.
// Will grow bigger in future!
abstract class Bot implements LongPollingSingleThreadUpdateConsumer {
    protected final String token;
    protected final TelegramClient telegramClient;
    // Commands is something special for every bot. So you should register them thyself.
    protected Command[] commands = new Command[0];

    Bot(String token, TelegramClient telegramClient) {
        this.token = token;
        this.telegramClient = telegramClient;
    }

    abstract protected void handleCommands(Message msg, String chatID);
    // Another shortcut, btw.
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

    public void registerCommand(String name, String description) {
        Command command = new Command(name, description);
        // IMHO. I think Arrays will be much more reliable and performant.
        // SO! I sacrifice some performance for creating new list and then convert it to array
        // Because code NEED to parse `commands` EVERY MESSAGE SENT! But do this flip-flop only once!
        List<Command> commandList = new ArrayList<>(Arrays.stream(commands).toList());
        commandList.add(command);
        commands = commandList.toArray(new Command[0]);
    }
    // Shortcut method to print/send all information about commands.
    public String getHelpMessage() {
        StringBuilder helpMessage = new StringBuilder();
        Arrays.stream(commands).forEach(com -> helpMessage.append(com.getInfo()));
        return helpMessage.toString();
    }

    public Command findCommand(String name) {
        for (Command com : commands) {
            if (com.getName().equals(name)) { return com; }
        }
        // We are doing nasty stuff here.
        return null;
    }
}
