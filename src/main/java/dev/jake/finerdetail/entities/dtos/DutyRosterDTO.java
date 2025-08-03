package dev.jake.finerdetail.entities.dtos;

import dev.jake.finerdetail.util.constants.DetailType;
import java.util.List;

public record DutyRosterDTO(Long id, DetailType detailType, String description,
                            List<DutyAssignmentDTO> dutyAssignments

) {

}
