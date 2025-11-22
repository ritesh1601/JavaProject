import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/**
 * Single-file Marks Management System
 * Main class: MarksManagement
 *
 * Developer-named frames:
 * Ritesh_LoginFrame
 * Yachika_DashboardFrame
 * Ramanshu_AddStudentFrame
 * Rohit_AddSubjectFrame
 * Arnav_MarksEntryFrame
 * Mayank_ViewMarksFrame
 * Ritik_ResultFrame
 * Taniya_ClassReportFrame
 * Sandeep_AttendanceFrame
 * Shikhar_DisplayGradeFrame
 * Ramanshu_ImportExportFrame (import/export functionality)
 *
 * Java 21-compatible code (avoids preview-only features)
 */
public class MarksManagement {

    // ---------- DATABASE CONNECTION ----------
    static class DB {
        // change URL, user, pass if needed
        static final String URL = "jdbc:mysql://localhost:3306/MarksManagement?createDatabaseIfNotExist=true&serverTimezone=UTC";
        static final String USER = "root";
        static final String PASS = "12345678";

        static Connection getConnection() throws Exception {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        }

        // Reset (drop) tables
        static void resetDatabase() {
            try (Connection con = getConnection();
                 Statement st = con.createStatement()) {
                st.executeUpdate("DROP TABLE IF EXISTS attendance");
                st.executeUpdate("DROP TABLE IF EXISTS marks");
                st.executeUpdate("DROP TABLE IF EXISTS subjects");
                st.executeUpdate("DROP TABLE IF EXISTS students");
                st.executeUpdate("DROP TABLE IF EXISTS users");
                System.out.println("Dropped old tables (if any).");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Create required tables and seed two users (admin & teacher)
        static void createTables() {
            try (Connection con = getConnection();
                 Statement st = con.createStatement()) {

                st.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                        "username VARCHAR(50) PRIMARY KEY, " +
                        "password VARCHAR(100) NOT NULL, " +
                        "role VARCHAR(30) NOT NULL)");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS students (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "roll VARCHAR(50) UNIQUE NOT NULL, " +
                        "class VARCHAR(50) NOT NULL)");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS subjects (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(100) UNIQUE NOT NULL)");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS marks (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "roll VARCHAR(50) NOT NULL, " +
                        "subject VARCHAR(100) NOT NULL, " +
                        "marks INT NOT NULL, " +
                        "UNIQUE KEY uniq_roll_subject (roll,subject), " +
                        "FOREIGN KEY (roll) REFERENCES students(roll) ON DELETE CASCADE ON UPDATE CASCADE)");

                st.executeUpdate("CREATE TABLE IF NOT EXISTS attendance (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "roll VARCHAR(50) NOT NULL, " +
                        "date DATE NOT NULL, " +
                        "status ENUM('Present','Absent') NOT NULL, " +
                        "FOREIGN KEY (roll) REFERENCES students(roll) ON DELETE CASCADE ON UPDATE CASCADE, " +
                        "UNIQUE KEY uniq_att (roll,date))");

                // seed users if not present
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT IGNORE INTO users(username,password,role) VALUES (?,?,?)")) {
                    ps.setString(1, "admin");
                    ps.setString(2, "admin123");
                    ps.setString(3, "admin");
                    ps.executeUpdate();

                    ps.setString(1, "teacher1");
                    ps.setString(2, "teach123");
                    ps.setString(3, "teacher");
                    ps.executeUpdate();
                }

                System.out.println("Tables created and users seeded (if not present).");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ---------- LOGIN (Ritesh) ----------
    public static class Ritesh_LoginFrame extends JFrame implements ActionListener {
        JTextField tfUser;
        JPasswordField pfPass;
        JButton btnLogin, btnExit;

        public Ritesh_LoginFrame() {
            super("Marks Management - Login (Ritesh)");
            tfUser = new JTextField(15);
            pfPass = new JPasswordField(15);
            btnLogin = new JButton("Login");
            btnExit = new JButton("Exit");
            btnLogin.addActionListener(this);
            btnExit.addActionListener(this);

            setLayout(new GridLayout(4, 1));
            JPanel p;
            p = new JPanel(); p.add(new JLabel("Username:")); p.add(tfUser); add(p);
            p = new JPanel(); p.add(new JLabel("Password:")); p.add(pfPass); add(p);
            p = new JPanel(); p.add(btnLogin); p.add(btnExit); add(p);

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(360, 200);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if (cmd.equals("Exit")) System.exit(0);

            if (cmd.equals("Login")) {
                String user = tfUser.getText().trim();
                String pass = new String(pfPass.getPassword());

                boolean ok = false;
                String role = "";
                try (Connection con = DB.getConnection();
                     PreparedStatement ps = con.prepareStatement("SELECT password, role FROM users WHERE username=?")) {
                    ps.setString(1, user);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String dbPass = rs.getString(1);
                            role = rs.getString(2);
                            if (dbPass.equals(pass)) ok = true;
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
                }

                if (ok) {
                    dispose();
                    new Yachika_DashboardFrame(user, role);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Credentials");
                }
            }
        }
    }

    // ---------- DASHBOARD (Yachika) ----------
    public static class Yachika_DashboardFrame extends JFrame implements ActionListener {
        String username, role;
        public Yachika_DashboardFrame(String username, String role) {
            super("Dashboard - " + username + " (Yachika)");
            this.username = username; this.role = role;
            setLayout(new GridLayout(6, 2, 5, 5));

            addButton("Add Student (Ramanshu)");
            addButton("View Students (Mayank)");
            addButton("Add Subject (Rohit)");
            addButton("Enter Marks (Arnav)");
            addButton("View Marks (Mayank)");
            addButton("Generate Result (Ritik)");
            addButton("Class Report (Taniya)");
            addButton("Attendance (Sandeep)");
            addButton("Display Grade (Shikhar)");
            addButton("Import/Export (Ramanshu)");
            addButton("Reset DB (Admin only)");
            addButton("Logout");

            setSize(600, 400);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }

        void addButton(String label) {
            JButton b = new JButton(label);
            b.addActionListener(this);
            add(b);
        }

        public void actionPerformed(ActionEvent e) {
            String c = e.getActionCommand();
            if (c.startsWith("Add Student")) { dispose(); new Ramanshu_AddStudentFrame(username, role); }
            else if (c.startsWith("View Students")) { dispose(); new Mayank_ViewMarksFrame(username, role); }
            else if (c.startsWith("Add Subject")) { dispose(); new Rohit_AddSubjectFrame(username, role); }
            else if (c.startsWith("Enter Marks")) { dispose(); new Arnav_MarksEntryFrame(username, role); }
            else if (c.equals("View Marks (Mayank)")) { dispose(); new Mayank_ViewMarksFrame(username, role); }
            else if (c.startsWith("Generate Result")) { dispose(); new Ritik_ResultFrame(username, role); }
            else if (c.startsWith("Class Report")) { dispose(); new Taniya_ClassReportFrame(username, role); }
            else if (c.startsWith("Attendance")) { dispose(); new Sandeep_AttendanceFrame(username, role); }
            else if (c.startsWith("Display Grade")) { dispose(); new Shikhar_DisplayGradeFrame(username, role); }
            else if (c.startsWith("Import/Export")) { dispose(); new Ramanshu_ImportExportFrame(username, role); }
            else if (c.startsWith("Reset DB")) {
                int r = JOptionPane.showConfirmDialog(this, "Drop and recreate tables? (Requires DB access)", "Confirm", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    DB.resetDatabase();
                    DB.createTables();
                    JOptionPane.showMessageDialog(this, "DB reset and tables created.");
                }
            } else if (c.equals("Logout")) { dispose(); new Ritesh_LoginFrame(); }
        }
    }

    // ---------- ADD STUDENT (Ramanshu) ----------
    public static class Ramanshu_AddStudentFrame extends JFrame implements ActionListener {
        JTextField tfName, tfRoll, tfClass;
        JButton btnAdd, btnBack;
        String user, role;

        public Ramanshu_AddStudentFrame(String user, String role) {
            super("Add Student (Ramanshu)");
            this.user = user; this.role = role;
            tfName = new JTextField(20);
            tfRoll = new JTextField(20);
            tfClass = new JTextField(10);
            btnAdd = new JButton("Add Student");
            btnBack = new JButton("Back");
            btnAdd.addActionListener(this);
            btnBack.addActionListener(a -> { dispose(); new Yachika_DashboardFrame(user, role); });

            JPanel p = new JPanel(new GridLayout(4,2,5,5));
            p.add(new JLabel("Name:")); p.add(tfName);
            p.add(new JLabel("Roll No:")); p.add(tfRoll);
            p.add(new JLabel("Class:")); p.add(tfClass);
            p.add(btnAdd); p.add(btnBack);
            add(p);

            setSize(450,220);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            String name = tfName.getText().trim();
            String roll = tfRoll.getText().trim();
            String cls = tfClass.getText().trim();
            if (name.isEmpty() || roll.isEmpty() || cls.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields required.");
                return;
            }
            try (Connection con = DB.getConnection()) {
                // check roll exists
                try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM students WHERE roll=?")) {
                    ps.setString(1, roll);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(this, "Roll number already exists.");
                            return;
                        }
                    }
                }
                // insert
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO students(name,roll,class) VALUES(?,?,?)")) {
                    ps.setString(1, name);
                    ps.setString(2, roll);
                    ps.setString(3, cls);
                    ps.executeUpdate();
                }
                // For existing subjects, insert default marks = -1 for this new student
                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery("SELECT name FROM subjects")) {
                    List<String> subs = new ArrayList<>();
                    while (rs.next()) subs.add(rs.getString(1));
                    for (String s : subs) {
                        try (PreparedStatement ps = con.prepareStatement(
                                "INSERT IGNORE INTO marks(roll,subject,marks) VALUES(?,?,?)")) {
                            ps.setString(1, roll);
                            ps.setString(2, s);
                            ps.setInt(3, -1);
                            ps.executeUpdate();
                        }
                    }
                }

