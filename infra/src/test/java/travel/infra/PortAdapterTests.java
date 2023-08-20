package travel.infra;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import travel.infra.adapter.PortAdapter;
import travel.infra.entity.PortEntity;
import travel.infra.repository.PortRepository;
import travel.port.PortPort;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}
