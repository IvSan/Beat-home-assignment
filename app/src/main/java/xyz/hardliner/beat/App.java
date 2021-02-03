package xyz.hardliner.beat;

import xyz.hardliner.beat.service.FareEstimator;
import xyz.hardliner.beat.service.FileReader;

public class App {

    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        var unit = new FareEstimator(new FileReader("./app/src/test/resources/paths.csv"));
        unit.run();
    }
}
