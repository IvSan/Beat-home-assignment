package xyz.hardliner.beat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hardliner.beat.domain.Ride;
import xyz.hardliner.beat.exception.EndOfFileException;

import java.util.HashMap;
import java.util.function.Supplier;

import static java.lang.System.currentTimeMillis;
import static xyz.hardliner.beat.service.LineParser.parse;

public class DataProcessor {

    private static Logger log = LoggerFactory.getLogger(DataProcessor.class);

    private final FileReader fileReader;
    private final RidesHandler ridesHandler;

    public DataProcessor(FileReader fileReader,
                         RidesHandler ridesHandler) {
        this.fileReader = fileReader;
        this.ridesHandler = ridesHandler;
    }

    public HashMap<Long, Ride> process() {
        try (fileReader) {
            Supplier<String> reader = fileReader.getReader();
            long startTime = currentTimeMillis();
            try {
                processDataFromSupplier(reader);
            } catch (EndOfFileException ex) {
                log.info("Processing done. Execution time: " + (currentTimeMillis() - startTime) / 1000f + " sec.");
            }
            return ridesHandler.getRides();
        }
    }

    private void processDataFromSupplier(Supplier<String> supplier) {
        while (true) {
            var line = supplier.get();
            try {
                ridesHandler.process(parse(line));
            } catch (Exception ex) {
                log.warn(String.format("While line '%s' processing exception happened: %s", line, ex.getMessage()));
            }
        }
    }
}
