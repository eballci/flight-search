package travel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import travel.exception.IdenticalDepartureAndArrivalException;
import travel.exception.IncorrectPortNameException;
import travel.exception.PastDateException;
import travel.handler.OneWayFlightQueryHandler;
import travel.handler.QueryHandler;
import travel.model.Flight;
import travel.model.Port;
import travel.port.FlightPort;
import travel.port.PortPort;
import travel.query.OneWayFlightQuery;
import travel.result.OneWayFlightResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OneWayFlightQueryHandlerTests {
    private FlightPort flightPort;
    private PortPort portPort;
    private QueryHandler<OneWayFlightResult, OneWayFlightQuery> handler;
    private final List<Flight> flights = new ArrayList<>();
    private final Port istanbul = Port.builder()
            .id(UUID.randomUUID())
            .name("istanbul")
            .build();
    private final Port ankara = Port.builder()
            .id(UUID.randomUUID())
            .name("ankara")
            .build();

    @BeforeEach
    void init(@Mock FlightPort flightPort, @Mock PortPort portPort) {
        Flight flight = Flight.builder()
                .id(UUID.randomUUID())
                .departure(istanbul)
                .arrival(ankara)
                .amount(199.99)
                .currency("TL")
                .build();

        flights.add(flight);

        this.flightPort = flightPort;
        this.portPort = portPort;
        handler = new OneWayFlightQueryHandler(flightPort, portPort);
        when(portPort.findByName("istanbul")).thenReturn(istanbul);
        when(portPort.findByName("ankara")).thenReturn(ankara);
        when(flightPort.getAvailableFlights(any(OneWayFlightQuery.class)))
                .thenAnswer(invocation -> {
                    OneWayFlightQuery query = invocation.getArgument(0);

                    flights.forEach(f -> {
                        f.setDepartureTime(query.getDepartureDate().atTime(21, 11, 10));
                    });

                    return flights;
                });
    }

    @Test
    void givenAllOneWayFlights_whenQueryPassed() {
        LocalDate requestedDepartureDate = LocalDate.now().plusDays(10);
        OneWayFlightQuery query = mock(OneWayFlightQuery.class);

        when(query.getDeparturePort()).thenReturn("istanbul");
        when(query.getArrivalPort()).thenReturn("ankara");
        when(query.getDepartureDate()).thenReturn(requestedDepartureDate);

        OneWayFlightResult result = handler.handle(query);

        verify(flightPort).getAvailableFlights(query);
        verify(portPort).findByName(query.getDeparturePort());
        verify(portPort).findByName(query.getArrivalPort());
        assertEquals(result.getDeparture(), istanbul);
        assertEquals(result.getArrival(), ankara);
        assertEquals(result.getFlightCount(), flights.size());
        assertTrue(result.getDepartureDate().isEqual(requestedDepartureDate));
        assertArrayEquals(result.getFlights().toArray(), flights.toArray());
    }

    @Test
    void throwsPastTimeQueryException_whenDepartureDatePast() {
        LocalDate requestedDepartureDate = LocalDate.now().minusDays(10);
        OneWayFlightQuery query = mock(OneWayFlightQuery.class);

        when(query.getDeparturePort()).thenReturn("istanbul");
        when(query.getArrivalPort()).thenReturn("ankara");
        when(query.getDepartureDate()).thenReturn(requestedDepartureDate);

        assertThrows(PastDateException.class, () -> handler.handle(query));

        verify(flightPort, never()).getAvailableFlights(query);
        verify(portPort, never()).findByName(query.getDeparturePort());
        verify(portPort, never()).findByName(query.getArrivalPort());
    }

    @Test
    void throwsIncorrectPortNameException_whenDeparturePortNameDoesNotPointAny() {
        LocalDate requestedDepartureDate = LocalDate.now().plusDays(10);
        OneWayFlightQuery query = mock(OneWayFlightQuery.class);

        when(query.getDeparturePort()).thenReturn("does-not-exist");
        when(query.getArrivalPort()).thenReturn("ankara");
        when(query.getDepartureDate()).thenReturn(requestedDepartureDate);

        assertThrows(IncorrectPortNameException.class, () -> handler.handle(query));

        verify(portPort).findByName(query.getDeparturePort());
        verify(portPort).findByName(query.getArrivalPort());
    }

    @Test
    void throwsIncorrectPortNameException_whenArrivalPortNameDoesNotPointAny() {
        LocalDate requestedDepartureDate = LocalDate.now().plusDays(10);
        OneWayFlightQuery query = mock(OneWayFlightQuery.class);

        when(query.getDeparturePort()).thenReturn("istanbul");
        when(query.getArrivalPort()).thenReturn("does-not-exist");
        when(query.getDepartureDate()).thenReturn(requestedDepartureDate);

        assertThrows(IncorrectPortNameException.class, () -> handler.handle(query));

        verify(portPort).findByName(query.getDeparturePort());
        verify(portPort).findByName(query.getArrivalPort());
    }

    @Test
    void throwsIdenticalDepartureAndArrivalException_whenDepartureAndArrivalSame() {
        LocalDate requestedDepartureDate = LocalDate.now().plusDays(10);
        OneWayFlightQuery query = mock(OneWayFlightQuery.class);

        when(query.getDeparturePort()).thenReturn("ankara");
        when(query.getArrivalPort()).thenReturn("ankara");
        when(query.getDepartureDate()).thenReturn(requestedDepartureDate);

        assertThrows(IdenticalDepartureAndArrivalException.class, () -> handler.handle(query));

        verify(portPort, never()).findByName(query.getDeparturePort());
        verify(portPort, never()).findByName(query.getArrivalPort());
    }
}
