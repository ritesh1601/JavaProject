package GroupProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Ritesh_LoginFrame extends JFrame implements ActionListener {
    JTextField tfUser;
    JPasswordField pfPass;
    JButton btnLogin, btnExit;

    public Ritesh_LoginFrame() {
        super("Marks Management - Login (Ritesh)");
        tfUser = new JTextField(15);
        pfPass = new JPasswordField(15);
        btnLogin = new JButton("Login");
        btnExit = new JButton("Exit");
        btnLogin.addActionListener(this);
        btnExit.addActionListener(this);

        setLayout(new GridLayout(4, 1));
        JPanel p;
        p = new JPanel(); p.add(new JLabel("Username:")); p.add(tfUser); add(p);
        p = new JPanel(); p.add(new JLabel("Password:")); p.add(pfPass); add(p);
        p = new JPanel(); p.add(btnLogin); p.add(btnExit); add(p);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("Exit")) System.exit(0);

        if (cmd.equals("Login")) {
            String user = tfUser.getText().trim();
            String pass = new String(pfPass.getPassword());

            boolean ok = false;
            String role = "";
            try (Connection con = DB.getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT password, role FROM users WHERE username=?")) {
                ps.setString(1, user);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String dbPass = rs.getString(1);
                        role = rs.getString(2);
                        if (dbPass.equals(pass)) ok = true;
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }

            if (ok) {
                dispose();
                new Yachika_DashboardFrame(user, role);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials");
            }
        }
    }
}
