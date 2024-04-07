package com.big.company.analytics.extractor;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.exception.FileExtractionException;
import com.big.company.analytics.exception.ParseExtractionException;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of {@code FileExtractor} for extracting {@code Employee} objects from a CSV file
 * with a fixed comma delimiter (`,`).
 * <p>
 * This implementation assumes that the CSV file has a header line that needs to be skipped.
 */
public final class EmployeeDataExtractor implements FileExtractor<Employee> {

    private final static String DELIMITER = ",";
    private final static Boolean HAS_HEADER = true;

    /**
     * Extracts {@code Employee} objects from a CSV file specified by path and filename.
     *
     * @param path     the path to the directory containing the CSV file
     * @param fileName the name of the CSV file
     * @return a list of {@code Employee} objects extracted from the CSV file
     * @throws FileExtractionException   if the file is not found or cannot be loaded
     * @throws ParseExtractionException  if any error occurs during parsing of the file content
     */
    @Override
    public List<Employee> extractFile(String path, String fileName) {
        if (path == null || fileName == null) throw new FileExtractionException("Path and filename should not be null");
        if (path.isBlank() || fileName.isBlank())
            throw new FileExtractionException("Path and filename should not be blank");

        return extractFile(loadFile(path, fileName));
    }

    /**
     * Extracts {@code Employee} objects from a specified CSV file.
     *
     * @param file the CSV file object from which to extract {@code Employee} objects
     * @return a list of {@code Employee} objects extracted from the CSV file
     * @throws FileExtractionException   if the file is not found or cannot be loaded
     * @throws ParseExtractionException  if any error occurs during parsing of the file content
     */
    @Override
    public List<Employee> extractFile(File file) {
        if (file == null) throw new FileExtractionException("File should not be null");
        FileReader fileReader = loadFileReader(file);

        List<Employee> employees = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(fileReader)) {
            String line;
            int curLine = 0;
            while ((line = br.readLine()) != null) {
                if (HAS_HEADER && curLine == 0) {
                    curLine++;
                    continue;
                }
                String[] values = line.split(DELIMITER);
                try {
                    employees.add(employeeFromLineValues(values));
                } catch (Exception e) {
                    throw new ParseExtractionException(String.format("Error on line %d -> %s", curLine, e.getMessage()));
                }
                curLine++;
            }
        } catch (IOException e) {
            throw new FileExtractionException("Error reading file after loaded");
        }
        return employees;
    }

    private static FileReader loadFileReader(File file) throws FileExtractionException {
        try {
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new FileExtractionException(
                    String.format("File doesn't exist or it's not readable | Filepath: %s | Filename: %s", file.getName(), file.getPath()));
        }
    }

    private static File loadFile(String path, String fileName) {
        String filePath = Paths.get(path, fileName).toString();
        return new File(filePath);
    }

    private static Employee employeeFromLineValues(String[] values) {
        return Employee.builder()
                .setId(getIntegerValue(values, 0))
                .setFirstName(getStringValue(values, 1))
                .setLastName(getStringValue(values, 2))
                .setSalary(getIntegerValue(values, 3))
                .setManagerId(getIntegerValue(values, 4))
                .build();
    }

    private static Integer getIntegerValue(String[] values, int index) {
        return (index < values.length) ? Integer.valueOf(values[index]) : null;
    }

    private static String getStringValue(String[] values, int index) {
        return (index < values.length) ? values[index] : null;
    }
}
