package dev.jake.finerdetail.controllers;

import dev.jake.finerdetail.entities.dtos.DutyAssignmentDTO;
import dev.jake.finerdetail.entities.dtos.DutyRosterDTO;
import dev.jake.finerdetail.services.DutyRosterService;
import dev.jake.finerdetail.util.mappers.DutyAssignmentMapper;
import dev.jake.finerdetail.util.mappers.DutyRosterMapper;
import jakarta.validation.Valid;
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
    public List<DutyRosterDTO> getAllRosters() {
        return service.getAllRosters().stream().map(DutyRosterMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    public DutyRosterDTO getRoster(@PathVariable Long id) {
        return DutyRosterMapper.toDTO(service.getRosterById(id));
    }

    @GetMapping("/{rosterId}/assignments/{assignmentId}")
    public DutyAssignmentDTO getAssignment(@PathVariable Long rosterId, @PathVariable Long assignmentId) {
        return DutyAssignmentMapper.toDTO(service.getAssignmentById(rosterId, assignmentId));
    }

    @GetMapping("/{rosterId}/assignments")
    public List<DutyAssignmentDTO> getAllAssignments(@PathVariable Long rosterId) {

        return service.getAllAssignments(rosterId).stream().map(DutyAssignmentMapper::toDTO).toList();
    }

    // add URI location to response header
    @PostMapping
    public ResponseEntity<DutyRosterDTO> createRoster(@Valid @RequestBody DutyRosterDTO rosterDTO) {


        DutyRosterDTO saved = DutyRosterMapper.toDTO(service.createRoster(DutyRosterMapper.fromDTO(rosterDTO)));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saved.id()).toUri();

        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping("/{rosterId}/assignments")
    public ResponseEntity<DutyAssignmentDTO> addAssignment(@PathVariable Long rosterId, @Valid @RequestBody DutyAssignmentDTO assignmentDTO) {
        DutyAssignmentDTO created = DutyAssignmentMapper.toDTO(service.addAssignment(rosterId, DutyAssignmentMapper.fromDTO(assignmentDTO)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{assignmentId}").buildAndExpand(created.id()).toUri();

        return ResponseEntity.created(location).body(created);

    }

    /**
     * PUT /rosters/{id} Updates existing roster with new information Returns 204 on success, 404 if
     * not found.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRoster(@Valid @RequestBody DutyRosterDTO rosterDTO, @PathVariable Long id) {
        service.updateRoster(id, DutyRosterMapper.fromDTO(rosterDTO));
    }

    /**
     * PUT /rosters/{rosterId}/assignments/update
     */
    @PutMapping("/{rosterId}/assignments/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAssignment(@Valid @RequestBody DutyAssignmentDTO assignmentDTO, @PathVariable Long rosterId) {

        service.updateAssignment(rosterId, DutyAssignmentMapper.fromDTO(assignmentDTO));

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
