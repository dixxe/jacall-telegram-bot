package io.github.dixxe;

import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Saver {
    // Wip method to saving data.
    public static Thread getSaveThread(String fileName, Object objectToSave, Boolean append) {
        Path workPath = Paths.get("");
        File outputFile = new File(workPath + fileName + ".bin");
        try {
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
        } catch (IOException e) {
            // ahh log it later future me
        }

        return new Thread(() -> {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(objectToSave);
                byte[] data = bos.toByteArray();

                // Something wrong with appending. I would like to fix it in the future.
                FileOutputStream outputStream = new FileOutputStream(outputFile, append);
                outputStream.write(data);
                outputStream.close();
            } catch (Exception e) {
                // Wip
                System.out.println(e);
            }
        });
    }

    public static Object readSaveFile(String fileName) {
        Path workPath = Paths.get("");
        File saveFile = new File(workPath + fileName + ".bin");
        try {
            byte[] saveFileContent = Files.readAllBytes(saveFile.toPath());
            ByteArrayInputStream bis = new ByteArrayInputStream(saveFileContent);
            ObjectInput in = new ObjectInputStream(bis);
            return in.readObject();
        } catch(Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
