import javax.swing.*;
import java.awt.*;

/**
 * Booking form: passenger name, train number (auto-populates train name),
 * class, date of journey, source, destination. "Book" validates everything,
 * saves to the DB, and shows the generated PNR in a confirmation dialog.
 */
public class ReservationFrame extends JFrame {

    private final DatabaseManager db;

    private final JTextField passengerNameField = new JTextField(18);
    private final JTextField trainNumberField = new JTextField(18);
    private final JTextField trainNameField = new JTextField(18);
    private final JComboBox<String> classTypeBox = new JComboBox<>(
            new String[]{"Sleeper (SL)", "AC 3 Tier (3A)", "AC 2 Tier (2A)", "AC First Class (1A)", "General"});
    private final JTextField dateField = new JTextField(18);
    private final JTextField sourceField = new JTextField(18);
    private final JTextField destinationField = new JTextField(18);

    public ReservationFrame(DatabaseManager db) {
        super("New Reservation");
        this.db = db;
        buildUi();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(460, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Passenger Name:"), gbc);
        gbc.gridx = 1;
        panel.add(passengerNameField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Train Number:"), gbc);
        gbc.gridx = 1;
        JPanel trainNumberPanel = new JPanel(new BorderLayout(5, 0));
        JButton fetchTrainButton = new JButton("Fetch Name");
        trainNumberPanel.add(trainNumberField, BorderLayout.CENTER);
        trainNumberPanel.add(fetchTrainButton, BorderLayout.EAST);
        panel.add(trainNumberPanel, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Train Name:"), gbc);
        gbc.gridx = 1;
        trainNameField.setEditable(false);
        trainNameField.setBackground(new Color(240, 240, 240));
        panel.add(trainNameField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Class:"), gbc);
        gbc.gridx = 1;
        panel.add(classTypeBox, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Date of Journey (dd-mm-yyyy):"), gbc);
        gbc.gridx = 1;
        panel.add(dateField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Source Station:"), gbc);
        gbc.gridx = 1;
        panel.add(sourceField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Destination Station:"), gbc);
        gbc.gridx = 1;
        panel.add(destinationField, gbc);
        row++;

        JButton bookButton = new JButton("Book Ticket");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(bookButton, gbc);

        fetchTrainButton.addActionListener(e -> handleFetchTrainName());
        bookButton.addActionListener(e -> handleBook());

        add(panel);
    }

    private void handleFetchTrainName() {
        String trainNumber = trainNumberField.getText().trim();
        if (!ValidationUtils.isNumeric(trainNumber)) {
            JOptionPane.showMessageDialog(this, "Train number must be numeric.",
                    "Invalid Train Number", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String name = db.getTrainName(trainNumber);
        if (name == null) {
            trainNameField.setText("");
            JOptionPane.showMessageDialog(this,
                    "No train found for number " + trainNumber
                            + ".\nTry a demo number: 12951, 12301, 12621, 12137, 22691.",
                    "Train Not Found", JOptionPane.WARNING_MESSAGE);
        } else {
            trainNameField.setText(name);
        }
    }

    private void handleBook() {
        String passengerName = passengerNameField.getText().trim();
        String trainNumber = trainNumberField.getText().trim();
        String trainName = trainNameField.getText().trim();
        String classType = (String) classTypeBox.getSelectedItem();
        String date = dateField.getText().trim();
        String source = sourceField.getText().trim();
        String destination = destinationField.getText().trim();

        StringBuilder errors = new StringBuilder();

        if (ValidationUtils.isBlank(passengerName)) {
            errors.append("- Passenger name is required.\n");
        }
        if (!ValidationUtils.isNumeric(trainNumber)) {
            errors.append("- Train number must be numeric.\n");
        }
        if (ValidationUtils.isBlank(trainName)) {
            errors.append("- Train name is empty. Click \"Fetch Name\" with a valid train number first.\n");
        }
        if (!ValidationUtils.isValidDate(date)) {
            errors.append("- Date of journey must be a valid date in dd-mm-yyyy format.\n");
        }
        if (ValidationUtils.isBlank(source)) {
            errors.append("- Source station is required.\n");
        }
        if (ValidationUtils.isBlank(destination)) {
            errors.append("- Destination station is required.\n");
        }
        if (!ValidationUtils.isBlank(source) && !ValidationUtils.isBlank(destination)
                && source.equalsIgnoreCase(destination)) {
            errors.append("- Source and destination cannot be the same.\n");
        }

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, errors.toString(),
                    "Please Fix the Following", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Reservation reservation = new Reservation(
                null, passengerName, trainNumber, trainName, classType, date, source, destination);
        Reservation saved = db.bookReservation(reservation);

        JOptionPane.showMessageDialog(this,
                "Booking Confirmed!\n\n" + saved.toDisplayString(),
                "Reservation Successful", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
