package com.big.company.analytics.domain;

import com.big.company.analytics.exception.EmployeeNodeException;
import com.big.company.analytics.services.impl.EmployeeDataExtractor;

import static com.big.company.analytics.test.util.AssertThrows.*;

import com.big.company.analytics.util.EmployeeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeNodeTests {

    private static final String TEST_FILEPATH = "src/test/resources/";
    private static final String TEST_FILENAME = "ValidatedDataWithHeader.csv";

    List<Employee> employees;

    @BeforeEach
    void init() {
        this.employees = new EmployeeDataExtractor().extractFile(TEST_FILEPATH, TEST_FILENAME);
    }

    @Test
    void shouldCreateEmployeeHierarchySuccessfully() {
        Employee ceo = EmployeeUtils.findCEO(employees);
        EmployeeNode employeeNode = new EmployeeNode(ceo);
        employees.stream()
                .filter(employee -> employee.getManagerId().isPresent())
                .forEach(employee -> assertTrue(employeeNode.addEmployee(employee)));

        assertEquals(123, employeeNode.employee().id());
        assertEquals(124, employeeNode.subordinates().get(0).employee().id());
        assertEquals(125, employeeNode.subordinates().get(1).employee().id());
        assertEquals(300, employeeNode
                .subordinates().get(0)
                .subordinates().get(0)
                .employee().id());
        assertEquals(305, employeeNode
                .subordinates().get(0)
                .subordinates().get(0)
                .subordinates().get(0)
                .employee().id());
    }

    @Test
    void shouldFailsWithInvalidEmployeeWhenAddToNode() {
        Employee ceo = EmployeeUtils.findCEO(employees);
        EmployeeNode employeeNode = new EmployeeNode(ceo);
        employees.stream()
                .filter(employee -> employee.getManagerId().isPresent())
                .forEach(employee -> assertTrue(employeeNode.addEmployee(employee)));

        Employee newEmployee = new Employee(
                306,
                "Mark",
                "Has Mysterious Manager",
                30000,
                999
        );
        assertFalse(employeeNode.addEmployee(newEmployee));

        assertThrows("Employee must not be null", EmployeeNodeException.class,
                () -> employeeNode.addEmployee(null));

        assertThrows("Employee doesn't have a manager", EmployeeNodeException.class,
                () -> employeeNode.addEmployee(ceo));
    }
}