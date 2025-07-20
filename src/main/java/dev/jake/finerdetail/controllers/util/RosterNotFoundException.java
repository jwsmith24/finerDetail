package dev.jake.finerdetail.controllers.util;

public class RosterNotFoundException extends RuntimeException {
    public RosterNotFoundException(Long id) {
        super("Could not find roster with ID: " + id);
    }
}
