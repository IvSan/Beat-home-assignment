package xyz.hardliner.beat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hardliner.beat.domain.Ride;
import xyz.hardliner.beat.exception.EndOfFileException;

import java.util.HashMap;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static xyz.hardliner.beat.service.LineParser.parse;

public class DataProcessor {

    private static final Logger log = LoggerFactory.getLogger(DataProcessor.class);

    private final FileReader fileReader;
    private final RidesHandler ridesHandler;
    private final ResultFileWriter fileWriter;

    public DataProcessor(FileReader fileReader,
                         RidesHandler ridesHandler,
                         ResultFileWriter fileWriter) {
        this.fileReader = fileReader;
        this.ridesHandler = ridesHandler;
        this.fileWriter = fileWriter;
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

            var rides = ridesHandler.getRides();
            fileWriter.writeToFile(rides);
            return rides;
        }
    }

    private void processDataFromSupplier(Supplier<String> supplier) {
        while (true) {
            var line = supplier.get();
            try {
                log.debug(format("Processing line: %s", line));
                ridesHandler.process(parse(line));
            } catch (Exception ex) {
                log.warn(format("While line '%s' processing exception happened: %s", line, ex.getMessage()));
                if (log.isDebugEnabled()) {
                    log.debug(format("While line '%s' processing exception happened: ", line), ex);
                }
            }
        }
    }
}
