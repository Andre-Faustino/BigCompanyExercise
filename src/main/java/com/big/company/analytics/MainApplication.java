package com.big.company.analytics;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.exception.EmployeeReportException;
import com.big.company.analytics.exception.FileExtractionException;
import com.big.company.analytics.exception.ParseExtractionException;
import com.big.company.analytics.extractor.EmployeeDataExtractor;
import com.big.company.analytics.extractor.FileExtractor;
import com.big.company.analytics.report.EmployeeHierarchyReport;
import com.big.company.analytics.report.EmployeeReport;

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

        FileExtractor<Employee> extractor = new EmployeeDataExtractor();
        List<Employee> employees = extractEmployeesFromFile(extractor, csvFile);

        System.out.println("Extraction successfully done!");
        System.out.println();

        System.out.println("Init report of managers with policy violation");
        System.out.println();

        EmployeeReport report = new EmployeeHierarchyReport();
        runReports(report, employees);
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

    private static List<Employee> extractEmployeesFromFile(FileExtractor<Employee> extractor, File csvFile) {
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
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void runReports(EmployeeReport report, List<Employee> employees) {
        try {
            report.inputEmployees(employees);
            report.reportManagersSalaryPolicyViolation();
            report.reportManagersWithExcessiveReportingLines();
        } catch (EmployeeReportException e) {
            System.out.println("ERROR creating the reports of employees");
            throw new EmployeeReportException(e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR : unexpected error");
            throw new RuntimeException(e.getMessage());
        }
    }
}
