package campusskillswap.interfaces;

import campusskillswap.exceptions.InvalidInputException;

/**
 * INTERFACE 1 - Validatable
 * Any model class that can check its own data implements this interface.
 * (Student implements it.)
 */
public interface Validatable {
    void validate() throws InvalidInputException;
}
