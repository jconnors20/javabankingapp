package gg.therealm;

public class Transaction {
    String amount;
    String accountId;
    String accountName;
    String transactionType;
    String transactionString;

    public Transaction(String transactionType, String amount, String accountId, String accountName) {
        this.amount = amount;
        this.accountId = accountId;
        this.accountName = accountName;
        this.transactionType = transactionType;
        this.transactionString = transactionType + " of amount $" + amount + " made by " + accountName;
    }
}
