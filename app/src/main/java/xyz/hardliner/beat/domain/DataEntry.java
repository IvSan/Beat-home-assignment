package xyz.hardliner.beat.domain;

public class DataEntry {

    public final long rideId;
    public final Position position;

    public DataEntry(long rideId, Position position) {
        this.rideId = rideId;
        this.position = position;
    }

    @Override
    public String toString() {
        return "DataEntry{" +
            "rideId=" + rideId +
            ", position=" + position +
            '}';
    }
}
