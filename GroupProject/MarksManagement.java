package GroupProject;

import javax.swing.SwingUtilities;

public class MarksManagement {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Ritesh_LoginFrame());
    }
} 
// javac -cp .:lib/mysql-connector-j-9.5.0.jar GroupProject/*.java