package GroupProject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Yachika_DashboardFrame extends JFrame implements ActionListener {
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
