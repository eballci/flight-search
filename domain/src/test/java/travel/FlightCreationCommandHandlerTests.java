package travel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import travel.command.FlightCreationCommand;
import travel.exception.IdenticalDepartureAndArrivalException;
import travel.exception.IncorrectPortNameException;
import travel.exception.PastTimeCommandException;
import travel.handler.CommandHandler;
import travel.handler.FlightCreationCommandHandler;
import travel.model.Flight;
import travel.model.Port;
import travel.port.FlightPort;
import travel.port.PortPort;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FlightCreationCommandHandlerTests {
    private FlightPort flightPort;
    private PortPort portPort;
    private CommandHandler<Flight, FlightCreationCommand> handler;
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
        this.flightPort = flightPort;
        this.portPort = portPort;
        this.handler = new FlightCreationCommandHandler(flightPort, portPort);
        when(flightPort.createFlight(any(FlightCreationCommand.class)))
                .thenAnswer(invocation -> {
                    FlightCreationCommand command = invocation.getArgument(0);

                    return Flight.builder()
                            .id(UUID.randomUUID())
                            .departure(command.getDeparturePort().compareTo(istanbul.getName()) == 0 ? istanbul : ankara)
                            .arrival(command.getDeparturePort().compareTo(istanbul.getName()) == 0 ? ankara : istanbul)
                            .departureTime(command.getDepartureTime())
                            .amount(command.getAmount())
                            .currency(command.getCurrency())
                            .build();
                });
        when(portPort.findByName("istanbul")).thenReturn(istanbul);
        when(portPort.findByName("ankara")).thenReturn(ankara);
    }

    @Test
    void givenFlight_whenCommandProper() {
        LocalDateTime departureTime = LocalDateTime.now().plusDays(10);
        var command = FlightCreationCommand.builder()
                .departurePort("istanbul")
                .arrivalPort("ankara")
                .departureTime(departureTime)
                .amount(189.90)
                .currency("TL")
                .build();

        var result = handler.handle(command);

        verify(flightPort).createFlight(command);
        verify(portPort).findByName(command.getDeparturePort());
        verify(portPort).findByName(command.getArrivalPort());
        assertEquals(result.getDeparture().getName(), command.getDeparturePort());
        assertEquals(result.getArrival().getName(), command.getArrivalPort());
        assertTrue(result.getDepartureTime().isEqual(command.getDepartureTime()));
        assertEquals(result.getAmount(), command.getAmount());
        assertEquals(result.getCurrency(), command.getCurrency());
    }

    @Test
    void throwsPastTimeCommandException_whenDepartureTimeIsPast() {
        LocalDateTime departureTime = LocalDateTime.now().minusDays(10);
        var command = FlightCreationCommand.builder()
                .departurePort("istanbul")
                .arrivalPort("ankara")
                .departureTime(departureTime)
                .amount(189.90)
                .currency("TL")
                .build();

        assertThrows(PastTimeCommandException.class, () -> handler.handle(command));

        verify(flightPort, never()).createFlight(command);
        verify(portPort, never()).findByName(command.getDeparturePort());
        verify(portPort, never()).findByName(command.getArrivalPort());
    }

    @Test
    void throwsIncorrectPortNameException_whenDeparturePortNameDoesNotPointAny() {
        LocalDateTime departureTime = LocalDateTime.now().plusDays(10);
        var command = FlightCreationCommand.builder()
                .departurePort("does-not-exist")
                .arrivalPort("ankara")
                .departureTime(departureTime)
                .amount(189.90)
                .currency("TL")
                .build();

        assertThrows(IncorrectPortNameException.class, () -> handler.handle(command));

        verify(portPort).findByName(command.getDeparturePort());
        verify(portPort).findByName(command.getArrivalPort());
    }

    @Test
    void throwsIncorrectPortNameException_whenArrivalPortNameDoesNotPointAny() {
        LocalDateTime departureTime = LocalDateTime.now().plusDays(10);
        var command = FlightCreationCommand.builder()
                .departurePort("istanbul")
                .arrivalPort("does-not-exist")
                .departureTime(departureTime)
                .amount(189.90)
                .currency("TL")
                .build();

        assertThrows(IncorrectPortNameException.class, () -> handler.handle(command));

        verify(portPort).findByName(command.getDeparturePort());
        verify(portPort).findByName(command.getArrivalPort());
    }

    @Test
    void throwsIdenticalDepartureAndArrivalException_whenDepartureAndArrivalSame() {
        LocalDateTime departureTime = LocalDateTime.now().plusDays(10);
        var command = FlightCreationCommand.builder()
                .departurePort("istanbul")
                .arrivalPort("istanbul")
                .departureTime(departureTime)
                .amount(189.90)
                .currency("TL")
                .build();

        assertThrows(IdenticalDepartureAndArrivalException.class, () -> handler.handle(command));

        verify(portPort, never()).findByName(command.getDeparturePort());
        verify(portPort, never()).findByName(command.getArrivalPort());
    }
}
