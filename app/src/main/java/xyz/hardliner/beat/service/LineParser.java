package xyz.hardliner.beat.service;

import xyz.hardliner.beat.domain.DataEntry;
import xyz.hardliner.beat.domain.LatLong;
import xyz.hardliner.beat.domain.Position;
import xyz.hardliner.beat.exception.InvalidDataPoint;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;

public class LineParser {

    final static double MIN_LAT = -90;
    final static double MAX_LAT = 90;
    final static double MIN_LON = -180;
    final static double MAX_LON = 180;

    public static DataEntry parse(String line) {
        var parts = line.split(",");

        var lat = parseDouble(parts[1]);
        var lon = parseDouble(parts[2]);

        if (lat < MIN_LAT || lat > MAX_LAT) {
            throw new InvalidDataPoint("Invalid latitude: " + lat);
        }
        if (lon < MIN_LON || lon > MAX_LON) {
            throw new InvalidDataPoint("Invalid longitude: " + lon);
        }

        return new DataEntry(
            parseLong(parts[0]),
            new Position(new LatLong(lat, lon), parseLong(parts[3]))
        );
    }

}
