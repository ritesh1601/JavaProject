package GroupProject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class Ramanshu_AddStudentFrame extends JFrame implements ActionListener {
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
