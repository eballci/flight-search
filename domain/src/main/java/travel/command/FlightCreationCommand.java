package travel.command;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FlightCreationCommand implements Command {
    private String departurePort;
    private String arrivalPort;
    private LocalDateTime departureTime;
    private double amount;
    private String currency;
}
