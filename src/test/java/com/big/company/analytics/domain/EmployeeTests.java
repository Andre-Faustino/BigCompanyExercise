package com.big.company.analytics.domain;

import static org.junit.jupiter.api.Assertions.*;
import static com.big.company.analytics.test.util.AssertThrows.assertThrows;

import org.junit.jupiter.api.Test;

import java.util.Optional;

public class EmployeeTests {

    @Test
    void shouldCreateEmployeeSuccessfully() {
        Employee employee = Employee.builder()
                .setId(123)
                .setFirstName("Joe")
                .setLastName("Doe")
                .setSalary(45000)
                .setManagerId(125)
                .build();

        assertEquals(employee.getId(), 123);
        assertEquals(employee.getFirstName(), "Joe");
        assertEquals(employee.getLastName(), "Doe");
        assertEquals(employee.getSalary(), 45000);
        assertEquals(employee.getManagerId(), Optional.of(125));
    }

    @Test
    void shouldEmployeeFailForMissingProperties() {
        assertThrows("Employee id is missing", NullPointerException.class,
                () -> Employee.builder()
                        .setFirstName("Joe")
                        .setLastName("Doe")
                        .setSalary(45000)
                        .build());

        assertThrows("Employee first name is missing", NullPointerException.class,
                () -> Employee.builder()
                        .setId(123)
                        .setLastName("Doe")
                        .setSalary(45000)
                        .build());

        assertThrows("Employee last name is missing", NullPointerException.class,
                () -> Employee.builder()
                        .setId(123)
                        .setFirstName("Joe")
                        .setSalary(45000)
                        .build());

        assertThrows("Employee salary is missing", NullPointerException.class,
                () -> Employee.builder()
                        .setId(123)
                        .setFirstName("Joe")
                        .setLastName("Doe")
                        .build());
    }
}
