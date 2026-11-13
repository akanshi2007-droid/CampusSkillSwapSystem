package campusskillswap.threads;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * THREAD 1 - implements Runnable.
 * Updates the clock in the dashboard status bar every second.
 * Swing components must be changed on the Event Dispatch Thread,
 * so we use SwingUtilities.invokeLater().
 */
public class ClockThread implements Runnable {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm:ss");
    private final JLabel label;
    private volatile boolean running = true;

    public ClockThread(JLabel label) {
        this.label = label;
    }

    @Override
    public void run() {
        while (running) {
            final String now = LocalDateTime.now().format(FORMAT);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    label.setText(now);
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void stop() {
        running = false;
    }
}
