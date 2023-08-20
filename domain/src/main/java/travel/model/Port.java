package travel.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Port {
    private UUID id;
    private String name;
}
