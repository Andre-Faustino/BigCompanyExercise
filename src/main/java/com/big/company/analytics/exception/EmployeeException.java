package com.big.company.analytics.exception;

public class EmployeeException extends RuntimeException {

    public EmployeeException(String errorMessage) {
        super(errorMessage);
    }

    public EmployeeException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
