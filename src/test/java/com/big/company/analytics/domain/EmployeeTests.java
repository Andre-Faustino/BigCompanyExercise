package com.big.company.analytics.domain;

import static org.junit.jupiter.api.Assertions.*;

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

        Exception employeeWithoutId = assertThrowsExactly(NullPointerException.class, () -> {
            Employee.builder()
                    .setFirstName("Joe")
                    .setLastName("Doe")
                    .setSalary(45000)
                    .build();
        });
        assertEquals("Employee id is missing", employeeWithoutId.getMessage());

        Exception employeeWithoutFirstName = assertThrowsExactly(NullPointerException.class, () -> {
            Employee.builder()
                    .setId(123)
                    .setLastName("Doe")
                    .setSalary(45000)
                    .build();
        });
        assertEquals("Employee first name is missing", employeeWithoutFirstName.getMessage());

        Exception employeeWithoutLastName = assertThrowsExactly(NullPointerException.class, () -> {
            Employee.builder()
                    .setId(123)
                    .setFirstName("Joe")
                    .setSalary(45000)
                    .build();
        });
        assertEquals("Employee last name is missing", employeeWithoutLastName.getMessage());

        Exception employeeWithoutSalary = assertThrowsExactly(NullPointerException.class, () -> {
            Employee.builder()
                    .setId(123)
                    .setFirstName("Joe")
                    .setLastName("Doe")
                    .build();
        });
        assertEquals("Employee salary is missing", employeeWithoutSalary.getMessage());

    }
}
