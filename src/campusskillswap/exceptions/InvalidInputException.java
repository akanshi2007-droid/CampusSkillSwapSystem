package campusskillswap.exceptions;

/** CUSTOM EXCEPTION 1 - thrown when a form field has wrong data. */
public class InvalidInputException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidInputException(String message) {
        super(message);
    }
}
