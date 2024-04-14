package com.big.company.analytics.services;

import java.io.File;

import com.big.company.analytics.exception.FileExtractionException;
import com.big.company.analytics.exception.ParseExtractionException;

import java.util.List;

/**
 * An interface for extracting elements from files.
 *
 * @param <T> the type of elements to be extracted
 */
public interface FileExtractor<T> {

    /**
     * Extracts elements from a file specified by path and filename.
     *
     * @param path     the path to the directory containing the file
     * @param fileName the name of the file
     * @return a list of elements of type <b>T</b> extracted from the file
     * @throws FileExtractionException  if the file is not found or cannot be loaded
     * @throws ParseExtractionException if any error occurs during parsing of the file content
     * @throws NullPointerException     if any params is null
     */
    List<T> extractFile(String path, String fileName);

    /**
     * Extracts elements from a specified file object.
     *
     * @param file the file object from which to extract <b>T</b> objects
     * @return a list of elements of type <b>T</b> extracted from the file
     * @throws FileExtractionException  if the file is not found or cannot be loaded
     * @throws ParseExtractionException if any error occurs during parsing of the file content
     * @throws NullPointerException     if any params is null
     */
    List<T> extractFile(File file);
}
