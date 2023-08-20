package travel.infra;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import travel.infra.entity.FlightEntity;
import travel.infra.entity.PortEntity;
import travel.infra.repository.FlightRepository;
import travel.infra.repository.PortRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FlightRepositoryTests {
    @Autowired
    PortRepository portRepository;

    @Autowired
    FlightRepository flightRepository;

    @Test
    public void doesAddsToTable() {
        PortEntity istanbul = new PortEntity();
        PortEntity ankara = new PortEntity();
        FlightEntity flight = new FlightEntity();

        istanbul.setName("istanbul");
        ankara.setName("ankara");
        portRepository.saveAll(List.of(istanbul, ankara));
        portRepository.flush();

        istanbul = portRepository.findByName("istanbul");
        ankara = portRepository.findByName("ankara");

        flight.setDeparture(istanbul);
        flight.setArrival(ankara);
        flight.setDepartureTime(LocalDateTime.now().plusDays(10));
        flight.setAmount(199.9);
        flight.setCurrency("TL");

        flightRepository.save(flight);
        flightRepository.flush();

        Assertions.assertEquals(flightRepository.findAllByOneWayQuery("istanbul", "ankara",
                LocalDateTime.of(flight.getDepartureTime().toLocalDate(), LocalTime.MIDNIGHT),
                flight.getDepartureTime().toLocalDate().atTime(LocalTime.MAX)).size(), 1);
    }
}