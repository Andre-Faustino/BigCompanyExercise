package com.big.company.analytics.exception;

/**
 * Custom exception class for handling employee-related errors and validations.
 * This exception is thrown when creating Employee objects or validating list of employees e.g. only one ceo allowed
 */
public class EmployeeException extends RuntimeException {

    /**
     * Constructs a new EmployeeException with the specified error message.
     *
     * @param errorMessage A String containing the error message.
     */
    public EmployeeException(String errorMessage) {
        super(errorMessage);
    }
}
