package xyz.hardliner.beat.service;

import xyz.hardliner.beat.domain.DataEntry;
import xyz.hardliner.beat.domain.LatLong;
import xyz.hardliner.beat.exception.InvalidDataEntry;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;

public class LineParser {

    public static DataEntry parse(String line) {
        var parts = line.split(",");
        try {
            return new DataEntry(
                parseLong(parts[0]),
                new LatLong(parseDouble(parts[1]), parseDouble(parts[2])),
                parseLong(parts[3])
            );
        } catch (Exception ex) {
            throw new InvalidDataEntry("Cannot parse line: " + line, ex);
        }
    }

}
