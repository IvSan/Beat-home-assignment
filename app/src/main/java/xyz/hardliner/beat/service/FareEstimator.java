package xyz.hardliner.beat.service;

import xyz.hardliner.beat.domain.DataEntry;
import xyz.hardliner.beat.exception.EndOfFileException;

import java.util.function.Supplier;

public class FareEstimator {

    private final FileReader fileReader;

    public FareEstimator(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public void run() {
        Supplier<DataEntry> reader = fileReader.getReader();
        try {
            while (true) {
                System.out.println(reader.get());
            }
        } catch (EndOfFileException ex) {
            System.out.println("Done");
        }

    }
}
