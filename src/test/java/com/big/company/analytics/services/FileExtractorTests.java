package com.big.company.analytics.services;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.exception.FileExtractionException;
import com.big.company.analytics.exception.ParseExtractionException;
import com.big.company.analytics.services.impl.EmployeeDataExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static com.big.company.analytics.test.util.AssertThrows.assertThrows;

class FileExtractorTests {

    private static final String TEST_FILEPATH = "src/test/resources/";
    FileExtractor<Employee> fileExtractor;

    @BeforeEach
    void init() {
        this.fileExtractor = new EmployeeDataExtractor();
    }

    @Test
    void shouldFailWhenExtractFileWithWrongParameters() {
        assertThrows("File should not be null", NullPointerException.class,
                () -> fileExtractor.extractFile(null));

        assertThrows("Path should not be null", NullPointerException.class,
                () -> fileExtractor.extractFile(null, null));

        assertThrows("File name should not be null", NullPointerException.class,
                () -> fileExtractor.extractFile("valid/path", null));

        assertThrows("Path and filename should not be blank", FileExtractionException.class,
                () -> fileExtractor.extractFile("", ""));
    }

    @Test
    void shouldFailWhenFileNotExistParameters() {
        assertThrows("File not found | Filepath: this\\path\\not\\exist | Filename: NoFile", FileExtractionException.class,
                () -> fileExtractor.extractFile("this/path/not/exist", "NoFile"));
    }

    @Test
    void shouldDataSuccessfullyExtracted() {
        List<Employee> employeeData = fileExtractor.extractFile(TEST_FILEPATH, "ValidatedDataWithHeader.csv");

        List<Employee> expectedEmployees = Arrays.asList(
                new Employee(123, "Joe", "Doe", 60000, null),
                new Employee(124, "Martin", "Chekov", 45000, 123),
                new Employee(125, "Bob", "Ronstad", 47000, 123),
                new Employee(300, "Alice", "Hasacat", 50000, 124),
                new Employee(305, "Brett", "Hardleaf", 34000, 300)
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

    @Test
    void shouldSuccessfullyInputDataWithoutHeader() {
        fileExtractor = new EmployeeDataExtractor(false);
        List<Employee> employeeData = fileExtractor.extractFile(TEST_FILEPATH, "ValidatedDataWithoutHeader.csv");

        List<Employee> expectedEmployees = Arrays.asList(
                new Employee(123, "Joe", "Doe", 60000, null),
                new Employee(124, "Martin", "Chekov", 45000, 123),
                new Employee(125, "Bob", "Ronstad", 47000, 123),
                new Employee(300, "Alice", "Hasacat", 50000, 124),
                new Employee(305, "Brett", "Hardleaf", 34000, 300)
        );

        assertEquals(expectedEmployees.size(), employeeData.size());
    }

    @Test
    void shouldWrongHeaderConfigFails() {
        assertThrows("Required header not found on header file: firstname", ParseExtractionException.class,
                () -> fileExtractor.extractFile(TEST_FILEPATH, "ValidatedDataWithoutHeader.csv"));


        FileExtractor<Employee> fileExtractorExpectingNoHeader = new EmployeeDataExtractor(false);
        assertThrows("Error on line 0 -> For input string: \"Id\"", ParseExtractionException.class,
                () -> fileExtractorExpectingNoHeader.extractFile(TEST_FILEPATH, "ValidatedDataWithHeader.csv"));
    }

    @Test
    void shouldInvalidDataHeaderFails() {
        assertThrows("Required header not found on header file: lastname", ParseExtractionException.class,
                () -> fileExtractor.extractFile(TEST_FILEPATH, "DataWithInvalidHeader.csv"));
    }

    @Test
    void shouldDataWithOddHeaderSuccess() {
        List<Employee> employeeData = fileExtractor.extractFile(TEST_FILEPATH, "DataWithOddCaseHeader.csv");

        List<Employee> expectedEmployees = Arrays.asList(
                new Employee(123, "Joe", "Doe", 60000, null),
                new Employee(124, "Martin", "Chekov", 45000, 123),
                new Employee(125, "Bob", "Ronstad", 47000, 123),
                new Employee(300, "Alice", "Hasacat", 50000, 124),
                new Employee(305, "Brett", "Hardleaf", 34000, 300)
        );

        assertEquals(expectedEmployees.size(), employeeData.size());
    }
}
