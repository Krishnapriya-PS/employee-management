package com.codenzic.employee_management.service;

import com.codenzic.employee_management.dto.EmployeeRequest;
import com.codenzic.employee_management.dto.EmployeeResponse;
import com.codenzic.employee_management.entity.Employee;
import com.codenzic.employee_management.entity.EmployeeStatus;
import com.codenzic.employee_management.exception.AppException;
import com.codenzic.employee_management.repository.EmployeeRepository;
import com.codenzic.employee_management.util.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.codenzic.employee_management.dto.PageResponse;
import org.springframework.data.domain.Sort;

@Service
public class EmployeeService {

    private static final int MAX_PAGE_SIZE = 100;
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        String email = normalizeEmail(request.email());

        if (employeeRepository.existsByEmail(email)) {
            throw new AppException(ErrorCodeConstants.EMPLOYEE_DUPLICATE, "Duplicate email: " + email);
        }

        Employee employee = new Employee();
        applyRequest(employee, request, email);
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public EmployeeResponse updateEmployeeIfExists(Long id, EmployeeRequest request) {
        Employee employee = findOrThrow(id);
        String email = normalizeEmail(request.email());

        if (employeeRepository.existsByEmailAndIdNot(email, id)) {
            throw new AppException(ErrorCodeConstants.EMPLOYEE_DUPLICATE, "Duplicate email: " + email);
        }

        applyRequest(employee, request, email);
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public void deleteEmployee(Long id) {
        employeeRepository.delete(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> getEmployees(EmployeeStatus status, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("id"));

        Page<Employee> result = (status == null)
                ? employeeRepository.findAll(pageable)
                : employeeRepository.findByStatus(status, pageable);

        return new PageResponse<>(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast());
    }

    // ---------- helpers ----------

    private Employee findOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCodeConstants.EMPLOYEE_NOT_FOUND,
                        "Employee not found with id " + id));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private void applyRequest(Employee employee, EmployeeRequest request, String email) {
        employee.setFirstName(request.firstName().trim());
        employee.setLastName(request.lastName().trim());
        employee.setEmail(email);
        employee.setPhone(request.phone());
        employee.setDepartment(request.department().trim());
        employee.setDesignation(request.designation().trim());
        employee.setSalary(request.salary());
        // if status is omitted, keep the current value (ACTIVE for a new employee)
        if (request.status() != null) {
            employee.setStatus(request.status());
        }
    }

    private EmployeeResponse toResponse(Employee e) {
        return new EmployeeResponse(
                e.getId(),
                e.getFirstName(),
                e.getLastName(),
                e.getEmail(),
                e.getPhone(),
                e.getDepartment(),
                e.getDesignation(),
                e.getSalary(),
                e.getStatus()
        );
    }
}