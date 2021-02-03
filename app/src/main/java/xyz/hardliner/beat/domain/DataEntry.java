package xyz.hardliner.beat.domain;

public class DataEntry {
    public final long rideId;
    public final LatLong coordinates;
    public final long timestamp;

    public DataEntry(long rideId, LatLong coordinates, long timestamp) {
        this.rideId = rideId;
        this.coordinates = coordinates;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "DataEntry{" +
            "rideId=" + rideId +
            ", coordinates=" + coordinates +
            ", timestamp=" + timestamp +
            '}';
    }
}
