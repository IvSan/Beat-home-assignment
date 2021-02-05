package xyz.hardliner.beat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import xyz.hardliner.beat.domain.DataEntry;
import xyz.hardliner.beat.domain.LatLong;
import xyz.hardliner.beat.domain.Position;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RidesHandlerTest {

    RidesHandler underTest;

    @BeforeEach
    public void initHandler() {
        underTest = new RidesHandler();
    }

    @Test
    void shouldCreateRide() {
        var datapoint = new DataEntry(855L, new Position(new LatLong(38.034659, 23.855761), 1612530824L));

        underTest.process(datapoint);

        assertEquals(1, underTest.getRides().size());
        assertEquals(datapoint, underTest.getRides().get(855L).lastData);
        assertEquals(BigDecimal.valueOf(3.47), underTest.getRides().get(855L).getCost());
    }

//    @Test
//    void shouldUpdateRegularRide2() {
//        var start = new DataEntry(855L, new Position(new LatLong(38.034659, 23.855761), 1612530824L));
//        // s=0.2094km dt=13s v~=57.99km/h daytime cost~=0.1550
//        var checkpoint = new DataEntry(855L, new Position(new LatLong(38.035559, 23.853661), 1612530837L));
//        // s=0.6881km dt=43s v~=57.61km/h daytime cost~=0.5092
//        var stop = new DataEntry(855L, new Position(new LatLong(38.031659, 23.859761), 1612530880L));
//
//        underTest.process(start);
//        underTest.process(checkpoint);
//        underTest.process(stop);
//
//        assertEquals(1, underTest.getRides().size());
//        assertEquals(stop, underTest.getRides().get(855L).lastData);
//        assertEquals(BigDecimal.valueOf(3.47), underTest.getRides().get(855L).getCost());
//    }

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
        assertEquals(BigDecimal.valueOf(4.61), underTest.getRides().get(855L).getCost().setScale(2, HALF_UP));
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
        assertEquals(BigDecimal.valueOf(5.95), underTest.getRides().get(855L).getCost().setScale(2, HALF_UP));
    }

}