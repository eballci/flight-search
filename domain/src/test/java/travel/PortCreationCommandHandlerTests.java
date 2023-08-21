package travel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import travel.command.PortCreationCommand;
import travel.exception.PortAlreadyExistsException;
import travel.handler.CommandHandler;
import travel.handler.PortCreationCommandHandler;
import travel.model.Port;
import travel.port.PortPort;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PortCreationCommandHandlerTests {
    @Mock
    private PortPort portPort;

    private CommandHandler<Port, PortCreationCommand> handler;

    @BeforeEach
    void init() {
        handler = new PortCreationCommandHandler(portPort);
        when(portPort.findByName("ankara"))
                .thenReturn(Port.builder()
                        .id(UUID.randomUUID())
                        .name("ankara")
                        .build());
        when(portPort.createPort(any(PortCreationCommand.class)))
                .thenAnswer(invocation -> {
                    PortCreationCommand command = invocation.getArgument(0);

                    return Port.builder()
                            .id(UUID.randomUUID())
                            .name(command.getName())
                            .build();
                });
    }

    @Test
    void givenPort_whenProperlyAdd() {
        var command = mock(PortCreationCommand.class);

        when(command.getName()).thenReturn("istanbul");

        var result = handler.handle(command);

        verify(portPort).findByName(command.getName());
        verify(portPort).createPort(command);
        assertEquals(result.getName(), command.getName());
    }

    @Test
    void throwsPortAlreadyExistsException_whenPortNameIsAlreadyInUse() {
        var command = mock(PortCreationCommand.class);

        when(command.getName()).thenReturn("ankara");

        assertThrows(PortAlreadyExistsException.class, () -> handler.handle(command));

        verify(portPort).findByName(command.getName());
        verify(portPort, never()).createPort(command);
    }
}
