package xyz.hardliner.beat.domain;

import java.time.ZoneId;
import java.util.List;

public class DataBatch {

    public final Long rideId;
    public final ZoneId city;
    public final List<Position> positions;

    public DataBatch(Long rideId, ZoneId city, List<Position> positions) {
        this.rideId = rideId;
        this.city = city;
        this.positions = positions;
    }

    @Override
    public String toString() {
        return "DataBatch{" +
            "rideId=" + rideId +
            ", city=" + city +
            ", positions=" + positions +
            '}';
    }
}
