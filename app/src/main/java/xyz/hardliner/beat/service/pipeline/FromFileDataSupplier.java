package xyz.hardliner.beat.service.pipeline;

import xyz.hardliner.beat.domain.DataBatch;
import xyz.hardliner.beat.domain.DataEntry;
import xyz.hardliner.beat.utils.TimezonesManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static xyz.hardliner.beat.service.pipeline.LineParser.parse;

public class FromFileDataSupplier implements DataSupplier {

    public final String path;

    private final TimezonesManager tzManager;
    private final Scanner scanner;
    private DataEntry firstDataEntryOfNextRide;

    public FromFileDataSupplier(String path, TimezonesManager tzManager) {
        this.path = path;
        this.tzManager = tzManager;
        try {
            var inputStream = new FileInputStream(path);
            scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
            firstDataEntryOfNextRide = parse(scanner.nextLine());
        } catch (FileNotFoundException ex) {
            throw new IllegalArgumentException("Cannot find file " + path, ex);
        }
    }

    @Override
    public synchronized DataBatch get() {
        if (firstDataEntryOfNextRide == null) {
            return null; // end of data
        }

        var batch = new DataBatch(
            firstDataEntryOfNextRide.rideId,
            tzManager.retrieveTimeZone(firstDataEntryOfNextRide.position.latLong),
            new ArrayList<>(Arrays.asList(firstDataEntryOfNextRide.position)));

        try {
            var next = parse(scanner.nextLine());
            while (batch.rideId.equals(next.rideId)) {
                batch.positions.add(next.position);
                next = parse(scanner.nextLine());
            }
            firstDataEntryOfNextRide = next;
        } catch (NoSuchElementException ex) {
            // end of file;
            firstDataEntryOfNextRide = null;
        }

        return batch;
    }
}
