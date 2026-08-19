package edu.ucalgary.oop;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Command-line entry point for the Paws &amp; Care Veterinary Clinic system.
 *
 * @author Andrew Dang (30209972), Shiv Sharma (30219086)
 */
public class ClinicApp {
    private static final String DB_URL = "jdbc:postgresql://localhost/vet_clinic";
    private static final String DB_USER = "oop";
    private static final String DB_PASSWORD = "ucalgary";
    private static final DateTimeFormatter APPOINTMENT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Scanner scanner;
    private final VeterinaryClinic clinic;
    private Staff loggedInStaff;

    /**
     * Creates the CLI using an initialized clinic controller.
     *
     * @param clinic clinic controller
     */
    public ClinicApp(VeterinaryClinic clinic) {
        if (clinic == null) {
            throw new IllegalArgumentException("Clinic cannot be null.");
        }
        this.clinic = clinic;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Runs the staff login and application menu until the user exits.
     */
    public void run() {
        System.out.println("============================================");
        System.out.println(" Paws & Care Veterinary Clinic Management");
        System.out.println("============================================");

        boolean running = true;
        while (running) {
            if (loggedInStaff == null) {
                running = login();
            } else {
                running = showMainMenu();
            }
        }
        System.out.println("Program closed.");
    }

    /**
     * Performs simple staff identification using an existing staff ID.
     *
     * @return false only when the user selects exit
     */
    private boolean login() {
        System.out.println("\nEnter staff ID to log in (0 to exit):");
        int staffId = readInt("> ");
        if (staffId == 0) {
            return false;
        }

        Staff staff = clinic.findStaffById(staffId);
        if (staff == null) {
            System.out.println("Staff ID not found. Please try again.");
            return true;
        }

        loggedInStaff = staff;
        System.out.println("Welcome, " + staff.getName() + " (" + staff.getRole() + ").");
        return true;
    }

    /**
     * Displays and processes the main menu.
     *
     * @return false when the user exits the entire program
     */
    private boolean showMainMenu() {
        System.out.println("\n---------------- Main Menu ----------------");
        System.out.println("1. View staff");
        System.out.println("2. View owners");
        System.out.println("3. Register owner");
        System.out.println("4. View pets");
        System.out.println("5. Register pet");
        System.out.println("6. View appointments");
        System.out.println("7. Schedule appointment");
        System.out.println("8. Cancel appointment");
        System.out.println("9. Look up record by ID");
        System.out.println("10. Logout");
        System.out.println("0. Exit");

        int choice = readInt("Choice: ");
        try {
            switch (choice) {
                case 1:
                    showStaff();
                    break;
                case 2:
                    showOwners();
                    break;
                case 3:
                    registerOwner();
                    break;
                case 4:
                    showPets();
                    break;
                case 5:
                    registerPet();
                    break;
                case 6:
                    showAppointments();
                    break;
                case 7:
                    scheduleAppointment();
                    break;
                case 8:
                    cancelAppointment();
                    break;
                case 9:
                    lookupRecord();
                    break;
                case 10:
                    loggedInStaff = null;
                    System.out.println("Logged out.");
                    break;
                case 0:
                    return false;
                default:
                    System.out.println("Please choose a valid menu option.");
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Input error: " + ex.getMessage());
        } catch (AppointmentSchedulingException ex) {
            System.out.println("Scheduling error: " + ex.getMessage());
        } catch (SQLException ex) {
            System.out.println("Database error: " + ex.getMessage());
        } catch (DateTimeParseException ex) {
            System.out.println("Date/time must use YYYY-MM-DD HH:MM, for example 2026-08-15 14:30.");
        }
        return true;
    }

    /** Displays staff and the current total count. */
    private void showStaff() {
        System.out.println("\nStaff (" + Staff.getStaffCount() + " total):");
        printDisplayables(clinic.getStaff());
    }

    /** Displays owners and the current total count. */
    private void showOwners() {
        System.out.println("\nOwners (" + Owner.getOwnerCount() + " total):");
        printDisplayables(clinic.getOwners());
    }

    /** Displays registered pets. */
    private void showPets() {
        System.out.println("\nPets:");
        printDisplayables(clinic.getPets());
    }

    /** Displays scheduled appointments. */
    private void showAppointments() {
        System.out.println("\nAppointments:");
        printDisplayables(clinic.getAppointments());
    }

    /**
     * Reads owner information and registers the owner in memory and PostgreSQL.
     *
     * @throws SQLException when the database insert fails
     */
    private void registerOwner() throws SQLException {
        System.out.print("Owner name: ");
        String name = scanner.nextLine();
        System.out.print("Phone number: ");
        String phone = scanner.nextLine();
        System.out.print("Email address: ");
        String email = scanner.nextLine();

        Owner owner = clinic.registerOwner(name, phone, email);
        System.out.println("Registered owner #" + owner.getId() + ".");
    }

    /**
     * Reads pet information and registers a Dog or Cat.
     *
     * @throws SQLException when the database insert fails
     */
    private void registerPet() throws SQLException {
        showOwners();
        int ownerId = readInt("Owner ID: ");
        Owner owner = clinic.findOwnerById(ownerId);
        if (owner == null) {
            System.out.println("Owner ID not found.");
            return;
        }

        System.out.print("Pet name: ");
        String name = scanner.nextLine();
        int age = readInt("Pet age: ");
        System.out.print("Pet type (Dog/Cat): ");
        String species = scanner.nextLine().trim();

        if (species.equalsIgnoreCase("Dog")) {
            boolean vaccinated = readYesNo("Is the dog vaccinated? (y/n): ");
            Dog dog = clinic.registerDog(name, age, owner, vaccinated);
            System.out.println("Registered dog #" + dog.getId() + ".");
        } else if (species.equalsIgnoreCase("Cat")) {
            boolean indoor = readYesNo("Is the cat an indoor pet? (y/n): ");
            Cat cat = clinic.registerCat(name, age, owner, indoor);
            System.out.println("Registered cat #" + cat.getId() + ".");
        } else {
            System.out.println("Only Dog and Cat are supported.");
        }
    }

    /**
     * Reads appointment information and schedules a validated appointment.
     *
     * @throws SQLException when database storage fails
     * @throws AppointmentSchedulingException when scheduling rules are violated
     */
    private void scheduleAppointment() throws SQLException, AppointmentSchedulingException {
        showPets();
        int petId = readInt("Pet ID: ");
        Pet pet = clinic.findPetById(petId);
        if (pet == null) {
            System.out.println("Pet ID not found.");
            return;
        }

        System.out.println("\nVeterinarians:");
        for (Staff member : clinic.getStaff()) {
            if (member instanceof Veterinarian) {
                System.out.println(member.getDisplayInfo());
            }
        }
        int vetId = readInt("Veterinarian staff ID: ");
        Veterinarian vet = clinic.findVeterinarianById(vetId);
        if (vet == null) {
            System.out.println("That ID does not belong to a veterinarian.");
            return;
        }

        System.out.print("Date and time (YYYY-MM-DD HH:MM): ");
        LocalDateTime dateTime = LocalDateTime.parse(scanner.nextLine().trim(), APPOINTMENT_FORMAT);
        System.out.print("Notes (press Enter for none): ");
        String notes = scanner.nextLine();

        Appointment appointment = clinic.scheduleAppointment(pet, vet, dateTime, notes);
        System.out.println("Scheduled appointment #" + appointment.getId() + ".");
    }

    /**
     * Cancels an appointment by database ID.
     *
     * @throws SQLException when database deletion fails
     */
    private void cancelAppointment() throws SQLException {
        showAppointments();
        int appointmentId = readInt("Appointment ID to cancel: ");
        if (clinic.cancelAppointment(appointmentId)) {
            System.out.println("Appointment cancelled.");
        } else {
            System.out.println("Appointment ID not found.");
        }
    }

    /** Provides simple ID-based lookups for staff, owners, pets, and appointments. */
    private void lookupRecord() {
        System.out.println("\n1. Staff  2. Owner  3. Pet  4. Appointment");
        int type = readInt("Record type: ");
        int id = readInt("ID: ");
        Displayable result = null;

        switch (type) {
            case 1:
                result = clinic.findStaffById(id);
                break;
            case 2:
                result = clinic.findOwnerById(id);
                break;
            case 3:
                result = clinic.findPetById(id);
                break;
            case 4:
                result = clinic.findAppointmentById(id);
                break;
            default:
                System.out.println("Unknown record type.");
        }

        if (result != null) {
            System.out.println(result.getDisplayInfo());
        } else if (type >= 1 && type <= 4) {
            System.out.println("No matching record was found.");
        }
    }

    /**
     * Prints objects through the Displayable interface.
     *
     * @param items objects to display
     */
    private void printDisplayables(ArrayList<? extends Displayable> items) {
        if (items.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        for (Displayable item : items) {
            System.out.println(item.getDisplayInfo());
        }
    }

    /**
     * Reads an integer while handling non-integer user input.
     *
     * @param prompt input prompt
     * @return valid integer
     */
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    /**
     * Reads a yes/no value.
     *
     * @param prompt input prompt
     * @return true for yes and false for no
     */
    private boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = scanner.nextLine().trim();
            if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                return true;
            }
            if (answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no")) {
                return false;
            }
            System.out.println("Please enter y or n.");
        }
    }

    /**
     * Starts the clinic application using the PostgreSQL settings taught in the course.
     *
     * @param args command-line arguments are not used
     */
    public static void main(String[] args) {
        ClinicDatabase database = new ClinicDatabase(DB_URL, DB_USER, DB_PASSWORD);
        VeterinaryClinic clinic = new VeterinaryClinic(database);

        try {
            clinic.initialize();
            new ClinicApp(clinic).run();
        } catch (SQLException ex) {
            System.out.println("Unable to start the clinic system because the database could not be reached.");
            System.out.println("Details: " + ex.getMessage());
        } finally {
            try {
                clinic.close();
            } catch (SQLException ex) {
                System.out.println("Warning: database connection did not close cleanly.");
            }
        }
    }
}
