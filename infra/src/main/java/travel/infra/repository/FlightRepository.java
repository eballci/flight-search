package travel.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import travel.infra.entity.FlightEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface FlightRepository extends JpaRepository<FlightEntity, UUID> {
    @Query("SELECT F FROM FlightEntity AS F WHERE F.departure.name = :departure AND F.arrival.name = :arrival AND F.departureTime >= :timeRangeStart AND F.departureTime <= :timeRangeEnd")
    List<FlightEntity> findAllByOneWayQuery(
            @Param("departure") String departure,
            @Param("arrival") String arrival,
            @Param("timeRangeStart") LocalDateTime timeRangeStart,
            @Param("timeRangeEnd") LocalDateTime timeRangeEnd
    );
}
