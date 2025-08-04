package dev.jake.finerdetail.entities;

import dev.jake.finerdetail.util.constants.DetailType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * The DutyAssignment object represents a given duty that will need to be filled via polling the roster.
 * It captures all the necessary information about the specific duty and will be tracked
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DutyAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate date;

    // need because soldiers will have different duty types
    @Enumerated(EnumType.STRING)
    private DetailType detailType;

    private String description; // concise & specific information for the individual detail

    @ManyToOne
    @JoinColumn(name = "roster_id")
    private DutyRoster roster;

    public DutyAssignment(LocalDate date, DetailType detailType) {
        this.date = date;
        this.description = "";
        this.detailType = detailType;
    }
    public DutyAssignment(LocalDate date, String description, DetailType detailType) {
        this.date = date;
        this.description = description;
        this.detailType = detailType;

    }


    @Override
    public String toString() {
        return String.format("[ %s on %s | %s", this.detailType, this.date, "TBD");
    }
}
