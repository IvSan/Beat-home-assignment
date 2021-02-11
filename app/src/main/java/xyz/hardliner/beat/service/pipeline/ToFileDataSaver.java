package xyz.hardliner.beat.service.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hardliner.beat.domain.RideReport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static java.math.RoundingMode.HALF_UP;

public class ToFileDataSaver implements DataSaver, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(ToFileDataSaver.class);

    public final String path;
    private final BufferedWriter writer;

    public ToFileDataSaver(String path) throws IOException {
        this.path = path;
        cleanUpOutputFile(path);
        this.writer = new BufferedWriter(new FileWriter(path, true));
    }

    private void cleanUpOutputFile(String path) throws IOException {
        File outputFile = new File(path);
        if (outputFile.exists()) outputFile.delete();
        outputFile.createNewFile();
    }

    @Override
    public void save(RideReport report) {
        try {
            writeToFile(report);
        } catch (Exception ex) {
            log.error("Cannot write result to file", ex);
            throw new IllegalStateException("Cannot write result to file", ex);
        }
    }

    private void writeToFile(RideReport report) throws IOException {
        var line = report.rideId + "," + report.cost.setScale(2, HALF_UP) + "\n";
        writer.append(line);
    }

    @Override
    public void close() throws Exception {
        writer.close();
    }
}
