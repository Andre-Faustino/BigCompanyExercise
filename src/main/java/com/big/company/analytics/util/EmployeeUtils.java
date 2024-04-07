package com.big.company.analytics.util;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.exception.EmployeeException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for operations related to employees.
 */
public abstract class EmployeeUtils {

    /**
     * Finds the CEO (Chief Executive Officer) from a list of employees.
     * The CEO is defined by not having a manager.
     *
     * @param employees List of employees
     * @return The CEO Employee object
     * @throws EmployeeException if the list has more than one CEO or no CEO found
     */
    public static Employee findCEO(List<Employee> employees) {
        List<Employee> cEOCandidates = employees.stream()
                .filter(employee -> employee.getManagerId().isEmpty())
                .toList();
        if (cEOCandidates.size() > 1) throw new EmployeeException("Employee list has more than one CEO");
        if (cEOCandidates.isEmpty()) throw new EmployeeException("Employee list has no CEO");
        return cEOCandidates.get(0);
    }
}
