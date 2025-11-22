import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MarksManagementApp {

    // ---------- DATABASE CONNECTION ----------
    static class DB {
        static Connection getConnection() throws Exception {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/MarksManagement", "root", "12345678"
            );
        }
    }

    // ---------- LOGIN PAGE ----------
    public static class LoginFrame extends JFrame implements ActionListener {
        JTextField tfUser;
        JPasswordField pfPass;
        JButton btnLogin, btnExit;

        public LoginFrame() {
            super("Marks Management Login");
            tfUser = new JTextField(15);
            pfPass = new JPasswordField(15);
            btnLogin = new JButton("Login");
            btnExit = new JButton("Exit");

            btnLogin.addActionListener(this);
            btnExit.addActionListener(this);

            setLayout(new GridLayout(4,1));
            JPanel p;

            p = new JPanel(); p.add(new JLabel("Username:")); p.add(tfUser); add(p);
            p = new JPanel(); p.add(new JLabel("Password:")); p.add(pfPass); add(p);
            p = new JPanel(); p.add(btnLogin); p.add(btnExit); add(p);

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(350,180);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Exit")) System.exit(0);

            String u = tfUser.getText().trim();
            String p = new String(pfPass.getPassword());
            boolean ok = false;
            String role = "";

            try (Connection con = DB.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                     "SELECT password, role FROM users WHERE username='" + u + "'"
                 )) {

                if (rs.next() && rs.getString(1).equals(p)) {
                    ok = true;
                    role = rs.getString(2);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }

            if (ok) {
                dispose();
                new HomeFrame(u, role);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials");
            }
        }
    }



    // ---------- HOME PAGE ----------
    public static class HomeFrame extends JFrame implements ActionListener {
        String user, role;

        public HomeFrame(String user, String role) {
            super("Home - " + user);
            this.user = user;
            this.role = role;

            setLayout(new GridLayout(10,1));

            addBtn("Add Student");
            addBtn("View Students");
            addBtn("Add Subjects");
            addBtn("Enter Marks");
            addBtn("View Marks");
            addBtn("Generate Result");
            addBtn("Import / Export Data");
            addBtn("Settings / About");
            addBtn("Logout");

            setSize(400,600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }

        void addBtn(String label) {
            JButton b = new JButton(label);
            b.addActionListener(this);
            JPanel p = new JPanel();
            p.add(b);
            add(p);
        }

        public void actionPerformed(ActionEvent e) {
            String c = e.getActionCommand();

            if (c.equals("Add Student")) { dispose(); new AddStudentFrame(user, role); }
            if (c.equals("View Students")) { dispose(); new ViewStudentsFrame(user, role); }
            if (c.equals("Add Subjects")) { dispose(); new AddSubjectFrame(user, role); }
            if (c.equals("Enter Marks")) { dispose(); new EnterMarksFrame(user, role); }
            if (c.equals("View Marks")) { dispose(); new ViewMarksFrame(user, role); }
            if (c.equals("Generate Result")) { dispose(); new GenerateResultFrame(user, role); }
            if (c.equals("Import / Export Data")) { dispose(); new ImportExportFrame(user, role); }
            if (c.equals("Settings / About")) { dispose(); new SettingsFrame(user, role); }

            if (c.equals("Logout")) { dispose(); new LoginFrame(); }
        }
    }



    // ---------- ADD STUDENT ----------
    public static class AddStudentFrame extends JFrame implements ActionListener {
        JTextField tfName, tfRoll, tfClass;
        JButton btnAdd, btnBack;
        String user, role;

        public AddStudentFrame(String user, String role) {
            super("Add Student");
            this.user = user;
            this.role = role;

            tfName = new JTextField(15);
            tfRoll = new JTextField(15);
            tfClass = new JTextField(15);

            btnAdd = new JButton("Add");
            btnBack = new JButton("Back");

            btnAdd.addActionListener(this);
            btnBack.addActionListener(e -> { dispose(); new HomeFrame(user, role); });

            JPanel p = new JPanel(new GridLayout(4,2));
            p.add(new JLabel("Name:")); p.add(tfName);
            p.add(new JLabel("Roll No.:")); p.add(tfRoll);
            p.add(new JLabel("Class:")); p.add(tfClass);
            p.add(btnAdd); p.add(btnBack);

            add(p);
            setSize(350,200);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            try (Connection con = DB.getConnection();
                 Statement st = con.createStatement()) {

                st.executeUpdate("INSERT INTO students(name, roll, class) VALUES('" +
                                  tfName.getText() + "','" + tfRoll.getText() + "','" +
                                  tfClass.getText() + "')");

                JOptionPane.showMessageDialog(this, "Student Added");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }



    // ---------- VIEW STUDENTS ----------
    public static class ViewStudentsFrame extends JFrame {
        public ViewStudentsFrame(String user, String role) {
            super("Students");
            JTextArea ta = new JTextArea(15,40);

            try (Connection con = DB.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM students")) {

                while (rs.next()) {
                    ta.append(
                        rs.getInt(1)+" | "+rs.getString(2)+" | "+
                        rs.getString(3)+" | "+rs.getString(4)+"\n"
                    );
                }

            } catch (Exception ex) {
                ta.setText(ex.getMessage());
            }

            JButton back = new JButton("Back");
            back.addActionListener(a -> { dispose(); new HomeFrame(user, role); });

            add(new JScrollPane(ta), BorderLayout.CENTER);
            add(back, BorderLayout.SOUTH);

            setSize(500,350);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }



    // ---------- ADD SUBJECTS ----------
    public static class AddSubjectFrame extends JFrame implements ActionListener {
        JTextField tfSub;
        JButton btnAdd, btnBack;
        String user, role;

        public AddSubjectFrame(String user, String role) {
            super("Add Subject");
            this.user = user; this.role = role;

            tfSub = new JTextField(15);
            btnAdd = new JButton("Add");
            btnBack = new JButton("Back");

            btnAdd.addActionListener(this);
            btnBack.addActionListener(a -> { dispose(); new HomeFrame(user, role); });

            JPanel p = new JPanel();
            p.add(new JLabel("Subject:")); p.add(tfSub); p.add(btnAdd); p.add(btnBack);

            add(p);
            setSize(350,150);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            try (Connection con = DB.getConnection();
                 Statement st = con.createStatement()) {

                st.executeUpdate("INSERT INTO subjects(name) VALUES('" +
                                  tfSub.getText() + "')");

                JOptionPane.showMessageDialog(this, "Subject Added");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }



    // ---------- ENTER MARKS ----------
    public static class EnterMarksFrame extends JFrame implements ActionListener {
        JTextField tfRoll, tfSub, tfMarks;
        JButton btnSubmit, btnBack;
        String user, role;

        public EnterMarksFrame(String user, String role) {
            super("Enter Marks");

            tfRoll = new JTextField(10);
            tfSub = new JTextField(10);
            tfMarks = new JTextField(10);

            btnSubmit = new JButton("Save");
            btnBack = new JButton("Back");

            btnSubmit.addActionListener(this);
            btnBack.addActionListener(a -> { dispose(); new HomeFrame(user, role); });

            JPanel p = new JPanel();
            p.add(new JLabel("Roll:")); p.add(tfRoll);
            p.add(new JLabel("Subject:")); p.add(tfSub);
            p.add(new JLabel("Marks:")); p.add(tfMarks);
            p.add(btnSubmit); p.add(btnBack);

            add(p);
            setSize(450,180);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            try (Connection con = DB.getConnection();
                 Statement st = con.createStatement()) {

                st.executeUpdate("INSERT INTO marks(roll, subject, marks) VALUES('" +
                                  tfRoll.getText() + "','" + tfSub.getText() + "'," +
                                  tfMarks.getText() + ")");

                JOptionPane.showMessageDialog(this, "Marks Saved");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }



    // ---------- VIEW MARKS ----------
    public static class ViewMarksFrame extends JFrame {
        public ViewMarksFrame(String user, String role) {
            super("All Marks");
            JTextArea ta = new JTextArea(15,40);

            try (Connection con = DB.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM marks")) {

                while (rs.next()) {
                    ta.append(
                        rs.getInt(1)+" | "+rs.getString(2)+" | "+
                        rs.getString(3)+" | "+rs.getInt(4)+"\n"
                    );
                }

            } catch (Exception ex) {
                ta.setText(ex.getMessage());
            }

            JButton back = new JButton("Back");
            back.addActionListener(a -> { dispose(); new HomeFrame(user, role); });

            add(new JScrollPane(ta), BorderLayout.CENTER);
            add(back, BorderLayout.SOUTH);

            setSize(500,350);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }



    // ---------- GENERATE RESULT ----------
    public static class GenerateResultFrame extends JFrame implements ActionListener {
        JTextField tfRoll;
        JTextArea ta;
        JButton btnCalc, btnBack;
        String user, role;

        public GenerateResultFrame(String user, String role) {
            super("Generate Result");

            tfRoll = new JTextField(15);
            ta = new JTextArea(10,30);
            btnCalc = new JButton("Calculate");
            btnBack = new JButton("Back");

            btnCalc.addActionListener(this);
            btnBack.addActionListener(a -> { dispose(); new HomeFrame(user, role); });

            JPanel p = new JPanel();
            p.add(new JLabel("Roll:")); p.add(tfRoll); p.add(btnCalc);

            add(p, BorderLayout.NORTH);
            add(new JScrollPane(ta), BorderLayout.CENTER);
            add(btnBack, BorderLayout.SOUTH);

            setSize(450,300);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            String roll = tfRoll.getText().trim();
            try (Connection con = DB.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                     "SELECT subject, marks FROM marks WHERE roll='" + roll + "'"
                 )) {

                int total = 0, c = 0;
                ta.setText("");

                while (rs.next()) {
                    int m = rs.getInt(2);
                    ta.append(rs.getString(1) + " = " + m + "\n");
                    total += m;
                    c++;
                }

                if (c > 0) {
                    ta.append("\nTotal = " + total);
                    ta.append("\nPercentage = " + (total / c));
                }

            } catch (Exception ex) {
                ta.setText(ex.getMessage());
            }
        }
    }



    // ---------- IMPORT / EXPORT ----------
    public static class ImportExportFrame extends JFrame {
        public ImportExportFrame(String user, String role) {
            super("Import / Export");
            JTextArea ta = new JTextArea("Import/Export feature coming soon...",10,30);

            JButton back = new JButton("Back");
            back.addActionListener(a -> { dispose(); new HomeFrame(user, role); });

            add(new JScrollPane(ta), BorderLayout.CENTER);
            add(back, BorderLayout.SOUTH);

            setSize(400,300);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }



    // ---------- SETTINGS ----------
    public static class SettingsFrame extends JFrame {
        public SettingsFrame(String user, String role) {
            super("Settings / About");

            JTextArea ta = new JTextArea(
                "Marks Management System\nNIT Jalandhar\nDeveloped by Students\n",10,30
            );

            JButton back = new JButton("Back");
            back.addActionListener(a -> { dispose(); new HomeFrame(user, role); });

            add(new JScrollPane(ta), BorderLayout.CENTER);
            add(back, BorderLayout.SOUTH);

            setSize(420,300);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }



    // ---------- MAIN ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
