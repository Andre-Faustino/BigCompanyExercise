package com.big.company.analytics.exception;

/**
 * Custom exception class for handling file extraction errors and validations.
 * Thrown when file is not found or is unreadable
 */
public class FileExtractionException extends RuntimeException {

    /**
     * Constructs a new FileExtractionException with the specified error message.
     *
     * @param errorMessage A String containing the error message.
     */
    public FileExtractionException(String errorMessage) {
        super(errorMessage);
    }
}
