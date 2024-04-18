package com.big.company.analytics.services;

import com.big.company.analytics.MainApplication;
import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.exception.FileReaderException;
import com.big.company.analytics.exception.ParseExtractionException;
import com.big.company.analytics.services.impl.AnalyticsManager;
import com.big.company.analytics.services.impl.EmployeeCsvFileReader;
import com.big.company.analytics.services.impl.EmployeeHierarchyReportService;
import com.big.company.analytics.services.impl.EmployeeNodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.big.company.analytics.test.util.AssertThrows.assertThrows;

import static org.junit.jupiter.api.Assertions.*;

class AnalyticsServiceTests {

    private static final String TEST_FILEPATH = "src/test/resources/";

    private FileReaderService<Employee> fileReaderService;
    private EmployeeNodeService nodeService;
    private EmployeeReportService reportService;

    @BeforeEach
    void setUp() {
        this.fileReaderService = new EmployeeCsvFileReader();
        this.nodeService = new EmployeeNodeGenerator();
        this.reportService = new EmployeeHierarchyReportService();
    }

    @Test
    void shouldAnalyticsManagerWithNullInjectionFails() {
        assertThrows("File reader service must not be null", NullPointerException.class,
                () -> new AnalyticsManager(null, nodeService, reportService));
        assertThrows("Employee node service must not be null", NullPointerException.class,
                () -> new AnalyticsManager(fileReaderService, null, reportService));
        assertThrows("Employee report service must not be null", NullPointerException.class,
                () -> new AnalyticsManager(fileReaderService, nodeService, null));
        assertThrows("File must not be null", NullPointerException.class,
                () -> new AnalyticsManager(fileReaderService, nodeService, reportService, null));
    }

    @Test
    void shouldAnalyticsManagerWithSpecificFileSuccessfullyOverwritesDefaultFile() {
        File specificFile = new File("specificFile.csv");
        AnalyticsManager analyticsManagerWithSpecificFile = new AnalyticsManager(fileReaderService, nodeService, reportService, specificFile);

        assertThrows("File not found | Filepath: / | Filename: specificFile.csv", FileReaderException.class,
                analyticsManagerWithSpecificFile::runAnalytics);
    }

    @Test
    void shouldAnalyticsManagerWithSpecificFileSuccessfullyOverwritesFileSystemProperty() {
        System.setProperty("file", TEST_FILEPATH + "SampleData.csv");
        File specificFile = new File("specificFile.csv");
        AnalyticsManager analyticsManagerWithSpecificFile = new AnalyticsManager(fileReaderService, nodeService, reportService, specificFile);

        assertThrows("File not found | Filepath: / | Filename: specificFile.csv", FileReaderException.class,
                analyticsManagerWithSpecificFile::runAnalytics);

        System.clearProperty("file");
    }

    private static List<String> validFiles() {
        return Arrays.asList(
                "SampleData.csv",
                "UnorderedData.csv",
                "ValidatedDataWithHeader.csv",
                "DataWithOddCaseHeader.csv",
                "InvertedColumnsData.csv"
        );
    }

    @ParameterizedTest()
    @MethodSource("validFiles")
    void shouldRunReportsWithValidFileSuccess(String fileName) {
        File file = new File(TEST_FILEPATH + fileName);
        AnalyticsManager analyticsManager = new AnalyticsManager(fileReaderService, nodeService, reportService, file);
        assertDoesNotThrow(analyticsManager::runAnalytics);
    }

    private static Stream<Arguments> invalidDataFiles() {
        return Stream.of(
                Arguments.of(
                        "ValidatedDataWithoutHeader.csv",
                        "Required header not found on header file: firstname"
                ),
                Arguments.of(
                        "DataWithInvalidHeader.csv",
                        "Required header not found on header file: lastname"
                ),
                Arguments.of(
                        "WrongFormatData.csv",
                        "Error on line number 2 -> For input string: \"WrongFormat\""
                )
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDataFiles")
    void shouldRunReportsWithInvalidDataFails(String fileName, String message) {
        File file = new File(TEST_FILEPATH + fileName);
        AnalyticsManager analyticsManager = new AnalyticsManager(fileReaderService, nodeService, reportService, file);

        assertThrows(message, ParseExtractionException.class, analyticsManager::runAnalytics);
    }

}