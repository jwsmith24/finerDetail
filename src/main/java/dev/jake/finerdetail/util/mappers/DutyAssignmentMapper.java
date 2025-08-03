package dev.jake.finerdetail.util.mappers;

import dev.jake.finerdetail.entities.DutyAssignment;
import dev.jake.finerdetail.entities.dtos.DutyAssignmentDTO;

public class DutyAssignmentMapper {

    public static DutyAssignmentDTO toDTO (DutyAssignment assignment) {
        return new DutyAssignmentDTO(
                assignment.getId(),
                assignment.getDate().toString(),
                assignment.getDetailType(),
                assignment.getDescription()
        );
    }
}
