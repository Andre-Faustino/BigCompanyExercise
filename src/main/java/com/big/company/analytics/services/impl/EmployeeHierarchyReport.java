package com.big.company.analytics.services.impl;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.domain.EmployeeNode;
import com.big.company.analytics.exception.EmployeeException;
import com.big.company.analytics.exception.EmployeeNodeException;
import com.big.company.analytics.exception.EmployeeReportException;
import com.big.company.analytics.services.EmployeeReport;
import com.big.company.analytics.util.EmployeeUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the {@code EmployeeReport} interface that generates reports based on
 * the hierarchy (N-tree) of employees.
 */
public class EmployeeHierarchyReport implements EmployeeReport {

    /**
     * Default threshold value for reporting lines be considered excessive.
     */
    private static final int DEFAULT_REPORTING_LINES_THRESHOLD = 4;

    /**
     * Default minimum percentage for salary policy violation.
     * Manager salary should be a minimum percentage (20%) more than its subordinate's average salary
     */
    private static final int DEFAULT_MINIMUM_PERCENTAGE = 20;

    /**
     * Default maximum percentage for salary policy violation.
     * Manager salary should NOT be a maximum percentage (50%) more than its subordinate's average salary
     */
    private static final int DEFAULT_MAXIMUM_PERCENTAGE = 50;

    /**
     * Root node of the employee hierarchy (CEO)
     */
    private EmployeeNode employeeHierarchy;

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Integer inputEmployees(List<Employee> employees) {
        if (employees == null) throw new EmployeeReportException("Employees list should not be null");
        try {
            Employee root = EmployeeUtils.findCEO(employees);
            employeeHierarchy = new EmployeeNode(root);

            return this.addUnorderedEmployeesToHierarchy(employees) + 1;
        } catch (EmployeeNodeException | EmployeeException e) {
            throw new EmployeeReportException(String.format("Error when creating Employee Hierarchy | %s", e.getMessage()));
        }
    }

