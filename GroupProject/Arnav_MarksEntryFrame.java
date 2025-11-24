package GroupProject;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class Arnav_MarksEntryFrame extends JFrame implements ActionListener {
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
