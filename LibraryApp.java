import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LibraryApp {

    static class DB {
        static Connection getConnection() throws Exception {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/JavaProject", "root", "12345678"
            );
        }
    }

    public static class LoginFrame extends JFrame implements ActionListener {
        JTextField tfUser;
        JPasswordField pfPass;
        JButton btnLogin, btnExit;

        public LoginFrame() {
            super("Library Login");
            tfUser = new JTextField(15);
            pfPass = new JPasswordField(15);
            btnLogin = new JButton("Login");
            btnExit = new JButton("Exit");
            btnLogin.addActionListener(this);
            btnExit.addActionListener(this);
            setLayout(new GridLayout(4,1));
            JPanel p;
            p = new JPanel(); p.add(new JLabel("Username:")); p.add(tfUser); add(p);
            p = new JPanel(); p.add(new JLabel("Password:")); p.add(pfPass); add(p);
            p = new JPanel(); p.add(btnLogin); p.add(btnExit); add(p);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(350,180);
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
                     Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery("SELECT password, role FROM users WHERE username='" + user + "'")) {

                    if (rs.next()) {
                        if (rs.getString(1).equals(pass)) {
                            ok = true;
                            role = rs.getString(2);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }

                if (ok) {
                    dispose();
                    new HomeFrame(user, role);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Credentials");
                }
            }
        }
    }

    public static class HomeFrame extends JFrame implements ActionListener {
        String username, role;

        public HomeFrame(String username, String role) {
            super("Home - " + username);
            this.username = username;
            this.role = role;
            setLayout(new GridLayout(10,1));
            addBtn("Home");
            addBtn("Book Categories");
            addBtn("Search Books");
            addBtn("Issue Book Page");
            addBtn("Return Book");
            addBtn("Member Profile");
            addBtn("Admin Panel");
            addBtn("Overdue Fine Calculation");
            addBtn("Help / About");
            addBtn("Logout");
            setSize(400,600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }

        void addBtn(String label) {
            JButton b = new JButton(label);
            b.addActionListener(this);
            JPanel p = new JPanel();
            p.add(b);
            add(p);
        }

        public void actionPerformed(ActionEvent e) {
            String c = e.getActionCommand();

            if (c.equals("Home")) JOptionPane.showMessageDialog(this, "Welcome " + username);

            if (c.equals("Book Categories")) { dispose(); new BookCategoriesFrame(username, role); }
            if (c.equals("Search Books")) { dispose(); new SearchBooksFrame(username, role); }
            if (c.equals("Issue Book Page")) { dispose(); new IssueBookFrame(username, role); }
            if (c.equals("Return Book")) { dispose(); new ReturnBookFrame(username, role); }
            if (c.equals("Member Profile")) { dispose(); new MemberProfileFrame(username, role); }

            if (c.equals("Admin Panel")) {
                if (role.equalsIgnoreCase("admin")) {
                    dispose();
                    new AdminPanelFrame(username, role);
                } else {
                    JOptionPane.showMessageDialog(this, "Admin Only");
                }
            }

            if (c.equals("Overdue Fine Calculation")) { dispose(); new OverdueFineFrame(username, role); }
            if (c.equals("Help / About")) { dispose(); new HelpFrame(username, role); }
            if (c.equals("Logout")) { dispose(); new LoginFrame(); }
        }
    }

    public static class BookCategoriesFrame extends JFrame {
        public BookCategoriesFrame(String user, String role) {
            super("Categories");
            String[] c = {"Fiction","Science","Programming","History","Travel","Reference"};
            JPanel p = new JPanel(new GridLayout(c.length,1));
            for (String s : c) p.add(new JLabel(s));
            JButton back = new JButton("Back");
            back.addActionListener(a -> { dispose(); new HomeFrame(user, role); });
            add(p, BorderLayout.CENTER);
            add(back, BorderLayout.SOUTH);
            setSize(300,300);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    public static class SearchBooksFrame extends JFrame implements ActionListener {
        JTextField tf;
        JTextArea ta;
        String user, role;

        public SearchBooksFrame(String user, String role) {
            super("Search Books");
            this.user = user;
            this.role = role;
            tf = new JTextField(20);
            ta = new JTextArea(10,30);
            JButton b = new JButton("Search");
            JButton back = new JButton("Back");
            b.addActionListener(this);
            back.addActionListener(a -> { dispose(); new HomeFrame(user, role); });
            JPanel p = new JPanel(); p.add(new JLabel("Title:")); p.add(tf); p.add(b);
            add(p, BorderLayout.NORTH);
            add(new JScrollPane(ta), BorderLayout.CENTER);
            add(back, BorderLayout.SOUTH);
            setSize(500,350);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            String q = tf.getText().trim();
            ta.setText("");

            try (Connection con = DB.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                     "SELECT * FROM books WHERE LOWER(title) LIKE '%" + q.toLowerCase() + "%'"
                 )) {

                while (rs.next()) {
                    ta.append(rs.getString(1) + " | " + rs.getString(2) + " | " +
                              rs.getString(3) + " | " + rs.getString(4) +
                              " | available=" + rs.getInt(5) + "\n");
                }
            } catch (Exception ex) {
                ta.setText(ex.getMessage());
            }
        }
    }

    public static class IssueBookFrame extends JFrame implements ActionListener {
        JTextField tfBookId, tfUser;
        String user, role;

        public IssueBookFrame(String user, String role) {
            super("Issue Book");
            this.user = user;
            this.role = role;
            tfBookId = new JTextField(15);
            tfUser = new JTextField(user,15);
            JButton b = new JButton("Issue");
            JButton back = new JButton("Back");
            b.addActionListener(this);
            back.addActionListener(a -> { dispose(); new HomeFrame(user, role); });
            JPanel p = new JPanel();
            p.add(new JLabel("Book ID:")); p.add(tfBookId);
            p.add(new JLabel("User:")); p.add(tfUser);
            p.add(b); p.add(back);
            add(p);
            setSize(420,140);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            String bookId = tfBookId.getText().trim();
            String toUser = tfUser.getText().trim();

            try (Connection con = DB.getConnection();
                 Statement st = con.createStatement()) {

                ResultSet rs = st.executeQuery("SELECT available FROM books WHERE book_id='" + bookId + "'");
                if (!rs.next()) { JOptionPane.showMessageDialog(this, "Book not found"); return; }
                if (rs.getInt(1) == 0) { JOptionPane.showMessageDialog(this, "Already Issued"); return; }

                st.executeUpdate("UPDATE books SET available=0 WHERE book_id='" + bookId + "'");
                st.executeUpdate(
                    "INSERT INTO issues (book_id, username, issue_date) VALUES ('" +
                    bookId + "','" + toUser + "', CURDATE())"
                );

                JOptionPane.showMessageDialog(this, "Issued");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    public static class ReturnBookFrame extends JFrame implements ActionListener {
        JTextField tfBookId, tfUser;
        String user, role;

        public ReturnBookFrame(String user, String role) {
            super("Return Book");
            this.user = user;
            this.role = role;
            tfBookId = new JTextField(15);
            tfUser = new JTextField(user,15);
            JButton b = new JButton("Return");
            JButton back = new JButton("Back");
            b.addActionListener(this);
            back.addActionListener(a -> { dispose(); new HomeFrame(user, role); });
            JPanel p = new JPanel();
            p.add(new JLabel("Book ID:")); p.add(tfBookId);
            p.add(new JLabel("User:")); p.add(tfUser);
            p.add(b); p.add(back);
            add(p);
            setSize(420,140);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            String b = tfBookId.getText().trim();
            String u = tfUser.getText().trim();

            try (Connection con = DB.getConnection();
                 Statement st = con.createStatement()) {

                st.executeUpdate("UPDATE books SET available=1 WHERE book_id='" + b + "'");
                st.executeUpdate("UPDATE issues SET return_date=CURDATE() WHERE book_id='" + b + "' AND username='" + u + "' AND return_date IS NULL");

                JOptionPane.showMessageDialog(this, "Returned");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    public static class MemberProfileFrame extends JFrame {
        public MemberProfileFrame(String user, String role) {
            super("Profile");
            JTextArea ta = new JTextArea(10,30);

            try (Connection con = DB.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT username, role FROM users WHERE username='" + user + "'")) {
                if (rs.next()) {
                    ta.setText("Username: " + rs.getString(1) + "\nRole: " + rs.getString(2));
                }
            } catch (Exception ex) {
                ta.setText(ex.getMessage());
            }

            JButton back = new JButton("Back");
            back.addActionListener(a -> { dispose(); new HomeFrame(user, role); });

            add(new JScrollPane(ta), BorderLayout.CENTER);
            add(back, BorderLayout.SOUTH);

            setSize(350,250);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    public static class AdminPanelFrame extends JFrame {
        public AdminPanelFrame(String user, String role) {
            super("Admin");
            setLayout(new GridLayout(4,1));
            JButton addb = new JButton("Add Book");
            JButton viewb = new JButton("View Books");
            JButton back = new JButton("Back");

            addb.addActionListener(a -> {
                JTextField id = new JTextField();
                JTextField t = new JTextField();
                JTextField au = new JTextField();
                JTextField c = new JTextField();
                Object[] f = {"ID:",id,"Title:",t,"Author:",au,"Category:",c};
                int r = JOptionPane.showConfirmDialog(this, f, "Add", JOptionPane.OK_CANCEL_OPTION);

                if (r == JOptionPane.OK_OPTION) {
                    try (Connection con = DB.getConnection();
                         Statement st = con.createStatement()) {

                        st.executeUpdate("INSERT INTO books VALUES ('" +
                                id.getText() + "','" + t.getText() + "','" +
                                au.getText() + "','" + c.getText() + "',1)");

                        JOptionPane.showMessageDialog(this, "Added");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                    }
                }
            });

            viewb.addActionListener(a -> {
                StringBuilder sb = new StringBuilder();
                try (Connection con = DB.getConnection();
                     Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery("SELECT * FROM books")) {

                    while (rs.next()) {
                        sb.append(rs.getString(1)).append(" | ")
                          .append(rs.getString(2)).append(" | ")
                          .append(rs.getString(3)).append(" | ")
                          .append(rs.getString(4)).append(" | av=")
                          .append(rs.getInt(5)).append("\n");
                    }
                } catch (Exception ex) {
                    sb.append(ex.getMessage());
                }

                JTextArea ta = new JTextArea(sb.toString(),15,40);
                JOptionPane.showMessageDialog(this,new JScrollPane(ta),"Books",JOptionPane.PLAIN_MESSAGE);
            });

            back.addActionListener(a -> { dispose(); new HomeFrame(user, role); });

            add(addb); add(viewb); add(back);
            setSize(400,250);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    public static class OverdueFineFrame extends JFrame implements ActionListener {
        JTextField tfUser;
        JTextArea ta;
        String user, role;

        public OverdueFineFrame(String user, String role) {
            super("Overdue Fine");
            this.user = user;
            this.role = role;
            tfUser = new JTextField(user,15);
            JButton b = new JButton("Calculate");
            ta = new JTextArea(8,30);
            JButton back = new JButton("Back");
            b.addActionListener(this);
            back.addActionListener(a -> { dispose(); new HomeFrame(user, role); });
            JPanel p = new JPanel(); p.add(new JLabel("User:")); p.add(tfUser); p.add(b);
            add(p, BorderLayout.NORTH);
            add(new JScrollPane(ta), BorderLayout.CENTER);
            add(back, BorderLayout.SOUTH);
            setSize(450,300);
            setLocationRelativeTo(null);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            String u = tfUser.getText().trim();

            try (Connection con = DB.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                     "SELECT COUNT(*) FROM issues WHERE username='" + u + "' AND return_date IS NULL AND issue_date < CURDATE() - INTERVAL 14 DAY"
                 )) {

                if (rs.next()) {
                    int o = rs.getInt(1);
                    int fine = o * 10;
                    ta.setText("Overdue books: " + o + "\nEstimated fine: " + fine);
                }
            } catch (Exception ex) {
                ta.setText(ex.getMessage());
            }
        }
    }

    public static class HelpFrame extends JFrame {
        public HelpFrame(String user, String role) {
            super("Help");
            JTextArea ta = new JTextArea(10,30);
            ta.setText("Simple Library System\nPages:\nHome, Book Categories, Search, Issue, Return, Profile, Admin, Overdue Fine, Help\n");
            JButton back = new JButton("Back");
            back.addActionListener(a -> { dispose(); new HomeFrame(user, role); });
            add(new JScrollPane(ta), BorderLayout.CENTER);
            add(back, BorderLayout.SOUTH);
            setSize(420,300);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
