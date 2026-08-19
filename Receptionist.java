package edu.ucalgary.oop;

/**
 * Represents a receptionist working at the clinic.
 *
 * @author Andrew Dang (30209972), Shiv Sharma (30219086)
 */
public class Receptionist extends Staff {
    private static final String ROLE = "Receptionist";

    /**
     * Creates a new receptionist not yet stored in the database.
     *
     * @param name receptionist name
     */
    public Receptionist(String name) {
        super(name);
    }

    /**
     * Creates a receptionist loaded from the database.
     *
     * @param id database ID
     * @param name receptionist name
     */
    public Receptionist(int id, String name) {
        super(id, name);
    }

    /**
     * Returns the fixed receptionist role.
     *
     * @return Receptionist
     */
    @Override
    public String getRole() {
        return ROLE;
    }

    /**
     * Returns information suitable for the CLI.
     *
     * @return formatted receptionist information
     */
    @Override
    public String getDisplayInfo() {
        return String.format("Staff #%d | %s | Receptionist", getId(), getName());
    }
}
