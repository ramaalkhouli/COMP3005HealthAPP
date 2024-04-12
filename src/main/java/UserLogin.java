import java.sql.*;
import java.util.Scanner;

public class UserLogin {


    /**
     * Saves user registration info to the database and returns the generated user ID.
     *
     * @param userType The type of the user (1 for member, 2 for trainer, 3 for admin staff).
     * @param email    The email of the user to register.
     * @param password The password of the user to register.
     * @return The generated user ID, or -1 if registration failed.
     */
    private static int saveToDatabase(int userType, String email, String password) {
        String insertSQL = "";
        int userId = -1;
        switch (userType) {
            case 1:
                insertSQL = "INSERT INTO Members (email, password) VALUES (?, ?)";
                break;
            case 2:
                insertSQL = "INSERT INTO Trainers (email, password) VALUES (?, ?)";
                break;
            case 3:
                insertSQL = "INSERT INTO AdministrativeStaff (email, password) VALUES (?, ?)";
                break;
        }

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Registration successful!");

                // Get the generated user ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1); // Retrieve the generated key
                    }
                }
            } else {
                System.out.println("A problem occurred during registration.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return userId; // Return the user ID or -1 if registration failed
    }


    /**
     *  Handles the login process.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int userType = 0;

        while (true) {
            // Ask for the type of user
            System.out.println("Welcome to RA Health Management!");
            System.out.println("Please select which type of user you are:");
            System.out.println("1. Member");
            System.out.println("2. Trainer");
            System.out.println("3. Administrative Staff");
            System.out.print("Enter choice (1-3): ");

            if (scanner.hasNextInt()) {
                userType = scanner.nextInt();
                scanner.nextLine(); // Consume newline left-over
                if (userType >= 1 && userType <= 3) {
                    break; // Exit loop if valid
                }
            } else {
                scanner.nextLine(); // Consume the invalid input
            }

            System.out.println("Invalid input. Please enter 1, 2, or 3.");
        }

        // Initialize variables for email and password
        String email, password;

        // Register user
        System.out.println("------ Registration ------");
        System.out.print("Enter email: ");
        email = scanner.nextLine();
        System.out.print("Enter password: ");
        password = scanner.nextLine();

        // Register user and retrieve the ID
        int userId = saveToDatabase(userType, email, password);

        // saving to database and redirecting to appropriate process
        switch (userType) {
            case 1:
                // Redirect or initiate the Member-specific process
                Member.main(new String[]{String.valueOf(userId)});
                break;
            case 2:
                // Redirect or initiate the Trainer-specific process
                Trainer.main(new String[]{String.valueOf(userId)});
                break;
            case 3:
                // Redirect or initiate the AdministrativeStaff-specific process
                AdministrativeStaff.main(new String[]{String.valueOf(userId)});
                break;
            default:
                System.out.println("An unexpected error occurred.");
                break;
        }

        // Close scanner
        scanner.close();
    }

}
