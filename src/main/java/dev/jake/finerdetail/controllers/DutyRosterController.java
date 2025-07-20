package dev.jake.finerdetail.controllers;

import dev.jake.finerdetail.controllers.util.RosterNotFoundException;
import dev.jake.finerdetail.entities.DutyRoster;
import dev.jake.finerdetail.repos.DutyRosterRepository;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/rosters")
public class DutyRosterController {


    private final DutyRosterRepository repository;

    public DutyRosterController(DutyRosterRepository dutyRosterRepository) {
        this.repository = dutyRosterRepository;
    }

    @GetMapping
    public List<DutyRoster> getAllRosters() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public DutyRoster getRoster(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new RosterNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<DutyRoster> createRoster(@RequestBody DutyRoster roster) {
        DutyRoster savedRoster = repository.save(roster);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedRoster.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(savedRoster);
    }


    // todo: implement PUT so that we can add and save new duty assignments

}
