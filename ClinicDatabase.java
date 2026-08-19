package edu.ucalgary.oop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

/**
 * Handles PostgreSQL JDBC operations for the veterinary clinic database.
 *
 * @author Andrew Dang (30209972), Shiv Sharma (30219086)
 */
public class ClinicDatabase {
    /** Database JDBC URL. */
    public final String DBURL;
    /** Database username. */
    public final String USERNAME;
    /** Database password. */
    public final String PASSWORD;

    private Connection dbConnect;

    /**
     * Creates a database helper using the supplied JDBC credentials.
     *
     * @param url JDBC database URL
     * @param user database username
     * @param password database password
     */
    public ClinicDatabase(String url, String user, String password) {
        if (url == null || user == null || password == null) {
            throw new IllegalArgumentException("Database connection values cannot be null.");
        }
        DBURL = url;
        USERNAME = user;
        PASSWORD = password;
    }

    /**
     * Opens a PostgreSQL JDBC connection.
     *
     * @throws SQLException when the database connection cannot be established
     */
    public void initializeConnection() throws SQLException {
        dbConnect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
    }

    /**
     * Returns whether the JDBC connection is currently open.
     *
     * @return true when connected
     * @throws SQLException if JDBC cannot inspect the connection state
     */
    public boolean isConnected() throws SQLException {
        return dbConnect != null && !dbConnect.isClosed();
    }

    /**
     * Loads all staff rows and converts them into Veterinarian or Receptionist objects.
     *
     * @return staff list
     * @throws SQLException when the query fails
     */
    public ArrayList<Staff> loadStaff() throws SQLException {
        ensureConnected();
        ArrayList<Staff> staff = new ArrayList<Staff>();
        String query = "SELECT id, name, role, specialization FROM staff ORDER BY id";

        try (Statement statement = dbConnect.createStatement();
                ResultSet results = statement.executeQuery(query)) {
            while (results.next()) {
                int id = results.getInt("id");
                String name = results.getString("name");
                String role = results.getString("role");
                String specialization = results.getString("specialization");

                if ("Vet".equalsIgnoreCase(role)) {
                    staff.add(new Veterinarian(id, name, specialization));
                } else if ("Receptionist".equalsIgnoreCase(role)) {
                    staff.add(new Receptionist(id, name));
                }
            }
        }
        return staff;
    }

    /**
     * Loads all owners from the database.
     *
     * @return owner list
     * @throws SQLException when the query fails
     */
    public ArrayList<Owner> loadOwners() throws SQLException {
        ensureConnected();
        ArrayList<Owner> owners = new ArrayList<Owner>();
        String query = "SELECT id, name, phone, email FROM owners ORDER BY id";

        try (Statement statement = dbConnect.createStatement();
                ResultSet results = statement.executeQuery(query)) {
            while (results.next()) {
                owners.add(new Owner(results.getInt("id"), results.getString("name"),
                        results.getString("phone"), results.getString("email")));
            }
        }
        return owners;
    }

    /**
     * Loads pets after owners have already been loaded so object relationships can be restored.
     *
     * @param owners existing owners
     * @return pet list
     * @throws SQLException when the query fails or a referenced owner is missing
     */
    public ArrayList<Pet> loadPets(ArrayList<Owner> owners) throws SQLException {
        ensureConnected();
        ArrayList<Pet> pets = new ArrayList<Pet>();
        String query = "SELECT id, name, age, species, owner_id, is_vaccinated, is_indoor "
                + "FROM pets ORDER BY id";

        try (Statement statement = dbConnect.createStatement();
                ResultSet results = statement.executeQuery(query)) {
            while (results.next()) {
                int ownerId = results.getInt("owner_id");
                Owner owner = null;
                for (Owner currentOwner : owners) {
                    if (currentOwner.getId() == ownerId) {
                        owner = currentOwner;
                        break;
                    }
                }
                if (owner == null) {
                    throw new SQLException("Pet references missing owner ID " + ownerId + ".");
                }

                int id = results.getInt("id");
                String name = results.getString("name");
                int age = results.getInt("age");
                String species = results.getString("species");

                if ("Dog".equalsIgnoreCase(species)) {
                    pets.add(new Dog(id, name, age, owner, results.getBoolean("is_vaccinated")));
                } else if ("Cat".equalsIgnoreCase(species)) {
                    pets.add(new Cat(id, name, age, owner, results.getBoolean("is_indoor")));
                }
            }
        }
        return pets;
    }

