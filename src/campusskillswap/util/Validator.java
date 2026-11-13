package campusskillswap.util;

import campusskillswap.exceptions.InvalidInputException;
import java.util.regex.Pattern;

/** All form validation in one place. Uses String methods and regular expressions. */
public final class Validator {
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private Validator() {
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static void validateName(String name) throws InvalidInputException {
        if (isBlank(name)) {
            throw new InvalidInputException("Full name cannot be empty.");
        }
        String n = name.trim();
        if (n.length() < 3 || n.length() > 50) {
            throw new InvalidInputException("Name must be 3 to 50 characters long.");
        }
        if (!n.matches("[A-Za-z ]+")) {
            throw new InvalidInputException("Name can contain only letters and spaces.");
        }
    }

    public static void validateEmail(String email) throws InvalidInputException {
        if (isBlank(email)) {
            throw new InvalidInputException("Email cannot be empty.");
        }
        if (!EMAIL.matcher(email.trim()).matches()) {
            throw new InvalidInputException("Please enter a valid email address.");
        }
    }

    public static void validatePassword(String password) throws InvalidInputException {
        if (password == null || password.length() < 6) {
            throw new InvalidInputException("Password must be at least 6 characters.");
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        if (!hasLetter || !hasDigit) {
            throw new InvalidInputException("Password must contain letters and numbers.");
        }
    }

    public static void validateConfirm(String password, String confirm) throws InvalidInputException {
        if (!password.equals(confirm)) {
            throw new InvalidInputException("Password and confirm password do not match.");
        }
    }

    /** Phone is optional; if given it must be a 10 digit number. */
    public static void validatePhone(String phone) throws InvalidInputException {
        if (isBlank(phone)) {
            return;
        }
        if (!phone.trim().matches("[6-9]\\d{9}")) {
            throw new InvalidInputException("Phone number must be 10 digits.");
        }
    }
}
