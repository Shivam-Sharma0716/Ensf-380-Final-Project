package edu.ucalgary.oop;

import java.util.ArrayList;

/**
 * Represents a registered pet owner.
 *
 * @author Andrew Dang (30209972), Shiv Sharma (30219086)
 */
public class Owner implements Displayable {
    private int id;
    private String name;
    private String phone;
    private String email;
    private final ArrayList<Pet> pets;
    private static int ownerCount = 0;

    /**
     * Creates a new owner that has not yet been stored in the database.
     *
     * @param name owner name
     * @param phone owner phone number
     * @param email owner email address
     */
    public Owner(String name, String phone, String email) {
        this(0, name, phone, email);
    }

    /**
     * Creates an owner loaded from the database.
     *
     * @param id database ID
     * @param name owner name
     * @param phone owner phone number
     * @param email owner email address
     */
    public Owner(int id, String name, String phone, String email) {
        if (id < 0) {
            throw new IllegalArgumentException("Owner ID cannot be negative.");
        }
        this.id = id;
        setName(name);
        setPhone(phone);
        setEmail(email);
        this.pets = new ArrayList<Pet>();
        ownerCount++;
    }

    /**
     * Returns the owner's database ID.
     *
     * @return owner ID
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
            throw new IllegalArgumentException("Database owner ID must be positive.");
        }
        this.id = id;
    }

    /**
     * Returns the owner name.
     *
     * @return owner name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the owner name.
     *
     * @param name new name
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be empty.");
        }
        this.name = name.trim();
    }

    /**
     * Returns the owner phone number.
     *
     * @return phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Updates the owner phone number.
     *
     * @param phone new phone number
     */
    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner phone number cannot be empty.");
        }
        this.phone = phone.trim();
    }

    /**
     * Returns the owner email address.
     *
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Updates the owner email address.
     *
     * @param email new email address
     */
    public void setEmail(String email) {
        if (email == null || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Owner email address is invalid.");
        }
        this.email = email.trim();
    }

    /**
     * Gets the pets that belong to this owner.
     *
     * @return list of pets
     */
    public ArrayList<Pet> getPets() {
        return pets;
    }

    /**
     * Associates a pet with this owner.
     *
     * @param pet pet to add
     */
    public void addPet(Pet pet) {
        if (pet == null) {
            throw new IllegalArgumentException("Pet cannot be null.");
        }
        if (!pets.contains(pet)) {
            pets.add(pet);
        }
    }

    /**
     * Returns the number of Owner objects created during the current run.
     *
     * @return current owner count
     */
    public static int getOwnerCount() {
        return ownerCount;
    }

    /** Removes a pet association when a database insert is rolled back. */
    void removePet(Pet pet) {
        pets.remove(pet);
    }

    /** Corrects the runtime owner count if registration fails before completion. */
    static void rollbackOwnerCount() {
        if (ownerCount > 0) {
            ownerCount--;
        }
    }

    /**
     * Returns information suitable for the CLI.
     *
     * @return formatted owner information
     */
    @Override
    public String getDisplayInfo() {
        return String.format("Owner #%d | %s | Phone: %s | Email: %s | Pets: %d",
                id, name, phone, email, pets.size());
    }
}
