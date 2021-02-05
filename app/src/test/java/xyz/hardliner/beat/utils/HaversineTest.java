package xyz.hardliner.beat.utils;

import org.junit.jupiter.api.Test;
import xyz.hardliner.beat.domain.LatLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static xyz.hardliner.beat.utils.Haversine.getDistance;

class HaversineTest {

    @Test
    void shouldGetDistance() {
        var start = new LatLong(38.034659, 23.855761);
        var stop = new LatLong(38.034659, 23.955761);
        assertEquals(0d, getDistance(start, start));
        assertEquals(8.758136625207994d, getDistance(start, stop));
    }

}