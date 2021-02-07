package xyz.hardliner.beat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import xyz.hardliner.beat.domain.DataEntry;
import xyz.hardliner.beat.domain.LatLong;
import xyz.hardliner.beat.domain.Position;
import xyz.hardliner.beat.exception.InvalidDataPoint;
import xyz.hardliner.beat.utils.TimezonesHelper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RidesHandlerTest {

    RidesHandler underTest;
    private final TimezonesHelper timezonesHelper =  new TimezonesHelper();

    @BeforeEach
    public void initHandler() {
        underTest = new RidesHandler(timezonesHelper);
    }

    @Test
    void shouldCreateRide() {
        var datapoint = new DataEntry(855L, new Position(new LatLong(38.034659, 23.855761), 1612530824L));

        underTest.process(datapoint);

        assertEquals(1, underTest.getRides().size());
        assertEquals(datapoint, underTest.getRides().get(855L).lastData);
        assertEquals(BigDecimal.valueOf(3.47), underTest.getRides().get(855L).getNormalizedCost());
    }

    @Test
    void shouldUpdateDayTimeRide() {
        var start = new DataEntry(855L, new Position(new LatLong(38.034659, 23.855761), 1612530824L));
        // s=2.066km dt=129s v~=57.66km/h daytime cost~=1.52884
        var checkpoint = new DataEntry(855L, new Position(new LatLong(38.049559, 23.841661), 1612530953L));
        // s=2.403km dt=150s v~=57.68km/h daytime cost~=1.77822
        var stop = new DataEntry(855L, new Position(new LatLong(38.067659, 23.856661), 1612531103L));

        underTest.process(start);
        underTest.process(checkpoint);
        underTest.process(stop);

        assertEquals(1, underTest.getRides().size());
        assertEquals(stop, underTest.getRides().get(855L).lastData);
        assertEquals(BigDecimal.valueOf(4.61), underTest.getRides().get(855L).getNormalizedCost());
    }

    @Test
    void shouldUpdateMixedTimeRide() {
        var start = new DataEntry(855L, new Position(new LatLong(38.034659, 23.855761), 1612475912L));
        // s=2.066km dt=129s v~=57.66km/h daytime cost~=1.52884
        var checkpoint = new DataEntry(855L, new Position(new LatLong(38.049559, 23.841661), 1612476041L));
        // s=2.403km dt=150s v~=57.68km/h nighttime cost~=3.1239
        var stop = new DataEntry(855L, new Position(new LatLong(38.067659, 23.856661), 1612476191L));

        underTest.process(start);
        underTest.process(checkpoint);
        underTest.process(stop);

        assertEquals(1, underTest.getRides().size());
        assertEquals(stop, underTest.getRides().get(855L).lastData);
        assertEquals(BigDecimal.valueOf(5.95), underTest.getRides().get(855L).getNormalizedCost());
    }

    @Test
    void shouldUpdateDayTimeIdle() {
        var start = new DataEntry(855L, new Position(new LatLong(38.034559, 23.855678), 1612530624L));
        // idle dt=200s daytime cost~=0.66111
        var checkpoint1 = new DataEntry(855L, new Position(new LatLong(38.034659, 23.855761), 1612530824L));
        // s=2.066km dt=129s v~=57.66km/h daytime cost~=1.52884
        var checkpoint2 = new DataEntry(855L, new Position(new LatLong(38.049559, 23.841661), 1612530953L));
        // s=2.403km dt=150s v~=57.68km/h daytime cost~=1.77822
        var stop = new DataEntry(855L, new Position(new LatLong(38.067659, 23.856661), 1612531103L));

        underTest.process(start);
        underTest.process(checkpoint1);
        underTest.process(checkpoint2);
        underTest.process(stop);

        assertEquals(1, underTest.getRides().size());
        assertEquals(stop, underTest.getRides().get(855L).lastData);
        assertEquals(BigDecimal.valueOf(5.27), underTest.getRides().get(855L).getNormalizedCost());
    }

    @Test
    void shouldUpdateNightTimeIdle() {
        var start = new DataEntry(855L, new Position(new LatLong(38.034559, 23.855678), 1612493712L));
        // idle dt=200s nighttime cost~=0.66111
        var checkpoint1 = new DataEntry(855L, new Position(new LatLong(38.034659, 23.855761), 1612493912L));
        // s=2.066km dt=129s v~=57.66km/h nighttime cost~=2.6858
        var checkpoint2 = new DataEntry(855L, new Position(new LatLong(38.049559, 23.841661), 1612494041L));
        // s=2.403km dt=150s v~=57.68km/h daytime cost~=1.77822
        var stop = new DataEntry(855L, new Position(new LatLong(38.067659, 23.856661), 1612494191L));

        underTest.process(start);
        underTest.process(checkpoint1);
        underTest.process(checkpoint2);
        underTest.process(stop);

        assertEquals(1, underTest.getRides().size());
        assertEquals(stop, underTest.getRides().get(855L).lastData);
        assertEquals(BigDecimal.valueOf(6.43), underTest.getRides().get(855L).getNormalizedCost());
    }

    @Test
    void shouldNotUpdateRideWithSpeedingDatapoint() {
        var start = new DataEntry(855L, new Position(new LatLong(38.034659, 23.855761), 1612530824L));
        // s=2.066km dt=129s v~=57.66km/h daytime cost~=1.52884
        var checkpoint1 = new DataEntry(855L, new Position(new LatLong(38.049559, 23.841661), 1612530953L));
        // s=2.403km dt=150s v~=57.68km/h daytime cost~=1.77822
        var checkpoint2 = new DataEntry(855L, new Position(new LatLong(38.067659, 23.856661), 1612531103L));
        // s=2.403km dt=80s v~=108.14km/h invalid
        var stop = new DataEntry(855L, new Position(new LatLong(38.049559, 23.841661), 1612531183L));

        underTest.process(start);
        underTest.process(checkpoint1);
        underTest.process(checkpoint2);
        Exception exception = assertThrows(InvalidDataPoint.class, () -> {
            underTest.process(stop);
        });

        assertEquals("Max allowed speed breach: 108.14 km/h", exception.getMessage());

        assertEquals(1, underTest.getRides().size());
        assertEquals(checkpoint2, underTest.getRides().get(855L).lastData);
        assertEquals(BigDecimal.valueOf(4.61), underTest.getRides().get(855L).getNormalizedCost());
    }

}