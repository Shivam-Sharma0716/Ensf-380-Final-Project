package edu.ucalgary.oop;

/**
 * Custom checked exception used when an appointment violates clinic
 * scheduling rules.
 *
 * @author Andrew Dang (30209972), Shiv Sharma (30219086)
 */
public class AppointmentSchedulingException extends Exception {
    /**
     * Creates an appointment scheduling exception.
     *
     * @param message explanation of the scheduling problem
     */
    public AppointmentSchedulingException(String message) {
        super(message);
    }
}
