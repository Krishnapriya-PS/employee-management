package com.codenzic.employee_management.dto;

public record ErrorResponse(ErrorDetail errors) {

    public record ErrorDetail(int code, String systemMessage, String userMessage) {}
}
