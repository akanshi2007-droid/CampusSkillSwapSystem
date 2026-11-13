package campusskillswap.ui;

import campusskillswap.dao.PreferenceDAO;
import campusskillswap.interfaces.Refreshable;
import campusskillswap.model.StudentPreference;
import campusskillswap.util.Session;
import campusskillswap.util.UIHelper;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

/** "My Preferences" tab: radio buttons, check boxes, slider, combo box, toggle button, text area. */
public class PreferencesPanel extends JPanel implements Refreshable {
    private final JRadioButton onlineRadio = new JRadioButton("Online");
    private final JRadioButton offlineRadio = new JRadioButton("Offline");
    private final JRadioButton bothRadio = new JRadioButton("Both", true);
    private final ButtonGroup modeGroup = new ButtonGroup();
    private final JCheckBox weekendsCheck = new JCheckBox("I am free on weekends");
    private final JCheckBox emailCheck = new JCheckBox("Send me email alerts for swap requests");
    private final JSlider hoursSlider = new JSlider(1, 20, 5);
    private final JLabel hoursLabel = new JLabel("5 hours / week");
    private final JComboBox<String> interestBox = new JComboBox<>(UIHelper.CATEGORIES);
    private final JToggleButton availableToggle = new JToggleButton("Available for swaps: YES", true);
    private final JTextArea notesArea = new JTextArea(4, 25);
    private final PreferenceDAO dao = new PreferenceDAO();

    public PreferencesPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        modeGroup.add(onlineRadio);
        modeGroup.add(offlineRadio);
        modeGroup.add(bothRadio);
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        modePanel.add(onlineRadio);
        modePanel.add(offlineRadio);
        modePanel.add(bothRadio);

        hoursSlider.setMajorTickSpacing(5);
        hoursSlider.setMinorTickSpacing(1);
        hoursSlider.setPaintTicks(true);
        hoursSlider.setPaintLabels(true);
        hoursSlider.addChangeListener(e -> hoursLabel.setText(hoursSlider.getValue() + " hours / week"));
        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.add(hoursSlider, BorderLayout.CENTER);
        sliderPanel.add(hoursLabel, BorderLayout.EAST);

        availableToggle.addItemListener(e ->
                availableToggle.setText("Available for swaps: " + (availableToggle.isSelected() ? "YES" : "NO")));

        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        UIHelper.addRow(form, 0, "Preferred mode:", modePanel);
        UIHelper.addRow(form, 1, "Availability:", weekendsCheck);
        UIHelper.addRow(form, 2, "", emailCheck);
        UIHelper.addRow(form, 3, "Time for swaps:", sliderPanel);
        UIHelper.addRow(form, 4, "Interested in:", interestBox);
        UIHelper.addRow(form, 5, "Status:", availableToggle);
        UIHelper.addRow(form, 6, "Notes:", new JScrollPane(notesArea));
        add(form, BorderLayout.NORTH);

        JButton saveBtn = new JButton("Save Preferences");
        JButton resetBtn = new JButton("Reset");
        saveBtn.addActionListener(e -> save());
        resetBtn.addActionListener(e -> refreshData());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnRow.add(saveBtn);
        btnRow.add(resetBtn);
        add(btnRow, BorderLayout.CENTER);

        refreshData();
    }

    /** Loads saved preferences (or defaults if the student never saved any). */
    @Override
    public void refreshData() {
        StudentPreference p = null;
        try {
            p = dao.find(Session.getInstance().getCurrentStudent().getId());
        } catch (SQLException ex) {
            System.out.println("Could not load preferences: " + ex.getMessage());
        }
        if (p == null) {
            p = new StudentPreference();
        }
        UIHelper.setSelectedText(modeGroup, p.getMode());
        weekendsCheck.setSelected(p.isWeekends());
        emailCheck.setSelected(p.isEmailAlerts());
        hoursSlider.setValue(p.getHoursPerWeek());
        interestBox.setSelectedItem(p.getInterestCategory());
        availableToggle.setSelected(p.isAvailableNow());
        notesArea.setText(p.getNotes());
    }

    private void save() {
        StudentPreference p = new StudentPreference();
        p.setStudentId(Session.getInstance().getCurrentStudent().getId());
        p.setMode(UIHelper.getSelectedText(modeGroup));
        p.setWeekends(weekendsCheck.isSelected());
        p.setEmailAlerts(emailCheck.isSelected());
        p.setHoursPerWeek(hoursSlider.getValue());
        p.setInterestCategory((String) interestBox.getSelectedItem());
        p.setAvailableNow(availableToggle.isSelected());
        p.setNotes(notesArea.getText().trim());
        try {
            dao.save(p);
            UIHelper.showInfo(this, "Preferences saved successfully!");
        } catch (SQLException ex) {
            UIHelper.showError(this, "Could not save preferences: " + ex.getMessage());
        }
    }
}
