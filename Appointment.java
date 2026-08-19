package edu.ucalgary.oop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an appointment between a pet and veterinarian.
 *
 * @author Andrew Dang (30209972), Shiv Sharma (30219086)
 */
public class Appointment implements Displayable {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private int id;
    private final Pet pet;
    private final Veterinarian veterinarian;
    private final LocalDateTime dateTime;
    private String notes;

    /**
     * Creates a new appointment that has not yet been inserted into the database.
     *
     * @param pet appointment pet
     * @param veterinarian assigned veterinarian
     * @param dateTime appointment date and time
     * @param notes optional appointment notes
     */
    public Appointment(Pet pet, Veterinarian veterinarian, LocalDateTime dateTime, String notes) {
        this(0, pet, veterinarian, dateTime, notes);
    }

    /**
     * Creates an appointment loaded from the database.
     *
     * @param id database ID
     * @param pet appointment pet
     * @param veterinarian assigned veterinarian
     * @param dateTime appointment date and time
     * @param notes appointment notes
     */
    public Appointment(int id, Pet pet, Veterinarian veterinarian,
            LocalDateTime dateTime, String notes) {
        if (id < 0) {
            throw new IllegalArgumentException("Appointment ID cannot be negative.");
        }
        if (pet == null || veterinarian == null || dateTime == null) {
            throw new IllegalArgumentException("Pet, veterinarian, and date/time are required.");
        }
        this.id = id;
        this.pet = pet;
        this.veterinarian = veterinarian;
        this.dateTime = dateTime;
        this.notes = notes == null ? "" : notes.trim();
    }

    /**
     * Returns the appointment database ID.
     *
     * @return appointment ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the database ID after insertion.
     *
     * @param id generated database ID
     */
    void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Database appointment ID must be positive.");
        }
        this.id = id;
    }

    /**
     * Returns the pet.
     *
     * @return appointment pet
     */
    public Pet getPet() {
        return pet;
    }

    /**
     * Returns the veterinarian.
     *
     * @return assigned veterinarian
     */
    public Veterinarian getVeterinarian() {
        return veterinarian;
    }

    /**
     * Returns the appointment date and time.
     *
     * @return appointment date/time
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Returns the appointment notes.
     *
     * @return notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Updates appointment notes.
     *
     * @param notes new notes
     */
    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes.trim();
    }

    /**
     * Returns information suitable for the CLI.
     *
     * @return formatted appointment information
     */
    @Override
    public String getDisplayInfo() {
        String noteText = notes.isEmpty() ? "No notes" : notes;
        return String.format("Appointment #%d | %s | Pet: %s (#%d) | Vet: %s (#%d) | %s",
                id, dateTime.format(DISPLAY_FORMAT), pet.getName(), pet.getId(),
                veterinarian.getName(), veterinarian.getId(), noteText);
    }
}
