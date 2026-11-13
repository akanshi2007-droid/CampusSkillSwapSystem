package campusskillswap.threads;

import campusskillswap.dao.SwapRequestDAO;
import campusskillswap.interfaces.NotificationListener;
import java.sql.SQLException;
import javax.swing.SwingUtilities;

/**
 * THREAD 2 - extends Thread.
 * Every few seconds it asks the database how many swap requests are waiting
 * for the logged-in student and informs the dashboard (NotificationListener).
 */
public class NotificationThread extends Thread {
    private final int studentId;
    private final NotificationListener listener;
    private final SwapRequestDAO dao = new SwapRequestDAO();
    private volatile boolean running = true;

    public NotificationThread(int studentId, NotificationListener listener) {
        super("Notification-Thread");
        this.studentId = studentId;
        this.listener = listener;
        setDaemon(true);        // daemon thread stops automatically when the app closes
    }

    @Override
    public void run() {
        while (running) {
            try {
                final int count = dao.countPendingReceived(studentId);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        listener.onPendingCount(count);
                    }
                });
                Thread.sleep(8000);
            } catch (SQLException e) {
                System.out.println("Notification check failed: " + e.getMessage());
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException ie) {
                    break;
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void stopChecking() {
        running = false;
        interrupt();
    }
}
