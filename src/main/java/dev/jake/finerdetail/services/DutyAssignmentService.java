package dev.jake.finerdetail.services;

import dev.jake.finerdetail.controllers.util.ResourceNotFoundException;
import dev.jake.finerdetail.entities.DutyAssignment;
import dev.jake.finerdetail.entities.DutyRoster;
import dev.jake.finerdetail.repos.DutyAssignmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DutyAssignmentService {

    private final DutyAssignmentRepository assignmentRepository;
    private final DutyRosterService dutyRosterService;

    public DutyAssignmentService(DutyAssignmentRepository dutyAssignmentRepository,
                                 DutyRosterService rosterService) {

        this.assignmentRepository = dutyAssignmentRepository;
        this.dutyRosterService = rosterService;
    }

    @Transactional(readOnly = true)
    public DutyAssignment getAssignmentById(Long rosterId, Long assignmentId) {
        DutyRoster targetRoster = dutyRosterService.getRosterById(rosterId);

        return targetRoster.getDutyAssignments().stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Assignment not " +
                        "found with ID: %s in roster %s", assignmentId, rosterId)));
    }

    @Transactional(readOnly = true)
    public List<DutyAssignment> getAllAssignments(Long rosterId) {

        DutyRoster targetRoster = dutyRosterService.getRosterById(rosterId);
        return targetRoster.getDutyAssignments();
    }

    /**
     * Load an existing roster, add a duty assignment, and then let JPA handle the database magic.
     */
    @Transactional
    public DutyAssignment addAssignment(Long rosterId, DutyAssignment assignment) {
        // create new assignment, link to roster
        DutyRoster roster = dutyRosterService.getRosterById(rosterId);

        assignment.setRoster(roster);
        roster.getDutyAssignments()
                .add(assignment);


        return assignmentRepository.save(assignment);
    }

    @Transactional
    public void updateAssignment(DutyAssignment newAssignment) {

        DutyAssignment savedAssignment = assignmentRepository.findById(newAssignment.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Target assignment does not exist")
        );

        savedAssignment.setDescription(newAssignment.getDescription());
        savedAssignment.setDate(newAssignment.getDate());

    }




    @Transactional
    public void removeAssignment(Long rosterId, Long assignmentId) {
        DutyRoster roster = dutyRosterService.getRosterById(rosterId);

        boolean removed = roster.getDutyAssignments()
                .removeIf(a -> a.getId().equals(assignmentId));

        if (!removed) {
            throw new EntityNotFoundException(String
                    .format("Assignment %S not found on roster %s", assignmentId, rosterId));
        }
    }


}
