package dev.jake.finerdetail.entities;


import dev.jake.finerdetail.util.constants.DetailType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
public class DutyRoster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DetailType detailType; // CQ_NCO, SD_RUNNER, etc

    private String description; // concise & relevant info about the detail

    protected DutyRoster(){}

    // custom constructor for creating new duty rosters that will be persisted
    public DutyRoster(DetailType detailType, String description) {
        this.detailType = detailType;
        this.description = description;
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

    @Override
    public String toString() {
        return String.format("[ id: %s, detail: %s, description: %s ]",
                this.id, this.detailType, this.description);
    }
}
