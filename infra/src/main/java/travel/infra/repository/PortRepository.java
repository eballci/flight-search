package travel.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.infra.entity.PortEntity;

import java.util.UUID;

public interface PortRepository extends JpaRepository<PortEntity, UUID> {
    PortEntity findByName(String name);
}
