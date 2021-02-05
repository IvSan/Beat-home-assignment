package xyz.hardliner.beat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hardliner.beat.service.DataProcessor;
import xyz.hardliner.beat.service.FileReader;
import xyz.hardliner.beat.service.RidesHandler;

public class App {

    private static Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        var unit = new DataProcessor(
            new FileReader("./app/src/test/resources/time_sorted_paths.csv"),
            new RidesHandler()
        );
        unit.process().entrySet().forEach(e -> {
            var ride = e.getValue();
            log.info(String.format("Ride id='%d', location='%s', fare='%s'",
                ride.rideId,
                ride.timezone,
                ride.getCost()
            ));
        });
    }
}
