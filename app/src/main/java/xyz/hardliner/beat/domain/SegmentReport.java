package xyz.hardliner.beat.domain;

public class SegmentReport {

    public final double distance;
    public final long duration;
    public final double speed; // km/sec
    public final int localMinutesOfDay;

    public SegmentReport(double distance,
                         long duration,
                         int localMinutesOfDay) {
        this.distance = distance;
        this.duration = duration;
        this.speed = distance / duration;
        this.localMinutesOfDay = localMinutesOfDay;
    }

    @Override
    public String toString() {
        return "SegmentReport{" +
            "distance=" + distance +
            ", duration=" + duration +
            ", speed=" + speed +
            ", localMinutesOfDay=" + localMinutesOfDay +
            '}';
    }
}
