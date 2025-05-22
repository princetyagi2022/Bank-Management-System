import java.sql.*;
import java.util.Scanner;

public class Banking_Transition {

    static Connection con = null;
    static Statement stmt = null;
    static Scanner sc = new Scanner(System.in);

    // Connect to database
    public static void connectDB() {
        String url = "jdbc:mysql://localhost:3306/bankdb";
        String user = "root";
        String password = "ERprince@12";
 
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            System.out.println("Connected to the database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Authenticate account
    public static boolean authenticate(long accId, String password) throws SQLException {
        String query = "SELECT * FROM accounts WHERE acc_id = ? AND password = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setLong(1, accId);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    // Debit
    public static void debit(long accId, int amount) throws SQLException {
        stmt.executeUpdate("UPDATE accounts SET balance = balance - " + amount + " WHERE acc_id = " + accId);
    }

    // Credit
    public static void credit(long accId, int amount) throws SQLException {
        stmt.executeUpdate("UPDATE accounts SET balance = balance + " + amount + " WHERE acc_id = " + accId);
    }

    // Show account
    public static void showAccount(long accId) throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT * FROM accounts WHERE acc_id = " + accId);
        if (rs.next()) {
            System.out.println("Account ID: " + rs.getLong("acc_id"));
            System.out.println("Username  : " + rs.getString("username"));
            System.out.println("Balance   : " + rs.getDouble("balance"));
        } else {
            System.out.println("Account not found.");
        }
    }

    // Open account
    public static void openAccount() throws SQLException {
        System.out.print("Enter New Account ID: ");
        long accId = sc.nextLong();
        System.out.print("Enter Username: ");
        String username = sc.next();
        System.out.print("Enter Password: ");
        String password = sc.next();
        System.out.print("Enter Initial Balance: ");
        double balance = sc.nextDouble();

        String query = "INSERT INTO accounts (acc_id, username, password, balance) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setLong(1, accId);
        ps.setString(2, username);
        ps.setString(3, password);
        ps.setDouble(4, balance);

        int rows = ps.executeUpdate();
        if (rows > 0) {
            System.out.println("Account created successfully.");
        } else {
            System.out.println("Account creation failed.");
        }
    }

    // Perform transaction
    public static void performTransaction() {
        try {
            System.out.print("Enter sender account ID: ");
            long sender = sc.nextLong();
            System.out.print("Enter sender password: ");
            String senderPass = sc.next();

            if (!authenticate(sender, senderPass)) {
                System.out.println("Authentication failed. Transaction aborted.");
                return;
            }

            System.out.print("Enter receiver account ID: ");
            long receiver = sc.nextLong();
            System.out.print("Enter amount to transfer: ");
            int amount = sc.nextInt();

            con.setAutoCommit(false);

            debit(sender, amount);
            credit(receiver, amount);

            con.commit();
            System.out.println("Transaction successful.\n");

            System.out.println("Updated Sender Account:");
            showAccount(sender);

            System.out.println("\nUpdated Receiver Account:");
            showAccount(receiver);

        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                    System.out.println("Transaction failed. Rolled back.");
                }
            } catch (SQLException ex) {
                System.out.println("Rollback failed: " + ex.getMessage());
            }
            e.printStackTrace();
        }
    }

    // Main
    public static void main(String[] args) {
        connectDB();

        int choice;
        do {
            System.out.println("\n--- Banking Transaction Menu ---");
            System.out.println("1. Debit");
            System.out.println("2. Credit");
            System.out.println("3. Transfer (Transaction)");
            System.out.println("4. Exit");
            System.out.println("5. Open New Account");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            try {
            	 
                switch (choice) {
                    case 1:
                        System.out.print("Enter Account ID to Debit: ");
                        long acc1 = sc.nextLong();
                        System.out.print("Enter Password: ");
                        String pass1 = sc.next();

                        if (!authenticate(acc1, pass1)) {
                            System.out.println("Authentication failed.");
                            break;
                        }

                        System.out.print("Enter Amount to Debit: ");
                        int amt1 = sc.nextInt();
                        debit(acc1, amt1);
                        System.out.println("Amount debited successfully.");
                        showAccount(acc1);
                        break;

                    case 2:
                        System.out.print("Enter Account ID to Credit: ");
                        long acc2 = sc.nextLong();
                        System.out.print("Enter Amount to Credit: ");
                        int amt2 = sc.nextInt();
                        credit(acc2, amt2);
                        System.out.println("Amount credited successfully.");
                        showAccount(acc2);
                        break;

                    case 3:
                        performTransaction();
                        break;

                    case 4:
                        System.out.println("Exiting...");
                        break;

                    case 5:
                        openAccount();
                        break;

                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        } while (choice != 4);

        try {
            if (con != null) con.close();
            if (stmt != null) stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
