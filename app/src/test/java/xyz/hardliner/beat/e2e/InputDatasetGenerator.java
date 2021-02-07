package xyz.hardliner.beat.e2e;

import xyz.hardliner.beat.domain.DataEntry;
import xyz.hardliner.beat.domain.LatLong;
import xyz.hardliner.beat.domain.Position;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.lang.Math.random;

public class InputDatasetGenerator {

    public static void prepareMockDataset(String outputPath, int ridesNumber) throws IOException {
        var writer = new BufferedWriter(new FileWriter(outputPath));
        var generator = batchGenerator();

        for (long i = 0; i < ridesNumber; i++) {
            for (DataEntry data : generator.apply(i)) {
                writer
                    .append(String.valueOf(data.rideId))
                    .append(",")
                    .append(String.valueOf(data.position.latLong.latitude))
                    .append(",")
                    .append(String.valueOf(data.position.latLong.longitude))
                    .append(",")
                    .append(String.valueOf(data.position.timestamp))
                    .append("\n");
            }
        }

        writer.close();
    }

    private static Function<Long, List<DataEntry>> batchGenerator() {
        return rideId -> {
            var list = new ArrayList<DataEntry>();
            list.add(new DataEntry(rideId, new Position(new LatLong(38.0, 24.0),
                randomNumWithinRange(1600000000L, 2000000000L))));

            for (int i = 0; i < randomNumWithinRange(20, 2000); i++) {
                var last = list.get(list.size() - 1);
                list.add(new DataEntry(
                    rideId,
                    new Position(
                        new LatLong(last.position.latLong.latitude + randomCoordinationShift(),
                            last.position.latLong.longitude + +randomCoordinationShift()),
                        last.position.timestamp + randomNumWithinRange(1, 300))));
            }

            return list;
        };
    }

    private static long randomNumWithinRange(long leftLimit, long rightLimit) {
        return leftLimit + (long) (random() * (rightLimit - leftLimit));
    }

    private static int randomNumWithinRange(int leftLimit, int rightLimit) {
        return leftLimit + (int) (random() * (rightLimit - leftLimit));
    }

    private static double randomCoordinationShift() {
        return (random() - 0.5) * 2 * 0.1;
    }

}
