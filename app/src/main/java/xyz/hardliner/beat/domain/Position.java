package xyz.hardliner.beat.domain;

public class Position {
    public final LatLong latLong;
    public final long timestamp;

    public Position(LatLong latLong, long timestamp) {
        this.latLong = latLong;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Position{" +
            "latLong=" + latLong +
            ", timestamp=" + timestamp +
            '}';
    }

    public boolean isDaytime() {
        return false; // TODO
    }
}
