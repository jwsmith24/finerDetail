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



    public DutyRosterService(DutyRosterRepository dutyRosterRepo, DutyAssignmentRepository assignmentRepository) {
        this.dutyRosterRepo = dutyRosterRepo;

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


}
