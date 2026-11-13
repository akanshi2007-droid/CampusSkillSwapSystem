package campusskillswap.ui;

import campusskillswap.dao.OfferDAO;
import campusskillswap.dao.SkillDAO;
import campusskillswap.exceptions.DuplicateEntryException;
import campusskillswap.exceptions.InvalidInputException;
import campusskillswap.model.Skill;
import campusskillswap.model.SkillOffer;
import campusskillswap.util.Session;
import campusskillswap.util.UIHelper;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

/**
 * "Offer a Skill" dialog.
 * Left: form (JComboBox, JTextField, JRadioButton, JSpinner, JTextArea, JCheckBox).
 * Right: JList showing the skills I already offer.
 */
public class OfferSkillDialog extends JDialog {
    private final JComboBox<String> categoryBox = new JComboBox<>(UIHelper.CATEGORIES);
    private final JComboBox<Skill> skillBox = new JComboBox<>();
    private final JCheckBox newSkillCheck = new JCheckBox("My skill is not in the list - add new:");
    private final JTextField newSkillField = new JTextField(15);
    private final JRadioButton beginnerRadio = new JRadioButton("Beginner");
    private final JRadioButton intermediateRadio = new JRadioButton("Intermediate", true);
    private final JRadioButton advancedRadio = new JRadioButton("Advanced");
    private final ButtonGroup levelGroup = new ButtonGroup();
    private final JSpinner experienceSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 20, 1));
    private final JTextArea descriptionArea = new JTextArea(4, 20);
    private final JCheckBox weekendsCheck = new JCheckBox("I can teach only on weekends");
    private final DefaultListModel<SkillOffer> listModel = new DefaultListModel<>();
    private final JList<SkillOffer> myOffersList = new JList<>(listModel);
    private final JButton removeBtn = new JButton("Remove Selected");

    private final SkillDAO skillDAO = new SkillDAO();
    private final OfferDAO offerDAO = new OfferDAO();
    private final Runnable onChange;

    public OfferSkillDialog(Frame owner, Runnable onChange) {
        super(owner, "Offer a Skill", true);
        this.onChange = onChange;
        setSize(900, 560);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // ----- left: form -----
        levelGroup.add(beginnerRadio);
        levelGroup.add(intermediateRadio);
        levelGroup.add(advancedRadio);
        JPanel levelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        levelPanel.add(beginnerRadio);
        levelPanel.add(intermediateRadio);
        levelPanel.add(advancedRadio);

        newSkillField.setEnabled(false);
        JPanel newSkillPanel = new JPanel(new BorderLayout(5, 0));
        newSkillPanel.add(newSkillCheck, BorderLayout.NORTH);
        newSkillPanel.add(newSkillField, BorderLayout.CENTER);

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Skill details"));
        UIHelper.addRow(form, 0, "Category:", categoryBox);
        UIHelper.addRow(form, 1, "Skill:", skillBox);
        UIHelper.addRow(form, 2, "", newSkillPanel);
        UIHelper.addRow(form, 3, "Your level:", levelPanel);
        UIHelper.addRow(form, 4, "Experience (years):", experienceSpinner);
        UIHelper.addRow(form, 5, "Description:", new JScrollPane(descriptionArea));
        UIHelper.addRow(form, 6, "", weekendsCheck);

        JButton offerBtn = new JButton("Offer Skill");
        JButton closeBtn = new JButton("Close");
        offerBtn.addActionListener(e -> offerSkill());
        closeBtn.addActionListener(e -> dispose());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        btnRow.add(offerBtn);
        btnRow.add(closeBtn);

        JPanel left = new JPanel(new BorderLayout());
        left.add(form, BorderLayout.CENTER);
        left.add(btnRow, BorderLayout.SOUTH);
        left.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        add(left, BorderLayout.CENTER);

        // ----- right: my offered skills (JList) -----
        myOffersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        removeBtn.setEnabled(false);
        removeBtn.addActionListener(e -> removeSelected());
        myOffersList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                removeBtn.setEnabled(!myOffersList.isSelectionEmpty());
            }
        });
        JPanel right = new JPanel(new BorderLayout(5, 5));
        right.setBorder(BorderFactory.createTitledBorder("Skills I offer"));
        right.setPreferredSize(new Dimension(280, 0));
        right.add(new JScrollPane(myOffersList), BorderLayout.CENTER);
        right.add(removeBtn, BorderLayout.SOUTH);
        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        rightWrapper.add(right, BorderLayout.CENTER);
        add(rightWrapper, BorderLayout.EAST);

        // ----- events -----
        categoryBox.addActionListener(e -> loadSkillsForCategory());
        newSkillCheck.addItemListener(e -> {
            boolean on = newSkillCheck.isSelected();
            newSkillField.setEnabled(on);
            skillBox.setEnabled(!on);
        });

        loadSkillsForCategory();
        loadMyOffers();
    }

    private void loadSkillsForCategory() {
        skillBox.removeAllItems();
        try {
            for (Skill s : skillDAO.findByCategory((String) categoryBox.getSelectedItem())) {
                skillBox.addItem(s);
            }
        } catch (SQLException ex) {
            UIHelper.showError(this, "Could not load skills: " + ex.getMessage());
        }
    }

    private void loadMyOffers() {
        listModel.clear();
        try {
            for (SkillOffer o : offerDAO.findByStudent(Session.getInstance().getCurrentStudent().getId())) {
                listModel.addElement(o);
            }
        } catch (SQLException ex) {
            UIHelper.showError(this, "Could not load your skills: " + ex.getMessage());
        }
    }

    private void offerSkill() {
        try {
            Skill skill;
            if (newSkillCheck.isSelected()) {
                String name = UIHelper.toTitleCase(newSkillField.getText());
                if (name.isEmpty() || name.length() > 40) {
                    throw new InvalidInputException("Enter a skill name (maximum 40 characters).");
                }
                skill = skillDAO.findByName(name);              // maybe it already exists
                if (skill == null) {
                    skill = new Skill(0, name, (String) categoryBox.getSelectedItem());
                    skill.setId(skillDAO.insert(skill));
                }
            } else {
                skill = (Skill) skillBox.getSelectedItem();
                if (skill == null) {
                    throw new InvalidInputException("Please choose a skill.");
                }
            }

            String description = descriptionArea.getText().trim();
            if (description.isEmpty()) {
                throw new InvalidInputException("Please write a short description.");
            }
            if (description.length() > 200) {
                throw new InvalidInputException("Description must be within 200 characters.");
            }

            SkillOffer offer = new SkillOffer();
            offer.setStudentId(Session.getInstance().getCurrentStudent().getId());
            offer.setSkillId(skill.getId());
            offer.setLevel(UIHelper.getSelectedText(levelGroup));
            offer.setExperienceYears((Integer) experienceSpinner.getValue());
            offer.setDescription(description);
            offer.setWeekendsOnly(weekendsCheck.isSelected());
            offerDAO.addOffer(offer);

            UIHelper.showInfo(this, skill.getName() + " added to your offered skills!");
            descriptionArea.setText("");
            newSkillField.setText("");
            newSkillCheck.setSelected(false);
            loadSkillsForCategory();
            loadMyOffers();
            onChange.run();

        } catch (InvalidInputException | DuplicateEntryException ex) {
            UIHelper.showError(this, ex.getMessage());
        } catch (SQLException ex) {
            UIHelper.showError(this, "Database error: " + ex.getMessage());
        }
    }

    private void removeSelected() {
        SkillOffer selected = myOffersList.getSelectedValue();
        if (selected == null) {
            return;
        }
        if (UIHelper.confirm(this, "Remove \"" + selected.getSkillName() + "\" from your offered skills?")) {
            try {
                offerDAO.delete(selected.getOfferId());
                loadMyOffers();
                onChange.run();
            } catch (SQLException ex) {
                UIHelper.showError(this, "Could not remove skill: " + ex.getMessage());
            }
        }
    }
}
