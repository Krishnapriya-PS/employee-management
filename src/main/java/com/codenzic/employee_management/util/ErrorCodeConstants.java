package com.codenzic.employee_management.util;

public final class ErrorCodeConstants {
    private ErrorCodeConstants() {}

    public static final String VALIDATION_FAILED  = "validation.failed";
    public static final String MALFORMED_REQUEST  = "malformed.request";
    public static final String EMPLOYEE_NOT_FOUND = "employee.not.found";
    public static final String EMPLOYEE_DUPLICATE = "employee.duplicate";
    public static final String DATABASE_ERROR     = "database.error";
    public static final String INTERNAL_ERROR     = "internal.error";
}
