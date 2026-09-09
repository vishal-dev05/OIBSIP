import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the bank: holds all accounts and each account's transaction log.
 * Acts as the "database" layer that ATM talks to.
 */
public class Bank {

    private final Map<String, Account> accounts = new HashMap<>();
    private final Map<String, List<Transaction>> transactionLogs = new HashMap<>();

    public void addAccount(Account account) {
        accounts.put(account.getAccountId(), account);
        transactionLogs.put(account.getAccountId(), new ArrayList<>());
    }

    public boolean accountExists(String accountId) {
        return accounts.containsKey(accountId);
    }

    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    /**
     * Verifies User ID + PIN. Returns the matching Account on success, null otherwise.
     */
    public Account authenticate(String accountId, String pin) {
        Account account = accounts.get(accountId);
        if (account != null && account.isPinCorrect(pin)) {
            return account;
        }
        return null;
    }

    public void logTransaction(String accountId, Transaction transaction) {
        transactionLogs.computeIfAbsent(accountId, k -> new ArrayList<>()).add(transaction);
    }

    public List<Transaction> getTransactionHistory(String accountId) {
        return transactionLogs.getOrDefault(accountId, new ArrayList<>());
    }
}