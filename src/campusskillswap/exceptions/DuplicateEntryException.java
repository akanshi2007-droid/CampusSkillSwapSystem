package campusskillswap.exceptions;

/** CUSTOM EXCEPTION 3 - thrown for duplicate email / duplicate skill offer / duplicate request. */
public class DuplicateEntryException extends Exception {
    private static final long serialVersionUID = 1L;

    public DuplicateEntryException(String message) {
        super(message);
    }
}
