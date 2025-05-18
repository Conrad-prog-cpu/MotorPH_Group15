

import gui.LoginPanel;
import java.util.List;
import javax.swing.UnsupportedLookAndFeelException;
import model.Attendance;

public class Main {
    public static void main(String[] args) {
       
        // Set look and feel for dark theme if needed
        try {
            javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
        }

        // Run on Event Dispatch Thread (best practice for Swing)
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginPanel(); // Launch the login GUI
        });
    }
}
