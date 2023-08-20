package travel.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.command.FlightCreationCommand;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateFlightRequest {
    private String departure;
    private String arrival;
    private LocalDateTime departureTime;
    private double amount;
    private String currency;

    public FlightCreationCommand toCommand() {
        return FlightCreationCommand.builder()
                .departurePort(departure)
                .arrivalPort(arrival)
                .departureTime(departureTime)
                .amount(amount)
                .currency(currency)
                .build();
    }
}
