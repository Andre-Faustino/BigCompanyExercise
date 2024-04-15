package com.big.company.analytics.services;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.exception.FileExtractionException;
import com.big.company.analytics.exception.ParseExtractionException;
import com.big.company.analytics.services.impl.EmployeeDataExtractorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static com.big.company.analytics.test.util.AssertThrows.assertThrows;

class FileExtractorServiceTests {

    private static final String TEST_FILEPATH = "src/test/resources/";
    FileExtractorService<Employee> fileExtractorService;

    @BeforeEach
    void init() {
        this.fileExtractorService = new EmployeeDataExtractorService();
    }

    @Test
    void shouldFailWhenExtractFileWithWrongParameters() {
        assertThrows("File should not be null", NullPointerException.class,
                () -> fileExtractorService.extractFile(null));

        assertThrows("Path should not be null", NullPointerException.class,
                () -> fileExtractorService.extractFile(null, null));

        assertThrows("File name should not be null", NullPointerException.class,
                () -> fileExtractorService.extractFile("valid/path", null));

        assertThrows("Path and filename should not be blank", FileExtractionException.class,
                () -> fileExtractorService.extractFile("", ""));
    }

    @Test
    void shouldFailWhenFileNotExistParameters() {
        assertThrows("File not found | Filepath: this\\path\\not\\exist | Filename: NoFile", FileExtractionException.class,
                () -> fileExtractorService.extractFile("this/path/not/exist", "NoFile"));
    }

    @Test
    void shouldDataSuccessfullyExtracted() {
        List<Employee> employeeData = fileExtractorService.extractFile(TEST_FILEPATH, "ValidatedDataWithHeader.csv");

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
                () -> fileExtractorService.extractFile(TEST_FILEPATH, "WrongFormatData.csv"));

        assertThrows("Error on line 2 -> Employee last name is missing", ParseExtractionException.class,
                () -> fileExtractorService.extractFile(TEST_FILEPATH, "MissingData.csv"));
    }

    @Test
    void shouldSuccessfullyInputDataWithoutHeader() {
        fileExtractorService = new EmployeeDataExtractorService(false);
        List<Employee> employeeData = fileExtractorService.extractFile(TEST_FILEPATH, "ValidatedDataWithoutHeader.csv");

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
                () -> fileExtractorService.extractFile(TEST_FILEPATH, "ValidatedDataWithoutHeader.csv"));


        FileExtractorService<Employee> fileExtractorServiceExpectingNoHeader = new EmployeeDataExtractorService(false);
        assertThrows("Error on line 0 -> For input string: \"Id\"", ParseExtractionException.class,
                () -> fileExtractorServiceExpectingNoHeader.extractFile(TEST_FILEPATH, "ValidatedDataWithHeader.csv"));
    }

    @Test
    void shouldInvalidDataHeaderFails() {
        assertThrows("Required header not found on header file: lastname", ParseExtractionException.class,
                () -> fileExtractorService.extractFile(TEST_FILEPATH, "DataWithInvalidHeader.csv"));
    }

    @Test
    void shouldDataWithOddHeaderSuccess() {
        List<Employee> employeeData = fileExtractorService.extractFile(TEST_FILEPATH, "DataWithOddCaseHeader.csv");

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
