package com.big.company.analytics.domain;

import static org.junit.jupiter.api.Assertions.*;
import static com.big.company.analytics.test.util.AssertThrows.assertThrows;

import com.big.company.analytics.exception.EmployeeException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class EmployeeTests {

    @Test
    void shouldCreateEmployeeSuccessfully() {
        Employee employee = new Employee(
                123,
                "Joe",
                "Doe",
                45000,
                125
        );

        assertEquals(123, employee.id());
        assertEquals("Joe", employee.firstName());
        assertEquals("Doe", employee.lastName());
        assertEquals(45000, employee.salary());
        assertEquals(Optional.of(125), employee.getManagerId());
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
