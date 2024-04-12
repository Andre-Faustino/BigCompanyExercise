package com.big.company.analytics.exception;

/**
 * Custom exception class for handling employee-node service errors and validation.
 * This exception is thrown when creating Employee Node or when add an invalid employee to node e.g. null employee or employee without manager
 */
public class EmployeeNodeServiceException extends RuntimeException {

    /**
     * Constructs a new EmployeeNodeServiceException with the specified error message.
     *
     * @param errorMessage A String containing the error message.
     */
    public EmployeeNodeServiceException(String errorMessage) {
        super(errorMessage);
    }
}