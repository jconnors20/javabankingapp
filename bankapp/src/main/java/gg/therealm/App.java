package gg.therealm;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Bank of Walrus Pussy!");
        System.out.println("Thank you for using our CLI banking application.");
        System.out.println("Please enter your name: ");
        String accountName = scanner.nextLine();
        System.out.println("Welcome, " + accountName + "! Please enter your account ID: ");
        String accountId = scanner.nextLine();
        System.out.println("Connecting to account with ID \"" + accountId + "\"...");
        System.out.println("\n");
        Account account = new Account(accountName, accountId);
        System.out.println("Current balance: $" + account.balance);

        char option;
        do {
            System.out.println("Please select an option - ");
            System.out.println("A: Display balance");
            System.out.println("B: Display all transactions");
            System.out.println("C: Make a deposit");
            System.out.println("D: Make a withdrawal");
            System.out.println("E: Exit application");
            option = Character.toUpperCase(scanner.next().charAt(0));

            switch (option) {
                case 'A':
                    System.out.println("\n");
                    double balance = account.getBalance();
                    System.out.println("=======================================");
                    System.out.println("Current balance: $" + balance);
                    System.out.println("=======================================");
                    System.out.println("\n");
                    break;

                case 'B':
                    System.out.println("=======================================");
                    System.out.println("\n");
                    account.printTransactions();
                    System.out.println("\n");
                    System.out.println("=======================================");
                    break;

                case 'C':
                    System.out.println("Enter an amount to deposit: ");
                    String depositInput = scanner.next();

                    try {

                        double amount = Double.parseDouble(depositInput);
                        account.createTransaction("deposit", amount);
                        System.out.println("\n");
                        System.out.println("=======================================");
                        System.out.println("Deposit of $" + amount + " completed!");
                        System.out.println("=======================================");
                        System.out.println("\n");
                    } catch (NumberFormatException e) {

                        System.out.println("Input value was not a number!");
                        System.out.println("\n");

                    }
                    break;

                case 'D':
                    System.out.println("Enter an amount to withdraw: ");
                    String withdrawalInput = scanner.next();

                    try {

                        double amount = Double.parseDouble(withdrawalInput);
                        account.createTransaction("withdrawal", amount);
                        System.out.println("\n");
                        System.out.println("=======================================");
                        System.out.println("Withdrawal of $" + amount + " completed!");
                        System.out.println("=======================================");
                        System.out.println("\n");

                    } catch (NumberFormatException e) {

                        System.out.println("Input value was not a number!");
                        System.out.println("\n");

                    }
                    break;

                case 'E':
                    System.out.println("Exiting application.");
                    scanner.close();
                    break;

                default:
                    System.out.println("Invalid option! Please choose a letter from the menu.");
                    System.out.println("\n");
            }
        } while (option != 'E');
    }
}
