package travel.infra.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import travel.command.FlightCreationCommand;
import travel.handler.CommandHandler;
import travel.handler.QueryHandler;
import travel.infra.dto.CreateFlightRequest;
import travel.infra.dto.OneWayFlightSearchRequest;
import travel.infra.dto.TwoWayFlightRequest;
import travel.model.Flight;
import travel.query.OneWayFlightQuery;
import travel.query.TwoWayFlightQuery;
import travel.result.OneWayFlightResult;
import travel.result.TwoWayFlightResult;

@RestController
@RequiredArgsConstructor
@RequestMapping("flights/")
public class FlightController {
    private final QueryHandler<OneWayFlightResult, OneWayFlightQuery> oneWayFlightQueryHandler;
    private final QueryHandler<TwoWayFlightResult, TwoWayFlightQuery> twoWayFlightQueryHandler;
    private final CommandHandler<Flight, FlightCreationCommand> flightCreationCommandHandler;

    @PostMapping("one-way-search/")
    @ResponseStatus(HttpStatus.OK)
    public OneWayFlightResult findOneWayFlights(@RequestBody OneWayFlightSearchRequest request) {
        return oneWayFlightQueryHandler.handle(request.toQuery());
    }

    @PostMapping("two-way-search/")
    @ResponseStatus(HttpStatus.OK)
    public TwoWayFlightResult findTwoWayFlights(@RequestBody TwoWayFlightRequest request) {
        return twoWayFlightQueryHandler.handle(request.toQuery());
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Flight createFlight(@RequestBody CreateFlightRequest request) {
        return flightCreationCommandHandler.handle(request.toCommand());
    }
}
