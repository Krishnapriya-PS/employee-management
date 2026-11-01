package com.codenzic.employee_management.exception;

public class AppException extends RuntimeException {

    private final String errorKey;

    public AppException(String errorKey, String detail) {
        super(detail);
        this.errorKey = errorKey;
    }

    public AppException(String errorKey, String detail, Throwable cause) {
        super(detail, cause);
        this.errorKey = errorKey;
    }

    public String getErrorKey() {
        return errorKey;
    }
}
