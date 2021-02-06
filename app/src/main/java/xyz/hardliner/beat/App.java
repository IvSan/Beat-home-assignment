package xyz.hardliner.beat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hardliner.beat.service.DataProcessor;
import xyz.hardliner.beat.service.FileReader;
import xyz.hardliner.beat.service.ResultFileWriter;
import xyz.hardliner.beat.service.RidesHandler;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private static final String input = "./app/src/test/resources/time_sorted_paths.csv";
    private static final String output = "./app/src/test/resources/result.csv";

    public static void main(String[] args) {
        var unit = new DataProcessor(
            new FileReader(input),
            new RidesHandler(),
            new ResultFileWriter(output));

        unit.process();
    }
}
