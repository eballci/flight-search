package travel.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.query.OneWayFlightQuery;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OneWayFlightSearchRequest {
    private String departure;
    private String arrival;
    private LocalDate departureDate;

    public OneWayFlightQuery toQuery() {
        return OneWayFlightQuery.builder()
                .departurePort(departure)
                .arrivalPort(arrival)
                .departureDate(departureDate)
                .build();
    }
}
