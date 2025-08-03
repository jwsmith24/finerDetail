package dev.jake.finerdetail.entities.dtos;

import dev.jake.finerdetail.util.constants.DetailType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record DutyRosterDTO(Long id, @NotNull DetailType detailType, @NotBlank String description,
                            List<DutyAssignmentDTO> dutyAssignments

) {

}
