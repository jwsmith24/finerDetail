package dev.jake.finerdetail.entities.dtos;

import dev.jake.finerdetail.util.constants.DetailType;

public record DutyAssignmentDTO(Long id, String date, DetailType detailType, String description) {
}
