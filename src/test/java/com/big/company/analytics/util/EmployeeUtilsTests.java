package com.big.company.analytics.util;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.exception.EmployeeException;
import com.big.company.analytics.test.util.AssertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeUtilsTests {

    List<Employee> employees;

    @BeforeEach
    void init() {
        Employee ceo = new Employee(123, "Mark", "Zuckerberg", 250000, null);
        Employee emp1 = new Employee(124, "Martin", "Chekov", 45000, 123);
        Employee emp2 = new Employee(125, "Bob", "Ronstad", 47000, 123);

        this.employees = new ArrayList<>();
        this.employees.addAll(Arrays.asList(ceo, emp1, emp2));
    }

    @Test
    void shouldFindCEO() {
        assertEquals(123, EmployeeUtils.findCEO(employees).id());
    }

    @Test
    void shouldFailsDueMultipleCEO() {
        Employee anotherCEO = new Employee(345, "Elon", "Musk", 250000, null);
        employees.add(anotherCEO);

        AssertThrows.assertThrows("Employee list has more than one CEO", EmployeeException.class,
                () -> EmployeeUtils.findCEO(employees));
    }

    @Test
    void shouldFailsDueNoCEO() {
        employees.removeIf(employee -> employee.getManagerId().isEmpty());
        AssertThrows.assertThrows("Employee list has no CEO", EmployeeException.class,
                () -> EmployeeUtils.findCEO(employees));
    }

}