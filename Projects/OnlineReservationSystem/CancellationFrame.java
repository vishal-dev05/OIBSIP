import javax.swing.*;
import java.awt.*;

/**
 * Cancellation form: enter a PNR, "Fetch" shows the full booking details,
 * "Cancel Booking" asks "Are you sure?" then deletes it from the database.
 */
public class CancellationFrame extends JFrame {

    private final DatabaseManager db;
    private final JTextField pnrField = new JTextField(15);
    private final JTextArea detailsArea = new JTextArea(8, 30);
    private final JButton cancelButton = new JButton("Cancel Booking");

    private Reservation currentReservation;

    public CancellationFrame(DatabaseManager db) {
        super("Cancel Reservation");
        this.db = db;
        buildUi();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Enter PNR:"));
        topPanel.add(pnrField);
        JButton fetchButton = new JButton("Fetch");
        topPanel.add(fetchButton);

        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        detailsArea.setBorder(BorderFactory.createTitledBorder("Booking Details"));

        cancelButton.setEnabled(false);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(cancelButton);

        setLayout(new BorderLayout(10, 10));
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        fetchButton.addActionListener(e -> handleFetch());
        cancelButton.addActionListener(e -> handleCancel());
    }

    private void handleFetch() {
        String pnr = pnrField.getText().trim();
        if (ValidationUtils.isBlank(pnr)) {
            JOptionPane.showMessageDialog(this, "Please enter a PNR number.",
                    "Missing PNR", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Reservation reservation = db.getReservationByPnr(pnr);
        if (reservation == null) {
            detailsArea.setText("No booking found for PNR: " + pnr);
            cancelButton.setEnabled(false);
            currentReservation = null;
        } else {
            detailsArea.setText(reservation.toDisplayString());
            cancelButton.setEnabled(true);
            currentReservation = reservation;
        }
    }

    private void handleCancel() {
        if (currentReservation == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel booking " + currentReservation.getPnr() + "?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean removed = db.cancelReservation(currentReservation.getPnr());
        if (removed) {
            JOptionPane.showMessageDialog(this, "Booking cancelled successfully.",
                    "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            detailsArea.setText("");
            pnrField.setText("");
            cancelButton.setEnabled(false);
            currentReservation = null;
        } else {
            JOptionPane.showMessageDialog(this, "Could not cancel booking. It may already be removed.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
