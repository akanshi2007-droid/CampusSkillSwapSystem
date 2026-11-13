package campusskillswap.ui;

import campusskillswap.dao.StudentDAO;
import campusskillswap.dao.SwapRequestDAO;
import campusskillswap.model.Student;
import campusskillswap.model.SwapRequest;
import campusskillswap.util.Session;
import campusskillswap.util.UIHelper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * SWAP REQUEST feature - step 2 and 3.
 * Top table: requests I RECEIVED  (Accept / Reject).
 * Bottom table: requests I SENT   (Cancel / View Contact).
 * A JSplitPane splits the two tables.
 */
public class SwapRequestsDialog extends JDialog {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy, HH:mm");

    private final DefaultTableModel receivedModel = createModel("From");
    private final DefaultTableModel sentModel = createModel("To");
    private final JTable receivedTable = new JTable(receivedModel);
    private final JTable sentTable = new JTable(sentModel);
    private List<SwapRequest> received = new ArrayList<>();
    private List<SwapRequest> sent = new ArrayList<>();

    private final SwapRequestDAO swapDAO = new SwapRequestDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final Student me = Session.getInstance().getCurrentStudent();
    private final Runnable onChange;

    public SwapRequestsDialog(Frame owner, Runnable onChange) {
        super(owner, "Swap Requests", true);
        this.onChange = onChange;
        setSize(880, 620);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        setupTable(receivedTable);
        setupTable(sentTable);

        // ----- top: received -----
        JButton acceptBtn = new JButton("Accept");
        JButton rejectBtn = new JButton("Reject");
        acceptBtn.addActionListener(e -> respond(SwapRequest.ACCEPTED));
        rejectBtn.addActionListener(e -> respond(SwapRequest.REJECTED));
        JPanel receivedButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        receivedButtons.add(acceptBtn);
        receivedButtons.add(rejectBtn);
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createTitledBorder("Requests I received"));
        top.add(new JScrollPane(receivedTable), BorderLayout.CENTER);
        top.add(receivedButtons, BorderLayout.SOUTH);

        // ----- bottom: sent -----
        JButton cancelBtn = new JButton("Cancel Request");
        JButton contactBtn = new JButton("View Contact");
        cancelBtn.addActionListener(e -> cancelRequest());
        contactBtn.addActionListener(e -> viewContact());
        JPanel sentButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sentButtons.add(contactBtn);
        sentButtons.add(cancelBtn);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createTitledBorder("Requests I sent"));
        bottom.add(new JScrollPane(sentTable), BorderLayout.CENTER);
        bottom.add(sentButtons, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, bottom);
        split.setDividerLocation(250);
        split.setResizeWeight(0.5);
        add(split, BorderLayout.CENTER);

        // ----- close / refresh -----
        JButton refreshBtn = new JButton("Refresh");
        JButton closeBtn = new JButton("Close");
        refreshBtn.addActionListener(e -> loadRequests());
        closeBtn.addActionListener(e -> dispose());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        footer.add(refreshBtn);
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        loadRequests();
    }

    private DefaultTableModel createModel(String personColumn) {
        return new DefaultTableModel(new String[]{personColumn, "Skill", "Message", "Status", "Date"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void setupTable(JTable table) {
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());   // coloured status
    }

    private void loadRequests() {
        try {
            received = swapDAO.findReceived(me.getId());
            sent = swapDAO.findSent(me.getId());
        } catch (SQLException ex) {
            UIHelper.showError(this, "Could not load requests: " + ex.getMessage());
            return;
        }
        receivedModel.setRowCount(0);
        for (SwapRequest r : received) {
            receivedModel.addRow(new Object[]{r.getRequesterName(), r.getSkillName(), r.getMessage(),
                    r.getStatus(), DATE_FORMAT.format(r.getRequestDate())});
        }
        sentModel.setRowCount(0);
        for (SwapRequest r : sent) {
            sentModel.addRow(new Object[]{r.getOwnerName(), r.getSkillName(), r.getMessage(),
                    r.getStatus(), DATE_FORMAT.format(r.getRequestDate())});
        }
        onChange.run();
    }

    /** Owner accepts or rejects a request (only while it is PENDING). */
    private void respond(String newStatus) {
        int row = receivedTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a received request first.");
            return;
        }
        SwapRequest r = received.get(row);
        if (!SwapRequest.PENDING.equals(r.getStatus())) {
            UIHelper.showError(this, "This request is already " + r.getStatus().toLowerCase() + ".");
            return;
        }
        try {
            swapDAO.updateStatus(r.getRequestId(), newStatus);
            if (SwapRequest.ACCEPTED.equals(newStatus)) {
                Student requester = studentDAO.findById(r.getRequesterId());
                UIHelper.showInfo(this, "Request accepted!\nContact " + describeContact(requester));
            }
            loadRequests();
        } catch (SQLException ex) {
            UIHelper.showError(this, "Could not update request: " + ex.getMessage());
        }
    }

    private void cancelRequest() {
        int row = sentTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a sent request first.");
            return;
        }
        SwapRequest r = sent.get(row);
        if (!SwapRequest.PENDING.equals(r.getStatus())) {
            UIHelper.showError(this, "Only pending requests can be cancelled.");
            return;
        }
        if (UIHelper.confirm(this, "Cancel your request for " + r.getSkillName() + "?")) {
            try {
                swapDAO.delete(r.getRequestId());
                loadRequests();
            } catch (SQLException ex) {
                UIHelper.showError(this, "Could not cancel request: " + ex.getMessage());
            }
        }
    }

    private void viewContact() {
        int row = sentTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a sent request first.");
            return;
        }
        SwapRequest r = sent.get(row);
        if (!SwapRequest.ACCEPTED.equals(r.getStatus())) {
            UIHelper.showInfo(this, "Contact details are visible after the request is accepted.");
            return;
        }
        try {
            Student owner = studentDAO.findById(r.getOwnerId());
            UIHelper.showInfo(this, "Contact " + describeContact(owner));
        } catch (SQLException ex) {
            UIHelper.showError(this, "Could not load contact: " + ex.getMessage());
        }
    }

    private String describeContact(Student s) {
        if (s == null) {
            return "(student not found)";
        }
        String phone = hasText(s.getPhone()) ? s.getPhone() : "not given";
        return s.getFullName() + "\nEmail: " + s.getEmail() + "\nPhone: " + phone;
    }

    private boolean hasText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /** Custom cell renderer: colours the Status column (green / red / orange). */
    private static class StatusRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                String status = String.valueOf(value);
                if (SwapRequest.ACCEPTED.equals(status)) {
                    c.setForeground(new Color(0x15803D));
                } else if (SwapRequest.REJECTED.equals(status)) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(new Color(0xD97706));
                }
            }
            return c;
        }
    }
}
