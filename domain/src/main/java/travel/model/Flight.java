package travel.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Flight {
    private UUID id;
    private Port departure;
    private Port arrival;
    private LocalDateTime departureTime;
    private double amount;
    private String currency;
}
