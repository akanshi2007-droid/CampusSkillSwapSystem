package campusskillswap.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.ParseException;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;

/** Small helper methods used by many screens (keeps the screen classes short). */
public final class UIHelper {
    public static final Color NAVY = new Color(0x1E293B);
    public static final Color SLATE = new Color(0x334155);
    public static final Color BLUE = new Color(0x2563EB);
    public static final Color GREEN = new Color(0x10B981);
    public static final Color PAGE_BG = new Color(0xF8FAFC);
    public static final Color TEXT = new Color(0x2D2D2D);

    public static final String[] CATEGORIES = {"Programming", "Design", "Communication", "Marketing", "Music", "Academics", "Other"};
    public static final String[] DEPARTMENTS = {"CSE", "CSE (Data Science)", "CSE (AI & ML)", "IT", "ECE", "EEE", "Mechanical", "Civil"};
    public static final String[] LEVELS = {"Beginner", "Intermediate", "Advanced"};

    private UIHelper() {
    }

    // ---------- message boxes ----------
    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Campus Skill Swap", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Please confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // ---------- labels and buttons ----------
    public static JLabel label(String text, int size, boolean bold, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(color);
        return l;
    }

    /** Flat coloured button with a hover effect (MouseAdapter = event handling). */
    public static JButton flatButton(String text, Color bg) {
        final JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        final Color normal = bg;
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(normal.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(normal);
            }
        });
        return b;
    }

    // ---------- forms ----------
    /** Adds "Label : field" as one row of a GridBagLayout form. */
    public static void addRow(JPanel panel, int row, String labelText, JComponent field) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = row;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.insets = new Insets(6, 6, 6, 12);
        panel.add(new JLabel(labelText), gc);

        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(4, 0, 4, 6);
        panel.add(field, gc);
    }

    /** JFormattedTextField with a mask: only 10 digits allowed. */
    public static JFormattedTextField createPhoneField() {
        try {
            MaskFormatter mask = new MaskFormatter("##########");
            mask.setPlaceholderCharacter('_');
            return new JFormattedTextField(mask);
        } catch (ParseException e) {
            return new JFormattedTextField();   // fallback if the mask is wrong
        }
    }

    public static String phoneText(JFormattedTextField field) {
        return field.getText().replace("_", "").trim();
    }

    public static void setPhoneText(JFormattedTextField field, String phone) {
        field.setText(phone == null ? "" : phone);
    }

    // ---------- radio buttons ----------
    public static String getSelectedText(ButtonGroup group) {
        Enumeration<AbstractButton> buttons = group.getElements();
        while (buttons.hasMoreElements()) {
            AbstractButton b = buttons.nextElement();
            if (b.isSelected()) {
                return b.getText();
            }
        }
        return "";
    }

    public static void setSelectedText(ButtonGroup group, String text) {
        Enumeration<AbstractButton> buttons = group.getElements();
        while (buttons.hasMoreElements()) {
            AbstractButton b = buttons.nextElement();
            b.setSelected(b.getText().equalsIgnoreCase(text));
        }
    }

    // ---------- text helpers ----------
    /** "web development" -> "Web Development" (String + StringBuilder practice). */
    public static String toTitleCase(String text) {
        StringBuilder sb = new StringBuilder();
        for (String word : text.trim().split("\\s+")) {
            if (word.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    /** Runs the given code every time the text in a text component changes. */
    public static void onTextChange(JTextComponent comp, final Runnable action) {
        comp.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                action.run();
            }
        });
    }

    /** Loads an image file and scales it for a JLabel. Returns null if the file is missing. */
    public static ImageIcon scaledIcon(String path, int w, int h) {
        if (path == null || path.isEmpty() || !new File(path).exists()) {
            return null;
        }
        Image img = new ImageIcon(path).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
