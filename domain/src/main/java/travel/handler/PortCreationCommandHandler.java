package travel.handler;

import lombok.RequiredArgsConstructor;
import travel.command.PortCreationCommand;
import travel.exception.PortAlreadyExistsException;
import travel.model.Port;
import travel.port.PortPort;

@RequiredArgsConstructor
public class PortCreationCommandHandler implements CommandHandler<Port, PortCreationCommand> {
    private final PortPort portPort;

    @Override
    public Port handle(PortCreationCommand command) {
        if (portPort.findByName(command.getName()) != null)
            throw new PortAlreadyExistsException("Port already exists.");

        return portPort.createPort(command);
    }
}
