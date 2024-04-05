package com.big.company.analytics.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an employee of BigCompany.
 */
public final class Employee {

    private final Integer id;
    private final String firstName;
    private final String lastName;
    private final Integer salary;
    private final Integer managerId;

    /**
     * Constructs an Employee object.
     *
     * @param builder Employee Builder
     */
    public Employee(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "Employee id is missing");
        this.firstName = Objects.requireNonNull(builder.firstName, "Employee first name is missing");
        this.lastName = Objects.requireNonNull(builder.lastName, "Employee last name is missing");
        this.salary = Objects.requireNonNull(builder.salary, "Employee salary is missing");
        this.managerId = builder.managerId;
    }

    /**
     * Retrieves the Employee ID.
     *
     * @return Employee ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Retrieves the Employee's first name.
     *
     * @return First name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Retrieves the Employee's last name.
     *
     * @return Last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Retrieves the Employee's salary.
     *
     * @return Salary
     */
    public Integer getSalary() {
        return salary;
    }

    /**
     * Retrieves the manager's ID if present.
     *
     * @return Optional manager ID
     */
    public Optional<Integer> getManagerId() {
        return Optional.ofNullable(managerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) && Objects.equals(firstName, employee.firstName) && Objects.equals(lastName, employee.lastName) && Objects.equals(salary, employee.salary) && Objects.equals(managerId, employee.managerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, salary, managerId);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", salary='" + salary + '\'' +
                ", managerId=" + managerId +
                '}';
    }

    /**
     * Creates a new instance of the Employee Builder.
     *
     * @return Employee Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for constructing Employee objects.
     */
    public static class Builder {

        private Integer id;
        private String firstName;
        private String lastName;
        private Integer salary;
        private Integer managerId;

        private Builder() {
        }

        /**
         * Sets the ID for the Employee being built.
         *
         * @param id Employee ID
         * @return Builder instance
         */
        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the first name for the Employee being built.
         *
         * @param firstName First name
         * @return Builder instance
         */
        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        /**
         * Sets the last name for the Employee being built.
         *
         * @param lastName Last name
         * @return Builder instance
         */
        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        /**
         * Sets the salary for the Employee being built.
         *
         * @param salary Salary
         * @return Builder instance
         */
        public Builder setSalary(Integer salary) {
            this.salary = salary;
            return this;
        }

        /**
         * Sets the manager ID for the Employee being built.
         *
         * @param managerId Manager ID
         * @return Builder instance
         */
        public Builder setManagerId(Integer managerId) {
            this.managerId = managerId;
            return this;
        }

        /**
         * Builds the Employee object.
         * <br>Required properties for build:
         * <ul>
         * <li>id</li>
         * <li>first name</li>
         * <li>last name</li>
         * <li>salary</li>
         * </ul>
         *
         * @return Employee object
         */
        public Employee build() {
            return new Employee(this);
        }
    }
}
