package com.big.company.analytics.services.impl;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.domain.EmployeeNode;
import com.big.company.analytics.services.EmployeeReportService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the {@code EmployeeReport} interface that generates reports based on
 * the hierarchy (N-tree) of employees.
 */
public class EmployeeHierarchyReportService implements EmployeeReportService {

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
     * {@inheritDoc}
     */
    @Override
    public Map<Employee, String> reportManagersSalaryPolicyViolation(EmployeeNode employeeHierarchy) {
        return reportManagersSalaryPolicyViolation(employeeHierarchy, DEFAULT_MINIMUM_PERCENTAGE, DEFAULT_MAXIMUM_PERCENTAGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Employee, String> reportManagersSalaryPolicyViolation(EmployeeNode employeeHierarchy, Integer minimumPercentage, Integer maximumPercentage) {
        Objects.requireNonNull(minimumPercentage, "Minimum Percentage must not be null");
        Objects.requireNonNull(maximumPercentage, "Maximum Percentage must not be null");

        if (employeeHierarchy == null)
            throw new NullPointerException("Employees hierarchy must not be null");

        Map<Employee, String> managersWithPolicyViolation =
                findManagersWithPolicyViolation(employeeHierarchy, minimumPercentage, maximumPercentage);
        printReportManagersSalaryPolicyViolation(managersWithPolicyViolation, minimumPercentage, maximumPercentage);

        return managersWithPolicyViolation;
    }

    /**
     * Prints a report of managers who violate the salary policy regarding their subordinates' average salary.
     * This method prints the details of managers with their corresponding violation descriptions.
     *
     * @param managersWithPolicyViolation a map containing managers who violate the salary policy along with the violation description
     * @param minimumPercentage           the minimum percentage by which a manager's salary should be more than the average salary of their subordinates
     * @param maximumPercentage           the maximum percentage by which a manager's salary should be more than the average salary of their subordinates
     */
    private synchronized void printReportManagersSalaryPolicyViolation(Map<Employee, String> managersWithPolicyViolation, Integer minimumPercentage, Integer maximumPercentage) {
        StringBuilder report = new StringBuilder();
        report.append(String.format("----- Report of employees with salary policy violation -----%n"));
        report.append(String.format("-> Minimum percentage allowed: %d %n", minimumPercentage));
        report.append(String.format("-> Maximum percentage allowed: %d %n", maximumPercentage));
        report.append(String.format("-> Number of employees with salary policy violation: %d%n", managersWithPolicyViolation.size()));
        report.append(String.format("%-12s|%-12s|%-12s|%-12s|%-12s%n",
                "ID",
                "FIRST NAME",
                "LAST NAME",
                "SALARY",
                "VIOLATION"));

        managersWithPolicyViolation.forEach((employee, violationDescr) ->
                report.append(String.format("%-12d|%-12s|%-12s|%-12d|%s%n",
                        employee.id(),
                        employee.firstName(),
                        employee.lastName(),
                        employee.salary(),
                        violationDescr)));

        System.out.println(report);
    }

    /**
     * Finds managers who violate the salary policy regarding their subordinates' average salary.
     *
     * @param employeeHierarchy the root node of the employee hierarchy
     * @param minimumPercentage the minimum percentage by which a manager's salary should be more than the average salary of their subordinates
     * @param maximumPercentage the maximum percentage by which a manager's salary should be more than the average salary of their subordinates
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
     * @param node   the current node in the employee hierarchy
     * @param result a map to store the manager node and its corresponding average salary of subordinates
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
    public Map<Employee, Integer> reportManagersWithExcessiveReportingLines(EmployeeNode employeeHierarchy) {
        return reportManagersWithExcessiveReportingLines(employeeHierarchy, DEFAULT_REPORTING_LINES_THRESHOLD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Employee, Integer> reportManagersWithExcessiveReportingLines(EmployeeNode employeeHierarchy, Integer reportingLinesThreshold) {
        Objects.requireNonNull(reportingLinesThreshold, "Reporting lines threshold must not be null");

        if (employeeHierarchy == null)
            throw new NullPointerException("Employees hierarchy must not be null");

        Map<Employee, Integer> managerAndReportingLines = getNodesWithDepthGreaterThan(employeeHierarchy, reportingLinesThreshold);
        printReportManagersWithExcessiveReportingLines(managerAndReportingLines, reportingLinesThreshold);

        return managerAndReportingLines;
    }

    /**
     * Prints a report of managers with excessive reporting lines.
     * This method prints the details of managers along with the number of reporting lines exceeding the specified threshold.
     *
     * @param managerAndReportingLines   a map containing managers with reporting lines greater than the depth threshold
     * @param reportingLinesThreshold    the threshold depth beyond which reporting lines are considered excessive
     */
    private synchronized void printReportManagersWithExcessiveReportingLines(Map<Employee, Integer> managerAndReportingLines, Integer reportingLinesThreshold) {
        StringBuilder report = new StringBuilder();
        report.append(String.format("----- Report of employees with reporting line higher than %d -----%n", reportingLinesThreshold));
        report.append(String.format("-> Number of employees with excessive reporting lines: %d%n", managerAndReportingLines.size()));
        report.append(String.format("%-12s|%-12s|%-12s|%-12s%n",
                "ID",
                "FIRST NAME",
                "LAST NAME",
                "EXCESSIVE REPORTING LINES"));

        managerAndReportingLines.forEach(((employee, reportingLines) ->
                report.append(String.format("%-12d|%-12s|%-12s|%-12d%n",
                        employee.id(),
                        employee.firstName(),
                        employee.lastName(),
                        reportingLines))));

        System.out.println(report);
    }


    /**
     * Retrieves managers with reporting lines greater than a specified depth threshold.
     *
     * @param employeeHierarchy the root node of the employee hierarchy
     * @param depthThreshold    the threshold depth beyond which reporting lines are considered excessive
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
     * @param node           the current node being traversed
     * @param depth          the depth of the current node in the hierarchy
     * @param depthThreshold the threshold depth beyond which reporting lines are considered excessive
     * @param result         a map to store managers with excessive reporting lines
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
