package travel.infra;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import travel.command.FlightCreationCommand;
import travel.infra.adapter.FlightAdapter;
import travel.infra.entity.FlightEntity;
import travel.infra.entity.PortEntity;
import travel.infra.repository.FlightRepository;
import travel.infra.repository.PortRepository;
import travel.port.FlightPort;
import travel.query.OneWayFlightQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FlightAdapterTests {
    @Mock
    FlightRepository flightRepository;

    @Mock
    PortRepository portRepository;

    FlightPort adapter;

    List<FlightEntity> flightEntities = new ArrayList<>();

    @BeforeEach
    void init() {
        var istanbul = new PortEntity();
        var ankara = new PortEntity();
        var entity = new FlightEntity();

        istanbul.setId(UUID.randomUUID());
        istanbul.setName("istanbul");
        ankara.setId(UUID.randomUUID());
        ankara.setName("ankara");
        entity.setId(UUID.randomUUID());
        entity.setDeparture(istanbul);
        entity.setArrival(ankara);
        entity.setAmount(199.9);
        entity.setCurrency("TL");
        flightEntities.add(entity);
        adapter = new FlightAdapter(flightRepository, portRepository);

        when(flightRepository.findAllByOneWayQuery(
                anyString(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class))).thenReturn(flightEntities);
        when(flightRepository.save(any(FlightEntity.class))).thenReturn(entity);
        when(portRepository.findByName("istanbul")).thenReturn(istanbul);
        when(portRepository.findByName("ankara")).thenReturn(ankara);
    }

    @Test
    void givenAllFlights_whenProperQuery() {
        var query = OneWayFlightQuery.builder()
                .departurePort("istanbul")
                .arrivalPort("ankara")
                .departureDate(LocalDate.now())
                .build();

        var first = adapter.getAvailableFlights(query).get(0);

        //TODO: make it better
        verify(flightRepository)
                .findAllByOneWayQuery(anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        Assertions.assertEquals(first.getDeparture().getName(), "istanbul");
        Assertions.assertEquals(first.getArrival().getName(), "ankara");
        Assertions.assertTrue(first.getDepartureTime().isEqual(flightEntities.get(0).getDepartureTime()));
        Assertions.assertEquals(first.getAmount(), 199.9);
        Assertions.assertEquals(first.getCurrency(), "TL");
    }

    @Test
    void givenFlight_whenCreating() {
        var command = FlightCreationCommand.builder()
                .departurePort("istanbul")
                .arrivalPort("ankara")
                .departureTime(LocalDateTime.now().plusDays(10))
                .amount(199.9)
                .currency("TL")
                .build();

        var flight = adapter.createFlight(command);

        verify(flightRepository).save(any(FlightEntity.class));
        Assertions.assertEquals(flight.getDeparture().getName(), command.getDeparturePort());
        Assertions.assertEquals(flight.getArrival().getName(), command.getArrivalPort());
        Assertions.assertEquals(flight.getAmount(), command.getAmount());
        Assertions.assertEquals(flight.getCurrency(), command.getCurrency());
    }
}
