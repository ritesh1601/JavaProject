package GroupProject;

import java.sql.*;

public class DB {
    public static String URL = "jdbc:mysql://localhost:3306/MarksManagement?createDatabaseIfNotExist=true&serverTimezone=UTC";
    public static String USER = "root";
    public static String PASS = "12345678";

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void resetDatabase() {
        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.execute("DROP TABLE IF EXISTS attendance");
            st.execute("DROP TABLE IF EXISTS marks");
            st.execute("DROP TABLE IF EXISTS subjects");
            st.execute("DROP TABLE IF EXISTS students");
            st.execute("DROP TABLE IF EXISTS users");
        } catch (Exception e) { System.out.println(e); }
    }

    public static void createTables() {
        try (Connection con = getConnection(); Statement st = con.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS users(
                username VARCHAR(50) PRIMARY KEY,
                password VARCHAR(100) NOT NULL,
                role VARCHAR(30) NOT NULL)
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS students(
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                roll VARCHAR(50) UNIQUE NOT NULL,
                class VARCHAR(50) NOT NULL)
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS subjects(
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) UNIQUE NOT NULL)
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS marks(
                id INT AUTO_INCREMENT PRIMARY KEY,
                roll VARCHAR(50) NOT NULL,
                subject VARCHAR(100) NOT NULL,
                marks INT NOT NULL,
                UNIQUE KEY uniq(roll,subject),
                FOREIGN KEY (roll) REFERENCES students(roll)
                ON DELETE CASCADE ON UPDATE CASCADE)
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS attendance(
                id INT AUTO_INCREMENT PRIMARY KEY,
                roll VARCHAR(50) NOT NULL,
                date DATE NOT NULL,
                status ENUM('Present','Absent') NOT NULL,
                UNIQUE KEY uniq(roll,date),
                FOREIGN KEY (roll) REFERENCES students(roll)
                ON DELETE CASCADE ON UPDATE CASCADE)
            """);

            PreparedStatement ps = con.prepareStatement(
                "INSERT IGNORE INTO users(username,password,role) VALUES (?,?,?)");
            ps.setString(1, "admin"); ps.setString(2, "admin123");
            ps.executeUpdate();
            ps.setString(1, "rabiShaw"); ps.setString(2, "Java");
            ps.executeUpdate();

        } catch (Exception e) { e.printStackTrace(); }
    }
}
