package xyz.hardliner.beat.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RideTest {

    @Test
    void shouldCreateRide() {
        var datapoint = new DataEntry(855L, new Position(new LatLong(38.034659, 23.855761), 1612530824L));
        var ride = new Ride(datapoint, ZoneId.of("Europe/Athens"));

        assertEquals(855L, ride.rideId);
        assertEquals(ZoneId.of("Europe/Athens"), ride.timezone);
        assertEquals(datapoint, ride.lastData);
        assertEquals(BigDecimal.valueOf(3.47), ride.getNormalizedCost());
    }

    @Test
    void shouldUpdateRide() {
        var datapoint = new DataEntry(855L, new Position(new LatLong(38.034659, 23.855761), 1612530824L));
        var ride = new Ride(datapoint, ZoneId.of("Europe/Athens"));

        assertEquals(BigDecimal.valueOf(3.47), ride.getNormalizedCost());

        var dataToUpdate = new DataEntry(855L, new Position(new LatLong(38.035, 23.856), 1612530854L));

        ride.updateRide(BigDecimal.valueOf(2.985), dataToUpdate);

        assertEquals(BigDecimal.valueOf(4.29), ride.getNormalizedCost());
        assertEquals(dataToUpdate, ride.lastData);
    }
}