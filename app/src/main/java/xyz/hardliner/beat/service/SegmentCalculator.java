package xyz.hardliner.beat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hardliner.beat.domain.CalculationReport;
import xyz.hardliner.beat.domain.Position;
import xyz.hardliner.beat.utils.TimezonesHelper;

import java.math.BigDecimal;
import java.time.ZoneId;

import static java.lang.String.format;
import static java.math.BigDecimal.valueOf;

public class SegmentCalculator {

    private static final Logger log = LoggerFactory.getLogger(SegmentCalculator.class);
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

    private final TimezonesHelper timezonesHelper;

    public SegmentCalculator(TimezonesHelper timezonesHelper) {
        this.timezonesHelper = timezonesHelper;
    }

    public CalculationReport calculateSegmentCost(Position start, Position stop, ZoneId timezone, String lineToLog) {
        var segmentDistance = start.latLong.distanceTo(stop.latLong);
        var segmentTime = stop.timestamp - start.timestamp;
        var segmentSpeed = segmentDistance / segmentTime;

        if (segmentSpeed > MAX_VALID_SPEED_KM_SEC) {
            log.warn(format("Processing line: '%s'. Max allowed speed breach: %.2f km/h",
                lineToLog, segmentSpeed / KMH_TO_KMSEC));
            return new CalculationReport(false);
        }

        BigDecimal segmentCost;

        if (segmentDistance == 0.0d || segmentSpeed < MAX_IDLE_SPEED_KM_SEC) {
            segmentCost = IDLE_FARE.multiply(valueOf(segmentTime));
            if (IS_DEBUG_LOGGING_ENABLED) log.debug(format(
                "Processing line: '%s'. S = %f km, dt = %d sec, V = %f km/h, Idle fare, cost is %s",
                lineToLog, segmentDistance, segmentTime, segmentSpeed / KMH_TO_KMSEC, segmentCost));
        } else {
            var localMinutesOfDay = timezonesHelper.getLocalMinutesOfDay(stop.timestamp, timezone);
            if (localMinutesOfDay == 0 || localMinutesOfDay > DAY_NIGHT_CHANGING_MINUTE) {
                segmentCost = DAY_TIME_FARE.multiply(valueOf(segmentDistance));
                if (IS_DEBUG_LOGGING_ENABLED) log.debug(format(
                    "Processing line: '%s'. S = %f km, dt = %d sec, V = %f km/h, Local time is %02d:%02d, DAY time fare, cost is %s",
                    lineToLog, segmentDistance, segmentTime, segmentSpeed / KMH_TO_KMSEC, localMinutesOfDay / 60, localMinutesOfDay % 60, segmentCost));
            } else {
                segmentCost = NIGHT_TIME_FARE.multiply(valueOf(segmentDistance));
                if (IS_DEBUG_LOGGING_ENABLED) log.debug(format(
                    "Processing line: '%s'. S = %f km, dt = %d sec, V = %f km/h, Local time is %02d:%02d, NIGHT time fare, cost is %s",
                    lineToLog, segmentDistance, segmentTime, segmentSpeed / KMH_TO_KMSEC, localMinutesOfDay / 60, localMinutesOfDay % 60, segmentCost));
            }
        }

        return new CalculationReport(segmentCost);
    }

}
