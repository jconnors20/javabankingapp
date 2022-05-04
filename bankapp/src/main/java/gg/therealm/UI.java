package gg.therealm;

import java.util.Scanner;

public class UI {
    private Account account;
    private Scanner scanner;

    public UI() {
        this.scanner = new Scanner(System.in);
        this.account = new Account();
    }

    public void initializeAccount() {
        System.out.println("Welcome to the Bank of America!");
        System.out.println("Thank you for using our CLI banking application.");
        System.out.println("Please enter your name: ");
        this.account.setAccountName(scanner.nextLine());
        System.out.println("Welcome, " + this.account.getAccountName() + "! Please enter your account ID: ");
        this.account.setAccountId(scanner.nextLine());
        System.out.println("How would you like to store / retrieve transactions?");
        System.out.println(" Please enter A for \"json\" or B for \"sqlite\"");
        this.account.setConnectionMethod(scanner.nextLine());
        System.out.println("Connecting to account with ID \"" + this.account.getAccountId() + "\"...");
        System.out.println("\n");
        this.account.checkExistingBalance();
        System.out.println("Current balance: $" + this.account.getBalance());
    }

    public void displayMenu() {
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
                    System.out.println("=======================================");
                    System.out.println("Current balance: $" + this.account.getBalance());
                    System.out.println("=======================================");
                    System.out.println("\n");
                    break;

                case 'B':
                    System.out.println("=======================================");
                    System.out.println("\n");
                    this.account.printTransactions();
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
