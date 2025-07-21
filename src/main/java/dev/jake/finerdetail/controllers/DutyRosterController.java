package dev.jake.finerdetail.controllers;

import dev.jake.finerdetail.entities.DutyAssignment;
import dev.jake.finerdetail.entities.DutyRoster;
import dev.jake.finerdetail.services.DutyRosterService;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/rosters")
public class DutyRosterController {


    private final DutyRosterService service;

    public DutyRosterController(DutyRosterService service) {
        this.service = service;
    }

    @GetMapping
    public List<DutyRoster> getAllRosters() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DutyRoster getRoster(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/{rosterId}/assignments/{assignmentId}")
    public DutyAssignment getAssignment(@PathVariable Long rosterId,
                                        @PathVariable Long assignmentId) {
        return service.getAssignmentById(rosterId, assignmentId);
    }

    @GetMapping("{rosterId}/assignments")
    public List<DutyAssignment> getAllAssignments(@PathVariable Long rosterId) {
        return service.getAllAssignments(rosterId);
    }

    // add URI location to response header
    @PostMapping
    public ResponseEntity<DutyRoster> createRoster(@RequestBody DutyRoster roster) {
        DutyRoster saved = service.create(roster);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saved.getId()).toUri();

        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping("/{rosterId}/assignments")
    public ResponseEntity<DutyAssignment> addAssignment(@PathVariable Long rosterId,
                                                    @RequestBody DutyAssignment assignment) {
        DutyAssignment created = service.addAssignment(rosterId, assignment);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{assignmentId}").buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(location).body(created);

    }

    /**
     * PUT /rosters/{id} Updates existing roster with new information
     * Returns 204 on success, 404 if not found.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRoster(@RequestBody DutyRoster roster, @PathVariable Long id) {
        service.update(id, roster);
    }

    /**
     * DELETE /rosters/{id} Returns 204 on success, 404 if not found.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoster(@PathVariable Long id) {
        service.deleteRoster(id);
    }

    @DeleteMapping("/{rosterId}/assignments/{assignmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAssignment(@PathVariable Long rosterId, @PathVariable Long assignmentId) {
        service.removeAssignment(rosterId, assignmentId);
    }
}
