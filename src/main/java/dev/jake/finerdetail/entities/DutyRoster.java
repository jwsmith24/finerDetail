package dev.jake.finerdetail.entities;


import dev.jake.finerdetail.controllers.util.ResourceNotFoundException;
import dev.jake.finerdetail.util.constants.DetailType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A detail object represents a DA Form 6 for a specific duty with the exception that it is a
 * continuous, living document so that it's not required to make a new one for each month and
 * carry over Soldier data. Incoming and outgoing Soldiers can simply be added or removed from
 * the roster by the detail manager.
 *
 * <p>
 * A DutyRoster object contains a list of roster entries which consist of a Soldier's metadata plus their last
 * time pulling that specific duty.
 * </p>
 */

@Entity
@Table(name = "duty_roster")
public class DutyRoster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DetailType detailType; // CQ_NCO, SD_RUNNER, etc

    private String description; // concise & relevant info about the detail

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "roster_id")
    private List<DutyAssignment> dutyAssignments; // all duties available to be assigned

    protected DutyRoster(){}

    // custom constructor for creating new duty rosters that will be persisted
    public DutyRoster(DetailType detailType, String description) {
        this.detailType = detailType;
        this.description = description;
        this.dutyAssignments = new ArrayList<>();
    }

    public DutyRoster(DetailType detailType, String description,
                      List<DutyAssignment> dutyAssignments) {
        this.detailType = detailType;
        this.description = description;
        this.dutyAssignments = dutyAssignments;
    }


    public List<DutyAssignment> getDutyAssignments() {
        return dutyAssignments;
    }

    /**
     * Filter list by assignment id and find the first entry that matches the target.
     */
    public DutyAssignment getDutyAssignment(Long assignmentId) {
        return dutyAssignments.stream()
                        .filter(a -> Objects.equals(a.getId(), assignmentId))
                        .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("Assignment with ID %s not found on roster ID %s", assignmentId, this.id)));

    }

    public String getDescription() {
        return this.description;
    }

    public Long getId() {
        return this.id;
    }

    public DetailType getDetailType() {
        return this.detailType;
    }

    public void setDetailType(DetailType detailType) {
        this.detailType = detailType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("[ id: %s, detail: %s, description: %s ]",
                this.id, this.detailType, this.description);
    }
}
