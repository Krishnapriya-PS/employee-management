package com.codenzic.employee_management.dto;

import com.codenzic.employee_management.entity.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record EmployeeRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must be at most 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must be at most 50 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email must be at most 100 characters")
        String email,

        @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be exactly 10 digits")
        String phone,

        @NotBlank(message = "Department is required")
        @Size(max = 50, message = "Department must be at most 50 characters")
        String department,

        @NotBlank(message = "Designation is required")
        @Size(max = 50, message = "Designation must be at most 50 characters")
        String designation,

        @NotNull(message = "Salary is required")
        @Positive(message = "Salary must be greater than 0")
        BigDecimal salary,

        EmployeeStatus status
) {}
