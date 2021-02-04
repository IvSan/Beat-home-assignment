package xyz.hardliner.beat;

import xyz.hardliner.beat.service.DataProcessor;
import xyz.hardliner.beat.service.FileReader;
import xyz.hardliner.beat.service.RidesHandler;

import static java.math.RoundingMode.HALF_UP;

public class App {

    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        var unit = new DataProcessor(
            new FileReader("./app/src/test/resources/time_sorted_paths.csv"),
            new RidesHandler()
        );
        unit.process().entrySet().forEach(e -> {
            var ride = e.getValue();
            System.out.printf("Ride id='%d', location='%s', fare='%s'%n",
                ride.rideId,
                ride.timezone,
                ride.getCost().setScale(2, HALF_UP)
            );
        });
    }
}
