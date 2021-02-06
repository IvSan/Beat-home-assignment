package xyz.hardliner.beat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hardliner.beat.domain.Ride;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class ResultFileWriter {

    private static final Logger log = LoggerFactory.getLogger(ResultFileWriter.class);

    private final String outputFileName;

    public ResultFileWriter(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public void writeToFile(HashMap<Long, Ride> rides) {
        try {
            saveWriteToFile(rides);
        } catch (Exception ex) {
            log.error("Cannot write result to file", ex);
            throw new IllegalStateException("Cannot write result to file", ex);
        }
    }

    private void saveWriteToFile(HashMap<Long, Ride> rides) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));

        for (Ride ride : rides.values()) {
            writer
                .append(String.valueOf(ride.rideId))
                .append(", ")
                .append(String.valueOf(ride.getNormalizedCost()))
                .append("\n");
        }

        writer.close();
    }

}
