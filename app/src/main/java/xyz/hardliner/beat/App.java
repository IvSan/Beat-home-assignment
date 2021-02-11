package xyz.hardliner.beat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hardliner.beat.service.pipeline.FromFileDataSupplier;
import xyz.hardliner.beat.service.pipeline.ToFileDataSaver;
import xyz.hardliner.beat.service.processing.DataProcessor;
import xyz.hardliner.beat.service.processing.FareByRulesCalculator;
import xyz.hardliner.beat.service.rule.GreeceRulebook;
import xyz.hardliner.beat.service.rule.Rulebook;
import xyz.hardliner.beat.utils.TimezonesHelper;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws ParseException, IOException {
        var cmd = commandLine(args);
        var input = cmd.getOptionValue("i");
        var output = cmd.getOptionValue("o");
        validateOptions(input, output);

        Map<ZoneId, Rulebook> localRules = Map.of(
            ZoneId.of("Europe/Athens"), new GreeceRulebook()
        );

        try (var dataSaver = new ToFileDataSaver(output)) {
            var timeZonesHelper = new TimezonesHelper();
            var dataProcessor = new DataProcessor(
                new FareByRulesCalculator(timeZonesHelper, localRules),
                new FromFileDataSupplier(input, timeZonesHelper),
                dataSaver
            );

            long startTime = currentTimeMillis();
            dataProcessor.process();
            log.info("Processing done. Execution time: " + (currentTimeMillis() - startTime) / 1000f + " sec.");
        } catch (Exception ex) {
            log.error("Cannot process data: " + ex.getMessage(), ex);
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
