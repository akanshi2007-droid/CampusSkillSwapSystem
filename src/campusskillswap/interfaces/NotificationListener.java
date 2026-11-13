package campusskillswap.interfaces;

/**
 * INTERFACE 5 - NotificationListener (callback / observer idea)
 * The background NotificationThread tells the dashboard how many
 * swap requests are waiting, without knowing anything about the GUI.
 */
public interface NotificationListener {
    void onPendingCount(int count);
}
