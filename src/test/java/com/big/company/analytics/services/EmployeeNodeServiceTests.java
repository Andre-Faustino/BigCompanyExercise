package com.big.company.analytics.services;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.domain.EmployeeNode;
import com.big.company.analytics.exception.EmployeeNodeServiceException;
import com.big.company.analytics.services.impl.EmployeeDataExtractor;
import com.big.company.analytics.services.impl.EmployeeNodeGenerator;
import com.big.company.analytics.test.util.AssertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.big.company.analytics.test.util.AssertThrows.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeNodeServiceTests {

    private static final String TEST_FILEPATH = "src/test/resources/";
    private static final String TEST_FILENAME = "SampleData.csv";
    List<Employee> employees;

    @BeforeEach
    void init() {
        this.employees = new EmployeeDataExtractor().extractFile(TEST_FILEPATH, TEST_FILENAME);
    }

    @Test
    void shouldGetEmployeesHierarchySuccessfully() {
        EmployeeNodeService nodeService = new EmployeeNodeGenerator();
        EmployeeNode employeesHierarchy = nodeService.getEmployeesHierarchy(employees);
        assertEquals(100, employeesHierarchy.size());
    }

    @Test
    void shouldGetEmployeesHierarchyWithUnorderedListSuccessfully() {
        List<Employee> unorderedEmployees = new EmployeeDataExtractor().extractFile(TEST_FILEPATH, "UnorderedData.csv");
        EmployeeNodeService nodeService = new EmployeeNodeGenerator();
        assertEquals(5, nodeService.getEmployeesHierarchy(unorderedEmployees).size());
    }

    @Test
    void shouldInvalidEmployeesListFails() {
        EmployeeNodeService nodeService = new EmployeeNodeGenerator();
        assertThrows("Employees list must not be null", NullPointerException.class,
                () -> nodeService.getEmployeesHierarchy(null));

        Employee anotherCEO = new Employee(345, "Elon", "Musk", 250000, null);
        employees.add(anotherCEO);

        AssertThrows.assertThrows("Error when creating Employee Hierarchy | Employee list has more than one CEO", EmployeeNodeServiceException.class,
                () -> nodeService.getEmployeesHierarchy(employees));
    }

    private static Stream provideEmployeeLists() {
        return Stream.of(
                Arguments.of(
                        Arrays.asList(
                                new Employee(123, "Joe", "Doe", 60000, null),
                                new Employee(124, "Martin", "Chekov", 45000, 123),
                                new Employee(125, "Bob", "Ronstad", 47000, 123),
                                new Employee(300, "Alice", "Hasacat", 50000, 124),
                                new Employee(305, "Brett", "Hardleaf", 34000, 300)
                        ),
                        5
                ),
                Arguments.of(
                        Arrays.asList(
                                new Employee(123, "Joe", "Doe", 60000, null),
                                new Employee(124, "Martin", "Chekov", 45000, 123),
                                new Employee(125, "Bob", "Ronstad", 47000, 123),
                                new Employee(300, "Alice", "Hasacat", 50000, 124)
                        ),
                        4
                ),
                Arguments.of(
                        Arrays.asList(
                                new Employee(123, "Joe", "Doe", 60000, null),
                                new Employee(124, "Martin", "Chekov", 45000, 123),
                                new Employee(125, "Bob", "Ronstad", 47000, 123),
                                new Employee(300, "Alice", "Hasacat", 50000, 124),
                                new Employee(305, "Brett", "Hardleaf", 34000, 300),
                                new Employee(306, "John", "Petrucci", 66000, 305)
                        ),
                        6
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideEmployeeLists")
    void shouldConcurrentlyEmployeeNodeGenerationSuccess(List<Employee> employees, int expectedSize) {
        EmployeeNodeService nodeService = new EmployeeNodeGenerator();

        CompletableFuture.supplyAsync(() -> nodeService.getEmployeesHierarchy(employees))
                .thenAccept(node -> assertEquals(expectedSize, node.size()))
                .join();
    }
}