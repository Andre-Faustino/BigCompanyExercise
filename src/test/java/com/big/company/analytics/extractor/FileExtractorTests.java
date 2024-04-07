package com.big.company.analytics.extractor;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.exception.FileExtractionException;
import com.big.company.analytics.exception.ParseExtractionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static com.big.company.analytics.test.util.AssertThrows.assertThrows;

public class FileExtractorTests {

    private static final String TEST_FILEPATH = "src/test/resources/";
    FileExtractor<Employee> fileExtractor;

    @BeforeEach
    void init() {
        this.fileExtractor = new EmployeeDataExtractor();
    }

    @Test
    void shouldFailWhenExtractFileWithWrongParameters() {

        assertThrows("File should not be null", FileExtractionException.class,
                () -> fileExtractor.extractFile(null));

        assertThrows("Path and filename should not be null", FileExtractionException.class,
                () -> fileExtractor.extractFile(null, null));

        assertThrows("Path and filename should not be blank", FileExtractionException.class,
                () -> fileExtractor.extractFile("", ""));
    }

    @Test
    void shouldDataSuccessfullyExtracted() {
        List<Employee> employeeData = fileExtractor.extractFile(TEST_FILEPATH, "ValidatedDataWithHeader.csv");

        List<Employee> expectedEmployees =
                Arrays.asList(
                        Employee.builder()
                                .setId(123)
                                .setFirstName("Joe")
                                .setLastName("Doe")
                                .setSalary(60000)
                                .build(),
                        Employee.builder()
                                .setId(124)
                                .setFirstName("Martin")
                                .setLastName("Chekov")
                                .setSalary(45000)
                                .setManagerId(123)
                                .build(),
                        Employee.builder()
                                .setId(125)
                                .setFirstName("Bob")
                                .setLastName("Ronstad")
                                .setSalary(47000)
                                .setManagerId(123)
                                .build(),
                        Employee.builder()
                                .setId(300)
                                .setFirstName("Alice")
                                .setLastName("Hasacat")
                                .setSalary(50000)
                                .setManagerId(124)
                                .build(),
                        Employee.builder()
                                .setId(305)
                                .setFirstName("Brett")
                                .setLastName("Hardleaf")
                                .setSalary(34000)
                                .setManagerId(300)
                                .build()
                );

        assertEquals(expectedEmployees.size(), employeeData.size());

        IntStream.range(0, expectedEmployees.size())
                .forEach(i -> assertEquals(expectedEmployees.get(i), employeeData.get(i)));
    }

    @Test
    void shouldInvalidDataExtractionFails() {
        assertThrows("Error on line 2 -> For input string: \"WrongFormat\"", ParseExtractionException.class,
                () -> fileExtractor.extractFile(TEST_FILEPATH, "WrongFormatData.csv"));

        assertThrows("Error on line 2 -> Employee last name is missing", ParseExtractionException.class,
                () -> fileExtractor.extractFile(TEST_FILEPATH, "MissingData.csv"));
    }
}
