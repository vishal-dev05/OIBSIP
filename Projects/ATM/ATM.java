import java.util.List;
import java.util.Scanner;

/**
 * The ATM "front end": drives login, shows the menu, and performs transactions
 * by delegating actual data changes to Bank/Account.
 */
public class ATM {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private final Bank bank;
    private final Scanner scanner;
    private Account currentAccount;

    public ATM(Bank bank) {
        this.bank = bank;
        this.scanner = new Scanner(System.in);
    }

    /** Entry point for the whole ATM session. */
    public void start() {
        System.out.println("=========================================");
        System.out.println("      WELCOME TO JAVA CONSOLE ATM");
        System.out.println("=========================================");

        if (login()) {
            runMenu();
        } else {
            System.out.println("\nToo many incorrect attempts. Card blocked. Goodbye.");
        }

        scanner.close();
    }

    /** Prompts for User ID and PIN, allowing up to MAX_LOGIN_ATTEMPTS tries. */
    private boolean login() {
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            System.out.print("\nEnter User ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            Account account = bank.authenticate(id, pin);
            if (account != null) {
                currentAccount = account;
                System.out.println("\nLogin successful. Welcome, " + account.getOwnerName() + "!");
                return true;
            }

            int remaining = MAX_LOGIN_ATTEMPTS - attempt;
            if (remaining > 0) {
                System.out.println("Incorrect User ID or PIN. Attempts remaining: " + remaining);
            }
        }
        return false;
    }

    /** Main menu loop, runs until the user chooses Quit. */
    private void runMenu() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewTransactionHistory();
                    break;
                case "2":
                    withdraw();
                    break;
                case "3":
                    deposit();
                    break;
                case "4":
                    transfer();
                    break;
                case "5":
                    System.out.println("\nThank you for using Java Console ATM. Goodbye, "
                            + currentAccount.getOwnerName() + "!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-5.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n----------------- MAIN MENU -----------------");
        System.out.println("Current Balance: " + String.format("%.2f", currentAccount.getBalance()));
        System.out.println("1. Transaction History");
        System.out.println("2. Withdraw");
        System.out.println("3. Deposit");
        System.out.println("4. Transfer");
        System.out.println("5. Quit");
        System.out.print("Choose an option: ");
    }

    private void viewTransactionHistory() {
        List<Transaction> history = bank.getTransactionHistory(currentAccount.getAccountId());
        System.out.println("\n------------- TRANSACTION HISTORY -------------");
        if (history.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        for (Transaction t : history) {
            System.out.println(t);
        }
    }

    private void withdraw() {
        double amount = promptForAmount("Enter amount to withdraw: ");
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }
        if (!currentAccount.hasSufficientFunds(amount)) {
            System.out.println("Insufficient Funds");
            return;
        }
        currentAccount.debit(amount);
        Transaction t = new Transaction(Transaction.Type.WITHDRAWAL, amount,
                currentAccount.getBalance(), "Cash withdrawal");
        bank.logTransaction(currentAccount.getAccountId(), t);
        System.out.println("Withdrawal successful. New balance: "
                + String.format("%.2f", currentAccount.getBalance()));
    }

    private void deposit() {
        double amount = promptForAmount("Enter amount to deposit: ");
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }
        currentAccount.credit(amount);
        Transaction t = new Transaction(Transaction.Type.DEPOSIT, amount,
                currentAccount.getBalance(), "Cash deposit");
        bank.logTransaction(currentAccount.getAccountId(), t);
        System.out.println("Deposit successful. New balance: "
                + String.format("%.2f", currentAccount.getBalance()));
    }

    private void transfer() {
        System.out.print("Enter recipient Account ID: ");
        String recipientId = scanner.nextLine().trim();

        if (recipientId.equals(currentAccount.getAccountId())) {
            System.out.println("Cannot transfer to your own account.");
            return;
        }
        if (!bank.accountExists(recipientId)) {
            System.out.println("Recipient account not found.");
            return;
        }

        double amount = promptForAmount("Enter amount to transfer: ");
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }
        if (!currentAccount.hasSufficientFunds(amount)) {
            System.out.println("Insufficient Funds");
            return;
        }

        Account recipient = bank.getAccount(recipientId);
        currentAccount.debit(amount);
        recipient.credit(amount);

        Transaction outTx = new Transaction(Transaction.Type.TRANSFER_OUT, amount,
                currentAccount.getBalance(), "Transfer to " + recipientId);
        Transaction inTx = new Transaction(Transaction.Type.TRANSFER_IN, amount,
                recipient.getBalance(), "Transfer from " + currentAccount.getAccountId());

        bank.logTransaction(currentAccount.getAccountId(), outTx);
        bank.logTransaction(recipientId, inTx);

        System.out.println("Transfer successful. New balance: "
                + String.format("%.2f", currentAccount.getBalance()));
    }

    /** Reads and validates a numeric amount from the console. */
    private double promptForAmount(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim();
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
            return -1;
        }
    }
}