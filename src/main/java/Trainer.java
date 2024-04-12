import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Trainer {


    /**
     * Updates the information of a trainer.
     */
    public static void updateTrainerInfo() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the Trainer ID you want to update:");
        int trainerId = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter new First Name:");
        String firstName = scanner.nextLine();

        System.out.println("Enter new Last Name:");
        String lastName = scanner.nextLine();

        System.out.println("Enter new Email:");
        String email = scanner.nextLine();

        System.out.println("Enter new Password:");
        String password = scanner.nextLine();

        System.out.println("Enter availability times (comma-separated, e.g., 09:00:00,13:00:00,18:00:00):");
        String[] availability = scanner.nextLine().split(",");

        String updateSql = "UPDATE Trainers SET FirstName = ?, LastName = ?, Email = ?, Password = ?, Availability = ? WHERE TrainerID = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);

            Array availabilityArray = conn.createArrayOf("TIME", availability);
            pstmt.setArray(5, availabilityArray);
            pstmt.setInt(6, trainerId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Trainer info updated successfully.");
            } else {
                System.out.println("No trainer found with the given Trainer ID: " + trainerId);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    /**
     * Sets the availability times for a trainer. This method prompts the user (trainer) to enter
     * their available times in HH:MM format.
     * The user can enter multiple times and must type 'done' when they have finished entering their times.
     * @param trainerId The unique identifier for the trainer whose availability is being set.
     */

    public static void setAvailability(int trainerId) {
        Scanner scanner = new Scanner(System.in);
        List<String> availabilityTimes = new ArrayList<>();
        System.out.println("Enter your availability times (HH:MM), type 'done' when finished:");

        String time;
        while (!(time = scanner.nextLine()).equalsIgnoreCase("done")) {
            availabilityTimes.add(time);
        }

        // Convert the list of times to an array format suitable for SQL
        String[] timesArray = availabilityTimes.toArray(new String[0]);

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE Trainers SET Availability = ? WHERE TrainerID = ?")) {
            // Use an SQL array for updating the time array column
            java.sql.Array sqlArray = conn.createArrayOf("TIME", timesArray);
            pstmt.setArray(1, sqlArray);
            pstmt.setInt(2, trainerId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Availability updated successfully.");
            } else {
                System.out.println("Failed to update availability.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Displays the profile(s) of member(s) based on a given name.
     * This method prompts the user for a name, searches for members by first or last name,
     * and prints out their information.
     */
    public static void viewMemberProfile() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the name of the member:");
        String name = scanner.nextLine();  // Get the name input from the user

        // Assuming 'name' could match either FirstName or LastName
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM Members WHERE FirstName LIKE ? OR LastName LIKE ?")) {
            pstmt.setString(1, "%" + name + "%");
            pstmt.setString(2, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                // Mark that we've found at least one member
                found = true;

                // Display member details
                System.out.println("MemberID: " + rs.getInt("MemberID"));
                System.out.println("Name: " + rs.getString("FirstName") + " " + rs.getString("LastName"));
                System.out.println("Email: " + rs.getString("Email"));
                // Consider omitting or securely handling sensitive data like passwords
                System.out.println("Date of Birth: " + rs.getString("DoB"));
                System.out.println("Street Name: " + rs.getString("StreetName"));
                System.out.println("City: " + rs.getString("City"));
                System.out.println("Province: " + rs.getString("Province"));
                System.out.println("Postal Code: " + rs.getString("PostalCode"));
                System.out.println("-----------------------------------------");
            }

            if (!found) {
                System.out.println("No member found with the name: " + name);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = "";
        int trainerId = -1;
        if (args.length > 0) {
            try {
                trainerId = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid Member ID passed.");
                return;
            }
        }

        // Check that memberId was successfully retrieved before entering the loop
        if (trainerId == -1) {
            System.out.println("Member ID not available. Exiting...");
            scanner.close();
            return;
        }

        try {
            while (true) {
                // Give member their functions  choices
                System.out.println("Please select what would like to do:");
                System.out.println("1. update Trainer Infomation");
                System.out.println("2. Set your availability");
                System.out.println("3. View member's profile");
                System.out.println("4. Logout");

                System.out.print("Enter choice (1-4): ");

                int activityType = scanner.nextInt();
                scanner.nextLine(); // Flush the buffer

                // execute operation based on user choice
                switch (activityType) {
                    case 1:
                        updateTrainerInfo();
                        break;
                    case 2:
                        setAvailability(trainerId);
                        break;
                    case 3:
                        viewMemberProfile();
                        break;
                    case 4:
                        System.out.println("Logging out...");
                        return; // Exit the method
                    default:
                        System.out.println("Invalid choice selected.");
                        break;
                }

            }
        } finally {
            scanner.close();
        }
    }
}