    /**
     * Handle and add unordered employees to the employee hierarchy.
     *
     * @param employees the list of employees to be added to the hierarchy
     * @return the number of employees successfully added to the hierarchy
     */
    private int addUnorderedEmployeesToHierarchy(List<Employee> employees) {
        Deque<Employee> validEmployeesQueue = removeEmployeesWithoutValidManagers(employees);
        int validEmployeesNumber = validEmployeesQueue.size();

        int cursor = 0;
        int queueSize = validEmployeesNumber;
        boolean retry = false;
        while (!validEmployeesQueue.isEmpty()) {
            if (cursor >= queueSize) {
                if (!retry) break;
                cursor = 0;
                queueSize = validEmployeesQueue.size();
                retry = false;
            }

            Employee employee = validEmployeesQueue.pop();
            if (!employeeHierarchy.addEmployee(employee)) {
                validEmployeesQueue.addLast(employee);
                retry = true;
            }
            cursor++;
        }
        return validEmployeesNumber;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Employee, String> reportManagersSalaryPolicyViolation() {
        return reportManagersSalaryPolicyViolation(DEFAULT_MINIMUM_PERCENTAGE, DEFAULT_MAXIMUM_PERCENTAGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Employee, String> reportManagersSalaryPolicyViolation(Integer minimumPercentage, Integer maximumPercentage) {
        Objects.requireNonNull(minimumPercentage);
        Objects.requireNonNull(maximumPercentage);

        EmployeeNode employees = this.employeeHierarchy;
        if (employees == null)
            throw new EmployeeReportException("Employees list not set, should input employees before reports call");

        Map<Employee, String> managersWithPolicyViolation = findManagersWithPolicyViolation(employees, minimumPercentage, maximumPercentage);

        System.out.printf("----- Report of employees with salary policy violation -----%n");
        System.out.printf("-> Minimum percentage allowed: %d %n", minimumPercentage);
        System.out.printf("-> Maximum percentage allowed: %d %n", maximumPercentage);
        System.out.printf("%-12s|%-12s|%-12s|%-12s|%-12s%n",
                "ID",
                "FIRST NAME",
                "LAST NAME",
                "SALARY",
                "VIOLATION");

        managersWithPolicyViolation.forEach((employee, violationDescr) ->
                System.out.printf("%-12d|%-12s|%-12s|%-12d|%s%n",
                        employee.id(),
                        employee.firstName(),
                        employee.lastName(),
                        employee.salary(),
                        violationDescr));
        System.out.println();

        return managersWithPolicyViolation;
    }

    /**
     * Finds managers who violate the salary policy regarding their subordinates' average salary.
     *
     * @param employeeHierarchy  the root node of the employee hierarchy
     * @param minimumPercentage  the minimum percentage by which a manager's salary should be more than the average salary of their subordinates
     * @param maximumPercentage  the maximum percentage by which a manager's salary should be more than the average salary of their subordinates
     * @return a map containing managers who violate the salary policy along with the violation description
     */
    private Map<Employee, String> findManagersWithPolicyViolation(EmployeeNode employeeHierarchy, Integer minimumPercentage, Integer maximumPercentage) {
        Map<Employee, Double> managersAndAverage = new HashMap<>();
        getNodesSubordinatesSalaryAverage(employeeHierarchy, managersAndAverage);

        Map<Employee, String> managersAndPolicyViolation = new HashMap<>();
        managersAndAverage.forEach(((employee, average) -> {
            double minimumSalaryAllowed = average * (1 + ((double) minimumPercentage / 100));
            double maximumSalaryAllowed = average * (1 + ((double) maximumPercentage / 100));
            double salary = employee.salary().doubleValue();

            if (salary < minimumSalaryAllowed)
                managersAndPolicyViolation.put(employee,
                        String.format("Salary is %.2f lesser than the minimum salary allowed", minimumSalaryAllowed - salary));

            if (salary > maximumSalaryAllowed)
                managersAndPolicyViolation.put(employee,
                        String.format("Salary is %.2f higher than the maximum salary allowed", salary - maximumSalaryAllowed));
        }));
        return managersAndPolicyViolation;
    }

    /**
     * Calculates the average salary of subordinates for each manager node in the employee hierarchy.
     *
     * @param node    the current node in the employee hierarchy
     * @param result  a map to store the manager node and its corresponding average salary of subordinates
     */
    private static void getNodesSubordinatesSalaryAverage(EmployeeNode node, Map<Employee, Double> result) {
        if (node.subordinates().isEmpty()) return;
        Double average = node.subordinates().stream()
                .collect(Collectors.averagingInt(child -> child.employee().salary()));
        result.put(node.employee(), average);

        for (EmployeeNode subordinate : node.subordinates()) {
            getNodesSubordinatesSalaryAverage(subordinate, result);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Employee, Integer> reportManagersWithExcessiveReportingLines() {
        return reportManagersWithExcessiveReportingLines(DEFAULT_REPORTING_LINES_THRESHOLD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Employee, Integer> reportManagersWithExcessiveReportingLines(Integer reportingLinesThreshold) {
        Objects.requireNonNull(reportingLinesThreshold);

        EmployeeNode employees = this.employeeHierarchy;
        if (employees == null)
            throw new EmployeeReportException("Employees list not set, should input employees before reports call");

        Map<Employee, Integer> managerAndReportingLines = getNodesWithDepthGreaterThan(employees, reportingLinesThreshold);

        System.out.printf("----- Report of employees with reporting line higher than %d -----%n", reportingLinesThreshold);
        System.out.printf("%-12s|%-12s|%-12s|%-12s%n",
                "ID",
                "FIRST NAME",
                "LAST NAME",
                "EXCESSIVE REPORTING LINES");

        managerAndReportingLines.forEach(((employee, reportingLines) ->
                System.out.printf("%-12d|%-12s|%-12s|%-12d%n",
                        employee.id(),
                        employee.firstName(),
                        employee.lastName(),
                        reportingLines)));
        System.out.println();

        return managerAndReportingLines;
    }

    /**
     * Retrieves managers with reporting lines greater than a specified depth threshold.
     *
     * @param employeeHierarchy    the root node of the employee hierarchy
     * @param depthThreshold       the threshold depth beyond which reporting lines are considered excessive
     * @return a map containing managers with reporting lines greater than the depth threshold
     */
    private Map<Employee, Integer> getNodesWithDepthGreaterThan(EmployeeNode employeeHierarchy, Integer depthThreshold) {
        Map<Employee, Integer> managerAndReportingLines = new HashMap<>();
        traverseDepthGreaterThan(employeeHierarchy, 0, depthThreshold, managerAndReportingLines);
        return managerAndReportingLines;
    }

    /**
     * Traverses the employee hierarchy to find managers with reporting lines greater than a specified depth threshold.
     *
     * @param node              the current node being traversed
     * @param depth             the depth of the current node in the hierarchy
     * @param depthThreshold    the threshold depth beyond which reporting lines are considered excessive
     * @param result            a map to store managers with excessive reporting lines
     */
    private void traverseDepthGreaterThan(EmployeeNode node, int depth, Integer depthThreshold, Map<Employee, Integer> result) {
        if (depth > depthThreshold) {
            result.put(node.employee(), depth - depthThreshold);
        }
        for (EmployeeNode subordinate : node.subordinates()) {
            traverseDepthGreaterThan(subordinate, depth + 1, depthThreshold, result);
        }
    }
}