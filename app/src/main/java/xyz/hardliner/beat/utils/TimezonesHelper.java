package xyz.hardliner.beat.utils;

import net.iakovlev.timeshape.TimeZoneEngine;
import xyz.hardliner.beat.domain.LatLong;

import java.time.Instant;
import java.time.ZoneId;

public class TimezonesHelper implements TimezonesManager {

    private final TimeZoneEngine engine;

    public TimezonesHelper() {
        engine = TimeZoneEngine.initialize();
    }

    public ZoneId retrieveTimeZone(LatLong latLong) {
        return engine.query(latLong.latitude, latLong.longitude)
            .orElseThrow(() -> new IllegalArgumentException("Cannot retrieve timezone for: " + latLong));
    }

    public int getLocalMinutesOfDay(long timestamp, ZoneId zoneId) {
        var zonedDateTime = Instant.ofEpochSecond(timestamp).atZone(zoneId);
        return zonedDateTime.getHour() * 60 + zonedDateTime.getMinute();
    }

}
