package dev.jake.finerdetail.util.mappers;

import dev.jake.finerdetail.entities.DutyAssignment;
import dev.jake.finerdetail.entities.dtos.DutyAssignmentDTO;
import java.time.LocalDate;

public class DutyAssignmentMapper {

    public static DutyAssignmentDTO toDTO(DutyAssignment assignment) {
        return new DutyAssignmentDTO(assignment.getId(), assignment.getDate().toString(), assignment.getDetailType(), assignment.getDescription());
    }

    public static DutyAssignment fromDTO(DutyAssignmentDTO assignmentDTO) {
        return new DutyAssignment(LocalDate.parse(assignmentDTO.date()),
                assignmentDTO.description(), assignmentDTO.detailType()

        );
    }
}
