package travel.handler;

import lombok.RequiredArgsConstructor;
import travel.exception.IncorrectPortNameException;
import travel.exception.PastTimeQueryException;
import travel.model.Flight;
import travel.model.Port;
import travel.port.FlightPort;
import travel.port.PortPort;
import travel.query.OneWayFlightQuery;
import travel.result.OneWayFlightResult;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class OneWayFlightQueryHandler implements QueryHandler<OneWayFlightResult, OneWayFlightQuery> {
    private final FlightPort flightPort;
    private final PortPort portPort;

    @Override
    public OneWayFlightResult handle(OneWayFlightQuery query) {
        if (query.getDepartureDate().isAfter(LocalDate.now()))
            throw new PastTimeQueryException("The departure date can not be past.");

        Port departure = portPort.findByName(query.getDeparturePort());
        Port arrival = portPort.findByName(query.getArrivalPort());

        if (departure == null)
            throw new IncorrectPortNameException("The departure port name is incorrect.");

        if (arrival == null)
            throw new IncorrectPortNameException("The arrival port name is incorrect.");

        List<Flight> flights = flightPort.getAvailableFlights(query);

        return OneWayFlightResult.builder()
                .departure(departure)
                .arrival(arrival)
                .flightCount(flights.size())
                .departureDate(query.getDepartureDate())
                .flights(flights)
                .build();
    }
}
