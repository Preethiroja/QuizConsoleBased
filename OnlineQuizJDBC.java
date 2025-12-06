import java.sql.*;
import java.util.Scanner;

public class OnlineQuizJDBC {
    static final String URL = "jdbc:mysql://localhost:3306/quizdb";
    static final String USER = "root";
    static final String PASS = "Preethi@123"; 

    static Scanner sc = new Scanner(System.in);

    // ✅ Connect to database
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // ✅ Login system
    static String login() {
        System.out.print("Username: ");
        String username = sc.next();
        System.out.print("Password: ");
        String password = sc.next();

        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT role FROM users WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("✅ Login successful as " + rs.getString("role"));
                return rs.getString("role");
            } else {
                System.out.println("❌ Invalid login");
                return null;
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    // ✅ Add new question (Admin only)
    static void addQuestion() {
        try (Connection con = getConnection()) {
            sc.nextLine(); // consume newline
            System.out.print("Enter Question: ");
            String question = sc.nextLine();
            System.out.print("Option 1: ");
            String o1 = sc.nextLine();
            System.out.print("Option 2: ");
            String o2 = sc.nextLine();
            System.out.print("Option 3: ");
            String o3 = sc.nextLine();
            System.out.print("Option 4: ");
            String o4 = sc.nextLine();
            System.out.print("Correct Answer (1-4): ");
            int ans = sc.nextInt();

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO questions(question, option1, option2, option3, option4, answer) VALUES(?,?,?,?,?,?)");
            ps.setString(1, question);
            ps.setString(2, o1);
            ps.setString(3, o2);
            ps.setString(4, o3);
            ps.setString(5, o4);
            ps.setInt(6, ans);

            ps.executeUpdate();
            System.out.println("✅ Question added successfully!");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // ✅ Take Quiz (User)
    static void takeQuiz() {
        try (Connection con = getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM questions");

            int score = 0;
            while (rs.next()) {
                System.out.println("\nQ" + rs.getInt("id") + ": " + rs.getString("question"));
                System.out.println("1. " + rs.getString("option1"));
                System.out.println("2. " + rs.getString("option2"));
                System.out.println("3. " + rs.getString("option3"));
                System.out.println("4. " + rs.getString("option4"));
                System.out.print("Your answer: ");
                int ans = sc.nextInt();

                if (ans == rs.getInt("answer")) {
                    score++;
                }
            }
            System.out.println("\n✅ Quiz Completed! Your Score: " + score);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // ✅ View All Questions (Admin)
    static void viewQuestions() {
        try (Connection con = getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM questions");

            System.out.println("\nID\tQuestion\tAnswer");
            System.out.println("--------------------------------------");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" + rs.getString("question") +
                        "\t" + rs.getInt("answer"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // ✅ Delete Question (Admin)
    static void deleteQuestion() {
        try (Connection con = getConnection()) {
            System.out.print("Enter Question ID to delete: ");
            int id = sc.nextInt();

            PreparedStatement ps = con.prepareStatement("DELETE FROM questions WHERE id=?");
            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("✅ Question deleted!");
            else System.out.println("❌ Question not found");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // ✅ Main Menu
    public static void main(String[] args) {
        System.out.println("===== Online Quiz Application =====");
        String role = login();
        if (role == null) return;

        while (true) {
            if (role.equals("admin")) {
                System.out.println("\n1. Add Question");
                System.out.println("2. View All Questions");
                System.out.println("3. Delete Question");
                System.out.println("4. Exit");
                System.out.print("Enter choice: ");
                int ch = sc.nextInt();
                switch (ch) {
                    case 1: addQuestion(); break;
                    case 2: viewQuestions(); break;
                    case 3: deleteQuestion(); break;
                    case 4: System.exit(0);
                    default: System.out.println("❌ Invalid choice");
                }
            } else { // user
                System.out.println("\n1. Take Quiz");
                System.out.println("2. Exit");
                System.out.print("Enter choice: ");
                int ch = sc.nextInt();
                switch (ch) {
                    case 1: takeQuiz(); break;
                    case 2: System.exit(0);
                    default: System.out.println("❌ Invalid choice");
                }
            }
        }
    }
}
