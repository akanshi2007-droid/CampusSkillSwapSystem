package campusskillswap.ui;

import campusskillswap.dao.OfferDAO;
import campusskillswap.dao.PreferenceDAO;
import campusskillswap.interfaces.Refreshable;
import campusskillswap.model.Student;
import campusskillswap.util.ProfileCalculator;
import campusskillswap.util.Session;
import campusskillswap.util.UIHelper;
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

/** "Overview" tab: 4 big buttons + profile completion progress bar. */
public class OverviewPanel extends JPanel implements Refreshable {
    private final DashboardFrame dashboard;
    private final JLabel idLabel = new JLabel();
    private final JLabel pendingLabel = new JLabel(" ");
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final OfferDAO offerDAO = new OfferDAO();
    private final PreferenceDAO preferenceDAO = new PreferenceDAO();

    public OverviewPanel(DashboardFrame dashboard) {
        this.dashboard = dashboard;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(java.awt.Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = UIHelper.label("Your Campus Skill Swap", 28, true, UIHelper.TEXT);
        idLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel question = UIHelper.label("What would you like to do today?", 18, false, UIHelper.TEXT);
        pendingLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        pendingLabel.setForeground(java.awt.Color.RED);

        JButton offerBtn = bigButton("Offer a Skill");
        JButton findBtn = bigButton("Find a Skill");
        JButton swapBtn = bigButton("Swap Requests");
        JButton profileBtn = bigButton("My Profile");
        offerBtn.addActionListener(e -> dashboard.openOfferDialog());
        findBtn.addActionListener(e -> dashboard.openTab(DashboardFrame.TAB_EXPLORE));
        swapBtn.addActionListener(e -> dashboard.openSwapRequests());
        profileBtn.addActionListener(e -> dashboard.openProfile());

        JPanel grid = new JPanel(new GridLayout(2, 2, 15, 15));
        grid.setOpaque(false);
        grid.add(offerBtn);
        grid.add(findBtn);
        grid.add(swapBtn);
        grid.add(profileBtn);
        grid.setMaximumSize(new Dimension(700, 160));
        grid.setPreferredSize(new Dimension(700, 160));

        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(500, 26));
        progressBar.setMaximumSize(new Dimension(500, 26));

        center(title);
        center(idLabel);
        center(question);
        center(grid);
        center(pendingLabel);
        center(progressBar);

        add(Box.createVerticalGlue());
        add(title);
        add(Box.createVerticalStrut(12));
        add(idLabel);
        add(Box.createVerticalStrut(18));
        add(question);
        add(Box.createVerticalStrut(25));
        add(grid);
        add(Box.createVerticalStrut(15));
        add(pendingLabel);
        add(Box.createVerticalStrut(10));
        add(progressBar);
        add(Box.createVerticalGlue());

        refreshData();
    }

    private JButton bigButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 17));
        return b;
    }

    private void center(JComponent c) {
        c.setAlignmentX(CENTER_ALIGNMENT);
    }

    public void setPendingCount(int count) {
        pendingLabel.setText(count > 0 ? "You have " + count + " pending swap request(s)!" : " ");
    }

    @Override
    public void refreshData() {
        Student s = Session.getInstance().getCurrentStudent();
        if (s == null) {
            return;
        }
        idLabel.setText("Student ID: " + s.getId());
        int percent;
        try {
            percent = ProfileCalculator.calculate(s,
                    offerDAO.countByStudent(s.getId()),
                    preferenceDAO.exists(s.getId()));
        } catch (SQLException ex) {
            percent = ProfileCalculator.calculate(s, 0, false);
        }
        progressBar.setValue(percent);
        progressBar.setString("Profile Completion: " + percent + "%");
    }
}
