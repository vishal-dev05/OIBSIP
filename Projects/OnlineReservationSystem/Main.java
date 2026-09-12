import javax.swing.*;

/**
 * Entry point. Initializes the SQLite database (creating tables and demo
 * data on first run), then launches the login window.
 *
 * Demo login: admin / admin123
 * Demo train numbers you can look up: 12951, 12301, 12621, 12137, 22691
 */
public class Main {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();
        db.initializeDatabase();

        SwingUtilities.invokeLater(() -> new LoginFrame(db).setVisible(true));
    }
}
