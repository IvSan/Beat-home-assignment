package xyz.hardliner.beat.service.processing;

import xyz.hardliner.beat.service.pipeline.DataSaver;
import xyz.hardliner.beat.service.pipeline.DataSupplier;

import java.util.Objects;
import java.util.stream.Stream;

public class DataProcessor {

    private final FareCalculator calculator;
    private final DataSupplier supplier;
    private final DataSaver saver;

    public DataProcessor(FareCalculator calculator, DataSupplier supplier, DataSaver saver) {
        this.calculator = calculator;
        this.supplier = supplier;
        this.saver = saver;
    }

    public void process() {
        Stream.generate(supplier).takeWhile(Objects::nonNull).parallel()
            .map(calculator::calculateRide)
            .forEach(saver::save);
    }

}
