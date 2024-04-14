# Big Company Interview Exercise

## Overview

This project is an interview exercise that aims to assess candidates problem-solving abilities, code skills, clean code
concepts, documentation, understanding of algorithms and data structures, quality insurance with tests, and their
approach to software design and implementation.

## Challenge

BIG COMPANY is employing a lot of employees.
Company would like to analyze its organizational structure and identify potential improvements.
Board wants to make sure that every manager earns at least 20% more than the average salary of its direct subordinates,
but no more than 50% more than that average.
Company wants to avoid too long reporting lines, therefore we would like to identify all employees which have more than
4 managers between them and the CEO.
Each line represents an employee (CEO included). CEO has no manager specified. Number of rows can be up to 1000.

Write a simple program which will read the file and report:

- which managers earn less than they should, and by how much
- which managers earn more than they should, and by how much
- which employees have a reporting line which is too long, and by how much

## Approuch

For this interview exercise, the following solutions have been devised:

- Utilize three interfaces for the extraction process, creation of the Employee hierarchy, and the reporting process.
- Employ an N-tree structure (Employee Node) to manage the hierarchy of employees, balancing memory usage and
  performance.
- Implement CSV tests for various scenarios to ensure robust test coverage.
- Leverage Java 17 records for cleaner, maintainable, and type-safe code.
- Output results using <i>'System.out'</i> for simplicity.
- Implement input validation to ensure the consistency of the data file.
- All services are stateless and objects are immutable (records). Also, no collection or node are modify by any service, been so considered thread-safes.

## Getting Started

To run this application, ensure Java 17 is installed on your system.

### Compile

You can compile the code using the Maven wrapper:

```
.\mvnw install
```

### Running

To execute the application, provide a CSV file containing the employee list. 
Specify the file location using the <i>-Dfile</i> parameter when running the JAR:


```
java "-Dfile=src/test/resources/SampleData.csv" -jar .\target\BigCompanyAnalytics-1.0-SNAPSHOT.jar 
```

If no file was specified, the application will try to find a SampleData.csv file on the same directory.

You can also indicate whether your file has a header or not (by default, it assumes there is a header) using the <i>-Dhas_header</i> property:
```
java "-Dfile=src/test/resources/SampleData.csv" "-Dhas_header=false" -jar .\target\BigCompanyAnalytics-1.0-SNAPSHOT.jar 
```

## Sample Data

The employee data should be provided in a CSV file format with the following headers:

- Id
- firstName
- lastName
- salary
- managerId

Ensure that the data adheres to the following conditions:

- The file format must be CSV.
- There should be only one CEO (CEO is an employee without a managerId).
- The delimiter should be a comma (,).
- firstName and lastName fields should contain text, while the remaining fields should contain integer numbers.
- If the <i>has_header</i> property is not defined or is set to true:
  - The file should contain the required headers (case-insensitive).
  - The order of the data columns can be arbitrary as long as it follows the headers.
- If the <i>has_header</i> property is set to false:
  - The data should not be ordered.