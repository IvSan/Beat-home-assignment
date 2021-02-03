package xyz.hardliner.beat.domain;

import static xyz.hardliner.beat.utils.Haversine.getDistance;

public class LatLong {

    public final double latitude;
    public final double longitude;

    public LatLong(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double distanceTo(LatLong stop) {
        return getDistance(this, stop);
    }

    @Override
    public String toString() {
        return "LatLong{" +
            "latitude=" + latitude +
            ", longitude=" + longitude +
            '}';
    }
}
