/*sample logins:
 *   User ID: 1001   PIN: 1234   (Alice, balance 5000.00)
 *   User ID: 1002   PIN: 5678   (Bob,   balance 3000.00)
 */
public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
 
        bank.addAccount(new Account("1001", "1234", "Alice", 5000.00));
        bank.addAccount(new Account("1002", "5678", "Bob", 3000.00));
 
        ATM atm = new ATM(bank);
        atm.start();
    }
}
