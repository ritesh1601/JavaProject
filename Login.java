import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
public class LogIn extends JFrame implements ActionListener {
private static final long serialVersionUID = 1L;
ImageIcon i;
JPanel p;
JLabel a1,a2,a3,l2;
JButton b1,b2,b3,b4;
JTextField t1,t2;
Boolean p1=false;
public LogIn() {
i=new ImageIcon("shubh.jpg");

51

l2=new JLabel(i);
a1=new JLabel(" User LogIn ");
a2=new JLabel(" USER EMPLOYEE ID ");
a3=new JLabel(" ENTER PASSWORD ");
b1=new JButton(" OK "); b1.addActionListener(this);
b1.setToolTipText("press to take Action");
b2=new JButton(" RESET "); b2.addActionListener(this);
b2.setToolTipText("press to take Action");
b3=new JButton(" New User "); b3.addActionListener(this);
b3.setToolTipText("press to take Action");
b4=new JButton(" EXIT "); b4.addActionListener(this);
b4.setToolTipText("press to take Action");
t1=new JTextField(20);
t2=new JPasswordField(20);
l2.setBounds(0,0,300,270);
setLayout(new GridLayout(5,1));
p=new JPanel(); p.add(a1); add(p);
p=new JPanel(); p.add(a2); p.add(t1); add(p);
p=new JPanel(); p.add(a3); p.add(t2); add(p);
p=new JPanel(); p.add(b1); p.add(b2); p.add(b3); p.add(b4); add(p);
pack();
setSize(600,600);
setTitle("My Window");
setVisible(true);
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); }
public void actionPerformed(ActionEvent ae) {
String s=ae.getActionCommand();
if(ae.getSource()==b2) {
t1.setText(""); t2.setText("");
}

else if(s.equals(" New User ")) {
setVisible(false);
registraction r2=new registraction();
r2.setVisible(true); }
else if(ae.getSource()==b4) {
System.exit(0); }
else if(ae.getSource()==b1) {
String a = t1.getText();
String b = t2.getText();
Try {
DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
System.out.println("Connecting to the database111...");
Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system","123");

Statement statement = connection.createStatement();

ResultSet res=statement.executeQuery("select 1 from reg where emp_id ='"+a+"' and password ='"+b+"'");
while(res.next()) {
a=res.getString(1);
System.out.println(a);
if(a.equals("1"))
p1 = true; }
statement.close();
connection.close();
}
catch (Exception ex)

52

{
System.out.println("The exception raised is:" + ex);
}
if(p1)
{
setVisible(false);
EmployeeScreen r1=new EmployeeScreen();
r1.setVisible(true);
}
else
{
JOptionPane.showMessageDialog(this,"Invaild User name or password ");
}
}

}
public static void main(String ar[])
{
new LogIn();
}
}