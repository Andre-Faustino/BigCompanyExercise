package com.big.company.analytics.services.impl;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.domain.EmployeeNode;
import com.big.company.analytics.exception.FileReaderException;
import com.big.company.analytics.exception.ParseExtractionException;
import com.big.company.analytics.exception.UnexpectedException;
import com.big.company.analytics.services.AnalyticsService;
import com.big.company.analytics.services.EmployeeNodeService;
import com.big.company.analytics.services.FileReaderService;
import com.big.company.analytics.services.EmployeeReportService;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * Manages analytics operations by coordinating data reading, processing, and reporting.
 * Works as the main flow for the analytics reporting operation.
 */
public class AnalyticsManager implements AnalyticsService {

    /**
     * Default name for the sample data CSV file.
     */
    public static final String DEFAULT_SAMPLE_DATA_CSV = "SampleData.csv";

    /**
     * Service for reading data from a file.
     */
    private final FileReaderService<Employee> fileReaderService;

    /**
     * Service for generating employee hierarchy nodes.
     */
    private final EmployeeNodeService nodeService;

    /**
     * Service for generating employee reports.
     */
    private final EmployeeReportService report;

    /**
     * The file containing employee data.
     */
    private final File file;

    /**
     * Constructs an {@code AnalyticsManager} object with the specified services and file.
     *
     * @param fileReaderService The service for reading data from a file.
     * @param nodeService       The service for generating employee hierarchy nodes.
     * @param report            The service for generating employee reports.
     * @param file              The file containing employee data.
     * @throws NullPointerException if any of the services or the file is null.
     */
    public AnalyticsManager(
            FileReaderService<Employee> fileReaderService,
            EmployeeNodeService nodeService,
            EmployeeReportService report,
            File file) {
        this.fileReaderService = Objects.requireNonNull(fileReaderService, "File reader service must not be null");
        this.nodeService = Objects.requireNonNull(nodeService, "Employee node service must not be null");
        this.report = Objects.requireNonNull(report, "Employee report service must not be null");
        this.file = Objects.requireNonNull(file, "File must not be null");
    }

    /**
     * Constructs an {@code AnalyticsManager} object with the specified services and default file.
     * The default file is set to "SampleData.csv" and the {@code AnalyticsManager} will search for it in the same directory the app is running.
     *
     * @param fileReaderService The service for reading data from a file.
     * @param nodeService       The service for generating employee hierarchy nodes.
     * @param report            The service for generating employee reports.
     * @throws NullPointerException if any of the services is null.
     */
    public AnalyticsManager(
            FileReaderService<Employee> fileReaderService,
            EmployeeNodeService nodeService,
            EmployeeReportService report) {
        this.fileReaderService = Objects.requireNonNull(fileReaderService, "File reader service must not be null");
        this.nodeService = Objects.requireNonNull(nodeService, "Employee node service must not be null");
        this.report = Objects.requireNonNull(report, "Employee report service must not be null");
        this.file = initDefaultFile();
    }

    /**
     * Initializes the default file with the name "SampleData.csv".
     *
     * @return The default file.
     */
    private File initDefaultFile() {
        System.out.printf("WARNING: File argument not found. Application will search standard file name: %s%n", DEFAULT_SAMPLE_DATA_CSV);
        return new File(DEFAULT_SAMPLE_DATA_CSV);
    }

    /**
     * Retrieves employees from the specified CSV file.
     *
     * @param csvFile The CSV file containing employee data.
     * @return A list of employees.
     * @throws FileReaderException      If an error occurs while extracting data from the file.
     * @throws ParseExtractionException If an error occurs while parsing the extracted data.
     * @throws UnexpectedException      If an unexpected error occurs.
     */
    private List<Employee> retrieveEmployeesFromFile(File csvFile) {
        try {
            return fileReaderService.readFile(csvFile);
        } catch (FileReaderException e) {
            System.out.println("ERROR when loading the file");
            throw new FileReaderException(e.getMessage());
        } catch (ParseExtractionException e) {
            System.out.println("ERROR when reading the file");
            throw new ParseExtractionException(e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR: unexpected error");
            throw new UnexpectedException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void runAnalytics() {
        System.out.println("=========== INITIALIZING ANALYTICS REPORTS ===========");
        System.out.println();

        System.out.println();
        System.out.println("Init reading of employees from file");

        System.out.printf("Loading file: %s%n", file.getName());
        List<Employee> employees = retrieveEmployeesFromFile(file);

        System.out.println("Reading successfully done!");
        System.out.printf("Employees loaded: %d%n", employees.size());
        System.out.println();

        System.out.println("Creating employee hierarchy...");

        EmployeeNode employeesHierarchy = nodeService.generateEmployeesHierarchy(employees);

        System.out.println("Employee hierarchy generated!");
        System.out.println();

        System.out.println("Init report of managers with policy violation");
        System.out.println();

        runReports(employeesHierarchy);
        System.out.println("=========== FINISHING ANALYTICS REPORTS ===========");
    }

    /**
     * Runs reports on the employee hierarchy.
     *
     * @param employees The root node of the employee hierarchy.
     */
    private void runReports(EmployeeNode employees) {
        try {
            report.reportManagersSalaryPolicyViolation(employees);
            report.reportManagersWithExcessiveReportingLines(employees);
        } catch (Exception e) {
            System.out.printf("ERROR creating the reports of employees | %s%n", e.getMessage());
            throw e;
        }
    }
}