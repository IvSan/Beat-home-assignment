package xyz.hardliner.beat.domain;

import java.math.BigDecimal;
import java.time.ZoneId;

import static java.math.RoundingMode.HALF_UP;
import static xyz.hardliner.beat.utils.TimezonesHelper.retrieveTimeZone;

public class Ride {

    private static final BigDecimal MIN_FARE = BigDecimal.valueOf(3.47);

    public final long rideId;
    public final ZoneId timezone;
    public DataEntry lastData;
    private BigDecimal cost;

    public Ride(DataEntry data) {
        this.rideId = data.rideId;
        this.lastData = data;
        this.timezone = retrieveTimeZone(data.position.latLong);
        this.cost = BigDecimal.valueOf(1.3);
    }

    public BigDecimal getCost() {
        if (cost.compareTo(MIN_FARE) < 0) {
            return MIN_FARE;
        }
        return cost.setScale(2, HALF_UP);
    }

    public void addCost(BigDecimal toAdd) {
        this.cost = this.cost.add(toAdd);
    }

    @Override
    public String toString() {
        return "Ride{" +
            "lastData=" + lastData +
            ", timezone=" + timezone +
            ", cost=" + cost +
            '}';
    }
}
