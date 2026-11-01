package com.codenzic.employee_management.dto;

import com.codenzic.employee_management.entity.EmployeeStatus;
import java.math.BigDecimal;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String department,
        String designation,
        BigDecimal salary,
        EmployeeStatus status
) {}