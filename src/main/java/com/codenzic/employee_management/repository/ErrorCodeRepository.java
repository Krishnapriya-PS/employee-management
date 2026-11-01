package com.codenzic.employee_management.repository;

import com.codenzic.employee_management.entity.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorCodeRepository extends JpaRepository<ErrorCode, Integer> {
}
