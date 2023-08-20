package travel.exception;

public class PastTimeQueryException extends RuntimeException {
    public PastTimeQueryException(String message) {
        super(message);
    }
}