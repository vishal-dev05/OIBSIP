
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
- The bundled `sqlite-jdbc-3.53.4.0.jar` (no separate DB server needed — SQLite is file-based)

## How to run

From inside this folder (`Projects/OnlineReservationSystem`):

**Windows (PowerShell) — classpath separator is `;`:**
```powershell
javac -cp "sqlite-jdbc-3.53.4.0.jar" -d out *.java
java -cp "sqlite-jdbc-3.53.4.0.jar;out" Main
```

**macOS/Linux — classpath separator is `:`:**
```bash
javac -cp "sqlite-jdbc-3.53.4.0.jar" -d out *.java
java -cp "sqlite-jdbc-3.53.4.0.jar:out" Main
```

A file called `reservation.db` will be created in this folder on first run —
this is the actual SQLite database. It's covered by the repo's root
`.gitignore` (`*.db`) so it won't get committed.

## Demo credentials