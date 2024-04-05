package com.big.company.analytics.util;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.exception.EmployeeException;
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
        Employee ceo = Employee.builder()
                .setId(123)
                .setFirstName("Mark")
                .setLastName("Zuckerberg")
                .setSalary(250000)
                .build();

        Employee emp1 = Employee.builder()
                .setId(124)
                .setFirstName("Martin")
                .setLastName("Chekov")
                .setSalary(45000)
                .setManagerId(123)
                .build();

        Employee emp2 = Employee.builder()
                .setId(125)
                .setFirstName("Bob")
                .setLastName("Ronstad")
                .setSalary(47000)
                .setManagerId(123)
                .build();

        this.employees = new ArrayList<>();
        this.employees.addAll(Arrays.asList(ceo, emp1, emp2));
    }

    @Test
    void shouldFindCEO() {
        assertEquals(123, EmployeeUtils.findCEO(employees).getId());
    }

    @Test
    void shouldFailsDueMultipleCEO() {
        Employee anotherCEO = Employee.builder()
                .setId(345)
                .setFirstName("Elon")
                .setLastName("Musk")
                .setSalary(250000)
                .build();
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