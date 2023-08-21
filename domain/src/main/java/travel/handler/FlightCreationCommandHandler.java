package travel.handler;

import lombok.RequiredArgsConstructor;
import travel.command.FlightCreationCommand;
import travel.exception.IdenticalDepartureAndArrivalException;
import travel.exception.IncorrectPortNameException;
import travel.exception.PastDepartureTimeException;
import travel.model.Flight;
import travel.model.Port;
import travel.port.FlightPort;
import travel.port.PortPort;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class FlightCreationCommandHandler implements CommandHandler<Flight, FlightCreationCommand> {
    private final FlightPort flightPort;
    private final PortPort portPort;

    @Override
    public Flight handle(FlightCreationCommand command) {
        if (command.getDepartureTime().isBefore(LocalDateTime.now()))
            throw new PastDepartureTimeException("The departure date can not be past.");

        if (command.getDeparturePort().compareToIgnoreCase(command.getArrivalPort()) == 0)
            throw new IdenticalDepartureAndArrivalException("The departure and arrival ports can not be identical.");

        Port departure = portPort.findByName(command.getDeparturePort());
        Port arrival = portPort.findByName(command.getArrivalPort());

        if (departure == null)
            throw new IncorrectPortNameException("The departure port name is incorrect.");

        if (arrival == null)
            throw new IncorrectPortNameException("The arrival port name is incorrect.");

        return flightPort.createFlight(command);
    }
}
