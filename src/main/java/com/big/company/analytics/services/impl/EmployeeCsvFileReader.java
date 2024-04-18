package com.big.company.analytics.services.impl;

import com.big.company.analytics.domain.Employee;
import com.big.company.analytics.exception.FileReaderException;
import com.big.company.analytics.exception.ParseExtractionException;
import com.big.company.analytics.services.FileReaderService;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

/**
 * An implementation of {@code FileReaderService} for retrieve {@code Employee} objects from a CSV file
 * with a fixed comma delimiter (`,`).
 * <p>
 * This implementation assumes that the CSV file has a header line that needs to be skipped.
 */
public final class EmployeeCsvFileReader implements FileReaderService<Employee> {

    /**
     * The delimiter used in the CSV file.
     */
    private static final String DELIMITER = ",";

    /**
     * Defines the order of header columns in a CSV file.
     */
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
    public EmployeeCsvFileReader(Boolean hasHeader) {
        this.hasHeader = (hasHeader != null) ? hasHeader : Defaults.HAS_HEADER;
    }

    /**
     * Constructs a new {@code EmployeeDataExtractor} with default settings, assuming the CSV file has a header line.
     */
    public EmployeeCsvFileReader() {
        this.hasHeader = Defaults.HAS_HEADER;
    }

    /**
     * Read {@code Employee} objects from a CSV file specified by path and filename.
     *
     * @param path     the path to the directory containing the CSV file
     * @param fileName the name of the CSV file
     * @return a list of {@code Employee} objects read from the CSV file
     * @throws FileReaderException  if the file is not found or cannot be loaded
     * @throws ParseExtractionException if any error occurs during parsing of the file content
     * @throws NullPointerException     if any params is null
     */
    @Override
    public List<Employee> readFile(String path, String fileName) {
        Objects.requireNonNull(path, "Path should not be null");
        Objects.requireNonNull(fileName, "File name should not be null");
        if (path.isBlank() || fileName.isBlank())
            throw new FileReaderException("Path and filename should not be blank");

        return readFile(loadFile(path, fileName));
    }

    /**
     * Read {@code Employee} objects from a specified CSV file.
     *
     * @param file the CSV file object from which {@code Employee} objects will be read
     * @return a list of {@code Employee} objects read from the CSV file
     * @throws FileReaderException  if the file is not found or cannot be loaded
     * @throws ParseExtractionException if any error occurs during parsing of the file content
     * @throws NullPointerException     if any params is null
     */
    @Override
    public List<Employee> readFile(File file) {
        Objects.requireNonNull(file, "File should not be null");

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
                        ? orderReadData(headerMapper, line.split(DELIMITER))
                        : line.split(DELIMITER);

                if (values.length < Defaults.MINIMUM_REQUIRED_VALUES_BY_CSV_LINE)
                    throw new ParseExtractionException(String.format("Error on line number %d -> %s %d", curLine, "Line has less elements than the required size", Defaults.MINIMUM_REQUIRED_VALUES_BY_CSV_LINE));

                employees.add(employeeFromLineValues(values, curLine));
                curLine++;
            }
        } catch (FileNotFoundException e) {
            throw new FileReaderException(
                    String.format("File not found | Filepath: %s | Filename: %s", Optional.ofNullable(file.getParent()).orElse("/"), file.getName()));
        } catch (SecurityException e) {
            throw new FileReaderException(
                    String.format("File reading not permitted | Filepath: %s | Filename: %s", Optional.ofNullable(file.getParent()).orElse("/"), file.getName()));
        } catch (IOException e) {
            throw new FileReaderException("Error when reading the file");
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
    private File loadFile(String path, String fileName) {
        String filePath = Paths.get(path, fileName).toString();
        return new File(filePath);
    }

    /**
     * Constructs an {@code Employee} object from an array of values representing employee data from a CSV lineNumber.
     *
     * @param values     the array of values representing read data
     * @param lineNumber the lineNumber number from which the data was read
     * @return the constructed {@code Employee} object
     * @throws ParseExtractionException if any error occurs during parsing of the employee data
     */
    private Employee employeeFromLineValues(String[] values, int lineNumber) {
        try {
            Integer id = Integer.valueOf(values[Defaults.ID_INDEX]);
            String firstName = String.valueOf(values[Defaults.FIRST_NAME_INDEX]);
            String lastName = String.valueOf(values[Defaults.LAST_NAME_INDEX]);
            Integer salary = Integer.valueOf(values[Defaults.SALARY_INDEX]);
            Integer managerId = (values.length >= 5) ? Integer.valueOf(values[Defaults.MANAGER_ID_INDEX]) : null;

            return new Employee(id, firstName, lastName, salary, managerId);
        } catch (Exception e) {
            throw new ParseExtractionException(String.format("Error on line number %d -> %s", lineNumber, e.getMessage()));
        }
    }

    /**
     * Creates a header mapper array to map CSV header columns to their respective positions
     * described on {@code headerOrder}.
     *
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
     * Orders read data based on the header mapper array.
     *
     * @param headerMapper the header mapper array
     * @param values       the array of values representing data from a CSV line
     * @return the ordered data array
     */
    private String[] orderReadData(int[] headerMapper, String[] values) {
        String[] orderedData = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            orderedData[headerMapper[i]] = values[i];
        }
        return orderedData;
    }

    /**
     * Provides default values for the {@code EmployeeDataExtractorService}.
     */
    private static class Defaults {
        /**
         * Default value indicating whether the CSV file has a header line.
         */
        static final boolean HAS_HEADER = true;
        /**
         * Default minimum required values for each line on csv be considered valid
         */
        static final int MINIMUM_REQUIRED_VALUES_BY_CSV_LINE = 4;
        /**
         * Default index for the 'id' column in the CSV file.
         */
        static final int ID_INDEX = 0;
        /**
         * Default index for the 'firstname' column in the CSV file.
         */
        static final int FIRST_NAME_INDEX = 1;
        /**
         * Default index for the 'lastname' column in the CSV file.
         */
        static final int LAST_NAME_INDEX = 2;
        /**
         * Default index for the 'salary' column in the CSV file.
         */
        static final int SALARY_INDEX = 3;
        /**
         * Default index for the 'managerid' column in the CSV file.
         */
        static final int MANAGER_ID_INDEX = 4;
    }
}
