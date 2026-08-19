package edu.ucalgary.oop;

/**
 * Abstract base class for all veterinary clinic staff members.
 * The concrete staff type is fixed by the subclass and cannot be changed.
 *
 * @author Andrew Dang (30209972), Shiv Sharma (30219086)
 */
public abstract class Staff implements Displayable {
    private int id;
    private String name;
    private static int staffCount = 0;

    /**
     * Creates a new staff member that has not yet been stored in the database.
     *
     * @param name staff member name
     */
    protected Staff(String name) {
        this(0, name);
    }

    /**
     * Creates a staff member with an existing database ID.
     *
     * @param id database ID
     * @param name staff member name
     */
    protected Staff(int id, String name) {
        setName(name);
        if (id < 0) {
            throw new IllegalArgumentException("Staff ID cannot be negative.");
        }
        this.id = id;
        staffCount++;
    }

    /**
     * Returns the database ID.
     *
     * @return staff ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the database ID after an insert operation.
     * Package access prevents normal application code from changing IDs.
     *
     * @param id generated database ID
     */
    void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Database staff ID must be positive.");
        }
        this.id = id;
    }

    /**
     * Returns the staff member's name.
     *
     * @return staff name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the staff member's name.
     *
     * @param name new staff name
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Staff name cannot be empty.");
        }
        this.name = name.trim();
    }

    /**
     * Returns the immutable staff role represented by the subclass.
     *
     * @return Vet or Receptionist
     */
    public abstract String getRole();

    /**
     * Returns the number of Staff objects created during the current run.
     *
     * @return current staff count
     */
    public static int getStaffCount() {
        return staffCount;
    }
}
