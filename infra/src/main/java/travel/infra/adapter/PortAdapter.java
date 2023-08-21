package travel.infra.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.command.PortCreationCommand;
import travel.infra.entity.PortEntity;
import travel.infra.repository.PortRepository;
import travel.model.Port;
import travel.port.PortPort;

@Service
@RequiredArgsConstructor
public class PortAdapter implements PortPort {
    private final PortRepository portRepository;

    @Override
    public Port findByName(String name) {
        var entity = portRepository.findByName(name);

        return entity == null ? null : Port.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    @Override
    public Port createPort(PortCreationCommand command) {
        var entity = portRepository.findByName(command.getName());

        if (entity != null)
            return entity.toModel();

        entity = new PortEntity();

        entity.setName(command.getName());
        return portRepository.save(entity).toModel();
    }
}
