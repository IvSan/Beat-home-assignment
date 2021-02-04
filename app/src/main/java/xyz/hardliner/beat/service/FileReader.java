package xyz.hardliner.beat.service;

import xyz.hardliner.beat.exception.EndOfFileException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Supplier;

public class FileReader implements AutoCloseable{

    public final String path;
    private FileInputStream inputStream;
    private Scanner scanner;
    private Supplier<String> reader;

    public FileReader(String path) {
        this.path = path;
    }

    public Supplier<String> getReader() {
        if (reader != null) {
            return reader;
        }

        try {
            inputStream = new FileInputStream(path);
            scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
        } catch (FileNotFoundException ex) {
            throw new IllegalArgumentException("Cannot find file " + path, ex);
        }

        return () -> {
            try {
                return scanner.nextLine();
            } catch (NoSuchElementException ex) {
                throw new EndOfFileException();
            }
        };
    }

    public void close() {
        if (reader == null) {
            return;
        }

        try {
            inputStream.close();
            scanner.close();
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot close file reading streams", ex);
        }

        reader = null;
    }
}
