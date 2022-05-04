package gg.therealm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Account {
    private double balance;
    private String accountName;
    private String accountId;
    private String connectionMethod;
    private SqliteDB conn;
    private Path transactionFolderPath;
    private Gson gson;

    public Account() {
        this.gson = new Gson();
    }

    void createTransaction(String transactionType, double amount) {

        String amountString = String.valueOf(amount);

        if (transactionType == "withdrawal") {
            amountString = "-" + amountString;
        }

        Transaction currentTransaction = new Transaction(transactionType, amountString, this.accountId,
                this.accountName);

        if (this.connectionMethod.equals("json")) {
            String withdrawalJson = gson.toJson(currentTransaction);

            try {

                List<Path> files = Files.walk(this.transactionFolderPath).toList();

                int transactionNumber = 1;
                if (!files.isEmpty()) {
                    transactionNumber = files.size();
                }

                String transactionFileName = "transaction" + String.valueOf(transactionNumber) + ".json";
                Path fullFilePath = Paths.get(this.transactionFolderPath.normalize().toString(), transactionFileName);

                Files.write(fullFilePath, withdrawalJson.getBytes());

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (this.connectionMethod.equals("sqlite")) {
            conn.executeInsertQuery(currentTransaction);
        } else {
            System.out.println("Connection method must be set before creating a transaction.");
        }
    }

    double getBalance() {

        double calculatedBalance = 0;

        if (this.connectionMethod.equals("json")) {
            try {
                List<Path> files = Files.walk(this.transactionFolderPath).toList();

                if (files.isEmpty()) {
                    this.balance = 0;
                    return 0;
                }

                List<String> lines;

                for (Path path : files) {

                    if (Files.isDirectory(path)) {
                        continue;
                    }

                    lines = Files.readAllLines(path);

                    StringBuilder currentFileContents = new StringBuilder();
                    for (String line : lines) {
                        currentFileContents.append(line);
                    }

                    JsonObject currentJsonObject = gson.fromJson(currentFileContents.toString(), JsonObject.class);

                    calculatedBalance += currentJsonObject.get("amount").getAsDouble();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (this.connectionMethod.equals("sqlite")) {
            try {

                ResultSet queryResults = this.conn.queryTransactions(this.accountId);

                while (queryResults.next()) {
                    calculatedBalance += Double.valueOf(queryResults.getString("amount"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection method must be set before checking balance.");
            return 0;
        }

        this.balance = calculatedBalance;
        return calculatedBalance;
    }

    void printTransactions() {

        double accountBalance = 0;
        if (this.connectionMethod.equals("json")) {
            try {

                System.out.println("All past transactions: ");

                List<Path> files = Files.walk(this.transactionFolderPath).toList();

                if (files.isEmpty()) {
                    System.out.println("Current account balance: $0.00");
                    return;
                }

                List<String> lines;

                for (Path path : files) {

                    if (Files.isDirectory(path)) {
                        continue;
                    }

                    lines = Files.readAllLines(path);

                    StringBuilder currentFileContents = new StringBuilder();
                    for (String line : lines) {
                        currentFileContents.append(line);
                    }

                    JsonObject currentJsonObject = gson.fromJson(currentFileContents.toString(), JsonObject.class);
                    System.out.println(currentJsonObject.get("transactionString").getAsString());
                    accountBalance += currentJsonObject.get("amount").getAsDouble();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (this.connectionMethod.equals("sqlite")) {
            try {

                ResultSet queryResults = this.conn.queryTransactions(this.accountId);

                while (queryResults.next()) {
                    System.out.println(queryResults.getString("transactionString"));
                    accountBalance += Double.valueOf(queryResults.getString("amount"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection method must be set before accessing transactions.");
        }

        System.out.println("Current account balance: $" + accountBalance);
    }

    public void checkExistingBalance() {
        if (this.connectionMethod.equals("json")) {
            if (Files.isDirectory(this.transactionFolderPath)) {
                try {
                    if (Files.list(this.transactionFolderPath).count() == 0) {
                        this.balance = 0;
                    } else {
                        this.balance = getBalance();
                    }
                } catch (Exception e) {
                    System.out.println("Checking transactions directory encountered errors: " + e.toString());
                }

            } else {
                try {
                    Files.createDirectories(this.transactionFolderPath);
                    this.balance = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (this.connectionMethod.equals("sqlite")) {
            getBalance();
        } else {
            System.out.println("Connection method must be set before accessing transactions.");
        }
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setConnectionMethod(String connectionMethod) {
        if (connectionMethod.equals("B")) {
            this.connectionMethod = "sqlite";
            this.conn = new SqliteDB();
            try {
                boolean tableExists = this.conn.tableExists();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.connectionMethod = "json";
            String currentDirectory = System.getProperty("user.dir");
            String transactionFolder = currentDirectory + "/bankapp/transactions/" + accountId + "/";
            this.transactionFolderPath = Paths.get(transactionFolder);
        }
    }
}
