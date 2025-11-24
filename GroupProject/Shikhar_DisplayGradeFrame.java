package GroupProject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Shikhar_DisplayGradeFrame extends JFrame implements ActionListener {
    JTextField tfRoll;
    JTextArea ta;
    JButton btnShow, btnBack;
    String user, role;

    static String calculateGrade(int marks) {
        if (marks >= 90) return "S";
        if (marks >= 80) return "A";
        if (marks >= 70) return "B";
        if (marks >= 60) return "C";
        if (marks >= 50) return "D";
        if (marks >= 40) return "E";
        return "U";
    }

    public Shikhar_DisplayGradeFrame(String user, String role) {
        super("Display Grades (Shikhar)");
        this.user = user;
        this.role = role;

        tfRoll = new JTextField(12);
        ta = new JTextArea(22, 90);
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        ta.setEditable(false);

        btnShow = new JButton("Show Grades");
        btnBack = new JButton("Back");

        btnShow.addActionListener(this);
        btnBack.addActionListener(a -> {
            dispose();
            new Yachika_DashboardFrame(user, role);
        });

        JPanel top = new JPanel();
        top.add(new JLabel("Roll No:"));
        top.add(tfRoll);
        top.add(btnShow);
        top.add(btnBack);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(ta), BorderLayout.CENTER);

        setSize(1000, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Show Grades")) {
            loadStudentGrades();
        }
    }

    void loadStudentGrades() {
        ta.setText("");
        String roll = tfRoll.getText().trim();

        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter roll number.");
            return;
        }

        try (Connection con = DB.getConnection()) {

            // ----- Fetch student details -----
            String name = "", cls = "";
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT name,class FROM students WHERE roll=?")) {
                ps.setString(1, roll);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        name = rs.getString("name");
                        cls = rs.getString("class");
                    } else {
                        ta.setText("No student found with roll: " + roll);
                        return;
                    }
                }
            }

            // ----- Header -----
            ta.append("==============================================\n");
            ta.append(" Student Name : " + name + "\n");
            ta.append(" Roll Number  : " + roll + "\n");
            ta.append(" Class        : " + cls + "\n");
            ta.append("==============================================\n\n");

            ta.append(String.format("%-25s %-10s %-10s\n", "Subject", "Marks", "Grade"));
            ta.append("-------------------------------------------------------------\n");

            // ----- Fetch marks for all subjects -----
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT subject, marks FROM marks WHERE roll=?")) {
                ps.setString(1, roll);

                try (ResultSet rs = ps.executeQuery()) {
                    boolean found = false;

                    while (rs.next()) {
                        found = true;
                        String subj = rs.getString("subject");
                        int marks = rs.getInt("marks");

                        String grade = (marks < 0 ? "-" : calculateGrade(marks));

                        ta.append(String.format("%-25s %-10s %-10s\n",
                                subj,
                                (marks < 0 ? "-" : marks),
                                grade));
                    }

                    if (!found) {
                        ta.append("No marks found.");
                    }
                }
            }

        } catch (Exception ex) {
            ta.setText("DB Error:\n" + ex.getMessage());
        }
    }
}