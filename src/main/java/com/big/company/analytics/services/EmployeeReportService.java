package com.big.company.analytics.services;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.domain.EmployeeNode;

import java.util.Map;

/**
 * The {@code EmployeeReport} interface provides methods for generating reports
 * related to employee analytics.
 */
public interface EmployeeReportService {

    /**
     * Generates a report printed in console on managers who violate the salary policy by falling outside
     * the specified percentage range.
     * <br>The criteria is that each manager should have a salary at least a minimum percentage
     * more than the average salary of its direct subordinates, but no more than the maximum percentage
     * than that average.
     *
     * @param employeeHierarchy the root of the employee hierarchy
     * @param minimumPercentage the minimum allowed percentage increase in salary.
     * @param maximumPercentage the maximum allowed percentage increase in salary.
     * @return a map of the managers and the salary violation description
     * @throws NullPointerException if any params is null
     */
    Map<Employee, String> reportManagersSalaryPolicyViolation(EmployeeNode employeeHierarchy, Integer minimumPercentage, Integer maximumPercentage);

    /**
     * Generates a report printed in console on managers who violate the salary policy by falling outside
     * the specified percentage range.
     * <ul>
     *     <li>minimum percentage is 20%</li>
     *     <li>maximum percentage is 50%</li>
     * </ul>
     * <br>The criteria is that each manager should have a salary at least a 20%
     * more than the average salary of its direct subordinates, but no more than the 50%
     * than that average.
     *
     * @param employeeHierarchy the root of the employee hierarchy
     * @return a map of the managers and the salary violation description
     * @throws NullPointerException if any params is null
     */
    Map<Employee, String> reportManagersSalaryPolicyViolation(EmployeeNode employeeHierarchy);

    /**
     * Generates a report printed in console on managers who have an excessive number of reporting lines until the ceo,
     * exceeding the specified threshold.
     *
     * @param reportingLinesThreshold the maximum allowed number of reporting lines.
     * @param employeeHierarchy       the root of the employee hierarchy
     * @return a map with managers and how much reporting lines higher than the threshold
     * @throws NullPointerException when any params is null
     */
    Map<Employee, Integer> reportManagersWithExcessiveReportingLines(EmployeeNode employeeHierarchy, Integer reportingLinesThreshold);

    /**
     * Generates a report printed in console on managers who have an excessive number of reporting lines until the ceo,
     * exceeding the standard threshold of 4.
     *
     * @param employeeHierarchy the root of the employee hierarchy
     * @return a map with managers and how much reporting lines higher than 4
     * @throws NullPointerException when any params is null
     */
    Map<Employee, Integer> reportManagersWithExcessiveReportingLines(EmployeeNode employeeHierarchy);
}
