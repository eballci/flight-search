package travel.result;

import lombok.Builder;
import lombok.Data;
import travel.model.Flight;
import travel.model.Port;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TwoWayFlightResult implements Result {
    private Port departure;
    private Port arrival;
    private int departureFlightCount;
    private int returnFlightCount;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private List<Flight> departureFlights;
    private List<Flight> returnFlights;
}
