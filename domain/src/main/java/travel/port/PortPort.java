package travel.port;

import travel.model.Port;

public interface PortPort {
    Port findByName(String name);
}
