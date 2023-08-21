package travel.exception;

public class PastDepartureTimeException extends RuntimeException {
    public PastDepartureTimeException(String message) {
        super(message);
    }
}
