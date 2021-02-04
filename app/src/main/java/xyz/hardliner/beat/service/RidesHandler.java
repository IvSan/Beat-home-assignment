package xyz.hardliner.beat.service;

import xyz.hardliner.beat.domain.DataEntry;
import xyz.hardliner.beat.domain.Ride;
import xyz.hardliner.beat.exception.TopSpeedBreached;

import java.math.BigDecimal;
import java.util.HashMap;

import static java.math.BigDecimal.valueOf;
import static xyz.hardliner.beat.utils.TimezonesHelper.getLocalMinutesOfDay;

public class RidesHandler {

    private static final int DAY_NIGHT_CHANGING_HOUR = 5;
    private static final int DAY_NIGHT_CHANGING_MINUTE = DAY_NIGHT_CHANGING_HOUR * 60;

    private static final BigDecimal DAY_TIME_FARE = valueOf(0.74);
    private static final BigDecimal NIGHT_TIME_FARE = valueOf(1.3);
    private static final BigDecimal IDLE_FARE = valueOf(11.90 / 3600);

    private static final int MAX_VALID_SPEED_KM_H = 100;
    private static final int MAX_IDLE_SPEED_KM_H = 100;
    private static final float KMH_TO_KMSEC = 0.00027778f;
    private static final float MAX_VALID_SPEED_KM_SEC = MAX_VALID_SPEED_KM_H * KMH_TO_KMSEC;
    private static final float MAX_IDLE_SPEED_KM_SEC = MAX_IDLE_SPEED_KM_H * KMH_TO_KMSEC;

    private final HashMap<Long, Ride> rides;

    public RidesHandler() {
        rides = new HashMap<>();
    }

    public boolean containsRide(long rideId) {
        return rides.containsKey(rideId);
    }

    public HashMap<Long, Ride> getRides() {
        return rides;
    }

    public void createRide(DataEntry data) {
        rides.put(data.rideId, new Ride(data));
    }

    public void updateRide(DataEntry data) {
        var ride = rides.get(data.rideId);

        var segmentDistance = ride.lastData.position.latLong.distanceTo(data.position.latLong);
        var segmentTime = data.position.timestamp - ride.lastData.position.timestamp;
        var speed = segmentDistance / segmentTime;

        if (speed > MAX_VALID_SPEED_KM_SEC) {
            throw new TopSpeedBreached("Max allowed speed breach: " + String.format("%.2f", speed / KMH_TO_KMSEC) + " km/h");
        }

        if (segmentDistance == 0.0d || speed < MAX_IDLE_SPEED_KM_SEC) {
            ride.addCost(IDLE_FARE.multiply(valueOf(segmentTime)));
        }

        var localMinutesOfDay = getLocalMinutesOfDay(data.position.timestamp, ride.timezone);
        if (localMinutesOfDay == 0 || localMinutesOfDay > DAY_NIGHT_CHANGING_MINUTE) {
            ride.addCost(DAY_TIME_FARE.multiply(valueOf(segmentDistance)));
        } else {
            ride.addCost(NIGHT_TIME_FARE.multiply(valueOf(segmentDistance)));
        }
        ride.lastData = data;
    }
}
