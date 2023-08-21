package travel.infra.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.domain.Persistable;
import travel.model.Port;

import java.util.UUID;

@Data
@Entity
@Table(name = "port")
public class PortEntity implements Persistable<UUID> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Override
    public boolean isNew() {
        return id == null;
    }

    public Port toModel() {
        return Port.builder()
                .id(id)
                .name(name)
                .build();
    }
}