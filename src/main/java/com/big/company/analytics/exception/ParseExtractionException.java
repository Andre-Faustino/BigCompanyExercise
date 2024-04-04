package com.big.company.analytics.exception;

public class ParseExtractionException extends RuntimeException {

    public ParseExtractionException(String errorMessage) {
        super(errorMessage);
    }

    public ParseExtractionException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
