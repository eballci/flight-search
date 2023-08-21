package travel.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PortCreationCommand implements Command {
    private String name;
}
