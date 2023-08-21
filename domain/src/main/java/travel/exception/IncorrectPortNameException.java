package travel.exception;

public class IncorrectPortNameException extends IllegalArgumentException {
    public IncorrectPortNameException(String message) {
        super(message);
    }
}
