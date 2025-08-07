package dev.jake.finerdetail.controllers;

import dev.jake.finerdetail.entities.dtos.DutyAssignmentDTO;
import dev.jake.finerdetail.services.DutyAssignmentService;
import dev.jake.finerdetail.util.mappers.DutyAssignmentMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/rosters/{rosterId}/assignments")
public class DutyAssignmentController {

    private final DutyAssignmentService assignmentService;

    public DutyAssignmentController(DutyAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }


    @GetMapping()
    public List<DutyAssignmentDTO> getAllAssignments(@PathVariable Long rosterId) {

        return assignmentService.getAllAssignments(rosterId)
                .stream().map(DutyAssignmentMapper::toDTO).toList();
    }

    @GetMapping("/{assignmentId}")
    public DutyAssignmentDTO getAssignment(@PathVariable Long rosterId, @PathVariable Long assignmentId) {
        return DutyAssignmentMapper.toDTO(assignmentService.getAssignmentById(rosterId, assignmentId));
    }

    @PostMapping()
    public ResponseEntity<DutyAssignmentDTO> addAssignment(@PathVariable Long rosterId, @Valid @RequestBody DutyAssignmentDTO assignmentDTO) {
        DutyAssignmentDTO created = DutyAssignmentMapper.toDTO(assignmentService.addAssignment(rosterId,
                DutyAssignmentMapper.fromDTO(assignmentDTO)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{assignmentId}").buildAndExpand(created.id()).toUri();

        return ResponseEntity.created(location).body(created);

    }

    /**
     * PUT /rosters/{rosterId}/assignments/update
     */
    @PutMapping("/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAssignment(@Valid @RequestBody DutyAssignmentDTO assignmentDTO,
                                 @PathVariable String rosterId) {

        assignmentService.updateAssignment(DutyAssignmentMapper.fromDTO(assignmentDTO));

    }


    @DeleteMapping("/{assignmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAssignment(@PathVariable Long rosterId, @PathVariable Long assignmentId) {
        assignmentService.removeAssignment(rosterId, assignmentId);
    }




}
