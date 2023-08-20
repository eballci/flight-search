package travel.infra.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import lombok.Data;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "flight")
public class FlightEntity implements Persistable<UUID> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "departure_port_id", nullable = false)
    private PortEntity departure;

    @ManyToOne
    @JoinColumn(name = "arrival_port_id", nullable = false)
    private PortEntity arrival;

    @Future
    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String currency;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
