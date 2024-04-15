package com.big.company.analytics.services;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.domain.EmployeeNode;
import com.big.company.analytics.services.impl.EmployeeDataExtractorService;

import static com.big.company.analytics.test.util.AssertThrows.*;

import com.big.company.analytics.services.impl.EmployeeHierarchyReportService;
import com.big.company.analytics.services.impl.EmployeeNodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeReportServiceTests {

    private static final String TEST_FILEPATH = "src/test/resources/";
    private static final String TEST_FILENAME = "SampleData.csv";
    List<Employee> employees;
    EmployeeNodeService nodeService;
    EmployeeReportService report;

    @BeforeEach
    void init() {
        this.employees = new EmployeeDataExtractorService().extractFile(TEST_FILEPATH, TEST_FILENAME);
        this.nodeService = new EmployeeNodeGenerator();
        this.report = new EmployeeHierarchyReportService();
    }

    @Test
    void shouldReportManagersWithSalaryPolicyViolation() {
        this.employees = new EmployeeDataExtractorService().extractFile(TEST_FILEPATH, "SalaryViolationPolicyData.csv");
        EmployeeNode employeesHierarchy = nodeService.getEmployeesHierarchy(employees);

        Map<Employee, String> managers = report.reportManagersSalaryPolicyViolation(employeesHierarchy);

        Integer expectedNumberOfManagersWithPolicyViolation = 2;
        assertEquals(expectedNumberOfManagersWithPolicyViolation, managers.size());

        managers = report.reportManagersSalaryPolicyViolation(employeesHierarchy, 27, 50);

        Integer expectedNumberOfManagersWithCustomPolicyViolation = 3;
        assertEquals(expectedNumberOfManagersWithCustomPolicyViolation, managers.size());
    }

    @Test
    void shouldReportManagersWithExcessiveReportingLines() {
        EmployeeNode employeesHierarchy = nodeService.getEmployeesHierarchy(employees);

        Integer reportingLinesThreshold = 6;
        Map<Employee, Integer> managers = report.reportManagersWithExcessiveReportingLines(employeesHierarchy, reportingLinesThreshold);

        Integer expectedNumberOfManagersWithReportingLinesHigherThan6 = 52;
        assertEquals(expectedNumberOfManagersWithReportingLinesHigherThan6, managers.size());

        managers = report.reportManagersWithExcessiveReportingLines(employeesHierarchy);
        Integer expectedNumberOfManagersWithReportingLinesHigherThan4 = 65;
        assertEquals(expectedNumberOfManagersWithReportingLinesHigherThan4, managers.size());

        Employee mockedEmp = new Employee(138, "Victoria", "Roberts", 51000, 128);

        assertTrue(managers.containsKey(mockedEmp));
        assertEquals(1, managers.get(mockedEmp));
    }

    @Test
    void shouldFailsWhenCallReportsMethodsIsNull() {
        assertThrows("Employees hierarchy must not be null", NullPointerException.class,
                () -> report.reportManagersSalaryPolicyViolation(null));
        assertThrows("Employees hierarchy must not be null", NullPointerException.class,
                () -> report.reportManagersWithExcessiveReportingLines(null));

        EmployeeNode employeesHierarchy = nodeService.getEmployeesHierarchy(employees);

        assertThrows("Minimum Percentage must not be null", NullPointerException.class,
                () -> report.reportManagersSalaryPolicyViolation(employeesHierarchy, null, null));
        assertThrows("Maximum Percentage must not be null", NullPointerException.class,
                () -> report.reportManagersSalaryPolicyViolation(employeesHierarchy, 25, null));
        assertThrows("Reporting lines threshold must not be null", NullPointerException.class,
                () -> report.reportManagersWithExcessiveReportingLines(employeesHierarchy, null));
    }

    @Test
    void shouldRunReportsConcurrently() {
        this.employees = new EmployeeDataExtractorService().extractFile(TEST_FILEPATH, "HugeData.csv");
        EmployeeNode employeesHierarchy = nodeService.getEmployeesHierarchy(employees);

        Arrays.asList(
                CompletableFuture.supplyAsync(() -> report.reportManagersSalaryPolicyViolation(employeesHierarchy))
                        .thenAccept(employees -> assertEquals(966, employees.size())),
                CompletableFuture.supplyAsync(() -> report.reportManagersWithExcessiveReportingLines(employeesHierarchy))
                        .thenAccept(employees -> assertEquals(2779, employees.size()))
        ).forEach(CompletableFuture::join);
    }
}