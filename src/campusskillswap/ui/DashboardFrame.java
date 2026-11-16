
package campusskillswap.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import campusskillswap.dao.StudentDAO;
import campusskillswap.interfaces.NotificationListener;
import campusskillswap.interfaces.Refreshable;
import campusskillswap.model.Student;
import campusskillswap.threads.ClockThread;
import campusskillswap.threads.NotificationThread;
import campusskillswap.util.Session;
import campusskillswap.util.UIHelper;

public class DashboardFrame extends JFrame implements NotificationListener, Refreshable {

    public static final int TAB_OVERVIEW = 0;
    public static final int TAB_EXPLORE = 1;
    public static final int TAB_PREFERENCES = 2;
    public static final int TAB_HELP = 3;

    // Midnight Blue & Gold Theme
    public static final Color BG = new Color(16, 24, 39);
    public static final Color PANEL = new Color(23, 38, 61);
    public static final Color NAVY = new Color(36, 69, 104);
    public static final Color GOLD = new Color(214, 173, 96);
    public static final Color CREAM = new Color(244, 235, 216);
    public static final Color MUTED = new Color(184, 196, 214);
    public static final Color BORDER = new Color(52, 68, 91);

    private final StudentDAO studentDAO = new StudentDAO();

    private final JLabel welcomeLabel = new JLabel();
    private final JLabel clockLabel = new JLabel();
    private final JLabel pendingLabel = new JLabel("Pending requests: 0");

    private final JPanel statusBar = new JPanel(new BorderLayout());
    private final JTabbedPane tabs = new JTabbedPane();

    private final OverviewPanel overviewPanel;

    private final ClockThread clockTask;
    private final NotificationThread notificationThread;

