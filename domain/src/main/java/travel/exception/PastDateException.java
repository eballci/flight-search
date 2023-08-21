package travel.exception;

public class PastDateException extends RuntimeException {
    public PastDateException(String message) {
        super(message);
    }
}