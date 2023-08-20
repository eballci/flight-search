package travel.query;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TwoWayFlightQuery implements Query {
    private String departurePort;
    private String arrivalPort;
    private LocalDate departureDate;
    private LocalDate returnDate;
}
