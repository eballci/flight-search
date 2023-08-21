package travel.port;

import travel.command.PortCreationCommand;
import travel.model.Port;

public interface PortPort {
    Port findByName(String name);
    Port createPort(PortCreationCommand command);
}
