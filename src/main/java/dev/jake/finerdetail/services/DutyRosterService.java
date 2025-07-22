package dev.jake.finerdetail.services;

import dev.jake.finerdetail.controllers.util.ResourceNotFoundException;
import dev.jake.finerdetail.entities.DutyAssignment;
import dev.jake.finerdetail.entities.DutyRoster;
import dev.jake.finerdetail.repos.DutyRosterRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DutyRosterService {

    private final DutyRosterRepository repo;


    public DutyRosterService(DutyRosterRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<DutyRoster> getAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public DutyRoster getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Roster not found with ID: " + id));
    }


    @Transactional(readOnly = true)
    public DutyAssignment getAssignmentById(Long rosterId, Long assignmentId) {
        DutyRoster targetRoster = this.getById(rosterId);

        return targetRoster.getDutyAssignments().stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Assignment not " +
                        "found with ID: %s in roster %s", assignmentId, rosterId)));
    }

    @Transactional(readOnly = true)
    public List<DutyAssignment> getAllAssignments(Long rosterId) {

        DutyRoster targetRoster = this.getById(rosterId);
        return Collections.unmodifiableList(targetRoster.getDutyAssignments());
    }

    @Transactional
    public DutyRoster create(DutyRoster roster) {
        return repo.save(roster);
    }

    @Transactional
    public void update(Long id, DutyRoster updated) {
        repo.findById(id)
                .map(existing -> {
                    existing.setDetailType(updated.getDetailType());
                    existing.setDescription(updated.getDescription());
                    return existing;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Roster not found with ID: " + id));
    }

    @Transactional
    public void deleteRoster(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Roster not found with ID: " + id);
        repo.deleteById(id);
    }

    @Transactional
    public void deleteAllRosters() {
        repo.deleteAll();
    }

    /**
     * Load an existing roster, add a duty assignment, and then let JPA handle the database magic.
     */
    @Transactional
    public DutyAssignment addAssignment(Long rosterId, DutyAssignment assignment) {
        DutyRoster roster = repo.findById(rosterId)
                .orElseThrow(() -> new ResourceNotFoundException("Roster with ID: " + rosterId +
                        " not found"));

        roster.getDutyAssignments().add(assignment);
        return assignment;
    }


    @Transactional
    public void removeAssignment(Long rosterId, Long assignmentId) {
        DutyRoster roster = repo.findById(rosterId)
                .orElseThrow(() -> new ResourceNotFoundException("Roster not found"));

        boolean removed = roster.getDutyAssignments()
                .removeIf(a -> a.getId().equals(assignmentId));

        if (!removed) {
            throw new EntityNotFoundException(String
                    .format("Assignment %S not found on roster %s", assignmentId, rosterId));
        }
    }
}
