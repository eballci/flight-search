package travel.result;

import lombok.Builder;
import lombok.Data;
import travel.model.Flight;
import travel.model.Port;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class OneWayFlightResult implements Result {
    private Port departure;
    private Port arrival;
    private int flightCount;
    private LocalDate departureDate;
    private List<Flight> flights;
}
