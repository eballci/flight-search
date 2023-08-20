package travel.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import travel.command.FlightCreationCommand;
import travel.handler.*;
import travel.model.Flight;
import travel.port.FlightPort;
import travel.port.PortPort;
import travel.query.OneWayFlightQuery;
import travel.query.TwoWayFlightQuery;
import travel.result.OneWayFlightResult;
import travel.result.TwoWayFlightResult;

@Configuration
@RequiredArgsConstructor
public class DomainConfiguration {
    private final FlightPort flightPort;
    private final PortPort portPort;

    @Bean
    public QueryHandler<OneWayFlightResult, OneWayFlightQuery> oneWayFlightQueryHandler() {
        return new OneWayFlightQueryHandler(flightPort, portPort);
    }

    @Bean
    public QueryHandler<TwoWayFlightResult, TwoWayFlightQuery> twoWayFlightQueryHandler() {
        return new TwoWayFlightQueryHandler(flightPort, portPort);
    }

    @Bean
    public CommandHandler<Flight, FlightCreationCommand> flightCreationCommandHandler() {
        return new FlightCreationCommandHandler(flightPort, portPort);
    }
}
