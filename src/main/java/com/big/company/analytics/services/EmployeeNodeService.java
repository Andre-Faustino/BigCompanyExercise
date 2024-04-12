package com.big.company.analytics.services;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.domain.EmployeeNode;
import com.big.company.analytics.exception.EmployeeNodeServiceException;

import java.util.List;

/**
 * Service for generating an employee hierarchy tree.
 */
public interface EmployeeNodeService {

    /**
     * Generates an employee hierarchy tree based on the provided list of employees.
     *
     * @param employees the list of employees to be reported
     * @return {@code EmployeeNode} as root node of the generated employee hierarchy tree
     * @throws EmployeeNodeServiceException if employees list is null or not valid
     */
    EmployeeNode getEmployeesHierarchy(List<Employee> employees);
}
