package campusskillswap.ui;

import campusskillswap.dao.StudentDAO;
import campusskillswap.exceptions.AuthenticationException;
import campusskillswap.exceptions.InvalidInputException;
import campusskillswap.model.Student;
import campusskillswap.util.Session;
import campusskillswap.util.UIHelper;
import campusskillswap.util.Validator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** Login screen. JTextField + JPasswordField + JCheckBox + JButton. */
public class LoginDialog extends JDialog implements ActionListener {
    private final WelcomeFrame owner;
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JCheckBox showPassword = new JCheckBox("Show password");
    private final JCheckBox rememberMe = new JCheckBox("Remember my email");
    private final JButton loginBtn = new JButton("Login");
    private final JButton cancelBtn = new JButton("Back");
    private final StudentDAO studentDAO = new StudentDAO();
    // java.util.prefs stores small settings on the computer
    private final Preferences prefs = Preferences.userNodeForPackage(LoginDialog.class);

    public LoginDialog(WelcomeFrame owner) {
        super(owner, "Login", true);
        this.owner = owner;
        setSize(440, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JLabel title = UIHelper.label("Student Login", 26, true, UIHelper.TEXT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        UIHelper.addRow(form, 0, "Email:", emailField);
        UIHelper.addRow(form, 1, "Password:", passwordField);
        UIHelper.addRow(form, 2, "", showPassword);
        UIHelper.addRow(form, 3, "", rememberMe);
        add(form, BorderLayout.CENTER);

        // "Show password" check box -> lambda ActionListener
        final char defaultEcho = passwordField.getEchoChar();
        showPassword.addActionListener(e ->
                passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : defaultEcho));

        // link-style label to open the Register screen
        JLabel registerLink = UIHelper.label("New here? Create an account", 13, true, UIHelper.BLUE);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new RegisterDialog(LoginDialog.this.owner).setVisible(true);
            }
        });

        loginBtn.setBackground(UIHelper.BLUE);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setOpaque(true);
        loginBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        JPanel south = new JPanel(new BorderLayout());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnRow.add(loginBtn);
        btnRow.add(cancelBtn);
        JPanel linkRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkRow.add(registerLink);
        south.add(btnRow, BorderLayout.NORTH);
        south.add(linkRow, BorderLayout.SOUTH);
        south.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(south, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(loginBtn);       // Enter key = Login

        String savedEmail = prefs.get("email", "");
        if (!savedEmail.isEmpty()) {
            emailField.setText(savedEmail);
            rememberMe.setSelected(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelBtn) {
            dispose();
        } else if (e.getSource() == loginBtn) {
            doLogin();
        }
    }

    private void doLogin() {
        try {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            Validator.validateEmail(email);
            if (password.isEmpty()) {
                throw new InvalidInputException("Please enter your password.");
            }

            Student student = studentDAO.login(email, password);    // may throw AuthenticationException
            Session.getInstance().setCurrentStudent(student);

            if (rememberMe.isSelected()) {
                prefs.put("email", email);
            } else {
                prefs.remove("email");
            }
            dispose();
            owner.dispose();
            new DashboardFrame().setVisible(true);

        } catch (InvalidInputException | AuthenticationException ex) {
            UIHelper.showError(this, ex.getMessage());
        } catch (SQLException ex) {
            UIHelper.showError(this, "Database error: " + ex.getMessage()
                    + "\n\nCheck that MySQL is running and db.properties is correct.");
        }
    }
}
