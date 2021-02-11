package xyz.hardliner.beat.domain;

import java.math.BigDecimal;

public class RideReport {

    public final Long rideId;
    public BigDecimal cost;

    public RideReport(Long rideId, BigDecimal cost) {
        this.rideId = rideId;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "RideReport{" +
            "rideId=" + rideId +
            ", cost=" + cost +
            '}';
    }
}
