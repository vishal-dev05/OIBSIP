markdown
# Java Console ATM

A console-based ATM simulation built with Java, using OOP principles
(encapsulation, separation of concerns across 5 classes).

## Folder layout

All files sit flat inside this folder (no `src` subfolder):

ATM/
├── Account.java
├── ATM.java
├── Bank.java
├── Main.java
├── Transaction.java
└── README.md


## Classes

| Class         | Responsibility                                                        |
|---------------|------------------------------------------------------------------------|
| `Main`        | Entry point. Seeds sample accounts and starts the ATM.                |
| `Bank`        | Stores all accounts and each account's transaction log; authenticates.|
| `Account`     | A single bank account: id, PIN, owner, balance (encapsulated fields). |
| `Transaction` | An immutable record of one transaction (type, amount, balance after). |
| `ATM`         | Console UI: login flow, menu loop, withdraw/deposit/transfer logic.   |

## Features

- Login with User ID + PIN, max 3 attempts before lockout
- Menu: Transaction History, Withdraw, Deposit, Transfer, Quit
- Balance validation before withdrawal/transfer ("Insufficient Funds")
- All transactions logged in an `ArrayList<Transaction>` per account
- Transfers update both sender and recipient accounts and log both sides

## Sample accounts (seeded in `Main.java`)

| User ID | PIN  | Owner | Starting Balance |
|---------|------|-------|-------------------|
| 1001    | 1234 | Alice | 5000.00           |
| 1002    | 5678 | Bob   | 3000.00           |

## How to run

From inside this folder (`Projects/ATM`):

```powershell
javac *.java -d out
java -cp out Main
```

The same commands work unchanged on macOS/Linux.

## Sample session

Enter User ID: 1001
Enter PIN: 1234
Login successful. Welcome, Alice!

----------------- MAIN MENU -----------------
Current Balance: 5000.00

Transaction History
Withdraw
Deposit
Transfer
Quit
Choose an option: 2
Enter amount to withdraw: 2000
Withdrawal successful. New balance: 3000.00

## Possible extensions (good for a demo / viva)

- Persist accounts/transactions to a file or database instead of in-memory
- PIN change option
- Interest calculation / account types (savings vs current)
- Unit tests with JUnit