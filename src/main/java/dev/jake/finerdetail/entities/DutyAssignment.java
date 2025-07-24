package dev.jake.finerdetail.entities;

import dev.jake.finerdetail.util.constants.DetailType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

/**
 * The DutyAssignment object represents a given duty that will need to be filled via polling the roster.
 * It captures all the necessary information about the specific duty and will be tracked
 */

@Entity
public class DutyAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate date;

    // need because soldiers will have different duty types
    @Enumerated(EnumType.STRING)
    private DetailType detailType;

    private String description; // concise & specific information for the individual detail

    protected DutyAssignment() {}

    public DutyAssignment(LocalDate date, DetailType detailType) {
        this.date = date;
        this.detailType = detailType;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public DetailType getDetailType() {
        return detailType;
    }

    public String getDescription() {
        return description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDetailType(DetailType detailType) {
        this.detailType = detailType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("[ %s on %s | %s", this.detailType, this.date, "TBD");
    }
}
