package com.big.company.analytics.services;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.exception.EmployeeReportException;
import com.big.company.analytics.services.impl.EmployeeDataExtractor;

import static com.big.company.analytics.test.util.AssertThrows.*;

import com.big.company.analytics.services.EmployeeReport;
import com.big.company.analytics.services.impl.EmployeeHierarchyReport;
import com.big.company.analytics.test.util.AssertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

        Employee anotherCEO = new Employee(345, "Elon", "Musk", 250000, null);
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

        Employee mockedEmp = new Employee(138, "Victoria", "Roberts", 51000, 128);

        assertTrue(managers.containsKey(mockedEmp));
        assertEquals(1, managers.get(mockedEmp));
    }

    @Test
    void shouldReportConcurrentlyWithThreadSafeEmployeeHierarchy() {
        EmployeeReport report = new EmployeeHierarchyReport();
        report.inputEmployees(employees);

        List<CompletableFuture<?>> futures = new ArrayList<>();
        List<Employee> newEmployees = new ArrayList<>();

        newEmployees.add(new Employee(123, "Joe", "Doe", 60000, null));
        newEmployees.add(new Employee(124, "Martin", "Chekov", 45000, 123));
        newEmployees.add(new Employee(125, "Bob", "Ronstad", 47000, 124));
        newEmployees.add(new Employee(126, "Alice", "Hasacat", 50000, 125));
        newEmployees.add(new Employee(127, "Brett", "Hardleaf", 34000, 126));

        futures.add(
                CompletableFuture.supplyAsync(report::reportManagersSalaryPolicyViolation)
                        .thenAccept(managers -> assertEquals(60, managers.size())));
        futures.add(
                CompletableFuture.supplyAsync(report::reportManagersWithExcessiveReportingLines)
                        .thenAccept(managers -> assertEquals(65, managers.size())));
        futures.add(
                CompletableFuture.supplyAsync(() -> report.inputEmployees(newEmployees))
                        .thenApply(managers -> report.reportManagersWithExcessiveReportingLines(2))
                        .thenAccept(managers -> assertEquals(2, managers.size())));
        futures.forEach(CompletableFuture::join);
    }
}