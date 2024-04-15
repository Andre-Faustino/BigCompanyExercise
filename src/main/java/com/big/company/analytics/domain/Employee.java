package com.big.company.analytics.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an employee of BigCompany.
 */
public record Employee(
        Integer id,
        String firstName,
        String lastName,
        Integer salary,
        Integer managerId
) {
    /**
     * Constructs an Employee object.
     *
     * @param id        Employee ID (required)
     * @param firstName First name (required)
     * @param lastName  Last name (required)
     * @param salary    Salary (required)
     * @param managerId Manager ID
     * @throws NullPointerException if any required params is null
     */
    public Employee {
        Objects.requireNonNull(id, "Employee id is missing");
        Objects.requireNonNull(firstName, "Employee first name is missing");
        Objects.requireNonNull(lastName, "Employee last name is missing");
        Objects.requireNonNull(salary, "Employee salary is missing");
    }

    /**
     * Retrieves the manager's ID if present.
     * It's preferred than using the usual getter from record since it can be null
     *
     * @return Optional manager ID
     */
    public Optional<Integer> getManagerId() {
        return Optional.ofNullable(managerId);
    }
}
