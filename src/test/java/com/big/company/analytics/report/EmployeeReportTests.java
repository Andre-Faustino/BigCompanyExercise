package com.big.company.analytics.report;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.exception.EmployeeException;
import com.big.company.analytics.exception.EmployeeReportException;
import com.big.company.analytics.extractor.EmployeeDataExtractor;

import static com.big.company.analytics.util.AssertThrows.*;

import com.big.company.analytics.util.AssertThrows;
import com.big.company.analytics.util.EmployeeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeReportTests {

    private static final String TEST_FILEPATH = "src/test/resources/";
    private static final String TEST_FILENAME = "SampleData.csv";
    List<Employee> employees;

    @BeforeEach
    void init() {
        this.employees = new EmployeeDataExtractor().extractFile(TEST_FILEPATH, TEST_FILENAME);
    }

    @Test
    void shouldInputEmployeesSuccessfully() {
        EmployeeReport report = new EmployeeHierarchyReport();
        assertDoesNotThrow(() -> report.inputEmployees(employees));
    }

    @Test
    void shouldInputUnorderedEmployeesSuccessfully() {
        List<Employee> unorderedEmployees = new EmployeeDataExtractor().extractFile(TEST_FILEPATH, "UnorderedData.csv");
        EmployeeReport report = new EmployeeHierarchyReport();
        assertEquals(5, report.inputEmployees(unorderedEmployees));
    }

    @Test
    void shouldInvalidEmployeesInputFails() {
        EmployeeReport report = new EmployeeHierarchyReport();
        assertThrows("Employees list should not be null", EmployeeReportException.class,
                () -> report.inputEmployees(null));

        Employee anotherCEO = Employee.builder()
                .setId(345)
                .setFirstName("Elon")
                .setLastName("Musk")
                .setSalary(250000)
                .build();
        employees.add(anotherCEO);

        AssertThrows.assertThrows("Error when creating Employee Hierarchy | Employee list has more than one CEO", EmployeeReportException.class,
                () -> report.inputEmployees(employees));
    }

    @Test
    void shouldFailWhenCallReportsMethodsWithoutInputEmployees() {
        EmployeeReport report = new EmployeeHierarchyReport();

        assertThrows("Employees list not set, should input employees before reports call", EmployeeReportException.class,
                report::reportManagersSalaryPolicyViolation);
        assertThrows("Employees list not set, should input employees before reports call", EmployeeReportException.class,
                report::reportManagersWithExcessiveReportingLines);
    }

    @Test
    void shouldReportManagersWithSalaryPolicyViolation() {
        this.employees = new EmployeeDataExtractor().extractFile(TEST_FILEPATH, "SalaryViolationPolicyData.csv");
        EmployeeReport report = new EmployeeHierarchyReport();
        report.inputEmployees(employees);

        Map<Employee, String> managers = report.reportManagersSalaryPolicyViolation();

        Integer expectedNumberOfManagersWithPolicyViolation = 2;
        assertEquals(expectedNumberOfManagersWithPolicyViolation, managers.size());

        managers = report.reportManagersSalaryPolicyViolation(27, 50);

        Integer expectedNumberOfManagersWithCustomPolicyViolation = 3;
        assertEquals(expectedNumberOfManagersWithCustomPolicyViolation, managers.size());
    }

    @Test
    void shouldReportManagersWithExcessiveReportingLines() {
        EmployeeReport report = new EmployeeHierarchyReport();
        report.inputEmployees(employees);

        Integer reportingLinesThreshold = 6;
        Map<Employee, Integer> managers = report.reportManagersWithExcessiveReportingLines(reportingLinesThreshold);

        Integer expectedNumberOfManagersWithReportingLinesHigherThan6 = 52;
        assertEquals(expectedNumberOfManagersWithReportingLinesHigherThan6, managers.size());

        managers = report.reportManagersWithExcessiveReportingLines();
        Integer expectedNumberOfManagersWithReportingLinesHigherThan4 = 65;
        assertEquals(expectedNumberOfManagersWithReportingLinesHigherThan4, managers.size());

        Employee mockedEmp = Employee.builder()
                .setId(138)
                .setFirstName("Victoria")
                .setLastName("Roberts")
                .setSalary(51000)
                .setManagerId(128)
                .build();

        assertTrue(managers.containsKey(mockedEmp));
        assertEquals(1, managers.get(mockedEmp));
    }
}