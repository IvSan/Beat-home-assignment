package xyz.hardliner.beat.service;

import xyz.hardliner.beat.domain.DataEntry;
import xyz.hardliner.beat.exception.EndOfFileException;
import xyz.hardliner.beat.exception.InvalidDataEntry;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Supplier;

import static xyz.hardliner.beat.service.LineParser.parse;

public class FileReader {

    public final String path;
    private FileInputStream inputStream;
    private Scanner scanner;
    private Supplier<DataEntry> reader;

    public FileReader(String path) {
        this.path = path;
    }

    public Supplier<DataEntry> getReader() {
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
                return getNextValidDataEntry(scanner);
            } catch (NoSuchElementException ex) {
                throw new EndOfFileException();
            }
        };
    }

    private static DataEntry getNextValidDataEntry(Scanner scanner) {
        while (true) {
            try {
                return parse(scanner.nextLine());
            } catch (InvalidDataEntry ex) {
                System.out.println(ex.getMessage());
            }
        }
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
