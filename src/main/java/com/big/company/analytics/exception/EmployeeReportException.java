package com.big.company.analytics.exception;

/**
 * Custom exception class for handling employees report errors and validations.
 * Thrown when there are error loading employees list to generate reports or generate a report without load the employees list
 */
public class EmployeeReportException extends RuntimeException {

    /**
     * Constructs a new EmployeeReportException with the specified error message.
     *
     * @param errorMessage A String containing the error message.
     */
    public EmployeeReportException(String errorMessage) {
        super(errorMessage);
    }
}
