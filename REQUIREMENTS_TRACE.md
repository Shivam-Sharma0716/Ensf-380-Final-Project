# Project Requirement Trace

| Requirement | Implementation |
|---|---|
| Inheritance | `Staff -> Veterinarian/Receptionist`; `Pet -> Dog/Cat` |
| Interface | `Displayable`, implemented by clinic domain classes |
| Abstract class | `Staff` and `Pet` |
| Static/final | staff/owner counters, role/species constants, appointment daily maximum, DB configuration constants |
| Database connectivity | `ClinicDatabase` using JDBC/PostgreSQL |
| Exception handling | `SQLException`, `IllegalArgumentException`, `DateTimeParseException`, custom `AppointmentSchedulingException` |
| Collections | `ArrayList` for staff, owners, pets, appointments, and owner pets |
| User interface | `ClinicApp` command-line menu using `Scanner` |
| Unit testing | `AppointmentServiceTest` using JUnit 4 |
| Packages | `edu.ucalgary.oop` |
| Javadoc | Javadoc comments throughout public classes/methods; generated HTML can be produced with `javadoc` |
| Staff management | staff loaded/displayed; simple staff-ID login; vet/receptionist subclasses |
| Owner management | database load, display, lookup, registration |
| Pet management | database load, display, lookup, Dog/Cat registration |
| Appointment management | load, display, lookup, schedule, cancel |
| Duplicate appointment prevention | `AppointmentService.scheduleAppointment` plus database unique constraint |
| Daily appointment limit | `AppointmentService.MAX_DAILY_APPOINTMENTS = 8` |
| Graceful DB failure | startup/menu catches and explains `SQLException` |
| In-memory runtime storage | `VeterinaryClinic` collections loaded from database at startup |
