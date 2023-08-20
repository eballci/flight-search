package travel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import travel.exception.IncorrectPortNameException;
import travel.exception.InvalidDateRangeException;
import travel.exception.PastTimeQueryException;
import travel.handler.QueryHandler;
import travel.handler.TwoWayFlightQueryHandler;
import travel.model.Flight;
import travel.model.Port;
import travel.port.FlightPort;
import travel.port.PortPort;
import travel.query.OneWayFlightQuery;
import travel.query.TwoWayFlightQuery;
import travel.result.TwoWayFlightResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TwoWayFlightQueryHandlerTests {
    private FlightPort flightPort;
    private PortPort portPort;
    private QueryHandler<TwoWayFlightResult, TwoWayFlightQuery> handler;
    private final List<Flight> departureFlights = new ArrayList<>();
    private final List<Flight> returnFlights = new ArrayList<>();
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
        Flight departureFlight = Flight.builder()
                .id(UUID.randomUUID())
                .departure(istanbul)
                .arrival(ankara)
                .departureTime(LocalDateTime.of(2023, 8, 20, 1, 1, 1))
                .amount(199.99)
                .currency("TL")
                .build();
        Flight returnFlight = Flight.builder()
                .id(UUID.randomUUID())
                .departure(ankara)
                .arrival(istanbul)
                .departureTime(LocalDateTime.of(2023, 8, 23, 1, 1, 1))
                .amount(299.99)
                .currency("TL")
                .build();

        departureFlights.add(departureFlight);
        returnFlights.add(returnFlight);

        this.flightPort = flightPort;
        this.portPort = portPort;
        handler = new TwoWayFlightQueryHandler(flightPort, portPort);
        when(flightPort.getAvailableFlights(ArgumentMatchers.any(OneWayFlightQuery.class)))
                .thenAnswer(invocation -> {
                    OneWayFlightQuery query = invocation.getArgument(0);
                    if (query.getDeparturePort().compareTo(departureFlight.getDeparture().getName()) == 0) {
                        return departureFlights;
                    } else {
                        return returnFlights;
                    }
                });
        when(portPort.findByName("istanbul")).thenReturn(istanbul);
        when(portPort.findByName("ankara")).thenReturn(ankara);
    }

    @Test
    void givenAllTwoWayFlights_whenQueryPassed() {
        LocalDate requestedDepartureDate = mock(LocalDate.class);
        LocalDate requestedReturnDate = mock(LocalDate.class);
        TwoWayFlightQuery query = mock(TwoWayFlightQuery.class);

        when(query.getDeparturePort()).thenReturn("istanbul");
        when(query.getArrivalPort()).thenReturn("ankara");
        when(query.getDepartureDate()).thenReturn(requestedDepartureDate);
        when(query.getReturnDate()).thenReturn(requestedReturnDate);
        when(requestedDepartureDate.isAfter(any(ChronoLocalDate.class))).thenReturn(false);
        when(requestedReturnDate.isAfter(any(ChronoLocalDate.class))).thenReturn(false);
        when(requestedDepartureDate.isEqual(requestedDepartureDate)).thenReturn(true);
        when(requestedReturnDate.isEqual(requestedReturnDate)).thenReturn(true);
        when(requestedDepartureDate.isAfter(requestedReturnDate)).thenReturn(false);
        when(requestedReturnDate.isBefore(requestedDepartureDate)).thenReturn(false);

        TwoWayFlightResult result = handler.handle(query);

        verify(flightPort, times(2))
                .getAvailableFlights(any(OneWayFlightQuery.class));
        verify(portPort).findByName(query.getDeparturePort());
        verify(portPort).findByName(query.getArrivalPort());
        assertEquals(result.getDeparture(), istanbul);
        assertEquals(result.getArrival(), ankara);
        assertEquals(result.getDepartureFlightCount(), departureFlights.size());
        assertEquals(result.getReturnFlightCount(), returnFlights.size());
        assertTrue(result.getDepartureDate().isEqual(requestedDepartureDate));
        assertTrue(result.getReturnDate().isEqual(requestedReturnDate));
        assertArrayEquals(result.getDepartureFlights().toArray(), departureFlights.toArray());
        assertArrayEquals(result.getReturnFlights().toArray(), returnFlights.toArray());
    }

    @Test
    void throwsPastTimeQueryException_whenDepartureDatePast() {
        LocalDate requestedDepartureDate = mock(LocalDate.class);
        LocalDate requestedReturnDate = mock(LocalDate.class);
        TwoWayFlightQuery query = mock(TwoWayFlightQuery.class);

        when(query.getDeparturePort()).thenReturn("istanbul");
        when(query.getArrivalPort()).thenReturn("ankara");
        when(query.getDepartureDate()).thenReturn(requestedDepartureDate);
        when(query.getReturnDate()).thenReturn(requestedReturnDate);
        when(requestedDepartureDate.isAfter(any(ChronoLocalDate.class))).thenReturn(true);

        assertThrows(PastTimeQueryException.class, () -> handler.handle(query));

        verify(flightPort, never()).getAvailableFlights(any(OneWayFlightQuery.class));
        verify(portPort, never()).findByName(query.getDeparturePort());
        verify(portPort, never()).findByName(query.getArrivalPort());
    }

    @Test
    void throwsPastTimeQueryException_whenReturnDatePast() {
        LocalDate requestedDepartureDate = mock(LocalDate.class);
        LocalDate requestedReturnDate = mock(LocalDate.class);
        TwoWayFlightQuery query = mock(TwoWayFlightQuery.class);

        when(query.getDeparturePort()).thenReturn("istanbul");
        when(query.getArrivalPort()).thenReturn("ankara");
        when(query.getDepartureDate()).thenReturn(requestedDepartureDate);
        when(query.getReturnDate()).thenReturn(requestedReturnDate);
        when(requestedReturnDate.isAfter(any(ChronoLocalDate.class))).thenReturn(true);

        assertThrows(PastTimeQueryException.class, () -> handler.handle(query));

        verify(flightPort, never()).getAvailableFlights(any(OneWayFlightQuery.class));
        verify(portPort, never()).findByName(query.getDeparturePort());
        verify(portPort, never()).findByName(query.getArrivalPort());
    }

    @Test
    void throwsPastTimeQueryException_whenReturnDateEarlierFromDepartureDate() {
        LocalDate requestedDepartureDate = mock(LocalDate.class);
        LocalDate requestedReturnDate = mock(LocalDate.class);
        TwoWayFlightQuery query = mock(TwoWayFlightQuery.class);

        when(query.getDeparturePort()).thenReturn("istanbul");
        when(query.getArrivalPort()).thenReturn("ankara");
        when(query.getDepartureDate()).thenReturn(requestedDepartureDate);
        when(query.getReturnDate()).thenReturn(requestedReturnDate);
        when(requestedDepartureDate.isAfter(requestedReturnDate)).thenReturn(true);

        assertThrows(InvalidDateRangeException.class, () -> handler.handle(query));

        verify(flightPort, never()).getAvailableFlights(any(OneWayFlightQuery.class));
        verify(portPort, never()).findByName(query.getDeparturePort());
        verify(portPort, never()).findByName(query.getArrivalPort());
    }

    @Test
    void throwsIncorrectPortNameException_whenDeparturePortNameDoesNotPointAny() {
        LocalDate requestedDepartureDate = mock(LocalDate.class);
        LocalDate requestedReturnDate = mock(LocalDate.class);
        TwoWayFlightQuery query = mock(TwoWayFlightQuery.class);

        when(query.getDeparturePort()).thenReturn("does-not-exist");
        when(query.getArrivalPort()).thenReturn("ankara");
        when(query.getDepartureDate()).thenReturn(requestedDepartureDate);
        when(query.getReturnDate()).thenReturn(requestedReturnDate);
        when(requestedDepartureDate.isAfter(any(ChronoLocalDate.class))).thenReturn(false);
        when(requestedReturnDate.isAfter(any(ChronoLocalDate.class))).thenReturn(false);
        when(requestedDepartureDate.isEqual(requestedDepartureDate)).thenReturn(true);
        when(requestedReturnDate.isEqual(requestedReturnDate)).thenReturn(true);
        when(requestedDepartureDate.isAfter(requestedReturnDate)).thenReturn(false);
        when(requestedReturnDate.isBefore(requestedDepartureDate)).thenReturn(false);

        assertThrows(IncorrectPortNameException.class, () -> handler.handle(query));

        verify(portPort).findByName(query.getDeparturePort());
        verify(portPort).findByName(query.getArrivalPort());
    }

    @Test
    void throwsIncorrectPortNameException_whenArrivalPortNameDoesNotPointAny() {
        LocalDate requestedDepartureDate = mock(LocalDate.class);
        LocalDate requestedReturnDate = mock(LocalDate.class);
        TwoWayFlightQuery query = mock(TwoWayFlightQuery.class);

        when(query.getDeparturePort()).thenReturn("istanbul");
        when(query.getArrivalPort()).thenReturn("does-not-exist");
        when(query.getDepartureDate()).thenReturn(requestedDepartureDate);
        when(query.getReturnDate()).thenReturn(requestedReturnDate);
        when(requestedDepartureDate.isAfter(any(ChronoLocalDate.class))).thenReturn(false);
        when(requestedReturnDate.isAfter(any(ChronoLocalDate.class))).thenReturn(false);
        when(requestedDepartureDate.isEqual(requestedDepartureDate)).thenReturn(true);
        when(requestedReturnDate.isEqual(requestedReturnDate)).thenReturn(true);
        when(requestedDepartureDate.isAfter(requestedReturnDate)).thenReturn(false);
        when(requestedReturnDate.isBefore(requestedDepartureDate)).thenReturn(false);

        assertThrows(IncorrectPortNameException.class, () -> handler.handle(query));

        verify(portPort).findByName(query.getDeparturePort());
        verify(portPort).findByName(query.getArrivalPort());
    }
}
