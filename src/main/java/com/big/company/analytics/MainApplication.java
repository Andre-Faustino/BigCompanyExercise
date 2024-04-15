package com.big.company.analytics;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.domain.EmployeeNode;
import com.big.company.analytics.exception.FileExtractionException;
import com.big.company.analytics.exception.ParseExtractionException;
import com.big.company.analytics.exception.UnexpectedException;
import com.big.company.analytics.services.EmployeeNodeService;
import com.big.company.analytics.services.impl.EmployeeDataExtractorService;
import com.big.company.analytics.services.FileExtractorService;
import com.big.company.analytics.services.impl.EmployeeHierarchyReportService;
import com.big.company.analytics.services.EmployeeReportService;
import com.big.company.analytics.services.impl.EmployeeNodeGenerator;

import java.io.File;
import java.util.List;

public class MainApplication {

    private static final String STANDARD_FILENAME = "SampleData.csv";

    public static void main(String[] args) {
        System.out.println("=========== INITIALIZING ANALYTICS REPORTS ===========");
        System.out.println();

        File csvFile = getFile();

        System.out.println();
        System.out.println("Init extraction of employees from file");

        FileExtractorService<Employee> extractor = getFileExtractor();
        List<Employee> employees = extractEmployeesFromFile(extractor, csvFile);

        System.out.println("Extraction successfully done!");
        System.out.printf("Employees loaded: %d%n", employees.size());
        System.out.println();

        System.out.println("Creating employee hierarchy...");

        EmployeeNodeService nodeService = new EmployeeNodeGenerator();
        EmployeeNode employeesHierarchy = nodeService.getEmployeesHierarchy(employees);

        System.out.println("Employee hierarchy generated!");
        System.out.println();

        System.out.println("Init report of managers with policy violation");
        System.out.println();

        EmployeeReportService report = new EmployeeHierarchyReportService();
        runReports(report, employeesHierarchy);
        System.out.println("=========== FINISHING ANALYTICS REPORTS ===========");
    }


    private static File getFile() {
        String filePath = System.getProperty("file");
        if (filePath == null) {
            System.out.printf("WARNING: File argument not found. Application will search standard file name: %s %n", STANDARD_FILENAME);
            filePath = STANDARD_FILENAME;
        }
        System.out.printf("Loading file: %s%n", filePath);
        return new File(filePath);
    }

    private static FileExtractorService<Employee> getFileExtractor() {
        String hasHeader = System.getProperty("has_header");
        if (hasHeader == null || (!hasHeader.equalsIgnoreCase("true") && !hasHeader.equalsIgnoreCase("false"))) {
            System.out.println("WARNING -> The 'has_header' property is not set to 'true' or 'false'. Using default configuration: expecting a file with a header.");
            return new EmployeeDataExtractorService();
        }

        boolean expectHeader = Boolean.parseBoolean(hasHeader);
        System.out.println("DataExtractor -> Using user configuration: Expect file with " + (expectHeader ? "header" : "NO header"));
        return new EmployeeDataExtractorService(expectHeader);
    }

    private static List<Employee> extractEmployeesFromFile(FileExtractorService<Employee> extractor, File csvFile) {
        try {
            return extractor.extractFile(csvFile);
        } catch (FileExtractionException e) {
            System.out.println("ERROR when loading the file");
            throw new FileExtractionException(e.getMessage());
        } catch (ParseExtractionException e) {
            System.out.println("ERROR when reading the file");
            throw new ParseExtractionException(e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR : unexpected error");
            throw new UnexpectedException(e.getMessage());
        }
    }

    private static void runReports(EmployeeReportService report, EmployeeNode employees) {
        try {
            report.reportManagersSalaryPolicyViolation(employees);
            report.reportManagersWithExcessiveReportingLines(employees);
        } catch (NullPointerException e) {
            System.out.printf("ERROR creating the reports of employees | %s%n", e.getMessage());
            throw new NullPointerException(e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR : unexpected error");
            throw new UnexpectedException(e.getMessage());
        }
    }
}
