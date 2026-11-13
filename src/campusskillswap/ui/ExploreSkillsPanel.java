package campusskillswap.ui;

import campusskillswap.dao.OfferDAO;
import campusskillswap.dao.SwapRequestDAO;
import campusskillswap.interfaces.Refreshable;
import campusskillswap.model.SkillOffer;
import campusskillswap.model.Student;
import campusskillswap.model.SwapRequest;
import campusskillswap.util.Session;
import campusskillswap.util.UIHelper;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * "Explore Skills" tab - category filter (JComboBox), search (JTextField),
 * skills table (JTable), right-click menu (JPopupMenu) and "Request Skill" button.
 * Also used on the first page (loggedIn = false) for public browsing.
 */
public class ExploreSkillsPanel extends JPanel implements Refreshable {
    private final boolean loggedIn;
    private final JComboBox<String> categoryBox;
    private final JTextField searchField = new JTextField(15);
    private final JLabel countLabel = new JLabel(" ");
    private final DefaultTableModel model;
    private final JTable table;
    private List<SkillOffer> offers = new ArrayList<>();
    private final OfferDAO offerDAO = new OfferDAO();
    private final SwapRequestDAO swapDAO = new SwapRequestDAO();

    public ExploreSkillsPanel(boolean loggedIn) {
        this.loggedIn = loggedIn;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ----- top: filter row -----
        String[] categories = new String[UIHelper.CATEGORIES.length + 1];
        categories[0] = "All";
        System.arraycopy(UIHelper.CATEGORIES, 0, categories, 1, UIHelper.CATEGORIES.length);
        categoryBox = new JComboBox<>(categories);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterRow.add(UIHelper.label("Category:", 14, true, UIHelper.TEXT));
        filterRow.add(categoryBox);
        filterRow.add(UIHelper.label("Search:", 14, true, UIHelper.TEXT));
        filterRow.add(searchField);
        searchField.setToolTipText("Type a skill name or student name");
        add(filterRow, BorderLayout.NORTH);

        // ----- centre: table (cells are not editable) -----
        model = new DefaultTableModel(new String[]{"Skill", "Category", "Level", "Offered By", "Experience (yrs)"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);                 // click a column header to sort
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ----- bottom: buttons -----
        JButton detailsBtn = new JButton("View Details");
        JButton requestBtn = new JButton("Request Skill");
        detailsBtn.addActionListener(e -> showDetails());
        requestBtn.addActionListener(e -> requestSelectedSkill());
        JPanel bottom = new JPanel(new BorderLayout());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.add(detailsBtn);
        btnRow.add(requestBtn);
        bottom.add(countLabel, BorderLayout.WEST);
        bottom.add(btnRow, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        // ----- events -----
        categoryBox.addActionListener(e -> loadOffers());
        UIHelper.onTextChange(searchField, this::loadOffers);

        final JPopupMenu popup = new JPopupMenu();
        JMenuItem popupRequest = new JMenuItem("Request this skill");
        JMenuItem popupDetails = new JMenuItem("View details");
        popupRequest.addActionListener(e -> requestSelectedSkill());
        popupDetails.addActionListener(e -> showDetails());
        popup.add(popupRequest);
        popup.add(popupDetails);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showDetails();                      // double click = details
                }
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        table.setRowSelectionInterval(row, row);
                        popup.show(table, e.getX(), e.getY());
                    }
                }
            }
        });

        loadOffers();
    }

    @Override
    public void refreshData() {
        loadOffers();
    }

    private void loadOffers() {
        try {
            offers = offerDAO.search((String) categoryBox.getSelectedItem(), searchField.getText());
            model.setRowCount(0);
            for (SkillOffer o : offers) {
                model.addRow(new Object[]{o.getSkillName(), o.getCategory(), o.getLevel(), o.getOwnerName(), o.getExperienceYears()});
            }
            countLabel.setText(offers.size() + " skill(s) found");
        } catch (SQLException ex) {
            countLabel.setText("Could not load skills");
            System.out.println("Load skills failed: " + ex.getMessage());
        }
    }

    private SkillOffer getSelectedOffer() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);  // needed because the table can be sorted
        return offers.get(modelRow);
    }

    private void showDetails() {
        SkillOffer o = getSelectedOffer();
        if (o == null) {
            UIHelper.showError(this, "Please select a skill from the table first.");
            return;
        }
        String text = String.format("Skill: %s%nCategory: %s%nLevel: %s%nOffered by: %s%nExperience: %d year(s)%nWeekends only: %s%n%nAbout:%n%s",
                o.getSkillName(), o.getCategory(), o.getLevel(), o.getOwnerName(),
                o.getExperienceYears(), o.isWeekendsOnly() ? "Yes" : "No", o.getDescription());
        JOptionPane.showMessageDialog(this, text, "Skill Details", JOptionPane.INFORMATION_MESSAGE);
    }

    /** SWAP REQUEST feature - step 1: a student requests a skill. */
    private void requestSelectedSkill() {
        if (!loggedIn) {
            UIHelper.showInfo(this, "Please login or register first to request a skill.");
            return;
        }
        SkillOffer offer = getSelectedOffer();
        if (offer == null) {
            UIHelper.showError(this, "Please select a skill from the table first.");
            return;
        }
        Student me = Session.getInstance().getCurrentStudent();
        if (offer.getStudentId() == me.getId()) {
            UIHelper.showError(this, "This is your own skill. You cannot request it.");
            return;
        }
        String message = JOptionPane.showInputDialog(this,
                "Write a short message to " + offer.getOwnerName() + ":",
                "Request " + offer.getSkillName(), JOptionPane.QUESTION_MESSAGE);
        if (message == null) {
            return;                                             // user pressed Cancel
        }
        message = message.trim();
        if (message.isEmpty()) {
            message = "Hi, I would like to learn " + offer.getSkillName() + " from you.";
        }
        if (message.length() > 200) {
            message = message.substring(0, 200);
        }

        try {
            if (swapDAO.alreadyRequested(me.getId(), offer.getOfferId())) {
                UIHelper.showInfo(this, "You already have a pending request for this skill.");
                return;
            }
            SwapRequest r = new SwapRequest();
            r.setRequesterId(me.getId());
            r.setOfferId(offer.getOfferId());
            r.setMessage(message);
            swapDAO.insert(r);
            UIHelper.showInfo(this, "Request sent to " + offer.getOwnerName() + "!\nCheck 'Swap Requests' for the reply.");
        } catch (SQLException ex) {
            UIHelper.showError(this, "Could not send request: " + ex.getMessage());
        }
    }
}
