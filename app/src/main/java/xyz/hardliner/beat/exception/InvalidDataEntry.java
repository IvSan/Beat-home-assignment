package xyz.hardliner.beat.exception;

public class InvalidDataEntry extends RuntimeException {

    public InvalidDataEntry() {
    }

    public InvalidDataEntry(String message) {
        super(message);
    }

    public InvalidDataEntry(String message, Throwable cause) {
        super(message, cause);
    }

}
