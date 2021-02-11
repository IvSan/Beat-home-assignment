package xyz.hardliner.beat.service.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hardliner.beat.domain.RideReport;
import xyz.hardliner.beat.domain.SegmentReport;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.math.BigDecimal.valueOf;

public class GreeceRulebook implements Rulebook {

    private static final Logger log = LoggerFactory.getLogger(GreeceRulebook.class);

    public static final BigDecimal MIN_FARE = BigDecimal.valueOf(3.47);

    public static final int DAY_NIGHT_CHANGING_HOUR = 5;
    public static final int DAY_NIGHT_CHANGING_MINUTE = DAY_NIGHT_CHANGING_HOUR * 60;

    public static final BigDecimal FLAG_FARE = valueOf(1.3);
    public static final BigDecimal DAY_TIME_FARE_PER_KM = valueOf(0.74);
    public static final BigDecimal NIGHT_TIME_FARE_PER_KM = valueOf(1.3);
    public static final BigDecimal IDLE_FARE_PER_SEC = valueOf(11.90 / 3600);

    public static final int MAX_VALID_SPEED_KM_H = 100;
    public static final int MAX_IDLE_SPEED_KM_H = 10;
    public static final float KMH_TO_KMSEC = 0.00027778f;
    public static final float MAX_VALID_SPEED_KM_SEC = MAX_VALID_SPEED_KM_H * KMH_TO_KMSEC;
    public static final float MAX_IDLE_SPEED_KM_SEC = MAX_IDLE_SPEED_KM_H * KMH_TO_KMSEC;

    @Override
    public RideReport applyRulesOnRide(RideReport report) {
        report.cost = report.cost.add(FLAG_FARE);
        if (report.cost.compareTo(MIN_FARE) < 0) {
            report.cost = MIN_FARE;
        }
        return report;
    }

    @Override
    public BigDecimal applyRulesOnSegment(SegmentReport report) {
        if (report.distance == 0.0d || report.speed < MAX_IDLE_SPEED_KM_SEC) {
            return IDLE_FARE_PER_SEC.multiply(valueOf(report.duration));
        } else {
            if (report.localMinutesOfDay == 0 || report.localMinutesOfDay > DAY_NIGHT_CHANGING_MINUTE) {
                return DAY_TIME_FARE_PER_KM.multiply(valueOf(report.distance));
            } else {
                return NIGHT_TIME_FARE_PER_KM.multiply(valueOf(report.distance));
            }
        }
    }

    @Override
    public boolean isValidSegment(SegmentReport report) {
        if (report.speed > MAX_VALID_SPEED_KM_SEC) {
            log.debug(format("Max allowed speed breach: %.2f km/h", report.speed / KMH_TO_KMSEC));
            return false;
        }
        return true;
    }
}
