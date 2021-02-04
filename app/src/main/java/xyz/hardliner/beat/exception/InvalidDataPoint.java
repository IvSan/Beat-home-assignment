package xyz.hardliner.beat.exception;

public class InvalidDataPoint extends RuntimeException {

    public InvalidDataPoint() {
    }

    public InvalidDataPoint(String message) {
        super(message);
    }

    public InvalidDataPoint(String message, Throwable cause) {
        super(message, cause);
    }

}
