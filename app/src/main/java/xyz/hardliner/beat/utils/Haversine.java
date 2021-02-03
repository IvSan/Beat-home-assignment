package xyz.hardliner.beat.utils;

import xyz.hardliner.beat.domain.LatLong;

import static java.lang.Math.asin;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

public class Haversine {

    public static final double R = 6371;

    public static double getDistance(LatLong start, LatLong stop) {
        double dLat = toRadians(stop.latitude - start.latitude);
        double dLon = toRadians(stop.longitude - start.longitude);
        double startLatRad = toRadians(start.latitude);
        double stopLatRad = toRadians(stop.latitude);
        double h = pow(sin(dLat / 2), 2) + pow(sin(dLon / 2), 2) * Math.cos(startLatRad) * Math.cos(stopLatRad);
        return 2 * R * asin(sqrt(h));
    }

}
