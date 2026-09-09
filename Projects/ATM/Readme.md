# Java Console ATM

A console-based ATM simulation built with Java, using OOP principles
(encapsulation, separation of concerns across 5 classes).

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

```bash
cd src
javac *.java -d ../out
cd ../out
java Main
```

## Possible extensions (good for a demo / viva)

- Persist accounts/transactions to a file or database instead of in-memory
- PIN change option
- Interest calculation / account types (savings vs current)
- Unit tests with JUnit
