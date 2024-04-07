package com.big.company.analytics.domain;

import com.big.company.analytics.exception.EmployeeNodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a node in an employee hierarchy tree.
 */
public final class EmployeeNode {

    private final Employee employee;
    private final List<EmployeeNode> subordinates = new ArrayList<>();

    /**
     * Constructs an EmployeeNode object.
     *
     * @param builder The builder object used to construct this EmployeeNode.
     * @throws NullPointerException if the employee in the builder is null.
     */
    public EmployeeNode(Builder builder) {
        try {
            this.employee = Objects.requireNonNull(builder.employee, "Employee must not be null");
        } catch (NullPointerException e) {
            throw new EmployeeNodeException(e.getMessage());
        }
    }

    /**
     * Gets the employee associated with this node.
     *
     * @return The employee object.
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Gets the list of subordinates of this employee node.
     *
     * @return The list of subordinate EmployeeNode objects.
     */
    public List<EmployeeNode> getSubordinates() {
        return subordinates;
    }

    /**
     * Adds an employee to the hierarchy as a subordinate of its manager.
     * If the manager of the {@code employeeToAdd} is not found in the hierarchy,
     * it returns false. In this case, it's important to note that the order in which employees
     * are added may affect this operation. The functionality for handling unordered
     * collections should be considered for future extensions.
     *
     * @param employeeToAdd The employee to add.
     * @return true if the employee was successfully added, false if the manager was not found.
     * @throws EmployeeNodeException if the employeeToAdd is null or if it doesn't have a manager.
     */
    public boolean addEmployee(Employee employeeToAdd) {
        if (employeeToAdd == null) throw new EmployeeNodeException("Employee must not be null");
        Integer managerId = employeeToAdd.getManagerId()
                .orElseThrow(() -> new EmployeeNodeException("Employee doesn't have a manager"));

        return addEmployee(employeeToAdd, managerId);
    }

    private boolean addEmployee(Employee employeeToAdd, Integer managerId) {
        if (managerId.equals(employee.getId())) {
            EmployeeNode employeeNode = EmployeeNode.builder()
                    .setEmployee(employeeToAdd)
                    .build();
            subordinates.add(employeeNode);
            return true;
        } else {
            for (EmployeeNode subordinate : subordinates) {
                if (subordinate.addEmployee(employeeToAdd, managerId)) return true;
            }
        }
        return false;
    }

    /**
     * Returns a new builder instance for constructing an EmployeeNode.
     *
     * @return The builder object.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for constructing EmployeeNode instances.
     */
    public static class Builder {

        private Employee employee;

        private Builder() {
        }

        /**
         * Sets the employee for the builder.
         *
         * @param employee The employee to set.
         * @return The builder object.
         */
        public Builder setEmployee(Employee employee) {
            this.employee = employee;
            return this;
        }

        /**
         * Builds and returns a new EmployeeNode instance.
         * <br>Required properties for build:
         * <ul>
         * <li>employee</li>
         * </ul>
         *
         * @return The constructed EmployeeNode object.
         */
        public EmployeeNode build() {
            return new EmployeeNode(this);
        }
    }
}
