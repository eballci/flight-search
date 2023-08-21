package travel.exception;

public class IdenticalDepartureAndArrivalException extends IllegalArgumentException {
    public IdenticalDepartureAndArrivalException(String message) {
        super(message);
    }
}
