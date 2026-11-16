
package campusskillswap.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import campusskillswap.dao.OfferDAO;
import campusskillswap.dao.PreferenceDAO;
import campusskillswap.interfaces.Refreshable;
import campusskillswap.model.Student;
import campusskillswap.util.ProfileCalculator;
import campusskillswap.util.Session;

public class OverviewPanel extends JPanel implements Refreshable {

    private final DashboardFrame dashboard;

    private final JLabel idLabel = new JLabel();
    private final JLabel pendingLabel = new JLabel(" ");

    private final JProgressBar progressBar =
            new JProgressBar(0, 100);

    private final OfferDAO offerDAO = new OfferDAO();
    private final PreferenceDAO preferenceDAO = new PreferenceDAO();

    // Midnight Blue & Gold Theme
    private static final Color BG = new Color(16, 24, 39);
    private static final Color PANEL = new Color(23, 38, 61);
    private static final Color NAVY = new Color(36, 69, 104);
    private static final Color GOLD = new Color(214, 173, 96);
    private static final Color CREAM = new Color(244, 235, 216);
    private static final Color MUTED = new Color(184, 196, 214);
    private static final Color BORDER = new Color(52, 68, 91);

    public OverviewPanel(DashboardFrame dashboard) {

        this.dashboard = dashboard;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG);

        setBorder(
                BorderFactory.createEmptyBorder(
                        25, 25, 25, 25
                )
        );

        // ----- Heading -----

        JLabel title = new JLabel("Your Campus Skill Swap");

        title.setFont(
                new Font("SansSerif", Font.BOLD, 28)
        );
        title.setForeground(CREAM);

        idLabel.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );
        idLabel.setForeground(GOLD);

        JLabel question = new JLabel(
                "What would you like to do today?"
        );

        question.setFont(
                new Font("SansSerif", Font.PLAIN, 18)
        );
        question.setForeground(MUTED);

        // ----- Dashboard Buttons -----

        JButton offerBtn = bigButton("Offer a Skill", GOLD);
        JButton findBtn = bigButton("Find a Skill", NAVY);
        JButton swapBtn = bigButton("Swap Requests", NAVY);
        JButton profileBtn = bigButton("My Profile", GOLD);

        offerBtn.addActionListener(
                e -> dashboard.openOfferDialog()
        );

        findBtn.addActionListener(
                e -> dashboard.openTab(DashboardFrame.TAB_EXPLORE)
        );

        swapBtn.addActionListener(
                e -> dashboard.openSwapRequests()
        );

        profileBtn.addActionListener(
                e -> dashboard.openProfile()
        );

        // ----- Button Grid -----

        JPanel grid = new JPanel(
                new GridLayout(2, 2, 18, 18)
        );

        grid.setOpaque(false);

        grid.add(offerBtn);
        grid.add(findBtn);
        grid.add(swapBtn);
        grid.add(profileBtn);

        grid.setMaximumSize(
                new Dimension(720, 180)
        );

        grid.setPreferredSize(
                new Dimension(720, 180)
        );

        // ----- Pending Requests -----

        pendingLabel.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );

        pendingLabel.setForeground(GOLD);

        // ----- Progress Bar -----

        progressBar.setStringPainted(true);

        progressBar.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        progressBar.setForeground(GOLD);
        progressBar.setBackground(PANEL);

        progressBar.setBorder(
                BorderFactory.createLineBorder(BORDER)
        );

        progressBar.setPreferredSize(
                new Dimension(520, 30)
        );

        progressBar.setMaximumSize(
                new Dimension(520, 30)
        );

        // ----- Center Alignment -----

        center(title);
        center(idLabel);
        center(question);
        center(grid);
        center(pendingLabel);
        center(progressBar);

        // ----- Add Components -----

        add(Box.createVerticalGlue());

        add(title);
        add(Box.createVerticalStrut(12));

        add(idLabel);
        add(Box.createVerticalStrut(22));

        add(question);
        add(Box.createVerticalStrut(30));

        add(grid);
        add(Box.createVerticalStrut(25));

        add(pendingLabel);
        add(Box.createVerticalStrut(15));

        add(progressBar);

        add(Box.createVerticalGlue());

        refreshData();
    }

    // ----- Button Styling -----

    private JButton bigButton(String text, Color background) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("SansSerif", Font.BOLD, 17)
        );

        button.setBackground(background);

        button.setForeground(
                background.equals(GOLD) ? BG : CREAM
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                background.equals(GOLD)
                                        ? GOLD.darker()
                                        : BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                18, 20, 18, 20
                        )
                )
        );

        return button;
    }

    // ----- Center Alignment -----

    private void center(JComponent component) {
        component.setAlignmentX(CENTER_ALIGNMENT);
    }

    // ----- Pending Request Count -----

    public void setPendingCount(int count) {

        if (count > 0) {

            pendingLabel.setText(
                    "You have " + count +
                    " pending swap request(s)!"
            );

            pendingLabel.setForeground(GOLD);

        } else {

            pendingLabel.setText(" ");
        }
    }

    // ----- Refresh Dashboard Data -----

    @Override
    public void refreshData() {

        Student s = Session.getInstance().getCurrentStudent();

        if (s == null) {
            return;
        }

        idLabel.setText(
                "Student ID: " + s.getId()
        );

        int percent;

        try {

            percent = ProfileCalculator.calculate(
                    s,
                    offerDAO.countByStudent(s.getId()),
                    preferenceDAO.exists(s.getId())
            );

        } catch (SQLException ex) {

            percent = ProfileCalculator.calculate(
                    s, 0, false
            );
        }

        progressBar.setValue(percent);

        progressBar.setString(
                "Profile Completion: " + percent + "%"
        );
    }
}
