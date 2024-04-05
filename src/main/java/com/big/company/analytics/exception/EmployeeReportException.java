package com.big.company.analytics.exception;

public class EmployeeReportException extends RuntimeException {

    public EmployeeReportException(String errorMessage) {
        super(errorMessage);
    }

    public EmployeeReportException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
