package campusskillswap.ui;

import campusskillswap.dao.StudentDAO;
import campusskillswap.exceptions.InvalidInputException;
import campusskillswap.model.Student;
import campusskillswap.util.Session;
import campusskillswap.util.UIHelper;
import campusskillswap.util.Validator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.io.File;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/** "My Profile" dialog: view and edit profile, choose a photo (JFileChooser), change password. */
public class ProfileDialog extends JDialog {
    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JComboBox<String> departmentBox = new JComboBox<>(UIHelper.DEPARTMENTS);
    private final JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
    private final JRadioButton maleRadio = new JRadioButton("Male");
    private final JRadioButton femaleRadio = new JRadioButton("Female");
    private final JRadioButton otherRadio = new JRadioButton("Other");
    private final ButtonGroup genderGroup = new ButtonGroup();
    private final JFormattedTextField phoneField = UIHelper.createPhoneField();
    private final JTextArea bioArea = new JTextArea(4, 20);
    private final JLabel bioCounter = new JLabel("0 / 300");
    private final JPasswordField newPasswordField = new JPasswordField(20);
    private final JLabel photoLabel = new JLabel("No photo", SwingConstants.CENTER);
    private String photoPath;

    private final StudentDAO studentDAO = new StudentDAO();
    private final Student current = Session.getInstance().getCurrentStudent();
    private final Runnable onSaved;

    public ProfileDialog(Frame owner, Runnable onSaved) {
        super(owner, "My Profile", true);
        this.onSaved = onSaved;
        setSize(620, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // ----- photo area (top) -----
        photoLabel.setPreferredSize(new Dimension(110, 110));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JButton chooseBtn = new JButton("Choose Photo...");
        chooseBtn.addActionListener(e -> choosePhoto());
        JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        photoPanel.add(photoLabel);
        photoPanel.add(chooseBtn);
        add(photoPanel, BorderLayout.NORTH);

        // ----- form -----
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderGroup.add(otherRadio);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        genderPanel.add(otherRadio);

        emailField.setEditable(false);                       // email cannot be changed
        emailField.setBackground(new Color(0xE5E7EB));
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        UIHelper.onTextChange(bioArea, () -> {
            int len = bioArea.getText().length();
            bioCounter.setText(len + " / 300");
            bioCounter.setForeground(len > 300 ? Color.RED : Color.BLACK);
        });
        JPanel bioPanel = new JPanel(new BorderLayout());
        bioPanel.add(new JScrollPane(bioArea), BorderLayout.CENTER);
        bioPanel.add(bioCounter, BorderLayout.SOUTH);
        newPasswordField.setToolTipText("Leave empty to keep your current password");

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 30));
        UIHelper.addRow(form, 0, "Student ID:", new JLabel(String.valueOf(current.getId())));
        UIHelper.addRow(form, 1, "Full Name:", nameField);
        UIHelper.addRow(form, 2, "Email:", emailField);
        UIHelper.addRow(form, 3, "Department:", departmentBox);
        UIHelper.addRow(form, 4, "Year of Study:", yearSpinner);
        UIHelper.addRow(form, 5, "Gender:", genderPanel);
        UIHelper.addRow(form, 6, "Phone:", phoneField);
        UIHelper.addRow(form, 7, "Bio:", bioPanel);
        UIHelper.addRow(form, 8, "New Password:", newPasswordField);
        add(form, BorderLayout.CENTER);

        // ----- buttons -----
        JButton saveBtn = new JButton("Save Changes");
        JButton cancelBtn = new JButton("Cancel");
        saveBtn.addActionListener(e -> save());
        cancelBtn.addActionListener(e -> dispose());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnRow.add(saveBtn);
        btnRow.add(cancelBtn);
        add(btnRow, BorderLayout.SOUTH);

        loadValues();
    }

    private void loadValues() {
        nameField.setText(current.getFullName());
        emailField.setText(current.getEmail());
        departmentBox.setSelectedItem(current.getDepartment());
        yearSpinner.setValue(Math.max(1, current.getYearOfStudy()));
        UIHelper.setSelectedText(genderGroup, current.getGender());
        UIHelper.setPhoneText(phoneField, current.getPhone());
        bioArea.setText(current.getBio() == null ? "" : current.getBio());
        photoPath = current.getPhotoPath();
        showPhoto();
    }

    private void showPhoto() {
        ImageIcon icon = UIHelper.scaledIcon(photoPath, 110, 110);
        photoLabel.setIcon(icon);
        photoLabel.setText(icon == null ? "No photo" : "");
    }

    private void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose profile photo");
        chooser.setFileFilter(new FileNameExtensionFilter("Images (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            photoPath = file.getAbsolutePath();
            showPhoto();
        }
    }

    private void save() {
        try {
            Student s = new Student();
            s.setId(current.getId());
            s.setFullName(nameField.getText().trim());
            s.setEmail(current.getEmail());
            s.setDepartment((String) departmentBox.getSelectedItem());
            s.setYearOfStudy((Integer) yearSpinner.getValue());
            s.setGender(UIHelper.getSelectedText(genderGroup));
            s.setPhone(UIHelper.phoneText(phoneField));
            s.setBio(bioArea.getText().trim());
            s.setPhotoPath(photoPath);
            s.validate();

            String newPassword = new String(newPasswordField.getPassword());
            if (!newPassword.isEmpty()) {
                Validator.validatePassword(newPassword);
            }

            studentDAO.update(s);
            if (!newPassword.isEmpty()) {
                studentDAO.updatePassword(s.getId(), newPassword);
            }
            Session.getInstance().setCurrentStudent(s);
            UIHelper.showInfo(this, "Profile updated successfully!");
            dispose();
            onSaved.run();

        } catch (InvalidInputException ex) {
            UIHelper.showError(this, ex.getMessage());
        } catch (SQLException ex) {
            UIHelper.showError(this, "Database error: " + ex.getMessage());
        }
    }
}
