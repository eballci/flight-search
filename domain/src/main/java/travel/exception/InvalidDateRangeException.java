package travel.exception;

public class InvalidDateRangeException extends IllegalArgumentException {
    public InvalidDateRangeException(String message) {
        super(message);
    }
}
