package xyz.hardliner.beat.domain;

import java.time.ZoneId;

import static xyz.hardliner.beat.service.TimezonesHandler.retrieveTimeZone;

public class Ride {

    public final DataEntry lastData;
    private final ZoneId timezone;
    private int cost; // In cents

    public Ride(DataEntry data) {
        this.lastData = data;
        this.timezone = retrieveTimeZone(data.position.latLong);
        this.cost = 130;
    }

    public float getCost() {
        if (cost <= 347) {
            return 3.47f;
        }
        return cost / 100f;
    }

    public void addCost(int cost) {
        this.cost += cost;
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
