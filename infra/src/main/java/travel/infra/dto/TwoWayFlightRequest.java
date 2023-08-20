package travel.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.query.TwoWayFlightQuery;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TwoWayFlightRequest {
    private String departure;
    private String arrival;
    private LocalDate departureDate;
    private LocalDate returnDate;

    public TwoWayFlightQuery toQuery() {
        return TwoWayFlightQuery.builder()
                .departurePort(departure)
                .arrivalPort(arrival)
                .departureDate(departureDate)
                .returnDate(returnDate)
                .build();
    }
}
