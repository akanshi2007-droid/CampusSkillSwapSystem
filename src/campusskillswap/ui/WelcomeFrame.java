package campusskillswap.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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

public class WelcomeFrame extends JFrame implements ActionListener {

    private final JButton exploreBtn;
    private final JButton loginBtn;
    private final JButton registerBtn;

    // Midnight Blue & Gold Theme
    private static final Color BG = new Color(16, 24, 39);
    private static final Color PANEL = new Color(23, 38, 61);
    private static final Color NAVY = new Color(36, 69, 104);
    private static final Color GOLD = new Color(214, 173, 96);
    private static final Color CREAM = new Color(244, 235, 216);
    private static final Color MUTED = new Color(184, 196, 214);
    private static final Color BORDER = new Color(52, 68, 91);

    public WelcomeFrame() {

        super("Campus Skill Swap");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(950, 650);
        setMinimumSize(new Dimension(850, 580));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(BG);

        // ----- Header -----

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 12));

        header.setBackground(PANEL);

        header.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 3, 0, GOLD
                        ),
                        BorderFactory.createEmptyBorder(
                                40, 15, 40, 15
                        )
                )
        );

        JLabel title = new JLabel("CAMPUS SKILL SWAP");

        title.setFont(
                new Font("SansSerif", Font.BOLD, 38)
        );

        title.setForeground(CREAM);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel tagline = new JLabel("Learn. Teach. Connect.");

        tagline.setFont(
                new Font("SansSerif", Font.PLAIN, 19)
        );

        tagline.setForeground(GOLD);
        tagline.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(title);
        header.add(tagline);

        add(header, BorderLayout.NORTH);

        // ----- Centre Section -----

        JPanel center = new JPanel(new BorderLayout());

        center.setBackground(BG);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));

        textPanel.setOpaque(false);

        textPanel.setBorder(
                BorderFactory.createEmptyBorder(45, 10, 15, 10)
        );

        JLabel line1 = new JLabel(
                "Exchange knowledge with your campus community."
        );

        line1.setFont(
                new Font("SansSerif", Font.BOLD, 22)
        );

        line1.setForeground(CREAM);
        line1.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel line2 = new JLabel(
                "Teach what you know. Learn something new. Build connections."
        );

        line2.setFont(
                new Font("SansSerif", Font.PLAIN, 15)
        );

        line2.setForeground(MUTED);
        line2.setHorizontalAlignment(SwingConstants.CENTER);

        textPanel.add(line1);
        textPanel.add(line2);

        center.add(textPanel, BorderLayout.NORTH);

        // ----- Main Buttons -----

        exploreBtn = createButton("Explore Skills", NAVY, CREAM);
        loginBtn = createButton("Login", GOLD, BG);
        registerBtn = createButton("Register", GOLD, BG);

        exploreBtn.addActionListener(this);
        loginBtn.addActionListener(this);
        registerBtn.addActionListener(this);

        JPanel buttons = new JPanel(
                new GridLayout(1, 3, 22, 0)
        );

        buttons.setOpaque(false);

        buttons.setPreferredSize(
                new Dimension(780, 150)
        );

        buttons.add(exploreBtn);
        buttons.add(loginBtn);
        buttons.add(registerBtn);

        JPanel buttonWrapper = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 0, 35)
        );

        buttonWrapper.setOpaque(false);
        buttonWrapper.add(buttons);

        center.add(buttonWrapper, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // ----- Footer -----

        JLabel footer = new JLabel(
                "Campus Skill Swap  \u2022  Share Skills  \u2022  Grow Together"
        );

        footer.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );

        footer.setForeground(MUTED);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setOpaque(true);
        footer.setBackground(PANEL);

        footer.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1, 0, 0, 0, BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                18, 10, 18, 10
                        )
                )
        );

        add(footer, BorderLayout.SOUTH);
    }

    // ----- Reusable Button Styling -----

    private JButton createButton(
            String text,
            Color background,
            Color foreground
    ) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("SansSerif", Font.BOLD, 17)
        );

        button.setBackground(background);
        button.setForeground(foreground);

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
                                20, 25, 20, 25
                        )
                )
        );

        return button;
    }

    // ----- Button Actions -----

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

    // ----- Public Explore -----

    private void openPublicExplore() {

        JFrame frame = new JFrame(
                "Campus Skill Swap - Explore Skills"
        );

        frame.setSize(850, 550);
        frame.setLocationRelativeTo(this);

        frame.add(new ExploreSkillsPanel(false));

        frame.setVisible(true);
    }
}