package com.big.company.analytics.exception;

/**
 * Custom exception class for handling parse errors and validations in file extraction.
 * Thrown when there are errors when parsing the file to the target object
 */
public class ParseExtractionException extends RuntimeException {

    /**
     * Constructs a new ParseExtractionException with the specified error message.
     *
     * @param errorMessage A String containing the error message.
     */
    public ParseExtractionException(String errorMessage) {
        super(errorMessage);
    }
}