    /**
     * Loads appointments and restores references to pets and veterinarians.
     *
     * @param pets loaded pets
     * @param staff loaded staff
     * @return appointment list
     * @throws SQLException when the query fails or a referenced object is missing
     */
    public ArrayList<Appointment> loadAppointments(ArrayList<Pet> pets,
            ArrayList<Staff> staff) throws SQLException {
        ensureConnected();
        ArrayList<Appointment> appointments = new ArrayList<Appointment>();
        String query = "SELECT id, pet_id, vet_id, date_time, notes FROM appointments ORDER BY date_time";

        try (Statement statement = dbConnect.createStatement();
                ResultSet results = statement.executeQuery(query)) {
            while (results.next()) {
                int petId = results.getInt("pet_id");
                int vetId = results.getInt("vet_id");
                Pet pet = null;
                for (Pet currentPet : pets) {
                    if (currentPet.getId() == petId) {
                        pet = currentPet;
                        break;
                    }
                }

                Veterinarian vet = null;
                for (Staff member : staff) {
                    if (member instanceof Veterinarian && member.getId() == vetId) {
                        vet = (Veterinarian) member;
                        break;
                    }
                }

                if (pet == null || vet == null) {
                    throw new SQLException("Appointment has an invalid pet or vet ID.");
                }

                appointments.add(new Appointment(results.getInt("id"), pet, vet,
                        results.getTimestamp("date_time").toLocalDateTime(),
                        results.getString("notes")));
            }
        }
        return appointments;
    }

    /**
     * Inserts a new owner and stores the generated ID back in the Owner object.
     *
     * @param owner owner to insert
     * @throws SQLException when the insert fails
     */
    public void insertOwner(Owner owner) throws SQLException {
        ensureConnected();
        String query = "INSERT INTO owners (name, phone, email) VALUES (?, ?, ?)";

        try (PreparedStatement statement = dbConnect.prepareStatement(query,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, owner.getName());
            statement.setString(2, owner.getPhone());
            statement.setString(3, owner.getEmail());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    owner.setId(keys.getInt(1));
                } else {
                    throw new SQLException("Owner was inserted but no database ID was returned.");
                }
            }
        }
    }

    /**
     * Inserts a new pet and stores the generated ID back in the Pet object.
     *
     * @param pet pet to insert
     * @throws SQLException when the insert fails
     */
    public void insertPet(Pet pet) throws SQLException {
        ensureConnected();
        String query = "INSERT INTO pets "
                + "(name, age, species, owner_id, is_vaccinated, is_indoor) VALUES (?, ?, ?::species_type, ?, ?, ?)";

        try (PreparedStatement statement = dbConnect.prepareStatement(query,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, pet.getName());
            statement.setInt(2, pet.getAge());
            statement.setString(3, pet.getSpecies());
            statement.setInt(4, pet.getOwner().getId());

            if (pet instanceof Dog) {
                Dog dog = (Dog) pet;
                statement.setBoolean(5, dog.isVaccinated());
                statement.setNull(6, Types.BOOLEAN);
            } else if (pet instanceof Cat) {
                Cat cat = (Cat) pet;
                statement.setNull(5, Types.BOOLEAN);
                statement.setBoolean(6, cat.isIndoor());
            } else {
                throw new IllegalArgumentException("Unsupported pet type.");
            }

            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    pet.setId(keys.getInt(1));
                } else {
                    throw new SQLException("Pet was inserted but no database ID was returned.");
                }
            }
        }
    }

    /**
     * Inserts a new appointment and stores the generated ID back in the object.
     *
     * @param appointment appointment to insert
     * @throws SQLException when the insert fails
     */
    public void insertAppointment(Appointment appointment) throws SQLException {
        ensureConnected();
        String query = "INSERT INTO appointments (pet_id, vet_id, date_time, notes) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = dbConnect.prepareStatement(query,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, appointment.getPet().getId());
            statement.setInt(2, appointment.getVeterinarian().getId());
            statement.setTimestamp(3, Timestamp.valueOf(appointment.getDateTime()));
            statement.setString(4, appointment.getNotes());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    appointment.setId(keys.getInt(1));
                } else {
                    throw new SQLException("Appointment was inserted but no database ID was returned.");
                }
            }
        }
    }

    /**
     * Deletes an appointment from the database.
     *
     * @param appointmentId database appointment ID
     * @return true when one row was deleted
     * @throws SQLException when the delete fails
     */
    public boolean deleteAppointment(int appointmentId) throws SQLException {
        ensureConnected();
        String query = "DELETE FROM appointments WHERE id = ?";

        try (PreparedStatement statement = dbConnect.prepareStatement(query)) {
            statement.setInt(1, appointmentId);
            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Closes the database connection.
     *
     * @throws SQLException when JDBC cannot close the connection
     */
    public void close() throws SQLException {
        if (dbConnect != null && !dbConnect.isClosed()) {
            dbConnect.close();
        }
    }

    /**
     * Ensures that database operations are not attempted before initialization.
     *
     * @throws SQLException when there is no active connection
     */
    private void ensureConnected() throws SQLException {
        if (!isConnected()) {
            throw new SQLException("Database connection has not been initialized.");
        }
    }
}
