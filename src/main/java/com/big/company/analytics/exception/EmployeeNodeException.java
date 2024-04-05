package com.big.company.analytics.exception;

public class EmployeeNodeException extends RuntimeException {

    public EmployeeNodeException(String errorMessage) {
        super(errorMessage);
    }

    public EmployeeNodeException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
