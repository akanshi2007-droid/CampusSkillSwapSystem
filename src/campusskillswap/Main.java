package campusskillswap;

import campusskillswap.ui.WelcomeFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Entry point of the application. Run this class. */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Using default look and feel.");
        }
        // Swing screens must be created on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WelcomeFrame().setVisible(true);
            }
        });
    }
}
