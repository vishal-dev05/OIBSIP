import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents one transaction record (deposit, withdrawal, transfer, etc).
 * Immutable once created - a transaction is a historical fact.
 */
public class Transaction {

    public enum Type {
        DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN
    }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final String details;
    private final LocalDateTime timestamp;

    public Transaction(Type type, double amount, double balanceAfter, String details) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    @Override
    public String toString() {
        return String.format(
                "[%s] %-13s | Amount: %10.2f | Balance After: %10.2f | %s",
                timestamp.format(FORMATTER), type, amount, balanceAfter, details
        );
    }
}