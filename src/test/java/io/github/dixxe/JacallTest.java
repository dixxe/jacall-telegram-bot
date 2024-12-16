package io.github.dixxe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Arrays;

public class JacallTest {
    Bot testBot = new JacallBot("123");
    @Test
    void commandRegisterAssertion() {

        String[] initCommandsNames = new String[] {"/start", "/help", "/remindme"};
        int i = 0;
        for (Command com : testBot.commands) {
            Assertions.assertEquals(initCommandsNames[i], com.getName());
            i++;
        }
    }
}
