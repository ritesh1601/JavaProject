package GroupProject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.sql.*;
import java.util.*;
import java.util.List;

public class Ramanshu_ImportExportFrame extends JFrame implements ActionListener {
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

