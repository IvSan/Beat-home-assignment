package xyz.hardliner.beat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hardliner.beat.service.DataProcessor;
import xyz.hardliner.beat.service.FileReader;
import xyz.hardliner.beat.service.ResultFileWriter;
import xyz.hardliner.beat.service.RidesHandler;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws ParseException {
        var cmd = commandLine(args);
        var input = cmd.getOptionValue("i");
        var output = cmd.getOptionValue("o");
        validateOptions(input, output);

        var dataProcessor = new DataProcessor(
            new FileReader(input),
            new RidesHandler(),
            new ResultFileWriter(output));

        try {
            dataProcessor.process();
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.error("Cannot process data: " + ex.getMessage(), ex);
            } else {
                log.error("Cannot process data: " + ex.getMessage());
            }
        }
    }

    private static CommandLine commandLine(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(commandLineOptions(), args);
    }

    private static Options commandLineOptions() {
        Options options = new Options();
        options.addOption("i", "input", true, "Path of input file");
        options.addOption("o", "output", true, "Path for out file");
        return options;
    }

    private static void validateOptions(String input, String output) {
        if (input == null || input.isBlank()) {
            log.error("Please, specify correct input file path.");
            System.exit(1);
        }
        if (output == null || output.isBlank()) {
            log.error("Please, specify correct output file path.");
            System.exit(1);
        }
    }
}
