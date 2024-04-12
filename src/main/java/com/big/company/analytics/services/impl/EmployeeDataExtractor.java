package com.big.company.analytics.services.impl;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.exception.FileExtractionException;
import com.big.company.analytics.exception.ParseExtractionException;
import com.big.company.analytics.services.FileExtractor;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * An implementation of {@code FileExtractor} for extracting {@code Employee} objects from a CSV file
 * with a fixed comma delimiter (`,`).
 * <p>
 * This implementation assumes that the CSV file has a header line that needs to be skipped.
 */
public final class EmployeeDataExtractor implements FileExtractor<Employee> {

    /**
     * The delimiter used in the CSV file.
     */
    private static final String DELIMITER = ",";

    private static final List<String> headerOrder =
            Arrays.asList("id", "firstname", "lastname", "salary", "managerid");

    /**
     * Flag indicating whether the CSV file has a header line.
     */
    private final boolean hasHeader;

    /**
     * Constructs a new {@code EmployeeDataExtractor} with the specified value for whether the CSV file has a header line.
     *
     * @param hasHeader {@code true} if the CSV file has a header line to be skipped, {@code false} otherwise
     */
    public EmployeeDataExtractor(Boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    /**
     * Constructs a new {@code EmployeeDataExtractor} with default settings, assuming the CSV file has a header line.
     */
    public EmployeeDataExtractor() {
        this.hasHeader = true;
    }

    /**
     * Extracts {@code Employee} objects from a CSV file specified by path and filename.
     *
     * @param path     the path to the directory containing the CSV file
     * @param fileName the name of the CSV file
     * @return a list of {@code Employee} objects extracted from the CSV file
     * @throws FileExtractionException  if the file is not found or cannot be loaded
     * @throws ParseExtractionException if any error occurs during parsing of the file content
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
     * @throws FileExtractionException  if the file is not found or cannot be loaded
     * @throws ParseExtractionException if any error occurs during parsing of the file content
     */
    @Override
    public List<Employee> extractFile(File file) {
        if (file == null) throw new FileExtractionException("File should not be null");

        List<Employee> employees = new ArrayList<>();
        try (
                FileReader fileReader = new FileReader(file);
                BufferedReader br = new BufferedReader(fileReader)
        ) {
            String line;
            int curLine = 0;
            int[] headerMapper = new int[0];
            while ((line = br.readLine()) != null) {
                if (hasHeader && curLine == 0) {
                    headerMapper = createHeaderMapper(line.split(DELIMITER));
                    curLine++;
                    continue;
                }
                String[] values = (hasHeader)
                        ? orderExtractedData(headerMapper, line.split(DELIMITER))
                        : line.split(DELIMITER);

                employees.add(employeeFromLineValues(values, curLine));
                curLine++;
            }
        } catch (FileNotFoundException e) {
            throw new FileExtractionException(
                    String.format("File not found | Filepath: %s | Filename: %s", file.getPath(), file.getName()));
        } catch (SecurityException e) {
            throw new FileExtractionException(
                    String.format("File reading not permitted | Filepath: %s | Filename: %s", file.getPath(), file.getName()));
        } catch (IOException e) {
            throw new FileExtractionException("Error when reading the file");
        }
        return employees;
    }

    /**
     * Loads a file specified by path and filename.
     *
     * @param path     the path to the directory containing the file
     * @param fileName the name of the file
     * @return the loaded {@code File} object
     */
    private static File loadFile(String path, String fileName) {
        String filePath = Paths.get(path, fileName).toString();
        return new File(filePath);
    }

    /**
     * Constructs an {@code Employee} object from an array of values representing employee data from a CSV line.
     *
     * @param values the array of values representing employee data
     * @param line   the line number from which the data was extracted
     * @return the constructed {@code Employee} object
     * @throws ParseExtractionException if any error occurs during parsing of the employee data
     */
    private static Employee employeeFromLineValues(String[] values, int line) {
        try {
            return new Employee(
                    getIntegerValue(values, 0),
                    getStringValue(values, 1),
                    getStringValue(values, 2),
                    getIntegerValue(values, 3),
                    getIntegerValue(values, 4)
            );
        } catch (Exception e) {
            throw new ParseExtractionException(String.format("Error on line %d -> %s", line, e.getMessage()));
        }
    }

    /**
     * Creates a header mapper array to map CSV header columns to their respective positions
     * described on {@code headerOrder}.
     * @param header the array representing the header line of the CSV file
     * @return the header mapper array
     * @throws ParseExtractionException if any error occurs during parsing of the header
     */
    private int[] createHeaderMapper(String[] header) throws ParseExtractionException {
        List<String> headerList = Arrays.stream(header).map(String::toLowerCase).toList();
        new HashSet<>(headerOrder).forEach(requiredHeader -> {
            if (!headerList.contains(requiredHeader))
                throw new ParseExtractionException(String.format("Required header not found on header file: %s", requiredHeader));
        });
        return headerList.stream().mapToInt(headerOrder::indexOf).toArray();
    }

    /**
     * Orders extracted data based on the header mapper array.
     *
     * @param headerMapper the header mapper array
     * @param values       the array of values representing data from a CSV line
     * @return the ordered data array
     */
    private String[] orderExtractedData(int[] headerMapper, String[] values) {
        String[] orderedData = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            orderedData[headerMapper[i]] = values[i];
        }
        return orderedData;
    }

    /**
     * Gets an integer value from the array of values at the specified index.
     *
     * @param values the array of values
     * @param index  the index from which to get the value
     * @return the integer value at the specified index, or {@code null} if the index is out of bounds
     */
    private static Integer getIntegerValue(String[] values, int index) {
        return (index < values.length) ? Integer.valueOf(values[index]) : null;
    }

    /**
     * Gets a string value from the array of values at the specified index.
     *
     * @param values the array of values
     * @param index  the index from which to get the value
     * @return the string value at the specified index, or {@code null} if the index is out of bounds
     */
    private static String getStringValue(String[] values, int index) {
        return (index < values.length) ? values[index] : null;
    }
}
