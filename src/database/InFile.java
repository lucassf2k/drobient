package database;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class InFile {
    private static final String FILE_PATH = "./db.txt";

    public static void Write(final String input) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(input);
            writer.newLine(); // Adiciona uma nova linha após o texto
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
