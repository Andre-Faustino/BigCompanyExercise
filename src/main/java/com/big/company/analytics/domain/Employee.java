package com.big.company.analytics.domain;

import java.util.Objects;
import java.util.Optional;

public final class Employee {

    private final Integer id;
    private final String firstName;
    private final String lastName;
    private final Integer salary;
    private final Integer managerId;

    public Employee(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "Employee id is missing");
        this.firstName = Objects.requireNonNull(builder.firstName, "Employee first name is missing");
        this.lastName = Objects.requireNonNull(builder.lastName,"Employee last name is missing");
        this.salary = Objects.requireNonNull(builder.salary, "Employee salary is missing");
        this.managerId = builder.managerId;
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getSalary() {
        return salary;
    }

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

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder {

        private Integer id;
        private String firstName;
        private String lastName;
        private Integer salary;
        private Integer managerId;

        private Builder() {}

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setSalary(Integer salary) {
            this.salary = salary;
            return this;
        }

        public Builder setManagerId(Integer managerId) {
            this.managerId = managerId;
            return this;
        }

        public Employee build()
        {
            return new Employee(this);
        }
    }
}