                JOptionPane.showMessageDialog(this, "Student added successfully.");
                tfName.setText(""); tfRoll.setText(""); tfClass.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        }
    }

    // ---------- ADD SUBJECT (Rohit) ----------
    public static class Rohit_AddSubjectFrame extends JFrame implements ActionListener {
        JTextField tfSubject;
        JButton btnAdd, btnBack;
        String user, role;

        public Rohit_AddSubjectFrame(String user, String role) {
            super("Add Subject (Rohit)");
            this.user = user; this.role = role;
            tfSubject = new JTextField(20);
            btnAdd = new JButton("Add Subject");
            btnBack = new JButton("Back");
            btnAdd.addActionListener(this);
            btnBack.addActionListener(a -> { dispose(); new Yachika_DashboardFrame(user, role); });

            JPanel p = new JPanel();
            p.add(new JLabel("Subject Name:")); p.add(tfSubject);
            p.add(btnAdd); p.add(btnBack);
            add(p);

            setSize(420,140);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            String subj = tfSubject.getText().trim();
            if (subj.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter subject name.");
                return;
            }
            try (Connection con = DB.getConnection()) {
                // insert subject
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO subjects(name) VALUES(?)")) {
                    ps.setString(1, subj);
                    ps.executeUpdate();
                } catch (SQLException sq) {
                    JOptionPane.showMessageDialog(this, "Subject might already exist: " + sq.getMessage());
                    return;
                }

                // for all students, insert default marks = -1
                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery("SELECT roll FROM students")) {
                    List<String> rolls = new ArrayList<>();
                    while (rs.next()) rolls.add(rs.getString(1));
                    for (String r : rolls) {
                        try (PreparedStatement ps = con.prepareStatement(
                                "INSERT IGNORE INTO marks(roll,subject,marks) VALUES(?,?,?)")) {
                            ps.setString(1, r);
                            ps.setString(2, subj);
                            ps.setInt(3, -1);
                            ps.executeUpdate();
                        }
                    }
                }

                JOptionPane.showMessageDialog(this, "Subject added and default marks inserted for all students.");
                tfSubject.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        }
    }

    // ---------- ENTER MARKS (Arnav) ----------
    public static class Arnav_MarksEntryFrame extends JFrame implements ActionListener {
        JTextField tfRoll, tfSubject, tfMarks;
        JButton btnSave, btnBack;
        String user, role;

        public Arnav_MarksEntryFrame(String user, String role) {
            super("Enter Marks (Arnav)");
            this.user = user; this.role = role;

            tfRoll = new JTextField(10);
            tfSubject = new JTextField(15);
            tfMarks = new JTextField(5);
            btnSave = new JButton("Save / Update");
            btnBack = new JButton("Back");

            btnSave.addActionListener(this);
            btnBack.addActionListener(a -> { dispose(); new Yachika_DashboardFrame(user, role); });

            JPanel p = new JPanel();
            p.add(new JLabel("Roll:")); p.add(tfRoll);
            p.add(new JLabel("Subject:")); p.add(tfSubject);
            p.add(new JLabel("Marks:")); p.add(tfMarks);
            p.add(btnSave); p.add(btnBack);
            add(p);

            setSize(600,140);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            String roll = tfRoll.getText().trim();
            String subject = tfSubject.getText().trim();
            String marksStr = tfMarks.getText().trim();
            if (roll.isEmpty() || subject.isEmpty() || marksStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields required.");
                return;
            }
            int marks;
            try { marks = Integer.parseInt(marksStr); }
            catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Marks must be integer."); return; }

            try (Connection con = DB.getConnection()) {
                // check student exists
                try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM students WHERE roll=?")) {
                    ps.setString(1, roll);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            JOptionPane.showMessageDialog(this, "Student with roll " + roll + " does not exist.");
                            return;
                        }
                    }
                }
                // check subject exists
                try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM subjects WHERE name=?")) {
                    ps.setString(1, subject);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            JOptionPane.showMessageDialog(this, "Subject does not exist. Add it first.");
                            return;
                        }
                    }
                }

                // insert or update marks
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO marks(roll,subject,marks) VALUES(?,?,?) ON DUPLICATE KEY UPDATE marks=?")) {
                    ps.setString(1, roll);
                    ps.setString(2, subject);
                    ps.setInt(3, marks);
                    ps.setInt(4, marks);
                    ps.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Marks saved.");
                tfRoll.setText(""); tfSubject.setText(""); tfMarks.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        }
    }

    // ---------- VIEW MARKS & STUDENTS (Mayank) ----------
    public static class Mayank_ViewMarksFrame extends JFrame implements ActionListener {
        JTextField tfFilter;
        JTextArea ta;
        JButton btnRefresh, btnBack, btnFilter;
        String user, role;

        public Mayank_ViewMarksFrame(String user, String role) {
            super("View Students & Marks (Mayank)");
            this.user = user; this.role = role;

            tfFilter = new JTextField(20);
            ta = new JTextArea(20, 70);
            ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            btnRefresh = new JButton("Refresh");
            btnFilter = new JButton("Filter (roll/class)");
            btnBack = new JButton("Back");

            btnRefresh.addActionListener(this);
            btnFilter.addActionListener(this);
            btnBack.addActionListener(a -> { dispose(); new Yachika_DashboardFrame(user, role); });

            JPanel top = new JPanel();
            top.add(new JLabel("Filter:")); top.add(tfFilter);
            top.add(btnFilter); top.add(btnRefresh); top.add(btnBack);

            add(top, BorderLayout.NORTH);
            add(new JScrollPane(ta), BorderLayout.CENTER);

            setSize(900, 600);
            setLocationRelativeTo(null);
            setVisible(true);

            loadAll("");
        }

        public void actionPerformed(ActionEvent e) {
            String c = e.getActionCommand();
            if (c.equals("Refresh")) loadAll("");
            else if (c.equals("Filter (roll/class)")) loadAll(tfFilter.getText().trim());
            else { /* handled by back button */ }
        }

        void loadAll(String filter) {
            ta.setText("");
            try (Connection con = DB.getConnection()) {
                // get list of subjects for columns
                List<String> subjects = new ArrayList<>();
                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery("SELECT name FROM subjects ORDER BY name")) {
                    while (rs.next()) subjects.add(rs.getString(1));
                }

                // header
                StringBuilder hdr = new StringBuilder();
                hdr.append(String.format("%-6s %-25s %-10s", "Roll", "Name", "Class"));
                for (String s : subjects) hdr.append(String.format(" %-10s", s.length() > 9 ? s.substring(0,9) : s));
                hdr.append("\n");
                ta.append(hdr.toString());
                ta.append("-".repeat(Math.max(0, hdr.length())));
                ta.append("\n");


                // students
                String q = "SELECT roll,name,class FROM students";
                if (!filter.isBlank()) {
                    q += " WHERE roll LIKE ? OR class LIKE ?";
                }
                try (PreparedStatement ps = con.prepareStatement(q)) {
                    if (!filter.isBlank()) {
                        ps.setString(1, "%" + filter + "%");
                        ps.setString(2, "%" + filter + "%");
                    }
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String roll = rs.getString("roll");
                            String name = rs.getString("name");
                            String cls = rs.getString("class");
                            StringBuilder line = new StringBuilder();
                            line.append(String.format("%-6s %-25s %-10s", roll, name, cls));

                            // for each subject, get mark
                            for (String s : subjects) {
                                try (PreparedStatement ps2 = con.prepareStatement(
                                        "SELECT marks FROM marks WHERE roll=? AND subject=?")) {
                                    ps2.setString(1, roll);
                                    ps2.setString(2, s);
                                    try (ResultSet rs2 = ps2.executeQuery()) {
                                        if (rs2.next()) {
                                            int m = rs2.getInt(1);
                                            String out = (m < 0 ? "-" : String.valueOf(m));
                                            line.append(String.format(" %-10s", out));
                                        } else {
                                            line.append(String.format(" %-10s", "-"));
                                        }
                                    }
                                }
                            }
                            ta.append(line.toString());
                            ta.append("\n");

                        }
                    }
                }
            } catch (Exception ex) {
                ta.setText("DB Error: " + ex.getMessage());
            }
        }
    }

    // ---------- RESULT GENERATION (Ritik) ----------
    public static class Ritik_ResultFrame extends JFrame implements ActionListener {
        JTextField tfRoll;
        JTextArea ta;
        JButton btnCalc, btnBack;
        String user, role;

        public Ritik_ResultFrame(String user, String role) {
            super("Generate Result (Ritik)");
            this.user = user; this.role = role;

            tfRoll = new JTextField(15);
            ta = new JTextArea(15, 50);
            btnCalc = new JButton("Calculate Result");
            btnBack = new JButton("Back");

            btnCalc.addActionListener(this);
            btnBack.addActionListener(a -> { dispose(); new Yachika_DashboardFrame(user, role); });

            JPanel p = new JPanel(); p.add(new JLabel("Roll:")); p.add(tfRoll); p.add(btnCalc); p.add(btnBack);
            add(p, BorderLayout.NORTH);
            add(new JScrollPane(ta), BorderLayout.CENTER);

            setSize(600, 400);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Calculate Result")) {
                String roll = tfRoll.getText().trim();
                if (roll.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter roll"); return; }
                try (Connection con = DB.getConnection()) {
                    // get marks where marks > 0
                    try (PreparedStatement ps = con.prepareStatement("SELECT subject,marks FROM marks WHERE roll=? AND marks>0")) {
                        ps.setString(1, roll);
                        try (ResultSet rs = ps.executeQuery()) {
                            int total = 0, count = 0;
                            StringBuilder sb = new StringBuilder();
                            while (rs.next()) {
                                String subj = rs.getString(1);
                                int m = rs.getInt(2);
                                sb.append(subj).append(" = ").append(m).append("\n");
                                total += m; count++;
                            }
                            sb.append("\nSubjects entered: ").append(count);
                            if (count > 0) {
                                double pct = (double) total / count;
                                sb.append("\nTotal = ").append(total).append("\nPercentage (avg) = ").append(String.format("%.2f", pct));
                                // grade for average
                                sb.append("\nGrade = ").append(calculateGrade((int)Math.round(pct)));
                            } else {
                                sb.append("\nNo marks entered (>0) for this student.");
                            }
                            ta.setText(sb.toString());
                        }
                    }
                } catch (Exception ex) {
                    ta.setText("DB Error: " + ex.getMessage());
                }
            }
        }
    }

    // ---------- CLASS REPORT (Taniya) ----------
    public static class Taniya_ClassReportFrame extends JFrame implements ActionListener {
        JTextArea ta;
        JButton btnTopper, btnAvg, btnBack;
        String user, role;
        public Taniya_ClassReportFrame(String user, String role) {
            super("Class Report (Taniya)");
            this.user = user; this.role = role;
            ta = new JTextArea(20, 60);
            ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            btnTopper = new JButton("Show Subject Toppers");
            btnAvg = new JButton("Show Subject Averages");
            btnBack = new JButton("Back");
            btnTopper.addActionListener(this);
            btnAvg.addActionListener(this);
            btnBack.addActionListener(a -> { dispose(); new Yachika_DashboardFrame(user, role); });

            JPanel p = new JPanel();
            p.add(btnTopper); p.add(btnAvg); p.add(btnBack);
            add(p, BorderLayout.NORTH);
            add(new JScrollPane(ta), BorderLayout.CENTER);

            setSize(800,600);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            String c = e.getActionCommand();
            if (c.equals("Show Subject Toppers")) showToppers();
            else if (c.equals("Show Subject Averages")) showAverages();
        }

        void showToppers() {
            ta.setText("");
            try (Connection con = DB.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT name FROM subjects")) {
                while (rs.next()) {
                    String subj = rs.getString(1);
                    try (PreparedStatement ps = con.prepareStatement(
                            "SELECT roll,marks FROM marks WHERE subject=? AND marks>=0 ORDER BY marks DESC LIMIT 1")) {
                        ps.setString(1, subj);
                        try (ResultSet rs2 = ps.executeQuery()) {
                            if (rs2.next()) {
                                ta.append(String.format("Topper in %-20s : %s (%d)\n", subj, rs2.getString(1), rs2.getInt(2)));
                            } else {
                                ta.append(String.format("Topper in %-20s : No marks yet\n", subj));
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ta.setText("DB Error: " + ex.getMessage());
            }
        }

        void showAverages() {
            ta.setText("");
            try (Connection con = DB.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT name FROM subjects")) {
                while (rs.next()) {
                    String subj = rs.getString(1);
                    try (PreparedStatement ps = con.prepareStatement(
                            "SELECT AVG(marks) FROM marks WHERE subject=? AND marks>=0")) {
                        ps.setString(1, subj);
                        try (ResultSet rs2 = ps.executeQuery()) {
                            if (rs2.next()) {
                                double avg = rs2.getDouble(1);
                                if (rs2.wasNull()) ta.append(String.format("Average for %-20s : No marks\n", subj));
                                else ta.append(String.format("Average for %-20s : %.2f\n", subj, avg));
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ta.setText("DB Error: " + ex.getMessage());
            }
        }
    }

    // ---------- ATTENDANCE (Sandeep) ----------
    public static class Sandeep_AttendanceFrame extends JFrame implements ActionListener {
        JTextField tfRoll;
        JComboBox<String> cbStatus;
        JTextField tfDate;
        JTextArea ta;
        JButton btnMark, btnView, btnBack;
        String user, role;

        public Sandeep_AttendanceFrame(String user, String role) {
            super("Attendance (Sandeep)");
            this.user = user; this.role = role;

            tfRoll = new JTextField(10);
            cbStatus = new JComboBox<>(new String[]{"Present", "Absent"});
            tfDate = new JTextField(LocalDate.now().toString(), 10); // yyyy-MM-dd
            ta = new JTextArea(15, 50);
            ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

            btnMark = new JButton("Mark Attendance");
            btnView = new JButton("View Monthly");
            btnBack = new JButton("Back");
            btnMark.addActionListener(this);
            btnView.addActionListener(this);
            btnBack.addActionListener(a -> { dispose(); new Yachika_DashboardFrame(user, role); });

            JPanel p = new JPanel(); p.add(new JLabel("Roll:")); p.add(tfRoll);
            p.add(new JLabel("Date (YYYY-MM-DD):")); p.add(tfDate);
            p.add(new JLabel("Status:")); p.add(cbStatus);
            p.add(btnMark); p.add(btnView); p.add(btnBack);
            add(p, BorderLayout.NORTH);
            add(new JScrollPane(ta), BorderLayout.CENTER);

            setSize(800, 500);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            String c = e.getActionCommand();
            if (c.equals("Mark Attendance")) markAttendance();
            else if (c.equals("View Monthly")) viewMonthly();
        }

        void markAttendance() {
            String roll = tfRoll.getText().trim();
            String dateStr = tfDate.getText().trim();
            String status = (String) cbStatus.getSelectedItem();
            if (roll.isEmpty() || dateStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Roll and date required."); return; }
            try (Connection con = DB.getConnection()) {
                // check student exists
                try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM students WHERE roll=?")) {
                    ps.setString(1, roll);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { JOptionPane.showMessageDialog(this, "Student not found."); return; }
                    }
                }
                // insert or update attendance
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO attendance(roll,date,status) VALUES(?,?,?) ON DUPLICATE KEY UPDATE status=?")) {
                    ps.setString(1, roll);
                    ps.setDate(2, java.sql.Date.valueOf(dateStr));
                    ps.setString(3, status);
                    ps.setString(4, status);
                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Attendance recorded.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        }

        void viewMonthly() {
            String roll = tfRoll.getText().trim();
            if (roll.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter roll to view."); return; }
            try (Connection con = DB.getConnection()) {
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT date,status FROM attendance WHERE roll=? ORDER BY date DESC")) {
                    ps.setString(1, roll);
                    try (ResultSet rs = ps.executeQuery()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(String.format("%-12s %-8s\n", "Date", "Status"));
                        sb.append("----------------------\n");
                        while (rs.next()) {
                            sb.append(String.format("%-12s %-8s\n", rs.getDate(1).toString(), rs.getString(2)));
                        }
                        ta.setText(sb.toString());
                    }
                }
            } catch (Exception ex) { ta.setText("DB Error: " + ex.getMessage()); }
        }
    }

    // ---------- DISPLAY GRADE (Shikhar) ----------
    public static class Shikhar_DisplayGradeFrame extends JFrame implements ActionListener {
        JTextArea ta;
        JButton btnRefresh, btnBack;
        String user, role;

        public Shikhar_DisplayGradeFrame(String user, String role) {
            super("Display Grades (Shikhar)");
            this.user = user; this.role = role;

            ta = new JTextArea(25, 80);
            ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            btnRefresh = new JButton("Refresh");
            btnBack = new JButton("Back");
            btnRefresh.addActionListener(this);
            btnBack.addActionListener(a -> { dispose(); new Yachika_DashboardFrame(user, role); });

            JPanel p = new JPanel(); p.add(btnRefresh); p.add(btnBack);
            add(p, BorderLayout.NORTH);
            add(new JScrollPane(ta), BorderLayout.CENTER);

            setSize(1000, 600);
            setLocationRelativeTo(null);
            setVisible(true);

            loadGrades();
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Refresh")) loadGrades();
        }

        void loadGrades() {
            ta.setText("");
            try (Connection con = DB.getConnection()) {
                List<String> subjects = new ArrayList<>();
                try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT name FROM subjects ORDER BY name")) {
                    while (rs.next()) subjects.add(rs.getString(1));
                }

                StringBuilder hdr = new StringBuilder();
                hdr.append(String.format("%-6s %-25s %-10s", "Roll", "Name", "Class"));
                for (String s : subjects) hdr.append(String.format(" %-8s", s.length() > 7 ? s.substring(0,7) : s));
                hdr.append("  Grade\n");
                ta.append(hdr.toString());
                ta.append("-".repeat(Math.max(0, hdr.length())));
                ta.append("\n");


                try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT roll,name,class FROM students")) {
                    while (rs.next()) {
                        String roll = rs.getString("roll");
                        String name = rs.getString("name");
                        String cls = rs.getString("class");
                        StringBuilder line = new StringBuilder();
                        line.append(String.format("%-6s %-25s %-10s", roll, name, cls));
                        int totalForAvg = 0, countForAvg = 0;
                        for (String subj : subjects) {
                            int m = -1;
                            try (PreparedStatement ps = con.prepareStatement("SELECT marks FROM marks WHERE roll=? AND subject=?")) {
                                ps.setString(1, roll);
                                ps.setString(2, subj);
                                try (ResultSet rs2 = ps.executeQuery()) {
                                    if (rs2.next()) m = rs2.getInt(1);
                                }
                            }
                            line.append(String.format(" %-8s", m < 0 ? "-" : String.valueOf(m)));
                            if (m >= 0) { totalForAvg += m; countForAvg++; }
                        }
                        int gradeVal = (countForAvg > 0) ? Math.round((float) totalForAvg / countForAvg) : 0;
                        String grade = calculateGrade(gradeVal);
                        line.append("  ").append(grade);
                        ta.append(line.toString());
                        ta.append("\n");

                    }
                }
            } catch (Exception ex) {
                ta.setText("DB Error: " + ex.getMessage());
            }
        }
    }

    // ---------- IMPORT / EXPORT (Ramanshu) ----------
    public static class Ramanshu_ImportExportFrame extends JFrame implements ActionListener {
        JTextArea taImport;
        JButton btnProcess, btnExportCSV, btnBack;
        String user, role;

        public Ramanshu_ImportExportFrame(String user, String role) {
            super("Import / Export (Ramanshu)");
            this.user = user; this.role = role;

            taImport = new JTextArea(15, 60);
            taImport.setText("# Paste lines as: roll,subject,marks  (marks integer)\n# Example:\n# R001,Math,78\n# R002,Physics,89\n");

            btnProcess = new JButton("Process Import");
            btnExportCSV = new JButton("Export all students+subjects (CSV)");
            btnBack = new JButton("Back");

            btnProcess.addActionListener(this);
            btnExportCSV.addActionListener(this);
            btnBack.addActionListener(a -> { dispose(); new Yachika_DashboardFrame(user, role); });

            JPanel p = new JPanel();
            p.add(btnProcess); p.add(btnExportCSV); p.add(btnBack);

            add(new JScrollPane(taImport), BorderLayout.CENTER);
            add(p, BorderLayout.SOUTH);

            setSize(900, 500);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            String c = e.getActionCommand();
            if (c.equals("Process Import")) processImport();
            else if (c.equals("Export all students+subjects (CSV)")) exportCSV();
        }

        void processImport() {
            String text = taImport.getText();
            String[] lines = text.split("\\r?\\n");
            StringBuilder errs = new StringBuilder();
            int processed = 0;
            try (Connection con = DB.getConnection()) {
                for (String line : lines) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split(",");
                    if (parts.length < 3) {
                        errs.append("Invalid line: ").append(line).append("\n");
                        continue;
                    }
                    String roll = parts[0].trim();
                    String subj = parts[1].trim();
                    int marks;
                    try { marks = Integer.parseInt(parts[2].trim()); } catch (NumberFormatException ex) {
                        errs.append("Invalid marks number in: ").append(line).append("\n"); continue;
                    }
                    // check student exists
                    try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM students WHERE roll=?")) {
                        ps.setString(1, roll);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                errs.append("Student not found: ").append(roll).append("\n"); continue;
                            }
                        }
                    }
                    // check subject exists (must exist)
                    try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM subjects WHERE name=?")) {
                        ps.setString(1, subj);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                errs.append("Subject not found: ").append(subj).append("\n"); continue;
                            }
                        }
                    }
                    // insert or update marks
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO marks(roll,subject,marks) VALUES(?,?,?) ON DUPLICATE KEY UPDATE marks=?")) {
                        ps.setString(1, roll);
                        ps.setString(2, subj);
                        ps.setInt(3, marks);
                        ps.setInt(4, marks);
                        ps.executeUpdate();
                        processed++;
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
                return;
            }
            String msg = "Processed: " + processed + " lines.\nErrors:\n" + errs.toString();
            JOptionPane.showMessageDialog(this, msg);
        }

        void exportCSV() {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save CSV");
            int r = fc.showSaveDialog(this);
            if (r != JFileChooser.APPROVE_OPTION) return;
            java.io.File f = fc.getSelectedFile();
            try (Connection con = DB.getConnection();
                 FileWriter fw = new FileWriter(f)) {
                // header
                List<String> subs = new ArrayList<>();
                try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT name FROM subjects ORDER BY name")) {
                    while (rs.next()) subs.add(rs.getString(1));
                }
                fw.append("Roll,Name,Class");
                for (String s : subs) fw.append(",").append(s);
                fw.append("\n");
                // rows
                try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT roll,name,class FROM students")) {
                    while (rs.next()) {
                        String roll = rs.getString(1);
                        String name = rs.getString(2);
                        String cls = rs.getString(3);
                        fw.append(escapeCsv(roll)).append(",").append(escapeCsv(name)).append(",").append(escapeCsv(cls));
                        for (String s : subs) {
                            int m = -1;
                            try (PreparedStatement ps = con.prepareStatement("SELECT marks FROM marks WHERE roll=? AND subject=?")) {
                                ps.setString(1, roll); ps.setString(2, s);
                                try (ResultSet rs2 = ps.executeQuery()) {
                                    if (rs2.next()) m = rs2.getInt(1);
                                }
                            }
                            fw.append(",").append(m < 0 ? "" : String.valueOf(m));
                        }
                        fw.append("\n");
                    }
                }
                JOptionPane.showMessageDialog(this, "Exported to " + f.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export error: " + ex.getMessage());
            }
        }

        String escapeCsv(String s) {
            if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
                s = s.replace("\"", "\"\"");
                return "\"" + s + "\"";
            }
            return s;
        }
    }

    // ---------- Utility: grade calculation ----------
    static String calculateGrade(int marks) {
        if (marks >= 90) return "S";
        if (marks >= 80) return "A";
        if (marks >= 70) return "B";
        if (marks >= 60) return "C";
        if (marks >= 50) return "D";
        if (marks >= 40) return "E";
        return "U";
    }

    // ---------- MAIN ----------
    public static void main(String[] args) {
        // create DB tables if needed (uncomment to auto-create)
        // DB.createTables();

        SwingUtilities.invokeLater(() -> new Ritesh_LoginFrame());
    }
}
