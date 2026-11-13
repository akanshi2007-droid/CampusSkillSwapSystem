package campusskillswap.ui;

import campusskillswap.util.UIHelper;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * FIRST PAGE of the application (like the reference screenshot).
 * implements ActionListener -> one actionPerformed() handles all 3 buttons.
 */
public class WelcomeFrame extends JFrame implements ActionListener {
    private final JButton exploreBtn;
    private final JButton loginBtn;
    private final JButton registerBtn;

    public WelcomeFrame() {
        super("Campus Skill Swap");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ----- header (dark blue) -----
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 8));
        header.setBackground(UIHelper.NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(35, 10, 35, 10));
        JLabel title = UIHelper.label("CAMPUS SKILL SWAP", 38, true, java.awt.Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel tagline = UIHelper.label("Learn. Teach. Connect.", 19, false, new java.awt.Color(0xCBD5E1));
        tagline.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(title);
        header.add(tagline);
        add(header, BorderLayout.NORTH);

        // ----- centre: text + 3 big buttons -----
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UIHelper.PAGE_BG);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        JLabel line1 = UIHelper.label("Exchange knowledge with your campus community.", 22, true, UIHelper.TEXT);
        line1.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel line2 = UIHelper.label("Teach what you know. Learn something new. Build connections.", 15, false, UIHelper.SLATE);
        line2.setHorizontalAlignment(SwingConstants.CENTER);
        textPanel.add(line1);
        textPanel.add(line2);
        center.add(textPanel, BorderLayout.NORTH);

        exploreBtn = UIHelper.flatButton("Explore Skills", UIHelper.SLATE);
        loginBtn = UIHelper.flatButton("Login", UIHelper.BLUE);
        registerBtn = UIHelper.flatButton("Register", UIHelper.GREEN);
        exploreBtn.addActionListener(this);
        loginBtn.addActionListener(this);
        registerBtn.addActionListener(this);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 20, 0));
        buttons.setOpaque(false);
        buttons.setPreferredSize(new Dimension(780, 190));
        buttons.add(exploreBtn);
        buttons.add(loginBtn);
        buttons.add(registerBtn);

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(buttons);
        center.add(buttonWrapper, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // ----- footer -----
        JLabel footer = UIHelper.label("Campus Skill Swap  \u2022  Share Skills  \u2022  Grow Together", 13, false, UIHelper.SLATE);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        footer.setOpaque(true);
        footer.setBackground(UIHelper.PAGE_BG);
        add(footer, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == exploreBtn) {
            openPublicExplore();
        } else if (source == loginBtn) {
            new LoginDialog(this).setVisible(true);
        } else if (source == registerBtn) {
            new RegisterDialog(this).setVisible(true);
        }
    }

    /** Anyone can browse skills without logging in. */
    private void openPublicExplore() {
        JFrame frame = new JFrame("Campus Skill Swap - Explore Skills");
        frame.setSize(850, 550);
        frame.setLocationRelativeTo(this);
        frame.add(new ExploreSkillsPanel(false));
        frame.setVisible(true);
    }
}
