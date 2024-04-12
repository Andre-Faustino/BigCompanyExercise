package com.big.company.analytics.report;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.exception.EmployeeReportException;

import java.util.List;
import java.util.Map;

/**
 * The {@code EmployeeReport} interface provides methods for generating reports
 * related to employee analytics. The reports are designed to be thread-safe
 * and can run concurrently without loss of data.
 */
public interface EmployeeReport {

    /**
     * Input a list of employees that should be used to generate reports.
     * The list doesn't need to be ordered.
     *
     * @param employees the list of employees to be reported
     * @return how many employees was successfully added
     * @throws EmployeeReportException if employees list is null or not valid
     */
    Integer inputEmployees(List<Employee> employees);

    /**
     * Generates a report printed in console on managers who violate the salary policy by falling outside
     * the specified percentage range.
     * <br>The criteria is that each manager should have a salary at least a minimum percentage
     * more than the average salary of its direct subordinates, but no more than the maximum percentage
     * than that average.
     * Needs {@code List<employees>} to input reported managers.
     * Can run simultaneously with other reports.
     *
     * @param minimumPercentage the minimum allowed percentage increase in salary.
     * @param maximumPercentage the maximum allowed percentage increase in salary.
     * @return a map of the managers and the salary violation description
     * @throws NullPointerException    if any param is null
     * @throws EmployeeReportException if employees list is not set
     */
    Map<Employee, String> reportManagersSalaryPolicyViolation(Integer minimumPercentage, Integer maximumPercentage);

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
     * Can run simultaneously with other reports.
     *
     * @return a map of the managers and the salary violation description
     * @throws EmployeeReportException if employees list is not set
     */
    Map<Employee, String> reportManagersSalaryPolicyViolation();

    /**
     * Generates a report printed in console on managers who have an excessive number of reporting lines until the ceo,
     * exceeding the specified threshold. Can run simultaneously with other reports.
     *
     * @param reportingLinesThreshold the maximum allowed number of reporting lines.
     * @return a map with managers and how much reporting lines higher than the threshold
     * @throws NullPointerException    if {@code reportingLines} is null
     * @throws EmployeeReportException if employees list is not set
     */
    Map<Employee, Integer> reportManagersWithExcessiveReportingLines(Integer reportingLinesThreshold);

    /**
     * Generates a report printed in console on managers who have an excessive number of reporting lines until the ceo,
     * exceeding the standard threshold of 4. Can run simultaneously with other reports.
     *
     * @return a map with managers and how much reporting lines higher than 4
     * @throws EmployeeReportException if employees list is not set
     */
    Map<Employee, Integer> reportManagersWithExcessiveReportingLines();
}
