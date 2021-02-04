package xyz.hardliner.beat.utils;

import net.iakovlev.timeshape.TimeZoneEngine;
import xyz.hardliner.beat.domain.LatLong;
import xyz.hardliner.beat.domain.Position;

import java.time.Instant;
import java.time.ZoneId;

public class TimezonesHelper {

    private static final TimeZoneEngine engine = TimeZoneEngine.initialize();

    public static ZoneId retrieveTimeZone(LatLong latLong) {
        return engine.query(latLong.latitude, latLong.longitude)
            .orElseThrow(() -> new IllegalArgumentException("Cannot retrieve timezone for: " + latLong));
    }

    public static int getLocalMinutesOfDay(Position position) {
        return getLocalMinutesOfDay(position.timestamp, retrieveTimeZone(position.latLong));
    }

    public static int getLocalMinutesOfDay(long timestamp, ZoneId zoneId) {
        var zonedDateTime = Instant.ofEpochSecond(timestamp).atZone(zoneId);
        return zonedDateTime.getHour() * 60 + zonedDateTime.getMinute();
    }

}
