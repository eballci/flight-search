package travel.infra;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import travel.infra.entity.PortEntity;
import travel.infra.repository.PortRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PortRepositoryTests {
    @Autowired
    PortRepository repository;

    @Test
    public void doesAddsToTable() {
        PortEntity entity = new PortEntity();

        entity.setName("istanbul");
        repository.save(entity);
        repository.flush();

        Assertions.assertEquals(0, repository.findByName("istanbul").getName().compareTo("istanbul"));
    }
}
