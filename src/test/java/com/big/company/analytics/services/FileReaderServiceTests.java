package com.big.company.analytics.services;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.exception.FileReaderException;
import com.big.company.analytics.exception.ParseExtractionException;
import com.big.company.analytics.services.impl.EmployeeCsvFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static com.big.company.analytics.test.util.AssertThrows.assertThrows;

class FileReaderServiceTests {

    private static final String TEST_FILEPATH = "src/test/resources/";
    FileReaderService<Employee> fileReaderService;

    @BeforeEach
    void init() {
        this.fileReaderService = new EmployeeCsvFileReader();
    }

    @Test
    void shouldFailWhenExtractFileWithWrongParameters() {
        assertThrows("File should not be null", NullPointerException.class,
                () -> fileReaderService.readFile(null));

        assertThrows("Path should not be null", NullPointerException.class,
                () -> fileReaderService.readFile(null, null));

        assertThrows("File name should not be null", NullPointerException.class,
                () -> fileReaderService.readFile("valid/path", null));

        assertThrows("Path and filename should not be blank", FileReaderException.class,
                () -> fileReaderService.readFile("", ""));
    }

    @Test
    void shouldFailWhenFileNotExistParameters() {
        assertThrows("File not found | Filepath: this\\path\\not\\exist | Filename: NoFile", FileReaderException.class,
                () -> fileReaderService.readFile("this/path/not/exist", "NoFile"));
    }

    @Test
    void shouldDataSuccessfullyExtracted() {
        List<Employee> employeeData = fileReaderService.readFile(TEST_FILEPATH, "ValidatedDataWithHeader.csv");

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
        assertThrows("Error on line number 2 -> For input string: \"WrongFormat\"", ParseExtractionException.class,
                () -> fileReaderService.readFile(TEST_FILEPATH, "WrongFormatData.csv"));

        assertThrows("Error on line number 2 -> Line has less elements than the required size 4", ParseExtractionException.class,
                () -> fileReaderService.readFile(TEST_FILEPATH, "MissingData.csv"));
    }

    @Test
    void shouldSuccessfullyInputDataWithoutHeader() {
        fileReaderService = new EmployeeCsvFileReader(false);
        List<Employee> employeeData = fileReaderService.readFile(TEST_FILEPATH, "ValidatedDataWithoutHeader.csv");

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
                () -> fileReaderService.readFile(TEST_FILEPATH, "ValidatedDataWithoutHeader.csv"));


        FileReaderService<Employee> fileReaderServiceExpectingNoHeader = new EmployeeCsvFileReader(false);
        assertThrows("Error on line number 0 -> For input string: \"Id\"", ParseExtractionException.class,
                () -> fileReaderServiceExpectingNoHeader.readFile(TEST_FILEPATH, "ValidatedDataWithHeader.csv"));
    }

    @Test
    void shouldInvalidDataHeaderFails() {
        assertThrows("Required header not found on header file: lastname", ParseExtractionException.class,
                () -> fileReaderService.readFile(TEST_FILEPATH, "DataWithInvalidHeader.csv"));
    }

    @Test
    void shouldDataWithOddHeaderSuccess() {
        List<Employee> employeeData = fileReaderService.readFile(TEST_FILEPATH, "DataWithOddCaseHeader.csv");

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
