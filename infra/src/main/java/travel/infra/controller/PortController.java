package travel.infra.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import travel.infra.dto.CreatePortRequest;
import travel.infra.entity.PortEntity;
import travel.infra.repository.PortRepository;

@RestController
@RequestMapping("ports/")
@RequiredArgsConstructor
public class PortController {
    private final PortRepository portRepository;

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPort(@RequestBody CreatePortRequest request) {
        var entity = new PortEntity();

        entity.setName(request.getName());
        portRepository.saveAndFlush(entity);
    }
}
