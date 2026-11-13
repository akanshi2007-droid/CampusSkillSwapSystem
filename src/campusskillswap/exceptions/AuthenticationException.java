package campusskillswap.exceptions;

/** CUSTOM EXCEPTION 2 - thrown when login fails. */
public class AuthenticationException extends Exception {
    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }
}
