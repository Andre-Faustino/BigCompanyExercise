package com.big.company.analytics.services.impl;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.domain.EmployeeNode;
import com.big.company.analytics.exception.EmployeeException;
import com.big.company.analytics.exception.EmployeeNodeException;
import com.big.company.analytics.exception.EmployeeNodeServiceException;
import com.big.company.analytics.services.EmployeeNodeService;
import com.big.company.analytics.util.EmployeeUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates an employee hierarchy based on the provided list of employees.
 * The employee hierarchy is built upon {@link EmployeeNode} class.
 */
public class EmployeeNodeGenerator implements EmployeeNodeService {

    /**
     * {@inheritDoc}
     */
    @Override
    public EmployeeNode generateEmployeesHierarchy(List<Employee> employees) {
        Objects.requireNonNull(employees, "Employees list must not be null");
        try {
            Employee ceo = EmployeeUtils.findCEO(employees);
            EmployeeNode root = new EmployeeNode(ceo);
            return this.addUnorderedEmployeesToHierarchy(root, employees);
        } catch (EmployeeNodeException | EmployeeException e) {
            throw new EmployeeNodeServiceException(String.format("Error when creating Employee Hierarchy | %s", e.getMessage()));
        }
    }

    /**
     * Adds unordered employees to the employee hierarchy.
     *
     * @param root      root of employee node hierarchy
     * @param employees the list of employees to be added to the hierarchy
     * @return the root node of the employee hierarchy
     */
    private EmployeeNode addUnorderedEmployeesToHierarchy(EmployeeNode root, List<Employee> employees) {
        Deque<Employee> validEmployeesQueue = removeEmployeesWithoutValidManagers(employees);

        int cursor = 0;
        int queueSize = validEmployeesQueue.size();
        boolean retry = false;
        while (!validEmployeesQueue.isEmpty()) {
            if (cursor >= queueSize) {
                if (!retry) break;
                cursor = 0;
                queueSize = validEmployeesQueue.size();
                retry = false;
            }

            Employee employee = validEmployeesQueue.pop();
            if (!root.addEmployee(employee)) {
                validEmployeesQueue.addLast(employee);
                retry = true;
            }
            cursor++;
        }
        return root;
    }

    /**
     * Remove employees that doesn't have a manager id or its manager id was not found in the list of employees.
     *
     * @param employees the list of employees to be validated
     * @return a deque of valid employees
     */
    private Deque<Employee> removeEmployeesWithoutValidManagers(List<Employee> employees) {
        Set<Integer> ids = new HashSet<>(employees.stream().map(Employee::id).toList());
        return employees.stream()
                .filter(employee -> {
                    if (employee.getManagerId().isEmpty()) return false;
                    if (!ids.contains(employee.getManagerId().get())) {
                        System.out.printf("Warning -> Removing employee with id %d due no manager id %d was found on the list%n", employee.id(), employee.getManagerId().get());
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toCollection(ArrayDeque::new));
    }
}
