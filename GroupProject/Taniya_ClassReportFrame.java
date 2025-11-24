package GroupProject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Taniya_ClassReportFrame extends JFrame implements ActionListener {

    JTextArea ta;
    JButton btnTopper, btnAvg, btnBack;
    String user, role;

    public Taniya_ClassReportFrame(String user, String role) {
        super("Class Report (Taniya)");
        this.user = user;
        this.role = role;

        ta = new JTextArea(20, 60);
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        btnTopper = new JButton("Show Subject Toppers");
        btnAvg = new JButton("Show Subject Averages");
        btnBack = new JButton("Back");

        btnTopper.addActionListener(this);
        btnAvg.addActionListener(this);
        btnBack.addActionListener(a -> {
            dispose();
            new Yachika_DashboardFrame(user, role);
        });

        JPanel p = new JPanel();
        p.add(btnTopper);
        p.add(btnAvg);
        p.add(btnBack);

        add(p, BorderLayout.NORTH);
        add(new JScrollPane(ta), BorderLayout.CENTER);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String c = e.getActionCommand();
        if (c.equals("Show Subject Toppers"))
            showToppers();
        else if (c.equals("Show Subject Averages"))
            showAverages();
    }

    // ----------- Show Subject Toppers ---------------
    void showToppers() {
        ta.setText("");
        try (Connection con = DB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT name FROM subjects")) {

            while (rs.next()) {
                String subj = rs.getString(1);

                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT roll,marks FROM marks WHERE subject=? AND marks>=0 ORDER BY marks DESC LIMIT 1"
                )) {
                    ps.setString(1, subj);
                    try (ResultSet rs2 = ps.executeQuery()) {
                        if (rs2.next()) {
                            ta.append(String.format("Topper in %-20s : %s (%d)\n",
                                    subj, rs2.getString(1), rs2.getInt(2)));
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

    // ----------- Show Averages --------------------
    void showAverages() {
        ta.setText("");
        try (Connection con = DB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT name FROM subjects")) {

            while (rs.next()) {
                String subj = rs.getString(1);

                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT AVG(marks) FROM marks WHERE subject=? AND marks>=0"
                )) {
                    ps.setString(1, subj);

                    try (ResultSet rs2 = ps.executeQuery()) {
                        if (rs2.next()) {
                            double avg = rs2.getDouble(1);
                            if (rs2.wasNull())
                                ta.append(String.format("Average for %-20s : No marks\n", subj));
                            else
                                ta.append(String.format("Average for %-20s : %.2f\n", subj, avg));
                        }
                    }
                }
            }

        } catch (Exception ex) {
            ta.setText("DB Error: " + ex.getMessage());
        }
    }
}