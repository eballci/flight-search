package travel.infra.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.command.FlightCreationCommand;
import travel.infra.entity.FlightEntity;
import travel.infra.repository.FlightRepository;
import travel.infra.repository.PortRepository;
import travel.model.Flight;
import travel.model.Port;
import travel.port.FlightPort;
import travel.query.OneWayFlightQuery;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightAdapter implements FlightPort {
    private final FlightRepository flightRepository;
    private final PortRepository portRepository;

    @Override
    public List<Flight> getAvailableFlights(OneWayFlightQuery query) {
        return flightRepository.findAllByOneWayQuery(
                        query.getDeparturePort(),
                        query.getArrivalPort(),
                        LocalDateTime.of(query.getDepartureDate(), LocalTime.MIDNIGHT),
                        query.getDepartureDate().atTime(LocalTime.MAX)
                )
                .stream()
                .map(flightEntity -> Flight.builder()
                        .id(flightEntity.getId())
                        .departure(
                                Port.builder()
                                        .id(flightEntity.getDeparture().getId())
                                        .name(flightEntity.getDeparture().getName())
                                        .build()
                        )
                        .arrival(
                                Port.builder()
                                        .id(flightEntity.getArrival().getId())
                                        .name(flightEntity.getArrival().getName())
                                        .build()
                        )
                        .departureTime(flightEntity.getDepartureTime())
                        .amount(flightEntity.getAmount())
                        .currency(flightEntity.getCurrency())
                        .build())
                .toList();
    }

    @Override
    public Flight createFlight(FlightCreationCommand command) {
        var departure = portRepository.findByName(command.getDeparturePort());
        var arrival = portRepository.findByName(command.getArrivalPort());
        var flightEntity = new FlightEntity();

        flightEntity.setDeparture(departure);
        flightEntity.setArrival(arrival);
        flightEntity.setDepartureTime(command.getDepartureTime());
        flightEntity.setAmount(command.getAmount());
        flightEntity.setCurrency(command.getCurrency());
        flightEntity = flightRepository.save(flightEntity);
        return Flight.builder()
                .id(flightEntity.getId())
                .departure(
                        Port.builder()
                                .id(flightEntity.getDeparture().getId())
                                .name(flightEntity.getDeparture().getName())
                                .build()
                )
                .arrival(
                        Port.builder()
                                .id(flightEntity.getArrival().getId())
                                .name(flightEntity.getArrival().getName())
                                .build()
                )
                .departureTime(flightEntity.getDepartureTime())
                .amount(flightEntity.getAmount())
                .currency(flightEntity.getCurrency())
                .build();
    }
}
