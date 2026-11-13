package campusskillswap.interfaces;

/**
 * INTERFACE 4 - Refreshable
 * Screens (panels / frames) that can reload their data from the database.
 * The dashboard calls refreshData() on the selected tab - this is POLYMORPHISM.
 */
public interface Refreshable {
    void refreshData();
}
