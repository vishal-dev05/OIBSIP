import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles all database access: connecting to SQLite, creating tables,
 * seeding demo data, and CRUD operations for users, trains, and reservations.
 *
 * All queries use PreparedStatement with bound parameters (never string
 * concatenation) to prevent SQL injection.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:reservation.db";

    /** Opens a fresh connection. Caller is responsible for closing it (try-with-resources). */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /** Creates tables if they don't exist yet, and seeds demo users/trains once. */
    public void initializeDatabase() {
        String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY," +
                "password TEXT NOT NULL)";

        String trainsTable = "CREATE TABLE IF NOT EXISTS trains (" +
                "train_number TEXT PRIMARY KEY," +
                "train_name TEXT NOT NULL)";

        String reservationsTable = "CREATE TABLE IF NOT EXISTS reservations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "pnr TEXT UNIQUE NOT NULL," +
                "passenger_name TEXT NOT NULL," +
                "train_number TEXT NOT NULL," +
                "train_name TEXT NOT NULL," +
                "class_type TEXT NOT NULL," +
                "journey_date TEXT NOT NULL," +
                "source_station TEXT NOT NULL," +
                "destination_station TEXT NOT NULL)";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(trainsTable);
            stmt.execute(reservationsTable);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }

        seedDemoUserIfMissing();
        seedDemoTrainsIfMissing();
    }

    private void seedDemoUserIfMissing() {
        String checkSql = "SELECT COUNT(*) FROM users";
        String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = getConnection();
             Statement checkStmt = conn.createStatement();
             ResultSet rs = checkStmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setString(1, "admin");
                    ps.setString(2, "admin123");
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to seed demo user", e);
        }
    }

    private void seedDemoTrainsIfMissing() {
        String checkSql = "SELECT COUNT(*) FROM trains";
        String insertSql = "INSERT INTO trains (train_number, train_name) VALUES (?, ?)";

        String[][] demoTrains = {
                {"12951", "Mumbai Rajdhani Express"},
                {"12301", "Howrah Rajdhani Express"},
                {"12621", "Tamil Nadu Express"},
                {"12137", "Punjab Mail"},
                {"22691", "Rajdhani Express (KSR Bengaluru)"}
        };

        try (Connection conn = getConnection();
             Statement checkStmt = conn.createStatement();
             ResultSet rs = checkStmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    for (String[] train : demoTrains) {
                        ps.setString(1, train[0]);
                        ps.setString(2, train[1]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to seed demo trains", e);
        }
    }

    /** Returns true if username+password match a row in users. */
    public boolean validateLogin(String username, String password) {
        String sql = "SELECT 1 FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Login check failed", e);
        }
    }

    /** Looks up a train's name from its number. Returns null if not found. */
    public String getTrainName(String trainNumber) {
        String sql = "SELECT train_name FROM trains WHERE train_number = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trainNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("train_name") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Train lookup failed", e);
        }
    }

    /**
     * Inserts a reservation and returns it with a freshly generated PNR.
     * PNR format: PNR + zero-padded auto-increment id, e.g. PNR000001.
     */
    public Reservation bookReservation(Reservation r) {
        String insertSql = "INSERT INTO reservations " +
                "(pnr, passenger_name, train_number, train_name, class_type, journey_date, source_station, destination_station) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Use a temporary placeholder, then patch in the real PNR built from the generated id.
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            long id;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO reservations (pnr, passenger_name, train_number, train_name, class_type, journey_date, source_station, destination_station) " +
                            "VALUES ('PENDING', ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, r.getPassengerName());
                ps.setString(2, r.getTrainNumber());
                ps.setString(3, r.getTrainName());
                ps.setString(4, r.getClassType());
                ps.setString(5, r.getJourneyDate());
                ps.setString(6, r.getSource());
                ps.setString(7, r.getDestination());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    id = keys.getLong(1);
                }
            }

            String pnr = String.format("PNR%06d", id);
            try (PreparedStatement update = conn.prepareStatement(
                    "UPDATE reservations SET pnr = ? WHERE id = ?")) {
                update.setString(1, pnr);
                update.setLong(2, id);
                update.executeUpdate();
            }

            conn.commit();
            r.setPnr(pnr);
            return r;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to book reservation", e);
        }
    }

    /** Fetches a reservation by PNR. Returns null if no such booking exists. */
    public Reservation getReservationByPnr(String pnr) {
        String sql = "SELECT * FROM reservations WHERE pnr = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Reservation(
                        rs.getString("pnr"),
                        rs.getString("passenger_name"),
                        rs.getString("train_number"),
                        rs.getString("train_name"),
                        rs.getString("class_type"),
                        rs.getString("journey_date"),
                        rs.getString("source_station"),
                        rs.getString("destination_station")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch reservation", e);
        }
    }

    /** Deletes a reservation by PNR. Returns true if a row was actually removed. */
    public boolean cancelReservation(String pnr) {
        String sql = "DELETE FROM reservations WHERE pnr = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to cancel reservation", e);
        }
    }
}
