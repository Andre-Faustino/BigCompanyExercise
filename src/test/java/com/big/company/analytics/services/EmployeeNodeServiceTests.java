package com.big.company.analytics.services;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.domain.EmployeeNode;
import com.big.company.analytics.exception.EmployeeNodeServiceException;
import com.big.company.analytics.services.impl.EmployeeCsvFileReader;
import com.big.company.analytics.services.impl.EmployeeNodeGenerator;
import com.big.company.analytics.test.util.AssertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.big.company.analytics.test.util.AssertThrows.assertThrows;
import static com.big.company.analytics.test.util.TestResourceConstants.TEST_FILENAME;
import static com.big.company.analytics.test.util.TestResourceConstants.TEST_FILEPATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeNodeServiceTests {

    List<Employee> employees;

    @BeforeEach
    void init() {
        this.employees = new EmployeeCsvFileReader().readFile(TEST_FILEPATH, TEST_FILENAME);
    }

    @Test
    void shouldGetEmployeesHierarchySuccessfully() {
        EmployeeNodeService nodeService = new EmployeeNodeGenerator();
        EmployeeNode employeesHierarchy = nodeService.generateEmployeesHierarchy(employees);
        assertEquals(100, employeesHierarchy.size());
    }

    @Test
    void shouldGetEmployeesHierarchyWithUnorderedListSuccessfully() {
        List<Employee> unorderedEmployees = new EmployeeCsvFileReader().readFile(TEST_FILEPATH, "UnorderedData.csv");
        EmployeeNodeService nodeService = new EmployeeNodeGenerator();
        assertEquals(5, nodeService.generateEmployeesHierarchy(unorderedEmployees).size());
    }

    @Test
    void shouldInvalidEmployeesListFails() {
        EmployeeNodeService nodeService = new EmployeeNodeGenerator();
        assertThrows("Employees list must not be null", NullPointerException.class,
                () -> nodeService.generateEmployeesHierarchy(null));

        Employee anotherCEO = new Employee(345, "Elon", "Musk", 250000, null);
        employees.add(anotherCEO);

        AssertThrows.assertThrows("Error when creating Employee Hierarchy | Employee list has more than one CEO", EmployeeNodeServiceException.class,
                () -> nodeService.generateEmployeesHierarchy(employees));
    }
}