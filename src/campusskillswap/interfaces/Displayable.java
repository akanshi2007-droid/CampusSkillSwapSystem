package campusskillswap.interfaces;

/**
 * INTERFACE 2 - Displayable
 * Gives every model a readable text for lists and dialogs.
 * Contains a DEFAULT method (Java 8 feature).
 */
public interface Displayable {
    String getDisplayText();

    // default method: classes get this for free, they may override it
    default String getShortText() {
        String text = getDisplayText();
        return text.length() > 30 ? text.substring(0, 27) + "..." : text;
    }
}
