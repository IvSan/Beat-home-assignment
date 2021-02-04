package xyz.hardliner.beat.service;

import xyz.hardliner.beat.domain.DataEntry;
import xyz.hardliner.beat.domain.Ride;
import xyz.hardliner.beat.exception.EndOfFileException;

import java.util.HashMap;
import java.util.function.Supplier;

import static java.lang.System.currentTimeMillis;
import static xyz.hardliner.beat.service.LineParser.parse;

public class DataProcessor {

    private final FileReader fileReader;
    private final RidesHandler ridesHandler;

    public DataProcessor(FileReader fileReader,
                         RidesHandler ridesHandler) {
        this.fileReader = fileReader;
        this.ridesHandler = ridesHandler;
    }

    public HashMap<Long, Ride> process() {
        Supplier<String> reader = fileReader.getReader();
        long startTime = currentTimeMillis();
        try {
            processAll(reader);
        } catch (EndOfFileException ex) {
            System.out.println("Processing done. Execution time: " + (currentTimeMillis() - startTime) / 1000f + " sec.");
            System.out.println(ridesHandler.getRides());
        }
        return ridesHandler.getRides();
    }

    private void processAll(Supplier<String> reader) {
        while (true) {
            var line = reader.get();
            try {
                processNext(parse(line));
            } catch (Exception ex) {
                System.out.println("While line '" + line + "' processing exception happened: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void processNext(DataEntry data) {
        if (!ridesHandler.containsRide(data.rideId)) {
            ridesHandler.createRide(data);
            return;
        }
        ridesHandler.updateRide(data);
    }
}
