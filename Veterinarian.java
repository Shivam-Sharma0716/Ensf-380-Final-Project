package edu.ucalgary.oop;

/**
 * Represents a veterinarian working at the clinic.
 *
 * @author Andrew Dang (30209972), Shiv Sharma (30219086)
 */
public class Veterinarian extends Staff {
    private static final String ROLE = "Vet";
    private String specialization;

    /**
     * Creates a new veterinarian not yet stored in the database.
     *
     * @param name veterinarian name
     * @param specialization area of expertise
     */
    public Veterinarian(String name, String specialization) {
        super(name);
        setSpecialization(specialization);
    }

    /**
     * Creates a veterinarian loaded from the database.
     *
     * @param id database ID
     * @param name veterinarian name
     * @param specialization area of expertise
     */
    public Veterinarian(int id, String name, String specialization) {
        super(id, name);
        setSpecialization(specialization);
    }

    /**
     * Returns the veterinarian specialization.
     *
     * @return specialization text
     */
    public String getSpecialization() {
        return specialization;
    }

    /**
     * Updates the veterinarian specialization.
     *
     * @param specialization new specialization
     */
    public void setSpecialization(String specialization) {
        if (specialization == null || specialization.trim().isEmpty()) {
            throw new IllegalArgumentException("Veterinarian specialization cannot be empty.");
        }
        this.specialization = specialization.trim();
    }

    /**
     * Returns the fixed veterinarian role.
     *
     * @return Vet
     */
    @Override
    public String getRole() {
        return ROLE;
    }

    /**
     * Returns information suitable for the CLI.
     *
     * @return formatted veterinarian information
     */
    @Override
    public String getDisplayInfo() {
        return String.format("Staff #%d | %s | Vet | Specialization: %s",
                getId(), getName(), specialization);
    }
}
