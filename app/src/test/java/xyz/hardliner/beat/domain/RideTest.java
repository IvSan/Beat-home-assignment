package xyz.hardliner.beat.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RideTest {

    @Test
    void shouldCreateRide() {
        var datapoint = new DataEntry(855L, new Position(new LatLong(38.034659, 23.855761), 1612530824L));
        var ride = new Ride(datapoint);

        assertEquals(855L, ride.rideId);
        assertEquals(ZoneId.of("Europe/Athens"), ride.timezone);
        assertEquals(datapoint, ride.lastData);
        assertEquals(BigDecimal.valueOf(3.47), ride.getCost());
    }

    @Test
    void shouldCalculateCosts() {
        var datapoint = new DataEntry(855L, new Position(new LatLong(38.034659, 23.855761), 1612530824L));
        var ride = new Ride(datapoint);

        assertEquals(BigDecimal.valueOf(3.47), ride.getCost());

        ride.addCost(BigDecimal.valueOf(2.985));

        assertEquals(BigDecimal.valueOf(4.29), ride.getCost());
    }
}