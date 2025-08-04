package dev.jake.finerdetail.entities.dtos;

import dev.jake.finerdetail.util.constants.DetailType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record DutyAssignmentDTO(Long id, @NotNull String date,
                                @NotNull DetailType detailType,
                                @NotBlank String description,
                                @NotNull Long rosterId

) {
}
