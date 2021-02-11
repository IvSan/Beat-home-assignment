package xyz.hardliner.beat.utils;

import xyz.hardliner.beat.domain.LatLong;

import java.time.ZoneId;

public interface TimezonesManager {

    ZoneId retrieveTimeZone(LatLong latLong);

    int getLocalMinutesOfDay(long timestamp, ZoneId zoneId);

}
