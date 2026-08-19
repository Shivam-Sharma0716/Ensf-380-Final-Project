package edu.ucalgary.oop;

/**
 * Represents a dog registered at the clinic.
 *
 * @author Andrew Dang (30209972), Shiv Sharma (30219086)
 */
public class Dog extends Pet {
    private static final String SPECIES = "Dog";
    private boolean vaccinated;

    /**
     * Creates a new dog.
     *
     * @param name dog name
     * @param age dog age
     * @param owner dog owner
     * @param vaccinated whether the dog is vaccinated
     */
    public Dog(String name, int age, Owner owner, boolean vaccinated) {
        super(name, age, owner);
        this.vaccinated = vaccinated;
    }

    /**
     * Creates a dog loaded from the database.
     *
     * @param id database ID
     * @param name dog name
     * @param age dog age
     * @param owner dog owner
     * @param vaccinated whether the dog is vaccinated
     */
    public Dog(int id, String name, int age, Owner owner, boolean vaccinated) {
        super(id, name, age, owner);
        this.vaccinated = vaccinated;
    }

    /**
     * Returns whether the dog is vaccinated.
     *
     * @return true when vaccinated
     */
    public boolean isVaccinated() {
        return vaccinated;
    }

    /**
     * Updates vaccination status.
     *
     * @param vaccinated new vaccination status
     */
    public void setVaccinated(boolean vaccinated) {
        this.vaccinated = vaccinated;
    }

    /**
     * Returns the fixed species.
     *
     * @return Dog
     */
    @Override
    public String getSpecies() {
        return SPECIES;
    }

    /**
     * Returns information suitable for the CLI.
     *
     * @return formatted dog information
     */
    @Override
    public String getDisplayInfo() {
        return String.format("Pet #%d | %s | Dog | Age: %d | Owner: %s (#%d) | Vaccinated: %s",
                getId(), getName(), getAge(), getOwner().getName(), getOwner().getId(),
                vaccinated ? "Yes" : "No");
    }
}
