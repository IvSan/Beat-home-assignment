package xyz.hardliner.beat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hardliner.beat.domain.DataEntry;
import xyz.hardliner.beat.domain.Ride;
import xyz.hardliner.beat.exception.TopSpeedBreached;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;
import static java.math.BigDecimal.valueOf;
import static xyz.hardliner.beat.utils.TimezonesHelper.getLocalMinutesOfDay;

public class RidesHandler {

    private static final Logger log = LoggerFactory.getLogger(RidesHandler.class);
    private static final boolean IS_DEBUG_LOGGING_ENABLED = log.isDebugEnabled();

    private static final int DAY_NIGHT_CHANGING_HOUR = 5;
    private static final int DAY_NIGHT_CHANGING_MINUTE = DAY_NIGHT_CHANGING_HOUR * 60;

    private static final BigDecimal DAY_TIME_FARE = valueOf(0.74);
    private static final BigDecimal NIGHT_TIME_FARE = valueOf(1.3);
    private static final BigDecimal IDLE_FARE = valueOf(11.90 / 3600);

    private static final int MAX_VALID_SPEED_KM_H = 100;
    private static final int MAX_IDLE_SPEED_KM_H = 10;
    private static final float KMH_TO_KMSEC = 0.00027778f;
    private static final float MAX_VALID_SPEED_KM_SEC = MAX_VALID_SPEED_KM_H * KMH_TO_KMSEC;
    private static final float MAX_IDLE_SPEED_KM_SEC = MAX_IDLE_SPEED_KM_H * KMH_TO_KMSEC;

    private final Map<Long, Ride> rides;

    public RidesHandler() {
        rides = new ConcurrentHashMap<>();
    }

    public Map<Long, Ride> getRides() {
        return rides;
    }

    public void process(DataEntry data) {
        if (!rides.containsKey(data.rideId)) {
            createNewRide(data);
            return;
        }
        updateExistingRide(data);
    }

    private void createNewRide(DataEntry data) {
        var ride = new Ride(data);
        if (IS_DEBUG_LOGGING_ENABLED)
            log.debug(format("New ride found, ride id '%d', ride timezone '%s'", ride.rideId, ride.timezone));
        rides.put(data.rideId, ride);
    }

    private void updateExistingRide(DataEntry data) {
        var ride = rides.get(data.rideId);

        var segmentDistance = ride.lastData.position.latLong.distanceTo(data.position.latLong);
        var segmentTime = data.position.timestamp - ride.lastData.position.timestamp;
        var segmentSpeed = segmentDistance / segmentTime;
        if (IS_DEBUG_LOGGING_ENABLED) log.debug(format("S = %f km, dt = %d sec, V = %f km/h", segmentDistance,
            segmentTime, segmentSpeed / KMH_TO_KMSEC));

        if (segmentSpeed > MAX_VALID_SPEED_KM_SEC) {
            throw new TopSpeedBreached("Max allowed speed breach: " + format("%.2f", segmentSpeed / KMH_TO_KMSEC) + " km/h");
        }

        BigDecimal segmentCost;
        if (segmentDistance == 0.0d || segmentSpeed < MAX_IDLE_SPEED_KM_SEC) {
            segmentCost = IDLE_FARE.multiply(valueOf(segmentTime));
            if (IS_DEBUG_LOGGING_ENABLED) log.debug(format("Idle fare, cost is %s", segmentCost));
        } else {
            var localMinutesOfDay = getLocalMinutesOfDay(data.position.timestamp, ride.timezone);
            if (localMinutesOfDay == 0 || localMinutesOfDay > DAY_NIGHT_CHANGING_MINUTE) {
                segmentCost = DAY_TIME_FARE.multiply(valueOf(segmentDistance));
                if (IS_DEBUG_LOGGING_ENABLED) log.debug(format("Local time is %02d:%02d, DAY time fare, cost is %s",
                    localMinutesOfDay / 60, localMinutesOfDay % 60, segmentCost));
            } else {
                segmentCost = NIGHT_TIME_FARE.multiply(valueOf(segmentDistance));
                if (IS_DEBUG_LOGGING_ENABLED) log.debug(format("Local time is %02d:%02d, NIGHT time fare, cost is %s",
                    localMinutesOfDay / 60, localMinutesOfDay % 60, segmentCost));
            }
        }

        ride.addCost(segmentCost);
        ride.lastData = data;
    }
}
