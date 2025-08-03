package dev.jake.finerdetail.util.mappers;

import dev.jake.finerdetail.entities.DutyRoster;
import dev.jake.finerdetail.entities.dtos.DutyRosterDTO;
import java.util.stream.Collectors;

public class DutyRosterMapper {

    public static DutyRosterDTO toDTO (DutyRoster roster) {
        return new DutyRosterDTO(
                roster.getId(),
                roster.getDetailType(),
                roster.getDescription(),
                roster.getDutyAssignments().stream()
                        .map(DutyAssignmentMapper::toDTO)
                        .toList()
        );
    }
}
