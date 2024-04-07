package com.big.company.analytics.report;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.domain.EmployeeNode;
import com.big.company.analytics.exception.EmployeeException;
import com.big.company.analytics.exception.EmployeeNodeException;
import com.big.company.analytics.exception.EmployeeReportException;
import com.big.company.analytics.util.EmployeeUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Implementation of the {@code EmployeeReport} interface that generates reports based on
 * the hierarchy (N-tree) of employees.
 */
public class EmployeeHierarchyReport implements EmployeeReport {

    private static final int STANDARD_REPORTING_LINES_THRESHOLD = 4;
    private static final int STANDARD_MINIMUM_PERCENTAGE = 20;
    private static final int STANDARD_MAXIMUM_PERCENTAGE = 50;

    /**
     * Root node of the employee hierarchy (CEO)
     */
    private EmployeeNode employeeHierarchy;

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer inputEmployees(List<Employee> employees) {
        if (employees == null) throw new EmployeeReportException("Employees list should not be null");
        try {
            Employee root = EmployeeUtils.findCEO(employees);
            employeeHierarchy = EmployeeNode.builder()
                    .setEmployee(root)
                    .build();

            return this.addUnorderedEmployeesToHierarchy(employees);
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
        List<Employee> iterate = employees.stream()
                .filter(employee -> employee.getManagerId().isPresent())
                .collect(Collectors.toList());

        AtomicBoolean shouldLoop = new AtomicBoolean(false);
        do {
            shouldLoop.set(false);
            iterate.removeIf(employee -> {
                boolean wasSuccessfullyAdded = employeeHierarchy.addEmployee(employee);
                if (wasSuccessfullyAdded) shouldLoop.set(true);
                return wasSuccessfullyAdded;
            });
        } while (shouldLoop.get());
        return employees.size() - iterate.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reportManagersSalaryPolicyViolation() {
        reportManagersSalaryPolicyViolation(STANDARD_MINIMUM_PERCENTAGE, STANDARD_MAXIMUM_PERCENTAGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reportManagersSalaryPolicyViolation(Integer minimumPercentage, Integer maximumPercentage) {
        Objects.requireNonNull(minimumPercentage);
        Objects.requireNonNull(maximumPercentage);
        if (employeeHierarchy == null)
            throw new EmployeeReportException("Employees list not set, should input employees before reports call");


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Employee, Integer> reportManagersWithExcessiveReportingLines() {
        return reportManagersWithExcessiveReportingLines(STANDARD_REPORTING_LINES_THRESHOLD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Employee, Integer> reportManagersWithExcessiveReportingLines(Integer reportingLinesThreshold) {
        Objects.requireNonNull(reportingLinesThreshold);
        if (employeeHierarchy == null)
            throw new EmployeeReportException("Employees list not set, should input employees before reports call");
        return getNodesWithDepthGreaterThan(reportingLinesThreshold);
    }

    private Map<Employee, Integer> getNodesWithDepthGreaterThan(Integer depthThreshold) {
        Map<Employee, Integer> managerAndReportingLines = new HashMap<>();
        traverseDepthGreaterThan(employeeHierarchy, 0, depthThreshold, managerAndReportingLines);

        System.out.printf("----- Report of employees with reporting line higher than %d -----%n", depthThreshold);
        System.out.println("  ID | FIRST NAME | LAST NAME | EXCESSIVE REPORTING LINES");

        managerAndReportingLines.forEach(((employee, reportingLines) ->
                System.out.printf(" %d |  %s  |  %s  |  %d  %n", employee.getId(), employee.getFirstName(), employee.getLastName(), reportingLines)));

        return managerAndReportingLines;
    }

    private void traverseDepthGreaterThan(EmployeeNode node, int depth, Integer depthThreshold, Map<Employee, Integer> result) {
        if (depth > depthThreshold) {
            result.put(node.getEmployee(), depth - depthThreshold);
        }
        for (EmployeeNode subordinate : node.getSubordinates()) {
            traverseDepthGreaterThan(subordinate, depth + 1, depthThreshold, result);
        }
    }

}
