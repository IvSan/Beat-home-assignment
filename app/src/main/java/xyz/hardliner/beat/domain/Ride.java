package xyz.hardliner.beat.domain;

public class Ride {

    public final DataEntry lastData;
    private int cost; // In cents

    public Ride(DataEntry lastData) {
        this.lastData = lastData;
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
            ", cost=" + cost +
            '}';
    }
}
