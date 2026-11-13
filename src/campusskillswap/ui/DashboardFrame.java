package campusskillswap.ui;

import campusskillswap.dao.StudentDAO;
import campusskillswap.interfaces.NotificationListener;
import campusskillswap.interfaces.Refreshable;
import campusskillswap.model.Student;
import campusskillswap.threads.ClockThread;
import campusskillswap.threads.NotificationThread;
import campusskillswap.util.Session;
import campusskillswap.util.UIHelper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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

/**
 * Student dashboard (after login).
 * JMenuBar + JTabbedPane (4 tabs) + status bar with live clock.
 * implements NotificationListener (gets pending-request count from a thread)
 * implements Refreshable (can reload itself).
 */
public class DashboardFrame extends JFrame implements NotificationListener, Refreshable {
    public static final int TAB_OVERVIEW = 0;
    public static final int TAB_EXPLORE = 1;
    public static final int TAB_PREFERENCES = 2;
    public static final int TAB_HELP = 3;

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
        setSize(950, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setJMenuBar(createMenuBar());

        // ----- heading -----
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UIHelper.PAGE_BG);
        top.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        welcomeLabel.setForeground(UIHelper.TEXT);
        JLabel sub = UIHelper.label("Find students, share your skills and start swapping.", 13, true, UIHelper.TEXT);
        top.add(welcomeLabel, BorderLayout.NORTH);
        top.add(sub, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // ----- tabs -----
        overviewPanel = new OverviewPanel(this);
        tabs.addTab("Overview", overviewPanel);
        tabs.addTab("Explore Skills", new ExploreSkillsPanel(true));
        tabs.addTab("My Preferences", new PreferencesPanel());
        tabs.addTab("Help", new HelpPanel());
        JPanel tabWrapper = new JPanel(new BorderLayout());
        tabWrapper.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        tabWrapper.setBackground(UIHelper.PAGE_BG);
        tabWrapper.add(tabs, BorderLayout.CENTER);
        add(tabWrapper, BorderLayout.CENTER);

        // When the tab changes, reload that tab's data (POLYMORPHISM via Refreshable)
        tabs.addChangeListener(e -> {
            Component selected = tabs.getSelectedComponent();
            if (selected instanceof Refreshable) {
                ((Refreshable) selected).refreshData();
            }
        });

        // ----- status bar -----
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        statusBar.add(clockLabel, BorderLayout.WEST);
        statusBar.add(pendingLabel, BorderLayout.CENTER);
        statusBar.add(logoutBtn, BorderLayout.EAST);
        pendingLabel.setHorizontalAlignment(JLabel.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // ----- window close -----
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (UIHelper.confirm(DashboardFrame.this, "Do you want to exit Campus Skill Swap?")) {
                    stopThreads();
                    System.exit(0);
                }
            }
        });

        // ----- start the 2 background threads -----
        Student me = Session.getInstance().getCurrentStudent();
        showStudentName(me);
        clockTask = new ClockThread(clockLabel);
        Thread clockThread = new Thread(clockTask, "Clock-Thread");
        clockThread.setDaemon(true);
        clockThread.start();

        notificationThread = new NotificationThread(me.getId(), this);
        notificationThread.start();
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem profileItem = new JMenuItem("My Profile");
        profileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        profileItem.addActionListener(e -> openProfile());
        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        refreshItem.addActionListener(e -> refreshData());
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        fileMenu.add(profileItem);
        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);
        fileMenu.add(exitItem);

        JMenu viewMenu = new JMenu("View");
        final JCheckBoxMenuItem statusItem = new JCheckBoxMenuItem("Show Status Bar", true);
        statusItem.addActionListener(e -> statusBar.setVisible(statusItem.isSelected()));
        viewMenu.add(statusItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem guideItem = new JMenuItem("User Guide");
        guideItem.addActionListener(e -> tabs.setSelectedIndex(TAB_HELP));
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Campus Skill Swap v1.0\nAOOP Project - B.Tech CSE (Data Science)\nBuilt with Java Swing and JDBC",
                "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(guideItem);
        helpMenu.add(aboutItem);

        bar.add(fileMenu);
        bar.add(viewMenu);
        bar.add(helpMenu);
        return bar;
    }

    private void showStudentName(Student s) {
        welcomeLabel.setText("Welcome, " + s.getFullName() + "!");
    }

    // ---------- actions used by OverviewPanel ----------
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

    @Override
    public void refreshData() {
        try {
            Student fresh = studentDAO.findById(Session.getInstance().getCurrentStudent().getId());
            if (fresh != null) {
                Session.getInstance().setCurrentStudent(fresh);
                showStudentName(fresh);
            }
        } catch (SQLException ex) {
            System.out.println("Could not refresh student: " + ex.getMessage());
        }
        overviewPanel.refreshData();
        Component selected = tabs.getSelectedComponent();
        if (selected instanceof Refreshable && selected != overviewPanel) {
            ((Refreshable) selected).refreshData();
        }
    }

    /** Called by NotificationThread (NotificationListener interface). */
    @Override
    public void onPendingCount(int count) {
        pendingLabel.setText("Pending requests: " + count);
        pendingLabel.setForeground(count > 0 ? Color.RED : Color.BLACK);
        overviewPanel.setPendingCount(count);
    }

    private void stopThreads() {
        clockTask.stop();
        notificationThread.stopChecking();
    }

    private void logout() {
        if (UIHelper.confirm(this, "Do you want to logout?")) {
            stopThreads();
            Session.getInstance().clear();
            dispose();
            new WelcomeFrame().setVisible(true);
        }
    }
}
