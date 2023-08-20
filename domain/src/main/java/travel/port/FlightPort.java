package travel.port;

import travel.command.FlightCreationCommand;
import travel.model.Flight;
import travel.query.OneWayFlightQuery;

import java.util.List;

public interface FlightPort {
    List<Flight> getAvailableFlights(OneWayFlightQuery query);
    Flight createFlight(FlightCreationCommand command);
}
