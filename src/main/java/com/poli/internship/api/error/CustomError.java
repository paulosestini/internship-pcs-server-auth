package com.poli.internship.api.error;

import org.springframework.graphql.execution.ErrorType;

public class CustomError extends RuntimeException {
    private ErrorType errorType;
    public CustomError(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }

}
