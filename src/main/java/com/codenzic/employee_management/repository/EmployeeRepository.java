package com.codenzic.employee_management.repository;

import com.codenzic.employee_management.entity.Employee;
import com.codenzic.employee_management.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);
}