    public DashboardFrame() {

        super("Campus Skill Swap - Student Dashboard");

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(1100, 750);
        setMinimumSize(new java.awt.Dimension(900, 650));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(BG);
        setJMenuBar(createMenuBar());

        // ----- Heading -----

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(PANEL);
        top.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, GOLD),
                        BorderFactory.createEmptyBorder(22, 28, 18, 28)
                )
        );

        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        welcomeLabel.setForeground(CREAM);

        JLabel sub = new JLabel(
                "Find students, share your skills and start swapping."
        );
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setForeground(MUTED);

        top.add(welcomeLabel, BorderLayout.NORTH);
        top.add(sub, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        // ----- Tabs -----

        overviewPanel = new OverviewPanel(this);

        tabs.addTab("Overview", overviewPanel);
        tabs.addTab("Explore Skills", new ExploreSkillsPanel(true));
        tabs.addTab("My Preferences", new PreferencesPanel());
        tabs.addTab("Help", new HelpPanel());

        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.setBackground(PANEL);
        tabs.setForeground(GOLD);
        tabs.setOpaque(true);
        tabs.setBorder(
                BorderFactory.createLineBorder(BORDER)
        );

        JPanel tabWrapper = new JPanel(new BorderLayout());
        tabWrapper.setBorder(
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        );
        tabWrapper.setBackground(BG);
        tabWrapper.add(tabs, BorderLayout.CENTER);

        add(tabWrapper, BorderLayout.CENTER);

        // Reload selected tab data

        tabs.addChangeListener(e -> {
            Component selected = tabs.getSelectedComponent();

            if (selected instanceof Refreshable) {
                ((Refreshable) selected).refreshData();
            }
        });

        // ----- Status Bar -----

        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn);
        logoutBtn.addActionListener(e -> logout());

        statusBar.setBackground(PANEL);
        statusBar.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1, 0, 0, 0, BORDER
                        ),
                        BorderFactory.createEmptyBorder(10, 25, 10, 25)
                )
        );

        clockLabel.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        clockLabel.setForeground(MUTED);

        pendingLabel.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        pendingLabel.setForeground(GOLD);
        pendingLabel.setHorizontalAlignment(JLabel.CENTER);

        statusBar.add(clockLabel, BorderLayout.WEST);
        statusBar.add(pendingLabel, BorderLayout.CENTER);
        statusBar.add(logoutBtn, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);

        // ----- Window Close -----

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                if (UIHelper.confirm(
                        DashboardFrame.this,
                        "Do you want to exit Campus Skill Swap?"
                )) {
                    stopThreads();
                    System.exit(0);
                }
            }
        });

        // ----- Start Background Threads -----

        Student me = Session.getInstance().getCurrentStudent();

        showStudentName(me);

        clockTask = new ClockThread(clockLabel);

        Thread clockThread = new Thread(clockTask, "Clock-Thread");
        clockThread.setDaemon(true);
        clockThread.start();

        notificationThread = new NotificationThread(me.getId(), this);
        notificationThread.start();
    }

    // ----- Button Styling -----

    private void styleButton(JButton button) {

        button.setBackground(GOLD);
        button.setForeground(BG);
        button.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(9, 18, 9, 18)
        );
    }

    // ----- Menu Bar -----

    private JMenuBar createMenuBar() {

        JMenuBar bar = new JMenuBar();

        bar.setBackground(PANEL);
        bar.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0, BORDER
                )
        );

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem profileItem = new JMenuItem("My Profile");
        profileItem.setAccelerator(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_P,
                        java.awt.event.InputEvent.CTRL_DOWN_MASK
                )
        );
        profileItem.addActionListener(e -> openProfile());

        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)
        );
        refreshItem.addActionListener(e -> refreshData());

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e ->
                dispatchEvent(
                        new WindowEvent(
                                this,
                                WindowEvent.WINDOW_CLOSING
                        )
                )
        );

        fileMenu.add(profileItem);
        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);
        fileMenu.add(exitItem);

        JMenu viewMenu = new JMenu("View");

        JCheckBoxMenuItem statusItem =
                new JCheckBoxMenuItem("Show Status Bar", true);

        statusItem.addActionListener(e ->
                statusBar.setVisible(statusItem.isSelected())
        );

        viewMenu.add(statusItem);

        JMenu helpMenu = new JMenu("Help");

        JMenuItem guideItem = new JMenuItem("User Guide");
        guideItem.addActionListener(e ->
                tabs.setSelectedIndex(TAB_HELP)
        );

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        this,
                        "Campus Skill Swap v1.0\n"
                                + "AOOP Project - B.Tech CSE (Data Science)\n"
                                + "Built with Java Swing and JDBC",
                        "About",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        helpMenu.add(guideItem);
        helpMenu.add(aboutItem);

        bar.add(fileMenu);
        bar.add(viewMenu);
        bar.add(helpMenu);

        return bar;
    }

    // ----- Student Information -----

    private void showStudentName(Student s) {
        welcomeLabel.setText("Welcome, " + s.getFullName() + "!");
    }

    // ----- Dashboard Actions -----

    public void openTab(int index) {
        tabs.setSelectedIndex(index);
    }

    public void openProfile() {
        new ProfileDialog(this, this::refreshData).setVisible(true);
    }

    public void openOfferDialog() {
        new OfferSkillDialog(this, this::refreshData).setVisible(true);
    }

    public void openSwapRequests() {
        new SwapRequestsDialog(this, this::refreshData).setVisible(true);
    }

    // ----- Refresh Data -----

    @Override
    public void refreshData() {

        try {

            Student fresh = studentDAO.findById(
                    Session.getInstance().getCurrentStudent().getId()
            );

            if (fresh != null) {
                Session.getInstance().setCurrentStudent(fresh);
                showStudentName(fresh);
            }

        } catch (SQLException ex) {

            System.out.println(
                    "Could not refresh student: " + ex.getMessage()
            );
        }

        overviewPanel.refreshData();

        Component selected = tabs.getSelectedComponent();

        if (selected instanceof Refreshable && selected != overviewPanel) {
            ((Refreshable) selected).refreshData();
        }
    }

    // ----- Notification Listener -----

    @Override
    public void onPendingCount(int count) {

        pendingLabel.setText("Pending requests: " + count);

        pendingLabel.setForeground(
                count > 0 ? new Color(235, 100, 100) : GOLD
        );

        overviewPanel.setPendingCount(count);
    }

    // ----- Stop Background Threads -----

    private void stopThreads() {
        clockTask.stop();
        notificationThread.stopChecking();
    }

    // ----- Logout -----

    private void logout() {

        if (UIHelper.confirm(this, "Do you want to logout?")) {

            stopThreads();
            Session.getInstance().clear();
            dispose();

            new WelcomeFrame().setVisible(true);
        }
    }
}
