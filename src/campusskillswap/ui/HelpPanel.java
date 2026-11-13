package campusskillswap.ui;

import campusskillswap.util.UIHelper;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/** "Help" tab: JTree of topics (left) + JEditorPane with HTML text (right) inside a JSplitPane. */
public class HelpPanel extends JPanel {
    private final Map<String, String> topics = new HashMap<>();     // topic name -> help text (HashMap)
    private final JEditorPane viewer = new JEditorPane();

    public HelpPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loadTopics();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Help Topics");
        DefaultMutableTreeNode start = new DefaultMutableTreeNode("Getting Started");
        start.add(new DefaultMutableTreeNode("Register and Login"));
        start.add(new DefaultMutableTreeNode("Update Profile"));
        DefaultMutableTreeNode skills = new DefaultMutableTreeNode("Skills");
        skills.add(new DefaultMutableTreeNode("Offer a Skill"));
        skills.add(new DefaultMutableTreeNode("Find a Skill"));
        DefaultMutableTreeNode swaps = new DefaultMutableTreeNode("Swap Requests");
        swaps.add(new DefaultMutableTreeNode("Send a Request"));
        swaps.add(new DefaultMutableTreeNode("Accept or Reject"));
        root.add(start);
        root.add(skills);
        root.add(swaps);

        JTree tree = new JTree(root);
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);                  // show all topics expanded
        }
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null) {
                String text = topics.get(node.getUserObject().toString());
                viewer.setText(text != null ? text : topics.get("Help Topics"));
                viewer.setCaretPosition(0);
            }
        });

        viewer.setContentType("text/html");
        viewer.setEditable(false);
        viewer.setText(topics.get("Help Topics"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree), new JScrollPane(viewer));
        split.setDividerLocation(220);
        add(split, BorderLayout.CENTER);
    }

    private void loadTopics() {
        topics.put("Help Topics", page("Welcome to Campus Skill Swap",
                "Select a topic on the left to learn how to use the application."));
        topics.put("Getting Started", page("Getting Started",
                "Register once, then login with your email and password. Complete your profile to reach 100%."));
        topics.put("Register and Login", page("Register and Login",
                "Click <b>Register</b> on the first page and fill in the form. Password needs letters and numbers (min 6 characters). Then click <b>Login</b>."));
        topics.put("Update Profile", page("Update Profile",
                "Use <b>My Profile</b> on the Overview tab (or press Ctrl+P). You can add a photo, phone number and bio."));
        topics.put("Skills", page("Skills",
                "You can <b>offer</b> skills you know and <b>find</b> skills you want to learn."));
        topics.put("Offer a Skill", page("Offer a Skill",
                "Click <b>Offer a Skill</b>, choose category and skill, select your level, write a short description and click <b>Offer Skill</b>."));
        topics.put("Find a Skill", page("Find a Skill",
                "Open <b>Explore Skills</b>. Filter by category or type in the search box. Select a row and click <b>Request Skill</b>."));
        topics.put("Swap Requests", page("Swap Requests",
                "Open <b>Swap Requests</b> to see requests you received and requests you sent."));
        topics.put("Send a Request", page("Send a Request",
                "Select a skill in Explore Skills, click <b>Request Skill</b> and write a short message to the student."));
        topics.put("Accept or Reject", page("Accept or Reject",
                "In <b>Swap Requests</b> select a pending request and click <b>Accept</b> or <b>Reject</b>. After accepting you can see the student's contact details."));
    }

    private String page(String title, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:SansSerif; padding:10px;'>");
        sb.append("<h2>").append(title).append("</h2>");
        sb.append("<p>").append(body).append("</p>");
        sb.append("</body></html>");
        return sb.toString();
    }
}
