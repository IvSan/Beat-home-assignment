package xyz.hardliner.beat;

import xyz.hardliner.beat.service.DataProcessor;
import xyz.hardliner.beat.service.FileReader;
import xyz.hardliner.beat.service.RidesHandler;

public class App {

    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        var unit = new DataProcessor(
            new FileReader("./app/src/test/resources/paths.csv"),
            new RidesHandler()
        );
        unit.process();
    }
}
