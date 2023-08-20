package travel.exception;

public class PastTimeCommandException extends RuntimeException {
    public PastTimeCommandException(String message) {
        super(message);
    }
}
