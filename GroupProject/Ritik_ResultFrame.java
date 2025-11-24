package GroupProject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Ritik_ResultFrame extends JFrame implements ActionListener {
    JTextField tfRoll;
    JTextArea ta;
    JButton btnCalc, btnBack;
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
