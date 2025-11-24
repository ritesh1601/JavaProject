package GroupProject;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class Rohit_AddSubjectFrame extends JFrame implements ActionListener {
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

