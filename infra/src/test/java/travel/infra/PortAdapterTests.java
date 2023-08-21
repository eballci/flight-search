package travel.infra;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import travel.command.PortCreationCommand;
import travel.infra.adapter.PortAdapter;
import travel.infra.entity.PortEntity;
import travel.infra.repository.PortRepository;
import travel.port.PortPort;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PortAdapterTests {
    @Mock
    PortRepository portRepository;

    PortPort adapter;

    PortEntity istanbul = new PortEntity();

    @BeforeEach
    void init() {
        adapter = new PortAdapter(portRepository);

        istanbul.setId(UUID.randomUUID());
        istanbul.setName("istanbul");
        when(portRepository.findByName(anyString())).thenReturn(null);
        when(portRepository.findByName("istanbul")).thenReturn(istanbul);
        when(portRepository.save(any(PortEntity.class)))
                .thenAnswer(invocation -> {
                    PortEntity entity = invocation.getArgument(0);
                    PortEntity savedEntity = new PortEntity();

                    savedEntity.setId(UUID.randomUUID());
                    savedEntity.setName(entity.getName());
                    return savedEntity;
                });
    }

    @Test
    void givenEmptyList_whenPortNameDoesNotPointAny() {
        var result = adapter.findByName("does-not-exist");

        verify(portRepository).findByName("does-not-exist");
        assertNull(result);
    }

    @Test
    void givenPort_whenPassedProperName() {
        var result = adapter.findByName("istanbul");

        verify(portRepository).findByName("istanbul");
        assertEquals(result.getName().compareTo("istanbul"), 0);
        assertEquals(result.getId(), istanbul.getId());
    }

    @Test
    void givenPort_whenCreationCommandPassed() {
        var command = PortCreationCommand.builder()
                .name("ankara")
                .build();
        var entity = new PortEntity();

        entity.setName(command.getName());

        var result = adapter.createPort(command);

        verify(portRepository).findByName(command.getName());
        verify(portRepository).save(eq(entity));
        assertNotNull(result.getId());
        assertEquals(result.getName().compareToIgnoreCase(command.getName()), 0);
    }

    @Test
    void givenExistingPort_whenCreationCommandPortNameAlreadyExists() {
        var command = PortCreationCommand.builder()
                .name("istanbul")
                .build();
        var entity = new PortEntity();

        entity.setName(command.getName());

        var result = adapter.createPort(command);

        verify(portRepository).findByName(command.getName());
        verify(portRepository, never()).save(eq(entity));
        assertNotNull(result.getId());
        assertEquals(result.getName().compareToIgnoreCase(command.getName()), 0);
    }
}
