# Big Company Interview Exercise

## Overview
This project is an interview exercise that aims to assess candidates problem-solving abilities, code skills, clean code concepts, documentation, understanding of algorithms and data structures, quality insurance with tests, and their approach to software design and implementation.

## Challenge

BIG COMPANY is employing a lot of employees. 
Company would like to analyze its organizational structure and identify potential improvements. 
Board wants to make sure that every manager earns at least 20% more than the average salary of its direct subordinates, but no more than 50% more than that average. 
Company wants to avoid too long reporting lines, therefore we would like to identify all employees which have more than 4 managers between them and the CEO.
Each line represents an employee (CEO included). CEO has no manager specified. Number of rows can be up to 1000.

Write a simple program which will read the file and report:
- which managers earn less than they should, and by how much
- which managers earn more than they should, and by how much
- which employees have a reporting line which is too long, and by how much

## Approuch

For this interview, I found the following solutions:
 - Create two interfaces for the extraction process and the report process
 - Allow some customization but provide standard values for the interfaces methods
 - Using an N-tree structure (Employee Node) to handle the hierarchy of employees. It's a good trade-off from spending extra memory to not lose performance on scale
 - Create csv tests for several test cases allowing a more clean test code

## Getting Started

For running this application, you should only have java 11 installed.

To compile the project:
```
.\mvnw install</code>
```
To run the application you should provide a csv file with the employee list.
You can specify the file location on <i>-Dfile</i> parameter when run the jar:

```
java "-Dfile=src/test/resources/SampleData.csv" -jar .\target\BigCompanyAnalytics-1.0-SNAPSHOT.jar 
```

If no file was specified, the application will try to find a SampleData.csv file on the same directory