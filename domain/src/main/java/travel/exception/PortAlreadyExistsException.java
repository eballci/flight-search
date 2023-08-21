package travel.exception;

public class PortAlreadyExistsException extends IllegalArgumentException {
    public PortAlreadyExistsException(String message) {
        super(message);
    }
}
