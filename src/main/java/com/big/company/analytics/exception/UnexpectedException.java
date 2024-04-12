package com.big.company.analytics.exception;

/**
 * Custom exception class for unexpected errors.
 * This exception should be only thrown by the MainApplication for catch unexpected exceptions
 */
public class UnexpectedException extends RuntimeException {

    /**
     * Constructs a new UnexpectedException with the specified error message.
     *
     * @param errorMessage A String containing the error message.
     */
    public UnexpectedException(String errorMessage) {
        super(errorMessage);
    }
}
