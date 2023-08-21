package travel.handler;

import lombok.RequiredArgsConstructor;
import travel.exception.IdenticalDepartureAndArrivalException;
import travel.exception.IncorrectPortNameException;
import travel.exception.InvalidDateRangeException;
import travel.exception.PastDateException;
import travel.model.Flight;
import travel.model.Port;
import travel.port.FlightPort;
import travel.port.PortPort;
import travel.query.OneWayFlightQuery;
import travel.query.TwoWayFlightQuery;
import travel.result.TwoWayFlightResult;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class TwoWayFlightQueryHandler implements QueryHandler<TwoWayFlightResult, TwoWayFlightQuery> {
    private final FlightPort flightPort;
    private final PortPort portPort;

    @Override
    public TwoWayFlightResult handle(TwoWayFlightQuery query) {
        if (query.getDepartureDate().isBefore(LocalDate.now()))
            throw new PastDateException("The departure date can not be past.");

        if (query.getReturnDate().isBefore(LocalDate.now()))
            throw new PastDateException("The return date can not be past.");

        if (query.getDepartureDate().isAfter(query.getReturnDate()))
            throw new InvalidDateRangeException("Return date cannot be earlier than departure date.");

        if (query.getDeparturePort().compareToIgnoreCase(query.getArrivalPort()) == 0)
            throw new IdenticalDepartureAndArrivalException("The departure and arrival ports can not be identical.");

        Port departure = portPort.findByName(query.getDeparturePort());
        Port arrival = portPort.findByName(query.getArrivalPort());

        if (departure == null)
            throw new IncorrectPortNameException("The departure port name is incorrect.");

        if (arrival == null)
            throw new IncorrectPortNameException("The arrival port name is incorrect.");

        OneWayFlightQuery departureQuery = OneWayFlightQuery.builder()
                .departurePort(query.getDeparturePort())
                .arrivalPort(query.getArrivalPort())
                .departureDate(query.getDepartureDate())
                .build();
        OneWayFlightQuery returnQuery = OneWayFlightQuery.builder()
                .departurePort(query.getArrivalPort())
                .arrivalPort(query.getDeparturePort())
                .departureDate(query.getReturnDate())
                .build();
        List<Flight> departureFlights = flightPort.getAvailableFlights(departureQuery);
        List<Flight> returnFlights = flightPort.getAvailableFlights(returnQuery);

        return TwoWayFlightResult.builder()
                .departure(departure)
                .arrival(arrival)
                .departureFlightCount(departureFlights.size())
                .returnFlightCount(returnFlights.size())
                .departureDate(query.getDepartureDate())
                .returnDate(query.getReturnDate())
                .departureFlights(departureFlights)
                .returnFlights(returnFlights)
                .build();
    }
}
