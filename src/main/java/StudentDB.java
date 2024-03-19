import java.sql.*;

/**
 * This class is responsible for performing database operations on the 'students' table.
 * It includes methods for retrieving, adding, updating, and deleting student records.
 *
 * @author Rama Alkhouli
 */
public class StudentDB {

    // database connection parameters
    private final String url = "jdbc:postgresql://localhost:5432/DB-A4";
    private final String user = "postgres";
    private final String password = "Aya1985";

    /**
     * Retrieves and displays all records from the students table.
     */
    public void getAllStudents() {
        String SQL = "SELECT * FROM students";

        try (Connection conn = DriverManager.getConnection(url, user, password); // establish connection to the database
             PreparedStatement pstmt = conn.prepareStatement(SQL)) { // prepares the SQL query for execution
            ResultSet x = pstmt.executeQuery(); //executes the query

            while (x.next()) {
                System.out.println("Student ID: " + x.getInt("student_id") + ", First Name: " + x.getString("first_name") + ", Last Name: " + x.getString("last_name") +
                        ", Email: " + x.getString("email") + ", Enrollment Date: " + x.getDate("enrollment_date"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Inserts a new student record into the students table.
     */
    public void addStudent(String first_name, String last_name, String email, String enrollment_date) {
        String SQL = "INSERT INTO students(first_name, last_name, email, enrollment_date) VALUES(?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, first_name);
            pstmt.setString(2, last_name);
            pstmt.setString(3, email);
            pstmt.setDate(4, java.sql.Date.valueOf(enrollment_date));
            pstmt.executeUpdate(); // execute the add query
            System.out.println("Student added successfully!");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Updates the email address for a student with a specific student_id.
     */
    public void updateStudentEmail(int student_id, String new_email) {
        String SQL = "UPDATE students SET email=? WHERE student_id=?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, new_email);
            pstmt.setInt(2, student_id);
            pstmt.executeUpdate();  // execute update query
            System.out.println("Student email updated successfully!");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Deletes the record of the student with the specified student_id.
     */
    public void deleteStudent(int student_id) {
        String SQL = "DELETE FROM students WHERE student_id=?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, student_id);
            pstmt.executeUpdate();
            System.out.println("Student deleted successfully!");

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        // Instantiate the StudentDB
        StudentDB dbOps = new StudentDB();

        // Retrieve and display all students
        System.out.println("Retrieve all students from students table");
        dbOps.getAllStudents();

        // Add a new student
        System.out.println("\nAdding students ...");
        dbOps.addStudent("John", "Doe", "john.doe@example.com", "2023-09-01");
        dbOps.addStudent("Jane", "Smith", "jane.smith@example.com", "2023-09-01");
        dbOps.addStudent("Jim", "Beam", "jim.beam@example.com", "2023-09-02");
        dbOps.addStudent("New", "Student", "new.student2@example.com", "2024-03-18");

        // display students again
        dbOps.getAllStudents();

        // Update a student's email
        System.out.println("\nUpdating a student's email...");
        dbOps.updateStudentEmail(4, "updated.student@example.com");

        // display students relation again
        dbOps.getAllStudents();

         //Delete a student
        System.out.println("\nDeleting a student bases on student ID");
        dbOps.deleteStudent(4);

        // display students relation  with the deleted row
        dbOps.getAllStudents();

    }
}