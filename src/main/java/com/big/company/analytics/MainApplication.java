package com.big.company.analytics;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.services.EmployeeNodeService;
import com.big.company.analytics.services.EmployeeReportService;
import com.big.company.analytics.services.FileReaderService;
import com.big.company.analytics.services.impl.AnalyticsManager;
import com.big.company.analytics.services.impl.EmployeeCsvFileReader;
import com.big.company.analytics.services.impl.EmployeeHierarchyReportService;
import com.big.company.analytics.services.impl.EmployeeNodeGenerator;

import java.io.File;
import java.util.Optional;

public class MainApplication {

    public static final String HAS_HEADER_SYSTEM_PROPERTY = "has_header";
    /**
     * System property key for specifying the file path.
     */
    public static final String FILE_SYSTEM_PROPERTY = "file";
    public static final String DEFAULT_HAS_HEADER_VALUE = "true";


    public static void main(String[] args) {

        String hasHeader = Optional.ofNullable(System.getProperty(HAS_HEADER_SYSTEM_PROPERTY)).orElse(DEFAULT_HAS_HEADER_VALUE);
        FileReaderService<Employee> fileReaderService = new EmployeeCsvFileReader(!hasHeader.equals("false"));

        EmployeeNodeService nodeService = new EmployeeNodeGenerator();
        EmployeeReportService reportService = new EmployeeHierarchyReportService();

        String filePath = System.getProperty(FILE_SYSTEM_PROPERTY);
        AnalyticsManager analyticsManager = (filePath != null) ?
                new AnalyticsManager(fileReaderService, nodeService, reportService, new File(filePath)) :
                new AnalyticsManager(fileReaderService, nodeService, reportService);

        analyticsManager.runAnalytics();
    }
}
