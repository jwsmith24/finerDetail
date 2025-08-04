package dev.jake.finerdetail.services;

import dev.jake.finerdetail.controllers.util.ResourceNotFoundException;
import dev.jake.finerdetail.entities.DutyAssignment;
import dev.jake.finerdetail.entities.DutyRoster;
import dev.jake.finerdetail.repos.DutyAssignmentRepository;
import dev.jake.finerdetail.repos.DutyRosterRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DutyRosterService {

    private static final Logger log = LoggerFactory.getLogger(DutyRosterService.class);

    private final DutyRosterRepository dutyRosterRepo;
    private final DutyAssignmentRepository assignmentRepository;


    public DutyRosterService(DutyRosterRepository dutyRosterRepo, DutyAssignmentRepository assignmentRepository) {
        this.dutyRosterRepo = dutyRosterRepo;
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional(readOnly = true)
    public List<DutyRoster> getAllRosters() {
        return dutyRosterRepo.findAll();
    }

    @Transactional(readOnly = true)
    public DutyRoster getRosterById(Long id) {
        return dutyRosterRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Roster not found with ID: " + id));
    }


    @Transactional(readOnly = true)
    public DutyAssignment getAssignmentById(Long rosterId, Long assignmentId) {
        DutyRoster targetRoster = this.getRosterById(rosterId);

        return targetRoster.getDutyAssignments().stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Assignment not " +
                        "found with ID: %s in roster %s", assignmentId, rosterId)));
    }

    @Transactional(readOnly = true)
    public List<DutyAssignment> getAllAssignments(Long rosterId) {

        DutyRoster targetRoster = this.getRosterById(rosterId);
        return targetRoster.getDutyAssignments();
    }

    @Transactional
    public DutyRoster createRoster(DutyRoster roster) {
        return dutyRosterRepo.save(roster);
    }

    @Transactional
    public void updateRoster(Long id, DutyRoster updated) {
        dutyRosterRepo.findById(id)
                .map(existing -> {
                    existing.setDetailType(updated.getDetailType());
                    existing.setDescription(updated.getDescription());
                    return existing;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Roster not found with ID: " + id));
    }

    @Transactional
    public void deleteRoster(Long id) {
        if (!dutyRosterRepo.existsById(id)) throw new ResourceNotFoundException("Roster not found with ID: " + id);
        dutyRosterRepo.deleteById(id);
    }

    @Transactional
    public void deleteAllRosters() {
        dutyRosterRepo.deleteAll();
    }

    /**
     * Load an existing roster, add a duty assignment, and then let JPA handle the database magic.
     */
    @Transactional
    public DutyAssignment addAssignment(Long rosterId, DutyAssignment assignment) {
        // create new assignment, link to roster
        DutyRoster roster =
                dutyRosterRepo.findById(rosterId).orElseThrow(() -> new ResourceNotFoundException("Target " +
                        "roster does not exist"));
        assignment.setRoster(roster);
        roster.getDutyAssignments()
                .add(assignment);


        return assignmentRepository.save(assignment);
    }

    @Transactional
    public void updateAssignment(DutyAssignment newAssignment) {

        log.info("Got roster to update with \n {}", newAssignment);

        DutyAssignment savedAssignment = assignmentRepository.findById(newAssignment.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Target assignment does not exist")
        );

        savedAssignment.setDescription(newAssignment.getDescription());
        savedAssignment.setDate(newAssignment.getDate());

    }




    @Transactional
    public void removeAssignment(Long rosterId, Long assignmentId) {
        DutyRoster roster = dutyRosterRepo.findById(rosterId)
                .orElseThrow(() -> new ResourceNotFoundException("Roster not found"));

        boolean removed = roster.getDutyAssignments()
                .removeIf(a -> a.getId().equals(assignmentId));

        if (!removed) {
            throw new EntityNotFoundException(String
                    .format("Assignment %S not found on roster %s", assignmentId, rosterId));
        }
    }
}
