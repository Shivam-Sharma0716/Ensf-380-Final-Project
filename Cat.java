package edu.ucalgary.oop;

/**
 * Represents a cat registered at the clinic.
 *
 * @author Andrew Dang (30209972), Shiv Sharma (30219086)
 */
public class Cat extends Pet {
    private static final String SPECIES = "Cat";
    private boolean indoor;

    /**
     * Creates a new cat.
     *
     * @param name cat name
     * @param age cat age
     * @param owner cat owner
     * @param indoor whether the cat is an indoor pet
     */
    public Cat(String name, int age, Owner owner, boolean indoor) {
        super(name, age, owner);
        this.indoor = indoor;
    }

    /**
     * Creates a cat loaded from the database.
     *
     * @param id database ID
     * @param name cat name
     * @param age cat age
     * @param owner cat owner
     * @param indoor whether the cat is an indoor pet
     */
    public Cat(int id, String name, int age, Owner owner, boolean indoor) {
        super(id, name, age, owner);
        this.indoor = indoor;
    }

    /**
     * Returns whether the cat is an indoor pet.
     *
     * @return true for an indoor cat
     */
    public boolean isIndoor() {
        return indoor;
    }

    /**
     * Updates the indoor status.
     *
     * @param indoor new indoor status
     */
    public void setIndoor(boolean indoor) {
        this.indoor = indoor;
    }

    /**
     * Returns the fixed species.
     *
     * @return Cat
     */
    @Override
    public String getSpecies() {
        return SPECIES;
    }

    /**
     * Returns information suitable for the CLI.
     *
     * @return formatted cat information
     */
    @Override
    public String getDisplayInfo() {
        return String.format("Pet #%d | %s | Cat | Age: %d | Owner: %s (#%d) | Indoor: %s",
                getId(), getName(), getAge(), getOwner().getName(), getOwner().getId(),
                indoor ? "Yes" : "No");
    }
}
