package gg.therealm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Account {
    double balance;
    String accountName;
    String accountId;
    Path transactionFolderPath;
    Gson gson = new Gson();

    public Account(String accountName, String accountId) {
        this.accountName = accountName;
        this.accountId = accountId;
        String currentDirectory = System.getProperty("user.dir");
        String transactionFolder = currentDirectory + "/bankapp/transactions/" + accountId + "/";
        
        this.transactionFolderPath = Paths.get(transactionFolder);
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
    }

    void createTransaction(String transactionType, double amount) {

        String amountString = String.valueOf(amount);

        if (transactionType == "withdrawal") {
            amountString = "-" + amountString;
        }

        Transaction currentTransaction = new Transaction(transactionType, amountString, this.accountId,
                this.accountName);
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
    }

    double getBalance() {

        double calculatedBalance = 0;

        try {
            List<Path> files = Files.walk(this.transactionFolderPath).toList();

            if (!files.isEmpty()) {

                for (Path path : files) {

                    if (!Files.isDirectory(path)) {
                        List<String> lines = Files.readAllLines(path);

                        StringBuilder currentFileContents = new StringBuilder();
                        for (String line : lines) {
                            currentFileContents.append(line);
                        }

                        JsonObject currentJsonObject = gson.fromJson(currentFileContents.toString(), JsonObject.class);

                        calculatedBalance += currentJsonObject.get("amount").getAsDouble();
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.balance = calculatedBalance;
        return calculatedBalance;
    }

    void printTransactions() {

        try {

            System.out.println("All past transactions: ");
            int accountBalance = 0;

            List<Path> files = Files.walk(this.transactionFolderPath).toList();

            if (!files.isEmpty()) {

                for (Path path : files) {

                    if (!Files.isDirectory(path)) {

                        List<String> lines = Files.readAllLines(path);

                        StringBuilder currentFileContents = new StringBuilder();
                        for (String line : lines) {
                            currentFileContents.append(line);
                        }

                        JsonObject currentJsonObject = gson.fromJson(currentFileContents.toString(), JsonObject.class);
                        System.out.println(currentJsonObject.get("transactionString").getAsString());
                        accountBalance += currentJsonObject.get("amount").getAsDouble();

                    }
                }

            }

            System.out.println("Current account balance: $" + accountBalance);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
