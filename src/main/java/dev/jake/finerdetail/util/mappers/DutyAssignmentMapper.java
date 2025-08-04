package dev.jake.finerdetail.util.mappers;

import dev.jake.finerdetail.entities.DutyAssignment;
import dev.jake.finerdetail.entities.DutyRoster;
import dev.jake.finerdetail.entities.dtos.DutyAssignmentDTO;

import java.time.LocalDate;

public class DutyAssignmentMapper {

    public static DutyAssignmentDTO toDTO(DutyAssignment assignment) {
        return new DutyAssignmentDTO(assignment.getId(), assignment.getDate().toString(),
                assignment.getDetailType(), assignment.getDescription(), assignment.getRoster().getId());
    }

    public static DutyAssignment fromDTO(DutyAssignmentDTO assignmentDTO) {


        return new DutyAssignment(assignmentDTO.id(), LocalDate.parse(assignmentDTO.date()),
                assignmentDTO.detailType(), assignmentDTO.description(), createStubRoster(assignmentDTO.rosterId())

        );
    }

    /**
     * JPA doesn't need the full roster info to update the assignment, the stub only passes
     * relevant info while avoiding an extra DB lookup (if we only had the ID to go off of)
     * @param id of the associated duty roster
     * @return roster stub
     */
    private static DutyRoster createStubRoster(Long id) {
        DutyRoster rosterStub = new DutyRoster();
        rosterStub.setId(id);
        return rosterStub;
    }
}
