package campusskillswap.ui;

import campusskillswap.dao.StudentDAO;
import campusskillswap.exceptions.DuplicateEntryException;
import campusskillswap.exceptions.InvalidInputException;
import campusskillswap.model.Student;
import campusskillswap.util.UIHelper;
import campusskillswap.util.Validator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

/**
 * Registration form.
 * Components: JTextField, JPasswordField, JComboBox, JSpinner,
 * JRadioButton (ButtonGroup), JFormattedTextField, JCheckBox, JButton.
 */
public class RegisterDialog extends JDialog implements ActionListener {
    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JPasswordField confirmField = new JPasswordField(20);
    private final JComboBox<String> departmentBox = new JComboBox<>(UIHelper.DEPARTMENTS);
    private final JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 4, 1));
    private final JRadioButton maleRadio = new JRadioButton("Male");
    private final JRadioButton femaleRadio = new JRadioButton("Female");
    private final JRadioButton otherRadio = new JRadioButton("Other");
    private final ButtonGroup genderGroup = new ButtonGroup();
    private final JFormattedTextField phoneField = UIHelper.createPhoneField();
    private final JCheckBox showPassword = new JCheckBox("Show passwords");
    private final JCheckBox termsCheck = new JCheckBox("I agree to the terms and conditions");
    private final JButton registerBtn = new JButton("Register");
    private final JButton clearBtn = new JButton("Clear");
    private final JButton cancelBtn = new JButton("Cancel");
    private final StudentDAO studentDAO = new StudentDAO();

    public RegisterDialog(java.awt.Frame owner) {
        super(owner, "Register", true);
        setSize(500, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JLabel title = UIHelper.label("Create Your Account", 26, true, UIHelper.TEXT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(title, BorderLayout.NORTH);

        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderGroup.add(otherRadio);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        genderPanel.add(otherRadio);

        phoneField.setToolTipText("10 digit mobile number (optional)");

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 30));
        UIHelper.addRow(form, 0, "Full Name:", nameField);
        UIHelper.addRow(form, 1, "Email:", emailField);
        UIHelper.addRow(form, 2, "Password:", passwordField);
        UIHelper.addRow(form, 3, "Confirm Password:", confirmField);
        UIHelper.addRow(form, 4, "", showPassword);
        UIHelper.addRow(form, 5, "Department:", departmentBox);
        UIHelper.addRow(form, 6, "Year of Study:", yearSpinner);
        UIHelper.addRow(form, 7, "Gender:", genderPanel);
        UIHelper.addRow(form, 8, "Phone:", phoneField);
        UIHelper.addRow(form, 9, "", termsCheck);
        add(form, BorderLayout.CENTER);

        final char defaultEcho = passwordField.getEchoChar();
        showPassword.addActionListener(e -> {
            char c = showPassword.isSelected() ? (char) 0 : defaultEcho;
            passwordField.setEchoChar(c);
            confirmField.setEchoChar(c);
        });

        registerBtn.setBackground(UIHelper.GREEN);
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setOpaque(true);
        registerBtn.addActionListener(this);
        clearBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        btnRow.add(registerBtn);
        btnRow.add(clearBtn);
        btnRow.add(cancelBtn);
        add(btnRow, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == registerBtn) {
            register();
        } else if (src == clearBtn) {
            clearForm();
        } else if (src == cancelBtn) {
            dispose();
        }
    }

    private void register() {
        try {
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());
            Validator.validatePassword(password);
            Validator.validateConfirm(password, confirm);

            Student s = new Student();
            s.setFullName(nameField.getText().trim());
            s.setEmail(emailField.getText().trim());
            s.setPassword(password);
            s.setDepartment((String) departmentBox.getSelectedItem());
            s.setYearOfStudy((Integer) yearSpinner.getValue());
            s.setGender(UIHelper.getSelectedText(genderGroup));
            s.setPhone(UIHelper.phoneText(phoneField));
            s.validate();                                   // may throw InvalidInputException

            if (!termsCheck.isSelected()) {
                throw new InvalidInputException("Please accept the terms and conditions.");
            }

            int id = studentDAO.register(s);                // may throw DuplicateEntryException
            UIHelper.showInfo(this, "Registration successful!\nYour Student ID is " + id + ".\nPlease login now.");
            dispose();

        } catch (InvalidInputException | DuplicateEntryException ex) {
            UIHelper.showError(this, ex.getMessage());
        } catch (SQLException ex) {
            UIHelper.showError(this, "Database error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmField.setText("");
        departmentBox.setSelectedIndex(0);
        yearSpinner.setValue(2);
        genderGroup.clearSelection();
        phoneField.setValue(null);
        showPassword.setSelected(false);
        termsCheck.setSelected(false);
    }
}
