package com.big.company.analytics.exception;

/**
 * Custom exception class for handling employee-node errors and validations.
 * This exception is thrown when creating Employee Node or when add an invalid employee to node e.g. null employee or employee without manager
 */
public class EmployeeNodeException extends RuntimeException {

    /**
     * Constructs a new EmployeeNodeException with the specified error message.
     *
     * @param errorMessage A String containing the error message.
     */
    public EmployeeNodeException(String errorMessage) {
        super(errorMessage);
    }
}