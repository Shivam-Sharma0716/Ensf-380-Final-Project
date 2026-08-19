package edu.ucalgary.oop;

import org.junit.*;
import static org.junit.Assert.*;
import java.time.LocalDateTime;

/**
 * JUnit 4 tests for appointment scheduling rules.
 *
 * @author Andrew Dang, Shiv Sharma
 */
public class AppointmentServiceTest {
    private AppointmentService service;
    private Veterinarian vet;
    private Pet pet;

    /** Creates fresh test objects before each test. */
    @Before
    public void setUp() {
        service = new AppointmentService();
        vet = new Veterinarian(1, "Test Vet", "General Practice");
        Owner owner = new Owner(1, "Test Owner", "555-0000", "owner@test.ca");
        pet = new Dog(1, "Test Dog", 3, owner, true);
    }

    /** Verifies that a valid appointment is added to the in-memory list. */
    @Test
    public void testScheduleValidAppointment() throws AppointmentSchedulingException {
        service.scheduleAppointment(pet, vet,
                LocalDateTime.of(2026, 8, 20, 10, 0), "Check-up");
        assertEquals("One valid appointment should be stored.", 1, service.getAppointments().size());
    }

    /** Verifies that one veterinarian cannot be booked twice at the exact same time. */
    @Test(expected = AppointmentSchedulingException.class)
    public void testRejectDuplicateVetTime() throws AppointmentSchedulingException {
        LocalDateTime time = LocalDateTime.of(2026, 8, 20, 10, 0);
        service.scheduleAppointment(pet, vet, time, "First appointment");
        service.scheduleAppointment(pet, vet, time, "Duplicate appointment");
    }

    /** Verifies that a veterinarian may have exactly the configured daily maximum. */
    @Test
    public void testAllowEightAppointmentsInOneDay() throws AppointmentSchedulingException {
        for (int i = 0; i < AppointmentService.MAX_DAILY_APPOINTMENTS; i++) {
            service.scheduleAppointment(pet, vet,
                    LocalDateTime.of(2026, 8, 21, 8 + i, 0), "Appointment " + (i + 1));
        }
        assertEquals("The configured daily maximum should be allowed.",
                AppointmentService.MAX_DAILY_APPOINTMENTS, service.getAppointments().size());
    }

    /** Verifies that an appointment beyond the daily veterinarian limit is rejected. */
    @Test(expected = AppointmentSchedulingException.class)
    public void testRejectNinthAppointmentInOneDay() throws AppointmentSchedulingException {
        for (int i = 0; i < AppointmentService.MAX_DAILY_APPOINTMENTS; i++) {
            service.scheduleAppointment(pet, vet,
                    LocalDateTime.of(2026, 8, 22, 8 + i, 0), "Appointment " + (i + 1));
        }
        service.scheduleAppointment(pet, vet,
                LocalDateTime.of(2026, 8, 22, 20, 0), "Too many appointments");
    }
}
