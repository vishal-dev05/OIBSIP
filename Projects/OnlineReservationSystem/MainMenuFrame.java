import javax.swing.*;
import java.awt.*;

/**
 * Shown right after a successful login. Just routes to the two main
 * features: making a new booking, and cancelling an existing one by PNR.
 */
public class MainMenuFrame extends JFrame {

    private final DatabaseManager db;

    public MainMenuFrame(DatabaseManager db) {
        super("Online Reservation System - Main Menu");
        this.db = db;
        buildUi();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 220);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("What would you like to do?", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(title, gbc);

        JButton bookButton = new JButton("Book a New Ticket");
        JButton cancelButton = new JButton("Cancel a Booking (by PNR)");
        JButton logoutButton = new JButton("Logout");

        gbc.gridy = 1;
        panel.add(bookButton, gbc);
        gbc.gridy = 2;
        panel.add(cancelButton, gbc);
        gbc.gridy = 3;
        panel.add(logoutButton, gbc);

        bookButton.addActionListener(e -> new ReservationFrame(db).setVisible(true));
        cancelButton.addActionListener(e -> new CancellationFrame(db).setVisible(true));
        logoutButton.addActionListener(e -> {
            new LoginFrame(db).setVisible(true);
            dispose();
        });

        add(panel);
    }
}
