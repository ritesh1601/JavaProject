public static class AttendanceFrame_Sandeep extends JFrame implements ActionListener {
    JTextField rollNoField;
    JComboBox<String> attendanceStatusBox;
    JTextField attendanceDateField;
    JTextArea reportArea;
    JButton saveAttendanceButton, showMonthlyButton, backButton;
    String currentUser, currentRole;

    public AttendanceFrame_Sandeep(String currentUser, String currentRole) {
        super("Attendance (Sandeep)");
        this.currentUser = currentUser;
        this.currentRole = currentRole;

        rollNoField = new JTextField(10);
        attendanceStatusBox = new JComboBox<>(new String[]{"Present", "Absent"});
        attendanceDateField = new JTextField(LocalDate.now().toString(), 10);
        reportArea = new JTextArea(15, 50);
        reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        saveAttendanceButton = new JButton("Mark Attendance");
        showMonthlyButton = new JButton("View Monthly");
        backButton = new JButton("Back");

        saveAttendanceButton.addActionListener(this);
        showMonthlyButton.addActionListener(this);
        backButton.addActionListener(a -> {
            dispose();
            new Yachika_DashboardFrame(currentUser, currentRole);
        });

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Roll No:")); topPanel.add(rollNoField);
        topPanel.add(new JLabel("Date (YYYY-MM-DD):")); topPanel.add(attendanceDateField);
        topPanel.add(new JLabel("Status:")); topPanel.add(attendanceStatusBox);
        topPanel.add(saveAttendanceButton); 
        topPanel.add(showMonthlyButton);
        topPanel.add(backButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        setSize(800, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Mark Attendance")) {
            saveAttendance();
        } else if (command.equals("View Monthly")) {
            showMonthlyAttendance();
        }
    }

    void saveAttendance() {
        String rollNo = rollNoField.getText().trim();
        String dateInput = attendanceDateField.getText().trim();
        String attendanceStatus = (String) attendanceStatusBox.getSelectedItem();

        if (rollNo.isEmpty() || dateInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Roll number and date are required.");
            return;
        }

        try (Connection conn = DB.getConnection()) {
            try (PreparedStatement checkStudent = conn.prepareStatement(
                    "SELECT 1 FROM students WHERE roll=?")) {
                checkStudent.setString(1, rollNo);
                try (ResultSet rs = checkStudent.executeQuery()) {
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(this, "Student not found.");
                        return;
                    }
                }
            }

            try (PreparedStatement insertAttendance = conn.prepareStatement(
                    "INSERT INTO attendance(roll,date,status) VALUES(?,?,?) ON DUPLICATE KEY UPDATE status=?")) {
                insertAttendance.setString(1, rollNo);
                insertAttendance.setDate(2, java.sql.Date.valueOf(dateInput));
                insertAttendance.setString(3, attendanceStatus);
                insertAttendance.setString(4, attendanceStatus);
                insertAttendance.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Attendance recorded successfully.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    void showMonthlyAttendance() {
        String rollNo = rollNoField.getText().trim();

        if (rollNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter roll number to view.");
            return;
        }

        try (Connection conn = DB.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT date,status FROM attendance WHERE roll=? ORDER BY date DESC")) {
                ps.setString(1, rollNo);
                try (ResultSet rs = ps.executeQuery()) {
                    StringBuilder report = new StringBuilder();
                    report.append(String.format("%-12s %-10s\n", "Date", "Status"));
                    report.append("--------------------------\n");
                    while (rs.next()) {
                        report.append(String.format("%-12s %-10s\n",
                                rs.getDate(1).toString(), rs.getString(2)));
                    }
                    reportArea.setText(report.toString());
                }
            }
        } catch (Exception ex) {
            reportArea.setText("Database Error: " + ex.getMessage());
        }
    }
}
