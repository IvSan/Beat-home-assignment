package xyz.hardliner.beat.service;

import xyz.hardliner.beat.domain.DataEntry;
import xyz.hardliner.beat.domain.Ride;
import xyz.hardliner.beat.exception.EndOfFileException;

import java.util.function.Supplier;

import static java.lang.System.currentTimeMillis;

public class DataProcessor {

    private final FileReader fileReader;
    private final RidesHandler ridesHandler;

    public DataProcessor(FileReader fileReader,
                         RidesHandler ridesHandler) {
        this.fileReader = fileReader;
        this.ridesHandler = ridesHandler;
    }

    public void process() {
        Supplier<DataEntry> reader = fileReader.getReader();
        long startTime = currentTimeMillis();
        try {
            while (true) {
                processNext(reader.get());
            }
        } catch (EndOfFileException ex) {
            System.out.println("Processing done. Execution time: " + (currentTimeMillis() - startTime) / 1000f + " sec.");
            System.out.println(ridesHandler.rides);
        }
    }

    private void processNext(DataEntry data) {
        if (!ridesHandler.rides.containsKey(data.rideId)) {
            ridesHandler.rides.put(data.rideId, new Ride(data));
        }
        System.out.println(data);

        var ride = ridesHandler.rides.get(data.rideId);
        ride.addCost(99);
    }
}
