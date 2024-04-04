package com.big.company.analytics.exception;

public class FileExtractionException extends RuntimeException {

    public FileExtractionException(String errorMessage) {
        super(errorMessage);
    }

    public FileExtractionException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
