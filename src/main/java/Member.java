import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Member {

    /**
     * Updates the personal information of a member in the database.
     *
     * @param memberID The ID of the member whose information is to be updated.
     */
    public static void updatePersonalInformation(int memberID) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter new First Name:");
        String firstName = scanner.nextLine();

        System.out.println("Enter new Last Name:");
        String lastName = scanner.nextLine();

        System.out.println("Enter your Email:");
        String email = scanner.nextLine();

        System.out.println("Enter your Password:");
        String password = scanner.nextLine();

        System.out.println("Enter your Date of Birth in this format (yyyy-mm-dd):");
        LocalDate dob = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dobInput = scanner.nextLine();
        dob = LocalDate.parse(dobInput, formatter);

        System.out.println("Enter your Street Address:");
        String streetName = scanner.nextLine();

        System.out.println("Enter your City:");
        String city = scanner.nextLine();

        System.out.println("Enter your Province:");
        String province = scanner.nextLine();

        System.out.println("Enter your Postal Code:");
        String postalCode = scanner.nextLine();

        String sql = "UPDATE Members SET FirstName = ?, LastName = ?, Email = ?, Password = ?, DoB = ?, StreetName = ?, City = ?, Province = ?, PostalCode = ? WHERE MemberID = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setDate(5, java.sql.Date.valueOf(dob));
            pstmt.setString(6, streetName);
            pstmt.setString(7, city);
            pstmt.setString(8, province);
            pstmt.setString(9, postalCode);
            pstmt.setInt(10, memberID); // This sets the member ID in the SQL query.

            int affectedRows = pstmt.executeUpdate();
            System.out.println(affectedRows + " rows were updated.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Adds fitness goals for a member.
     * @param memberId The ID of the member whose fitness goals are being added or updated.
     */
    public static void addFitnessGoals(int memberId) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your new fitness goals:");
        String fitnessGoals = scanner.nextLine();

        // First, check if a dashboard exists for the MemberID
        String checkSql = "SELECT COUNT(*) FROM PersonalizedDashboards WHERE MemberID = ?";

        // Insert a new dashboard if it doesn't exist
        String insertSql = "INSERT INTO PersonalizedDashboards (MemberID, FitnessGoals) VALUES (?, ?) ON CONFLICT (MemberID) DO NOTHING";

        // Update the dashboard with the new fitness goals
        String updateSql = "UPDATE PersonalizedDashboards SET FitnessGoals = ? WHERE MemberID = ?";

        // Query to retrieve the DashboardID associated with the MemberID
        String retrieveSql = "SELECT DashboardID FROM PersonalizedDashboards WHERE MemberID = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, memberId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // No dashboard exists, insert a new one
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, memberId);
                    insertStmt.setString(2, fitnessGoals);
                    insertStmt.executeUpdate();
                }
            }

            // Update the dashboard with the new fitness goals
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, fitnessGoals);
                updateStmt.setInt(2, memberId);
                updateStmt.executeUpdate();
            }

            // Retrieve and print the DashboardID associated with the fitness goals
            try (PreparedStatement retrieveStmt = conn.prepareStatement(retrieveSql)) {
                retrieveStmt.setInt(1, memberId);
                rs = retrieveStmt.executeQuery();
                if (rs.next()) {
                    int dashboardId = rs.getInt("DashboardID");
                    System.out.println("Your updated fitness goals are associated with Dashboard ID: " + dashboardId);
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Adds or updates health metrics for a member. This method prompts the user to enter their new health metrics (e.g., weight),
     * checks if a personalized dashboard exists for the member, and either inserts or updates a dashboard
     *
     * @param memberId The ID of the member whose health metrics are being added or updated.
     */

    public static void addHealthMetrics(int memberId) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your new Health Metrics: Weight ");
        double healthMetrics = Double.parseDouble(scanner.nextLine()); // Convert String to double

        // First, check if a dashboard exists for the MemberID
        String checkSql = "SELECT COUNT(*) FROM PersonalizedDashboards WHERE MemberID = ?";

        // Insert a new dashboard if it doesn't exist
        String insertSql = "INSERT INTO PersonalizedDashboards (MemberID, HealthMetrics) VALUES (?, ?) ON CONFLICT (MemberID) DO NOTHING";

        // Update the dashboard with the new health metrics
        String updateSql = "UPDATE PersonalizedDashboards SET HealthMetrics = ? WHERE MemberID = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, memberId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // No dashboard exists, insert a new one
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, memberId);
                    insertStmt.setDouble(2, healthMetrics); // Use setDouble for double precision type
                    insertStmt.executeUpdate();
                }
            }

            // Now, update the dashboard with the new health metrics
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setDouble(1, healthMetrics); // Use setDouble for double precision type
                updateStmt.setInt(2, memberId);
                int affectedRows = updateStmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Personalized dashboard updated successfully.");
                } else {
                    System.out.println("Failed to update the personalized dashboard.");
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Interactively checks and updates a user's fitness achievement based on fitness goals set in
     * the PersonalizedDashboard Table
     */
    public static void checkFitnessAchievements() {
        Scanner scanner = new Scanner(System.in);

        // Prompt for member Dashboard ID
        System.out.println("Enter your Dashboard ID to check your fitness goals:");
        int dashboardId = scanner.nextInt(); scanner.nextLine(); // Clear newline character

        String sql = "SELECT FitnessGoals FROM PersonalizedDashboards WHERE DashboardID = ?";
        String updateSql = "UPDATE PersonalizedDashboards SET FitnessAchievements = ? WHERE DashboardID = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dashboardId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String fitnessGoals = rs.getString("FitnessGoals");
                if (fitnessGoals != null && !fitnessGoals.isEmpty()) {
                    System.out.println("Your fitness goals: " + fitnessGoals);
                    System.out.println("Have you achieved any of your fitness goals? (Yes/No)");
                    String response = scanner.nextLine().trim().toLowerCase();

                    // Prepare the update statement within the same try block
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        if ("yes".equals(response)) {
                            System.out.println("Bravo! Keep up the great work!");
                            updateStmt.setString(1, "Goal Achieved");
                            updateStmt.setInt(2, dashboardId);
                            updateStmt.executeUpdate(); // Execute the update
                        } else {
                            System.out.println("It's a work in progress, and the biggest step is the first step. Keep going!");
                            updateStmt.setString(1, "Goal is in progress");
                            updateStmt.setInt(2, dashboardId);
                            updateStmt.executeUpdate(); // Execute the update
                        }
                    } catch (SQLException e) {
                        System.out.println("Failed to update fitness achievements: " + e.getMessage());
                    }
                } else {
                    System.out.println("You haven't set a fitness goal yet.");
                }
            } else {
                System.out.println("No fitness goals found for Dashboard ID: " + dashboardId);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Checks and updates a member's health statistics by comparing their previous weight stored in
     * the database to their current weight.
     */
    public static void checkHealthStatistics() {
        double oldWeight = 0.0;
        Scanner scanner = new Scanner(System.in);

        // prompt user to enter personalized Dashboard ID
        System.out.println("Enter your Dashboard ID:");
        int dashboardID = Integer.parseInt(scanner.nextLine());

        // prompt user to enter their current weight
        System.out.println("Enter your current weight in Kg:");
        double currentWeight = Double.parseDouble(scanner.nextLine());

        // SQL query to select the current HealthMetrics
        String selectSql = "SELECT HealthMetrics FROM PersonalizedDashboards WHERE DashboardID = ?";

        // SQL query to update the Health Statistics
        String updateSql = "UPDATE PersonalizedDashboards SET HealthStatistics = ? WHERE DashboardID = ?";

        // connect to Db and prepare the statements
        try (Connection conn = DBConnection.connect();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            selectStmt.setInt(1, dashboardID);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                // retrieve the old weight from health metrics column
                oldWeight = rs.getDouble("HealthMetrics");
            }
            // calculate wight change
            double weightChange = currentWeight - oldWeight;
            String healthUpdateMessage;

            if (weightChange > 0) {
                System.out.println("You have gained weight: " + weightChange + " kg");
                healthUpdateMessage = "Weight increased by " + weightChange + " kg.";
            } else if (weightChange < 0) {
                System.out.println("You have lost weight: " + Math.abs(weightChange) + " kg");
                healthUpdateMessage = "Weight decreased by " + Math.abs(weightChange) + " kg.";
            } else {
                System.out.println("Your weight has remained the same.");
                healthUpdateMessage = "Weight is the same.";
            }

            // Now update the HealthMetrics in the database
            updateStmt.setString(1, healthUpdateMessage);
            updateStmt.setInt(2, dashboardID);
            int affectedRows = updateStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Health Statistics updated successfully.");
            } else {
                System.out.println("Failed to update health metrics.");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Populates the exercise routine for a member's dashboard. This method automatically assigns a predefined exercise routine
     * to a member's dashboard based on their Dashboard ID.
     *
     * @param dashboardId The Dashboard ID for which the exercise routine is to be populated.
     */

    public static void populateExerciseRoutine(int dashboardId) {
        String routine = "Monday: Cardio, "
                + "Tuesday: Arms, "
                + "Wednesday: Abs and Obliques, "
                + "Thursday: Lower Body, "
                + "Friday: Cardio, "
                + "Saturday and Sunday: Rest";

        String sql = "UPDATE PersonalizedDashboards SET ExerciseRoutine = ? WHERE DashboardID = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, routine);
            pstmt.setInt(2, dashboardId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Exercise routine populated successfully for Dashboard ID: " + dashboardId);
            } else {
                System.out.println("Failed to populate the exercise routine. Dashboard ID may not exist.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Displays the exercise routine associated with a member's dashboard ID. This method first ensures the exercise routine
     * is populated for the given Dashboard ID and then retrieves and displays the routine.
     *
     */
    public static void displayExerciseRoutines() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your Dashboard ID to display your exercise routines:");
        int dashboardId = Integer.parseInt(scanner.nextLine());

        // Ensure the exercise routine is populated for this DashboardID before display
        populateExerciseRoutine(dashboardId);

        String sql = "SELECT ExerciseRoutine FROM PersonalizedDashboards WHERE DashboardID = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dashboardId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String routine = rs.getString("ExerciseRoutine");
                System.out.println("Exercise routine for Dashboard ID " + dashboardId + ":");
                System.out.println(routine);
            } else {
                System.out.println("No exercise routine found for Dashboard ID: " + dashboardId);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Check the availability of a trainer
     * @param trainerId
     * @param sessionTime
     * @return true if trainer is available, ele false
     */
    private static boolean isTrainerAvailable(int trainerId, LocalTime sessionTime) {
        String sql = "SELECT Availability FROM Trainers WHERE TrainerID = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, trainerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Fetch the array of available times
                Time[] timesArray = (Time[])rs.getArray("Availability").getArray();

                // Convert SQL Time array to LocalTime array for comparison
                for (Time sqlTime : timesArray) {
                    LocalTime availableTime = sqlTime.toLocalTime();
                    if (availableTime.equals(sessionTime)) {
                        return true; // The trainer is available at the requested time
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return false; // The trainer is not available at the requested time
    }

    /**
     * Schedules a personal training session for a member with a trainer.
     * Ensures the trainer is available at the requested time before scheduling.
     */
    public static void schedulePersonalTrainingSession(int memberId) {
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        System.out.println("Enter Trainer ID:");
        int trainerId = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter Session Date (yyyy-mm-dd):");
        LocalDate sessionDate = LocalDate.parse(scanner.nextLine(), dateFormatter);

        System.out.println("Enter Session Time (HH:mm):");
        LocalTime sessionTime = LocalTime.parse(scanner.nextLine(), timeFormatter);

        // Check Trainer's Availability
        if (!isTrainerAvailable(trainerId, sessionTime)) {
            System.out.println("Trainer is not available at the specified time. Please choose a different time.");
            return;
        }

        // Schedule the session
        String insertSql = "INSERT INTO PersonalTrainingSessions (SessionSchedule, Time, MemberID, TrainerID) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

            pstmt.setDate(1, java.sql.Date.valueOf(sessionDate));
            pstmt.setTime(2, java.sql.Time.valueOf(sessionTime));
            pstmt.setInt(3, memberId);
            pstmt.setInt(4, trainerId);

            pstmt.executeUpdate();
            System.out.println("Personal training session scheduled successfully.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Allows a member to schedule a group training session by entering a class name.
     */
    public static void scheduleGroupTrainingSession(int memberId) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the name of the class you want to schedule:");
        String className = scanner.nextLine();

        // Start a transaction to ensure both the select and the insert are processed together
        try (Connection conn = DBConnection.connect()) {
            // Disable auto-commit for transaction
            conn.setAutoCommit(false);

            // Look up the class by name to find its ID
            String classSql = "SELECT ClassID FROM Classes WHERE Name = ?";
            int classId = -1;
            try (PreparedStatement classStmt = conn.prepareStatement(classSql)) {
                classStmt.setString(1, className);
                ResultSet classRs = classStmt.executeQuery();

                if (classRs.next()) {
                    classId = classRs.getInt("ClassID");
                } else {
                    System.out.println("No class found with the name: " + className);
                    conn.rollback(); // rollback transaction
                    return;
                }
            }

            // Insert a new record into the Members_Classes table
            String scheduleSql = "INSERT INTO Members_Classes (MemberID, ClassID) VALUES (?, ?)";
            try (PreparedStatement scheduleStmt = conn.prepareStatement(scheduleSql)) {
                scheduleStmt.setInt(1, memberId);
                scheduleStmt.setInt(2, classId);

                int affectedRows = scheduleStmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("You have successfully scheduled the class: " + className);
                    conn.commit(); // commit transaction
                } else {
                    System.out.println("Failed to schedule the class. Please try again.");
                    conn.rollback(); // rollback transaction
                }
            } catch (SQLException e) {
                conn.rollback(); // rollback transaction on error
                System.out.println("Database error: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Displays all available fitness classes from Classes table  Ordered by Name.
     */
    public static void displayAvailableFitnessClasses() {
        String sql = "SELECT Name, Description FROM Classes ORDER BY Name";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Available Fitness Classes:");
            while (rs.next()) {
                String name = rs.getString("Name");
                String description = rs.getString("Description");

                System.out.println("Class Name: " + name);
                System.out.println("Description: " + description);
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int memberId = -1;
        if (args.length > 0) {
            try {
                memberId = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid Member ID passed.");
                return;
            }
        }

        // Check that memberId was successfully retrieved before entering the loop
        if (memberId == -1) {
            System.out.println("Member ID not available. Exiting...");
            scanner.close();
            return;
        }
        try {
            while (true) {
                // Give member their functions  choices
                System.out.println("Please select what would like to do:");
                System.out.println("1. Update personal information");
                System.out.println("2. Add fitness goals");
                System.out.println("3. Add Health Metrics");
                System.out.println("4. Check Fitness Achievements");
                System.out.println("5. Check Health Statistics ");
                System.out.println("6. Schedule personal training sessions");
                System.out.println("7. Display all Available Group Fitness Classes");
                System.out.println("8. Schedule group fitness classes");
                System.out.println("9. Display Exercises Routines");
                System.out.println("10. Logout");

                System.out.print("Enter choice (1-10): ");

                int activityType = scanner.nextInt();
                scanner.nextLine(); // Flush the buffer

                // execute operation based on user choice
                switch (activityType) {
                    case 1:
                        updatePersonalInformation(memberId);
                        break;
                    case 2:
                        addFitnessGoals(memberId);
                        break;
                    case 3:
                        addHealthMetrics(memberId);
                        break;
                    case 4:
                        checkFitnessAchievements();
                        break;
                    case 5:
                        checkHealthStatistics();
                        break;
                    case 6:
                        schedulePersonalTrainingSession(memberId);
                        break;
                    case 7:
                        displayAvailableFitnessClasses();
                        break;
                    case 8:
                        scheduleGroupTrainingSession(memberId);
                        break;
                    case 9:
                        displayExerciseRoutines();
                        break;
                    case 10:
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
