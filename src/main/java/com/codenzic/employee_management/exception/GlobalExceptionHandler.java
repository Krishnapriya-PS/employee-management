package com.codenzic.employee_management.exception;

import com.codenzic.employee_management.dto.ErrorResponse;
import com.codenzic.employee_management.dto.ErrorResponse.ErrorDetail;
import com.codenzic.employee_management.entity.ErrorCode;
import com.codenzic.employee_management.service.ErrorCodeService;
import com.codenzic.employee_management.util.ErrorCodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ErrorCodeService errorCodeService;

    public GlobalExceptionHandler(ErrorCodeService errorCodeService) {
        this.errorCodeService = errorCodeService;
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleApp(AppException ex) {
        log.error("AppException key={} detail={}", ex.getErrorKey(), ex.getMessage(), ex);
        return build(ex.getErrorKey(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        // the field messages from the DTO annotations become the user message
        String fieldMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .distinct()
                .collect(Collectors.joining("; "));
        log.warn("Validation failed: {}", fieldMessages);
        return build(ErrorCodeConstants.VALIDATION_FAILED, fieldMessages);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return build(ErrorCodeConstants.MALFORMED_REQUEST, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrity(DataIntegrityViolationException ex) {
        log.error("Data integrity violation", ex);
        return build(ErrorCodeConstants.EMPLOYEE_DUPLICATE, null);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDb(DataAccessException ex) {
        log.error("Database exception", ex);
        return build(ErrorCodeConstants.DATABASE_ERROR, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return build(ErrorCodeConstants.INTERNAL_ERROR, null);
    }

    /**
     * Builds the response from the error_codes row.
     * userMessageOverride is used only for validation, where the user message
     * is the specific field message (e.g. "Email is required").
     */
    private ResponseEntity<ErrorResponse> build(String errorKey, String userMessageOverride) {
        ErrorCode def = errorCodeService.get(errorKey);
        String userMessage = (userMessageOverride != null && !userMessageOverride.isBlank())
                ? userMessageOverride
                : def.getUserMessage();

        ErrorDetail detail = new ErrorDetail(def.getCode(), def.getSystemMessage(), userMessage);
        return ResponseEntity.status(def.getHttpStatus()).body(new ErrorResponse(detail));
    }
}