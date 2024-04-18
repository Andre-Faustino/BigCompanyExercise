package com.big.company.analytics.exception;

/**
 * Custom exception class for handling file reading errors and validations.
 * Thrown when file is not found or is unreadable
 */
public class FileReaderException extends RuntimeException {

    /**
     * Constructs a new FileReaderException with the specified error message.
     *
     * @param errorMessage A String containing the error message.
     */
    public FileReaderException(String errorMessage) {
        super(errorMessage);
    }
}
