import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * First screen shown: username + password, "Login" button.
 * On success, opens MainMenuFrame; on failure, shows "Access Denied".
 */
public class LoginFrame extends JFrame {

    private final DatabaseManager db;
    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);

    public LoginFrame(DatabaseManager db) {
        super("Online Reservation System - Login");
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
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Train Reservation Login", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        JLabel hint = new JLabel("Demo login: admin / admin123", SwingConstants.CENTER);
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        gbc.gridy = 4;
        panel.add(hint, gbc);

        loginButton.addActionListener(this::handleLogin);
        passwordField.addActionListener(this::handleLogin);

        add(panel);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (ValidationUtils.isBlank(username) || ValidationUtils.isBlank(password)) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (db.validateLogin(username, password)) {
            new MainMenuFrame(db).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Access Denied: Invalid username or password.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}
