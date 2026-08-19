package edu.ucalgary.oop;

/**
 * Abstract base class for pets treated by the clinic.
 * The species is determined by the concrete subclass and cannot be changed.
 *
 * @author Andrew Dang (30209972), Shiv Sharma (30219086)
 */
public abstract class Pet implements Displayable {
    private int id;
    private String name;
    private int age;
    private Owner owner;

    /**
     * Creates a new pet not yet stored in the database.
     *
     * @param name pet name
     * @param age pet age
     * @param owner pet owner
     */
    protected Pet(String name, int age, Owner owner) {
        this(0, name, age, owner);
    }

    /**
     * Creates a pet loaded from the database.
     *
     * @param id database ID
     * @param name pet name
     * @param age pet age
     * @param owner pet owner
     */
    protected Pet(int id, String name, int age, Owner owner) {
        if (id < 0) {
            throw new IllegalArgumentException("Pet ID cannot be negative.");
        }
        this.id = id;
        setName(name);
        setAge(age);
        setOwner(owner);
    }

    /**
     * Returns the pet database ID.
     *
     * @return pet ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the database ID after an insert.
     *
     * @param id generated database ID
     */
    void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Database pet ID must be positive.");
        }
        this.id = id;
    }

    /**
     * Returns the pet name.
     *
     * @return pet name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the pet name.
     *
     * @param name new pet name
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Pet name cannot be empty.");
        }
        this.name = name.trim();
    }

    /**
     * Returns the pet age.
     *
     * @return age in years
     */
    public int getAge() {
        return age;
    }

    /**
     * Updates the pet age.
     *
     * @param age non-negative age
     */
    public void setAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Pet age cannot be negative.");
        }
        this.age = age;
    }

    /**
     * Returns the pet owner.
     *
     * @return owner object
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * Updates the pet owner and associates this pet with the new owner.
     *
     * @param owner new owner
     */
    public void setOwner(Owner owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Pet must have an owner.");
        }
        if (this.owner != null && this.owner != owner) {
            this.owner.removePet(this);
        }
        this.owner = owner;
        owner.addPet(this);
    }

    /**
     * Returns the immutable species represented by the concrete subclass.
     *
     * @return Dog or Cat
     */
    public abstract String getSpecies();
}
