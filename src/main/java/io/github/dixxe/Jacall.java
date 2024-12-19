package io.github.dixxe;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

class JacallBot extends Bot {
    private List<Thread> activeThreads = new ArrayList<>();
    public JacallBot(String botToken) {
        super(botToken, new OkHttpTelegramClient(botToken));
        registerCommand("/start", "Command that starts bot");
        registerCommand("/help", "FAQ command");
        registerCommand("/remindme", "[dd.MM.yy-HH:mm] [текст_для_напоминания] - Команда напоминалка!");
        registerCommand("/threads", "Получить список всех тредов бота");
    }

    @Override
    // Method that runs on every update (message edited, send, deleted, etc.)
    public void consume(Update update) {
        // Everything will be caught.
        try {
            if (!update.hasMessage() | update.getMessage().getText() == null) {
                return;
            }
            if (!(update.getMessage().getText().charAt(0) == '/')) {
                return;
            }
            Message recievedMessage = update.getMessage();
            String chatID = recievedMessage.getChatId().toString();

            handleCommands(recievedMessage, chatID);

        } catch (Exception e) {
            botSendError(update.getMessage().getChatId().toString(), e);
        }

    }
    @Override
    protected void handleCommands(Message msg, String chatID) {
        // Preprocessing steps:
        String content = msg.getText();
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
        // Access arguments for every command even if it don't need it.
        List<String> commandArgs = Command.proccesArguments(content);

        // Real processing steps:
        // Don't forget java switch are NOT EXHAUSTIVE!!
        switch (requestedCommand) {
            // Separate each command for own method it's much clearer.
            case "/start" -> handleStartCommand(msg, chatID);
            case "/help" -> botSendMessage(chatID, getHelpMessage());
            case "/remindme" -> handleRemindMeCommand(msg, chatID, commandArgs);
            case "/threads" -> handleThreadsCommand(chatID);
        }
    }

    private void handleThreadsCommand(String chatID) {
        String readed = (String) Saver.readSaveFile("Reminders");
        botSendMessage(chatID, readed);
    }

    private void handleRemindMeCommand(Message msg, String chatID, List<String> commandArgs) {
        if (commandArgs.isEmpty()) {
            botSendMessage(chatID,
                    "Команда напоминалка!\nПример использования: \'/remindme 15.12.24-21:22 привет мир!\'");
            return;
        }
        // There's HELLA bunch of try-catch blocks. Welcome to JAVA BOII
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy-HH:mm");
            LocalDateTime timeToRemind = LocalDateTime.parse(commandArgs.get(0), formatter);
            LocalDateTime currentCheckTime = LocalDateTime.now();

            String textToRemind = StringUtils.join(commandArgs.subList(1, commandArgs.size()), " ");
            Saver.getSaveThread("Reminders", textToRemind + "\r\n", true).start();
            if (currentCheckTime.isAfter(timeToRemind)) {
                botSendMessage(chatID, "Вы запланировали напоминалку в прошлое!");
                return;
            }

            Thread remindThread = getRemindThread(msg, chatID, currentCheckTime, timeToRemind, textToRemind);
            activeThreads.add(remindThread);
            remindThread.start();
            botSendMessage(chatID, "Напоминалка поставлена!");
        } catch (Exception _e) {
            botSendMessage(chatID, "Недостаточно аргументов или они неправильно введены!");
        }
    }

    private void handleStartCommand(Message msg, String chatID) {
        // Start should work only in user chats.
        if (!msg.getChat().isUserChat()) {
            return;
        }

        SendMessage reply = new SendMessage(chatID, "Вас приветствует бот на джаве!");
        reply.setReplyMarkup(ReplyKeyboardMarkup
                .builder()
                .keyboardRow(new KeyboardRow("/help", "/remindme"))
                .resizeKeyboard(true)
                .build()
        );

        try {
            telegramClient.execute(reply);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private Thread getRemindThread(Message msg,
                                   String chatID,
                                   LocalDateTime currentCheckTime,
                                   LocalDateTime timeToRemind,
                                   String textToRemind) {

        return new Thread(() -> {
            LocalDateTime currentTime = LocalDateTime.now();
            while (!currentTime.isAfter(timeToRemind)) {
                currentTime = LocalDateTime.now();
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            botSendMessage(chatID, String.format("Вы запланировали напоминалку \"%s\" @%s",
                    textToRemind, msg.getFrom().getUserName()));
        });
    }

}

