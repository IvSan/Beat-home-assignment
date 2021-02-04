package xyz.hardliner.beat.exception;

public class TopSpeedBreached extends InvalidDataPoint {

    public TopSpeedBreached() {
    }

    public TopSpeedBreached(String message) {
        super(message);
    }

    public TopSpeedBreached(String message, Throwable cause) {
        super(message, cause);
    }
}
