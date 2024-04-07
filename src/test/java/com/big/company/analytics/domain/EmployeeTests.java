package com.big.company.analytics.domain;

import static org.junit.jupiter.api.Assertions.*;
import static com.big.company.analytics.test.util.AssertThrows.assertThrows;

import com.big.company.analytics.exception.EmployeeException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class EmployeeTests {

    @Test
    void shouldCreateEmployeeSuccessfully() {
        Employee employee = new Employee(
                123,
                "Joe",
                "Doe",
                45000,
                125
        );

        assertEquals(employee.id(), 123);
        assertEquals(employee.firstName(), "Joe");
        assertEquals(employee.lastName(), "Doe");
        assertEquals(employee.salary(), 45000);
        assertEquals(employee.getManagerId(), Optional.of(125));
    }

    @Test
    void shouldEmployeeFailForMissingProperties() {
        assertThrows("Employee id is missing", EmployeeException.class,
                () -> new Employee(
                        null,
                        "Joe",
                        "Doe",
                        45000,
                        125
                ));

        assertThrows("Employee first name is missing", EmployeeException.class,
                () -> new Employee(
                        123,
                        null,
                        "Doe",
                        45000,
                        125
                ));

        assertThrows("Employee last name is missing", EmployeeException.class,
                () -> new Employee(
                        123,
                        "Joe",
                        null,
                        45000,
                        125
                ));

        assertThrows("Employee salary is missing", EmployeeException.class,
                () -> new Employee(
                        123,
                        "Joe",
                        "Doe",
                        null,
                        125
                ));
    }
}
