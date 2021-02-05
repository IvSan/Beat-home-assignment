package xyz.hardliner.beat.service;

import org.junit.jupiter.api.Test;
import xyz.hardliner.beat.exception.InvalidDataPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static xyz.hardliner.beat.service.LineParser.parse;

class LineParserTest {

    @Test
    void shouldParseLine() {
        var line = "1,37.963158,23.724872,1405595144";

        var data = parse(line);

        assertEquals(1, data.rideId);
        assertEquals(37.963158, data.position.latLong.latitude);
        assertEquals(23.724872, data.position.latLong.longitude);
        assertEquals(1405595144, data.position.timestamp);
    }

    @Test
    void shouldNotParseInvalidLatitude() {
        var line = "1,137.963158,23.724872,1405595144";

        Exception exception = assertThrows(InvalidDataPoint.class, () -> {
            parse(line);
        });

        assertEquals("Invalid latitude: 137.963158", exception.getMessage());
    }

}