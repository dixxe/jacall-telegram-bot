package io.github.dixxe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class SaverTest {
    Path workPath = Paths.get("");
    List<String> testList = Arrays.asList("Hello", "World");
    String testString = "test_me! проверь меня!";
    String testFileName = "test_file";
    int testNum = 42;
    Thread saveThread = Saver.getSaveThread(testFileName, testList, false);

    @Test
    void isSaveFileExists() throws InterruptedException {

        saveThread.start();
        saveThread.join();

        Assertions.assertTrue(new File(workPath + testFileName + ".bin").isFile());
    }
    @Test
    void isSaveDecodesRight() throws InterruptedException {

        saveThread.start();
        saveThread.join();

        List<String> content = (List<String>) Saver.readSaveFile(testFileName);
        Assertions.assertEquals(testList, content);
    }
    @Test
    void isStringSavesRight() throws InterruptedException {
        saveThread = Saver.getSaveThread(testFileName, testString, false);
        saveThread.start();
        saveThread.join();

        Assertions.assertTrue(new File(workPath + testFileName + ".bin").isFile());
        String content = (String) Saver.readSaveFile(testFileName);
        Assertions.assertEquals(testString, content);
    }
    @Test
    void isIntSavesRight() throws InterruptedException {
        saveThread = Saver.getSaveThread(testFileName, testNum, false);
        saveThread.start();
        saveThread.join();

        Assertions.assertTrue(new File(workPath + testFileName + ".bin").isFile());
        int content = (Integer) Saver.readSaveFile(testFileName);
        Assertions.assertEquals(testNum, content);
    }
    // This test failing. Fix the Saver code ASAP.
    @Test
    void isAppendWorksRight() {
        saveThread = Saver.getSaveThread(testFileName, testString + "\n", true);
        saveThread = Saver.getSaveThread(testFileName, testString + "\n", true);

        String content = (String) Saver.readSaveFile(testFileName);
        Assertions.assertEquals(testString + "\n" + testString + "\n", content);
    }
}
