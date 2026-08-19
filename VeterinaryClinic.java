package edu.ucalgary.oop;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Coordinates in-memory clinic data with the PostgreSQL database.
 *
 * @author Andrew Dang (30209972), Shiv Sharma (30219086)
 */
public class VeterinaryClinic {
    private final ClinicDatabase database;
    private ArrayList<Staff> staff;
    private ArrayList<Owner> owners;
    private ArrayList<Pet> pets;
    private AppointmentService appointmentService;

    /**
     * Creates a clinic controller using the supplied database helper.
     *
     * @param database database helper
     */
    public VeterinaryClinic(ClinicDatabase database) {
        if (database == null) {
            throw new IllegalArgumentException("Database helper cannot be null.");
        }
        this.database = database;
        this.staff = new ArrayList<Staff>();
        this.owners = new ArrayList<Owner>();
        this.pets = new ArrayList<Pet>();
        this.appointmentService = new AppointmentService();
    }

    /**
     * Connects to PostgreSQL and loads staff, owners, pets, and appointments into memory.
     *
     * @throws SQLException when database initialization or loading fails
     */
    public void initialize() throws SQLException {
        database.initializeConnection();
        staff = database.loadStaff();
        owners = database.loadOwners();
        pets = database.loadPets(owners);
        appointmentService = new AppointmentService(database.loadAppointments(pets, staff));
    }

    /** Gets the staff loaded in the clinic.
     * @return staff list
     */
    public ArrayList<Staff> getStaff() {
        return staff;
    }

    /** Gets the owners loaded in the clinic.
     * @return owner list
     */
    public ArrayList<Owner> getOwners() {
        return owners;
    }

    /** Gets the pets loaded in the clinic.
     * @return pet list
     */
    public ArrayList<Pet> getPets() {
        return pets;
    }

    /**
     * Returns scheduled appointments.
     *
     * @return appointment list
     */
    public ArrayList<Appointment> getAppointments() {
        return appointmentService.getAppointments();
    }

    /**
     * Finds a staff member by ID. This method supports the simple staff-ID login.
     *
     * @param id staff ID
     * @return matching staff member, or null when not found
     */
    public Staff findStaffById(int id) {
        for (Staff member : staff) {
            if (member.getId() == id) {
                return member;
            }
        }
        return null;
    }

    /**
     * Finds a veterinarian by staff ID.
     *
     * @param id staff ID
     * @return veterinarian, or null when the ID does not represent a veterinarian
     */
    public Veterinarian findVeterinarianById(int id) {
        Staff member = findStaffById(id);
        if (member instanceof Veterinarian) {
            return (Veterinarian) member;
        }
        return null;
    }

    /**
     * Finds an owner by ID.
     *
     * @param id owner ID
     * @return owner, or null
     */
    public Owner findOwnerById(int id) {
        for (Owner owner : owners) {
            if (owner.getId() == id) {
                return owner;
            }
        }
        return null;
    }

    /**
     * Finds a pet by ID.
     *
     * @param id pet ID
     * @return pet, or null
     */
    public Pet findPetById(int id) {
        for (Pet pet : pets) {
            if (pet.getId() == id) {
                return pet;
            }
        }
        return null;
    }

    /**
     * Finds an appointment by ID.
     *
     * @param id appointment ID
     * @return appointment, or null
     */
    public Appointment findAppointmentById(int id) {
        return appointmentService.findAppointmentById(id);
    }

    /**
     * Registers a new owner in the database and in-memory collection.
     *
     * @param name owner name
     * @param phone phone number
     * @param email email address
     * @return registered owner
     * @throws SQLException when the database insert fails
     */
    public Owner registerOwner(String name, String phone, String email) throws SQLException {
        Owner owner = new Owner(name, phone, email);
        try {
            database.insertOwner(owner);
            owners.add(owner);
            return owner;
        } catch (SQLException ex) {
            Owner.rollbackOwnerCount();
            throw ex;
        }
    }

    /**
     * Registers a dog in the database and in-memory collection.
     *
     * @param name dog name
     * @param age dog age
     * @param owner owner
     * @param vaccinated vaccination status
     * @return registered dog
     * @throws SQLException when the database insert fails
     */
    public Dog registerDog(String name, int age, Owner owner, boolean vaccinated) throws SQLException {
        Dog dog = new Dog(name, age, owner, vaccinated);
        try {
            database.insertPet(dog);
            pets.add(dog);
            return dog;
        } catch (SQLException ex) {
            owner.removePet(dog);
            throw ex;
        }
    }

    /**
     * Registers a cat in the database and in-memory collection.
     *
     * @param name cat name
     * @param age cat age
     * @param owner owner
     * @param indoor indoor status
     * @return registered cat
     * @throws SQLException when the database insert fails
     */
    public Cat registerCat(String name, int age, Owner owner, boolean indoor) throws SQLException {
        Cat cat = new Cat(name, age, owner, indoor);
        try {
            database.insertPet(cat);
            pets.add(cat);
            return cat;
        } catch (SQLException ex) {
            owner.removePet(cat);
            throw ex;
        }
    }

    /**
     * Schedules an appointment in memory and then stores it in PostgreSQL. If the database
     * insert fails, the in-memory appointment is removed again so the two stores remain consistent.
     *
     * @param pet pet being seen
     * @param veterinarian assigned veterinarian
     * @param dateTime appointment date/time
     * @param notes notes
     * @return scheduled appointment
     * @throws AppointmentSchedulingException when a clinic scheduling rule is violated
     * @throws SQLException when the database insert fails
     */
    public Appointment scheduleAppointment(Pet pet, Veterinarian veterinarian,
            LocalDateTime dateTime, String notes)
            throws AppointmentSchedulingException, SQLException {
        Appointment appointment = appointmentService.scheduleAppointment(
                pet, veterinarian, dateTime, notes);
        try {
            database.insertAppointment(appointment);
            return appointment;
        } catch (SQLException ex) {
            appointmentService.removeAppointment(appointment);
            throw ex;
        }
    }

    /**
     * Cancels an appointment in PostgreSQL and then removes it from memory.
     *
     * @param appointmentId appointment ID
     * @return true when an appointment was cancelled
     * @throws SQLException when the database delete fails
     */
    public boolean cancelAppointment(int appointmentId) throws SQLException {
        Appointment appointment = appointmentService.findAppointmentById(appointmentId);
        if (appointment == null) {
            return false;
        }

        if (database.deleteAppointment(appointmentId)) {
            appointmentService.removeAppointment(appointment);
            return true;
        }
        return false;
    }

    /**
     * Closes database resources.
     *
     * @throws SQLException when closing the database connection fails
     */
    public void close() throws SQLException {
        database.close();
    }
}
