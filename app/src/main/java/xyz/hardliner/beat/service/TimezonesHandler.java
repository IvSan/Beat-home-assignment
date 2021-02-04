package xyz.hardliner.beat.service;

import net.iakovlev.timeshape.TimeZoneEngine;
import xyz.hardliner.beat.domain.LatLong;
import xyz.hardliner.beat.domain.Position;

import java.time.Instant;
import java.time.ZoneId;

public class TimezonesHandler {

    private static final TimeZoneEngine engine = TimeZoneEngine.initialize();

    public static ZoneId retrieveTimeZone(LatLong latLong) {
        return engine.query(latLong.latitude, latLong.longitude)
            .orElseThrow(() -> new IllegalArgumentException("Cannot retrieve timezone for: " + latLong));
    }

    public static int getLocalHour(Position position) {
        return getLocalHour(position.timestamp, retrieveTimeZone(position.latLong));
    }

    public static int getLocalHour(long timestamp, ZoneId zoneId) {
        return Instant.ofEpochSecond(timestamp).atZone(zoneId).getHour();
    }

}
