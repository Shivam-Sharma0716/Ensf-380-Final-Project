package edu.ucalgary.oop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Provides in-memory appointment scheduling and validation.
 *
 * @author Andrew Dang (30209972), Shiv Sharma (30219086)
 */
public class AppointmentService {
    /** Maximum number of appointments permitted for one veterinarian per day. */
    public static final int MAX_DAILY_APPOINTMENTS = 8;

    private final ArrayList<Appointment> appointments;

    /**
     * Creates an empty appointment service.
     */
    public AppointmentService() {
        this.appointments = new ArrayList<Appointment>();
    }

    /**
     * Creates an appointment service using appointments already loaded from the database.
     *
     * @param appointments existing appointments
     */
    public AppointmentService(ArrayList<Appointment> appointments) {
        if (appointments == null) {
            throw new IllegalArgumentException("Appointment list cannot be null.");
        }
        this.appointments = new ArrayList<Appointment>(appointments);
    }

    /**
     * Gets the current appointments.
     *
     * @return list of appointments
     */
    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    /**
     * Adds an appointment that was already loaded and validated by the database.
     *
     * @param appointment existing appointment
     */
    public void addLoadedAppointment(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }
        appointments.add(appointment);
    }

    /**
     * Schedules a new appointment after enforcing the duplicate-time and daily-limit rules.
     *
     * @param pet pet being seen
     * @param veterinarian assigned veterinarian
     * @param dateTime appointment date/time
     * @param notes appointment notes
     * @return newly created appointment
     * @throws AppointmentSchedulingException when the veterinarian is double-booked or at the daily limit
     */
    public Appointment scheduleAppointment(Pet pet, Veterinarian veterinarian,
            LocalDateTime dateTime, String notes) throws AppointmentSchedulingException {
        if (pet == null || veterinarian == null || dateTime == null) {
            throw new IllegalArgumentException("Pet, veterinarian, and date/time are required.");
        }

        for (Appointment appointment : appointments) {
            if (appointment.getVeterinarian().getId() == veterinarian.getId()
                    && appointment.getDateTime().equals(dateTime)) {
                throw new AppointmentSchedulingException(
                        "That veterinarian is already booked at the selected date and time.");
            }
        }

        LocalDate requestedDate = dateTime.toLocalDate();
        int appointmentsForDay = 0;
        for (Appointment appointment : appointments) {
            if (appointment.getVeterinarian().getId() == veterinarian.getId()
                    && appointment.getDateTime().toLocalDate().equals(requestedDate)) {
                appointmentsForDay++;
            }
        }

        if (appointmentsForDay >= MAX_DAILY_APPOINTMENTS) {
            throw new AppointmentSchedulingException(
                    "That veterinarian has reached the daily limit of "
                    + MAX_DAILY_APPOINTMENTS + " appointments.");
        }

        Appointment appointment = new Appointment(pet, veterinarian, dateTime, notes);
        appointments.add(appointment);
        return appointment;
    }

    /**
     * Finds an appointment by database ID.
     *
     * @param appointmentId appointment ID
     * @return appointment, or null when not found
     */
    public Appointment findAppointmentById(int appointmentId) {
        for (Appointment appointment : appointments) {
            if (appointment.getId() == appointmentId) {
                return appointment;
            }
        }
        return null;
    }

    /**
     * Removes an appointment from in-memory storage.
     *
     * @param appointment appointment to remove
     * @return true when the appointment was removed
     */
    public boolean removeAppointment(Appointment appointment) {
        return appointments.remove(appointment);
    }
}
