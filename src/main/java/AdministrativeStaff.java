import java.sql.*;
import java.util.Scanner;

public class AdministrativeStaff {


    /**
     * Updates the information of an administrative staff member.
     */
    public static void updateAdministrativeStaffInfo(int staffId) {
        Scanner scanner = new Scanner(System.in);

        scanner.nextLine();

        System.out.println("Enter new First Name:");
        String firstName = scanner.nextLine();

        System.out.println("Enter new Last Name:");
        String lastName = scanner.nextLine();

        System.out.println("Enter new Email:");
        String email = scanner.nextLine();

        System.out.println("Enter new Password:");
        String password = scanner.nextLine();

        String updateSql = "UPDATE AdministrativeStaff SET FirstName = ?, LastName = ?, Email = ?, Password = ? WHERE StaffID = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setInt(5, staffId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Administrative staff info updated successfully.");
            } else {
                System.out.println("No staff found with the given Staff ID: " + staffId);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    public static void bookRoom(int staffId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("List of available rooms:");

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT RoomID, Name FROM Rooms WHERE Availability = TRUE");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int roomId = rs.getInt("RoomID");
                String roomName = rs.getString("Name");
                System.out.println("Room ID: " + roomId + " - Name: " + roomName);
            }

            System.out.println("Enter the ID of the room you wish to book:");
            int roomIdToBook = scanner.nextInt();
            scanner.nextLine(); // Flush the buffer

            // Update room availability and assign the staffId to the room
            String updateSql = "UPDATE Rooms SET Availability = FALSE, StaffID = ? WHERE RoomID = ? AND Availability = TRUE";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, staffId);
                updateStmt.setInt(2, roomIdToBook);

                int affectedRows = updateStmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Room booked successfully.");
                } else {
                    System.out.println("Failed to book the room. It may not exist, is already booked, or an invalid staff ID was provided.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Adds a new piece of equipment to the database for monitoring.
     */
    public static void addEquipment(int staffId) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the type of equipment:");
        String equipmentType = scanner.nextLine();

        System.out.println("Enter a brief description about how old/new the equipment is:");
        String description = scanner.nextLine();

        scanner.nextLine(); // Flush the buffer after reading an integer

        String sql = "INSERT INTO Equipment (EquipmentType, Description, StaffID) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipmentType);
            pstmt.setString(2, description);
            pstmt.setInt(3, staffId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Equipment added successfully for monitoring.");
            } else {
                System.out.println("Failed to add the equipment. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Monitors the status of equipment by listing them and showing which staff member is responsible for them.
     */
    public static void displayMonitoredEquipment() {
        // Query to select all equipment and the staff responsible for them
        String sql = "SELECT e.EquipmentID, e.EquipmentType, e.Description, e.StaffID "
                + "FROM Equipment e "
                + "JOIN AdministrativeStaff s ON e.StaffID = s.StaffID";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Equipment Monitoring:");
            while (rs.next()) {
                int equipmentId = rs.getInt("EquipmentID");
                String equipmentType = rs.getString("EquipmentType");
                String description = rs.getString("Description");
                int staffId = rs.getInt("StaffID");

                System.out.println("Equipment ID: " + equipmentId + " " +
                        "Type: " + equipmentType + " " +
                        "Description: " + description + " " +
                        "Managed by staff with ID: " + staffId);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Updates the schedule for a class by changing its time based on class name provided by the user.
     */
    public static void updateClassSchedule() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the name of the class you want to reschedule:");
        String className = scanner.nextLine();

        // Check if the class exists and retrieve the current scheduled time
        String sql = "SELECT ClassID, ScheduledTime FROM Classes WHERE Name = ?";
        int classId = -1;
        Time currentScheduledTime = null;

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, className);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                classId = rs.getInt("ClassID");
                currentScheduledTime = rs.getTime("ScheduledTime");
                System.out.println("Current scheduled time for " + className + " is: " + currentScheduledTime);
            } else {
                System.out.println("Class not found. Please check the name and try again.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return;
        }

        // Ask for new time
        System.out.println("Enter the new time for the class (HH:MM):");
        String newTimeStr = scanner.nextLine();

        // Update the class schedule with the new time
        String updateSql = "UPDATE Classes SET ScheduledTime = ? WHERE ClassID = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            updateStmt.setTime(1, Time.valueOf(newTimeStr + ":00"));
            updateStmt.setInt(2, classId);

            int affectedRows = updateStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Class schedule updated successfully for " + className + ".");
            } else {
                System.out.println("Failed to update the class schedule. Please ensure the class name is correct and try again.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Processes payment and updates the Billing table.
     * This method assumes a successful payment through an external service.
     */
    public static void processPaymentAndBilling() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Member ID for billing:");
        int memberId = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter Service ID for the payment:");
        int serviceId = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter Payment Method (Credit Card, PayPal, ApplePay):");
        String paymentMethod = scanner.nextLine();

        boolean paymentSuccess = true; // This would be the result of the payment service API call

        if (paymentSuccess) {
            // Initialize outside to be visible in the nested try block
            String serviceDescription = "";

            try (Connection conn = DBConnection.connect()) {
                // Retrieve the service description for the provided service ID
                String serviceSql = "SELECT ServiceDescription FROM Services WHERE ServiceID = ?";

                try (PreparedStatement serviceStmt = conn.prepareStatement(serviceSql)) {
                    serviceStmt.setInt(1, serviceId);
                    ResultSet serviceRs = serviceStmt.executeQuery();
                    if (serviceRs.next()) {
                        serviceDescription = serviceRs.getString("ServiceDescription");
                    } else {
                        System.out.println("Service ID not found.");
                        return;
                    }
                } catch (SQLException e) {
                    System.out.println("Database error when retrieving service: " + e.getMessage());
                    return;
                }

                // Update the Billing table with the payment details
                String billingSql = "INSERT INTO Billing (Date, PaymentMethod, MemberID, ServiceID, Service) VALUES (CURRENT_DATE, ?, ?, ?, ?)";

                try (PreparedStatement billingStmt = conn.prepareStatement(billingSql)) {
                    billingStmt.setString(1, paymentMethod);
                    billingStmt.setInt(2, memberId);
                    billingStmt.setInt(3, serviceId);
                    billingStmt.setString(4, serviceDescription); // Use the retrieved service description

                    int affectedRows = billingStmt.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Payment processed and billing updated successfully.");
                    } else {
                        System.out.println("Failed to update billing. Please try again.");
                    }
                } catch (SQLException e) {
                    System.out.println("Database error when updating billing: " + e.getMessage());
                }
            } catch (SQLException e) {
                System.out.println("Database connection error: " + e.getMessage());
            }
        } else {
            System.out.println("Payment processing failed. Please try again.");
        }
    }



    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int staffId = -1;
        if (args.length > 0) {
            try {
                staffId = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid Member ID passed.");
                return;
            }
        }

        // Check that memberId was successfully retrieved before entering the loop
        if (staffId == -1) {
            System.out.println("Member ID not available. Exiting...");
            scanner.close();
            return;
        }
        try {
            while (true) {
                // Give member their functions  choices
                System.out.println("Please select what would like to do:");
                System.out.println("1. Update Administrative Staff Information");
                System.out.println("2. Book a Room");
                System.out.println("3. Add Equipment to Monitor");
                System.out.println("4. Display List of Monitored Equipment");
                System.out.println("5. Update Class Schedule");
                System.out.println("6. Issue Billing and Payment Processing");
                System.out.println("7. Logout");

                System.out.print("Enter choice (1-7): ");

                int activityType = scanner.nextInt();
                scanner.nextLine(); // Flush the buffer

                // execute operation based on user choice
                switch (activityType) {
                    case 1:
                        updateAdministrativeStaffInfo(staffId);
                        break;
                    case 2:
                        bookRoom(staffId);
                        break;
                    case 3:
                        addEquipment(staffId);
                        break;
                    case 4:
                        displayMonitoredEquipment();
                        break;
                    case 5:
                        updateClassSchedule();
                        break;
                    case 6:
                        processPaymentAndBilling();
                        break;
                    case 7:
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

