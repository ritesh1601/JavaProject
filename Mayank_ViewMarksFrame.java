import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

public class Mayank_ViewMarksFrame extends JFrame implements ActionListener {
    JTextField tfFilter;
    JTextArea ta;
    JButton btnRefresh;
    JButton btnBack;
    JButton btnFilter;
    String user;
    String role;

    // Mock subjects
    ArrayList<String> subjects = new ArrayList<String>();

    // Mock students { roll, name, class }
    ArrayList<String[]> students = new ArrayList<String[]>();

    // Mock marks: key = roll + "-" + subject
    HashMap<String, Integer> marks = new HashMap<String, Integer>();

    public Mayank_ViewMarksFrame(String var1, String var2) {
        super("View Students & Marks (Mayank - Offline Demo)");

        this.user = var1;
        this.role = var2;

        this.tfFilter = new JTextField(20);
        this.ta = new JTextArea(20, 70);
        this.ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        this.ta.setEditable(false);

        this.btnRefresh = new JButton("Refresh");
        this.btnFilter = new JButton("Filter (roll/class)");
        this.btnBack = new JButton("Back");

        this.btnRefresh.addActionListener(this);
        this.btnFilter.addActionListener(this);

        this.btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JPanel top = new JPanel();
        top.add(new JLabel("Filter:"));
        top.add(this.tfFilter);
        top.add(this.btnFilter);
        top.add(this.btnRefresh);
        top.add(this.btnBack);

        this.add(top, BorderLayout.NORTH);
        this.add(new JScrollPane(this.ta), BorderLayout.CENTER);

        this.setSize(900, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        loadMockData();
        this.setVisible(true);

        this.loadAll("");
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if ("Refresh".equals(cmd)) {
            loadAll("");
        } else if ("Filter (roll/class)".equals(cmd)) {
            loadAll(tfFilter.getText().trim());
        }
    }

    // --------------------------------------------------------
    // MOCK DATA SECTION
    // --------------------------------------------------------
    void loadMockData() {

        // Subjects
        subjects.add("Math");
        subjects.add("Science");
        subjects.add("English");
        subjects.add("Computers");

        // Students
        students.add(new String[]{"101", "Alice Sharma", "10A"});
        students.add(new String[]{"102", "Rohit Singh", "10A"});
        students.add(new String[]{"103", "Priya Verma", "10B"});
        students.add(new String[]{"104", "Aditya Kumar", "10B"});

        // Mock marks
        addMark("101", "Math", 88);
        addMark("101", "Science", 92);
        addMark("101", "English", 76);
        addMark("101", "Computers", 95);

        addMark("102", "Math", 65);
        addMark("102", "Science", 70);
        addMark("102", "English", 68);
        addMark("102", "Computers", 83);

        addMark("103", "Math", 91);
        addMark("103", "Science", 89);
        addMark("103", "English", 90);
        addMark("103", "Computers", 94);

        addMark("104", "Math", 55);
        addMark("104", "Science", 60);
        addMark("104", "English", 58);
        addMark("104", "Computers", 71);
    }

    void addMark(String roll, String subject, int m) {
        marks.put(roll + "-" + subject, m);
    }

    // --------------------------------------------------------
    // DISPLAY DATA (OFFLINE MODE)
    // --------------------------------------------------------
    void loadAll(String filter) {
        this.ta.setText("");

        // Header
        StringBuilder header = new StringBuilder();
        header.append(String.format("%-6s %-25s %-10s", "Roll", "Name", "Class"));

        for (String s : subjects) {
            String title = s.length() > 9 ? s.substring(0, 9) : s;
            header.append(String.format(" %-10s", title));
        }

        header.append("\n");
        ta.append(header.toString());

        // Dashes
        StringBuilder dash = new StringBuilder();
        for (int i = 0; i < header.length(); i++) dash.append("-");
        dash.append("\n");
        ta.append(dash.toString());

        // Display students
        for (String[] st : students) {
            String roll = st[0];
            String name = st[1];
            String cls = st[2];

            // Filter check
            if (filter.length() > 0) {
                if (!(roll.contains(filter) || cls.contains(filter))) {
                    continue;
                }
            }

            StringBuilder row = new StringBuilder();
            row.append(String.format("%-6s %-25s %-10s", roll, name, cls));

            for (String sub : subjects) {
                String key = roll + "-" + sub;
                Integer m = marks.get(key);
                if (m == null) row.append(String.format(" %-10s", "-"));
                else row.append(String.format(" %-10s", m));
            }

            row.append("\n");
            ta.append(row.toString());
        }
    }

    public static void main(String[] args) {
        new Mayank_ViewMarksFrame("user", "role");
    }
}
