# Online Reservation System (Java Swing + SQLite/JDBC)

A GUI train/transport reservation system: log in, book a ticket (which
generates a PNR), and cancel a booking by PNR.

## Classes

| Class               | Responsibility                                                             |
|---------------------|------------------------------------------------------------------------------|
| `Main`              | Entry point. Initializes the database, launches the login screen.          |
| `DatabaseManager`   | All JDBC/SQLite access: schema setup, seeding, login check, CRUD.          |
| `Reservation`       | POJO holding one booking's fields, plus a display-formatted summary.       |
| `ValidationUtils`   | Static helpers: blank check, numeric check, strict `dd-MM-yyyy` date check.|
| `LoginFrame`        | Username/password screen; denies access on bad credentials.                |
| `MainMenuFrame`     | Post-login hub: routes to booking or cancellation.                         |
| `ReservationFrame`  | Booking form: passenger, train number (auto-fetch name), class, date, source/destination, PNR confirmation. |
| `CancellationFrame` | PNR input, Fetch button shows full details, Cancel asks "Are you sure?" then deletes. |

## Requirements

- Java JDK 17+ (any recent JDK works)
- The bundled `lib/sqlite-jdbc-<version>.jar` (no separate DB server needed — SQLite is file-based)

## How to run

From the project root:

```bash
javac -cp "lib/sqlite-jdbc-3.53.4.0.jar" -d out src/*.java
java -cp "lib/sqlite-jdbc-3.53.4.0.jar:out" Main
```

On Windows, use `;` instead of `:` as the classpath separator:

```powershell
javac -cp "lib/sqlite-jdbc-3.53.4.0.jar" -d out src/*.java
java -cp "lib/sqlite-jdbc-3.53.4.0.jar;out" Main
```

A file called `reservation.db` will be created in the working directory on
first run — this is the actual SQLite database. It's already listed in the
repo's root `.gitignore` (`*.db`) so it won't get committed.

## Demo credentials

```
Username: admin
Password: admin123
```

## Demo train numbers (for the "Fetch Name" auto-populate)

| Train Number | Train Name                     |
|--------------|---------------------------------|
| 12951        | Mumbai Rajdhani Express         |
| 12301        | Howrah Rajdhani Express         |
| 12621        | Tamil Nadu Express              |
| 12137        | Punjab Mail                     |
| 22691        | Rajdhani Express (KSR Bengaluru)|

## Feature checklist coverage

- Login form with denied access on invalid credentials
- Reservation form with all required fields, train name auto-populated from train number
- Book button saves to SQLite and generates a unique PNR (`PNR` + zero-padded auto-increment id)
- Confirmation dialog shown after a successful booking
- Cancellation form: PNR input + Fetch button showing full booking details
- Confirm cancellation with an "Are you sure?" dialog; deletes from the database
- Input validation: required fields, strict date format, numeric train number

## Notes / possible extensions

- Passwords are stored in plain text in this demo for simplicity — a real
  system should hash them (e.g. with BCrypt).
- Train catalog is seeded with 5 demo entries; you could add an admin screen
  to manage trains instead of hardcoding them.
- Could add a "My Bookings" list view showing all reservations for search/browse.
