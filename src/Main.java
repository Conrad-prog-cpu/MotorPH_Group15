

import gui.LoginPanel;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
    public static void main(String[] args) {
       
        // Set look and feel for dark theme if needed
        try {
            javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
        }

        // Run on Event Dispatch Thread (best practice for Swing)
        javax.swing.SwingUtilities.invokeLater(() -> {
            LoginPanel LoginPanel = new LoginPanel(); // Launch the login GUI
        });
    }
}
