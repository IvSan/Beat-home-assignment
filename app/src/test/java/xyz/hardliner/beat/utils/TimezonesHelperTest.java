package xyz.hardliner.beat.utils;

import org.junit.jupiter.api.Test;
import xyz.hardliner.beat.domain.LatLong;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimezonesHelperTest {

    private TimezonesHelper underTest = new TimezonesHelper();

    @Test
    void shouldRetrieveTimeZone() {
        assertEquals(ZoneId.of("Europe/Athens"), underTest.retrieveTimeZone(new LatLong(38.034659, 23.855761)));
        assertEquals(ZoneId.of("Europe/Amsterdam"), underTest.retrieveTimeZone(new LatLong(52.35792039688913, 4.85652114520263)));
        assertEquals(ZoneId.of("America/Lima"), underTest.retrieveTimeZone(new LatLong(-12.05, -77.03)));
    }

    @Test
    void shouldGetLocalMinutesOfDay() {
        assertEquals(913, underTest.getLocalMinutesOfDay(1612530824L, ZoneId.of("Europe/Athens"))); //winter time
        assertEquals(493, underTest.getLocalMinutesOfDay(1594530824L, ZoneId.of("Europe/Athens"))); //summer time
        assertEquals(0, underTest.getLocalMinutesOfDay(1612476050L, ZoneId.of("Europe/Athens")));
        assertEquals(1439, underTest.getLocalMinutesOfDay(1612562390L, ZoneId.of("Europe/Athens")));
    }

}