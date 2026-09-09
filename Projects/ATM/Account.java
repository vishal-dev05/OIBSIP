/**
 * Represents a single bank account.
 * Demonstrates encapsulation: fields are private, accessed via getters/setters.
 */
public class Account {

    private final String accountId;
    private String pin;
    private final String ownerName;
    private double balance;

    public Account(String accountId, String pin, String ownerName, double balance) {
        this.accountId = accountId;
        this.pin = pin;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    // ---------- Getters ----------
    public String getAccountId() {
        return accountId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public double getBalance() {
        return balance;
    }

    // ---------- PIN handling ----------
    public boolean isPinCorrect(String enteredPin) {
        return this.pin.equals(enteredPin);
    }

    public void setPin(String newPin) {
        this.pin = newPin;
    }

    // ---------- Balance mutation (package-visible logic lives in Bank/ATM,
    // but the actual field update always happens here to keep balance safe) ----------
    public void credit(double amount) {
        this.balance += amount;
    }

    /**
     * Attempts to debit the account. Returns false if funds are insufficient
     * so the caller can show "Insufficient Funds" without throwing.
     */
    public boolean debit(double amount) {
        if (amount > this.balance) {
            return false;
        }
        this.balance -= amount;
        return true;
    }

    public boolean hasSufficientFunds(double amount) {
        return this.balance >= amount;
    }

    @Override
    public String toString() {
        return "Account{id='" + accountId + "', owner='" + ownerName + "', balance=" + balance + "}";
    }
}