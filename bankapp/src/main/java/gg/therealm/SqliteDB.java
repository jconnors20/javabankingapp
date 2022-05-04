package gg.therealm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class SqliteDB {

    private Connection conn = null;
    private Statement stmt = null;

    SqliteDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            String dbConnectionString = "jdbc:sqlite:db/transactions.db";

            System.out.println("Connecting to SQLite DB with connection string: " + dbConnectionString);
            this.conn = DriverManager.getConnection(dbConnectionString);

            if (!tableExists()) {
                createTransactionTable();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ResultSet queryTransactions(String accountId) {

        ResultSet queryResults = null;
        String sql = "SELECT * FROM transactions WHERE accountId=\"" + accountId + "\"";
        try {

            Statement selectStatement = conn.createStatement();
            queryResults = selectStatement.executeQuery(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return queryResults;
    }

    public void executeInsertQuery(Transaction transaction) {

        String sql = "INSERT INTO transactions(amount, accountId, accountName, transactionType, transactionString) VALUES (?, ?, ?, ?, ?)";

        try {

            PreparedStatement insertStatement = conn.prepareStatement(sql);
            insertStatement.setString(1, transaction.amount);
            insertStatement.setString(2, transaction.accountId);
            insertStatement.setString(3, transaction.accountName);
            insertStatement.setString(4, transaction.transactionType);
            insertStatement.setString(5, transaction.transactionString);
            insertStatement.executeUpdate();
            System.out.println("Transaction completed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean tableExists() {

        boolean exists = false;
        try (ResultSet rs = this.conn.getMetaData().getTables(null, null, "transactions", null)) {
            while (rs.next()) {
                String tName = rs.getString("TABLE_NAME");
                if (tName != null && tName.equals("transactions")) {
                    exists = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }

    public void createTransactionTable() {
        String sql = "CREATE TABLE \"transactions\" ( \"id\" INTEGER NOT NULL UNIQUE, \"amount\"	NUMERIC, \"accountId\"	TEXT, \"accountName\"	TEXT, \"transactionType\"	TEXT, \"transactionString\"	TEXT, PRIMARY KEY(\"id\" AUTOINCREMENT))";

        try{

            Statement stmt = conn.createStatement();
            stmt.execute(sql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
