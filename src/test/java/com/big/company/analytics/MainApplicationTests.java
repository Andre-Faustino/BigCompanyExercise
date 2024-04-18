package com.big.company.analytics;

import com.big.company.analytics.exception.FileReaderException;

import static com.big.company.analytics.test.util.AssertThrows.assertThrows;

import com.big.company.analytics.exception.ParseExtractionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MainApplicationTests {

    private static final String TEST_FILEPATH = "src/test/resources/";

    @Test
    void shouldMainMethodWithNoHeaderFileSuccessWhenConfig() {
        String fileName = "ValidatedDataWithoutHeader.csv";
        System.setProperty("file", TEST_FILEPATH + fileName);
        System.setProperty("has_header", "false");

        assertDoesNotThrow(() -> MainApplication.main(null));
    }

    @Test
    void shouldMainMethodWithNoFileFails() {
        assertThrows("File not found | Filepath: / | Filename: SampleData.csv", FileReaderException.class,
                () -> MainApplication.main(null));
    }

    @Test
    void shouldMainMethodWithWrongFilePathFails() {
        String fileName = "SampleData.csv";
        System.setProperty("file", TEST_FILEPATH + "extra/invalid/path/" + fileName);

        assertThrows("File not found | Filepath: src\\test\\resources\\extra\\invalid\\path | Filename: SampleData.csv", FileReaderException.class,
                () -> MainApplication.main(null));
    }
    
    @Test
    void shouldMainMethodWithUnorderedAndNoHeaderFileFailsWithNoHeaderConfig() {
        String fileName = "InvertedColumnsDataWithoutHeader.csv";
        System.setProperty("file", TEST_FILEPATH + fileName);
        System.setProperty("has_header", "false");

        assertThrows("Error on line number 0 -> For input string: \"Doe\"", ParseExtractionException.class,
                () -> MainApplication.main(null));
    }

    @AfterEach
    void clean() {
        System.clearProperty("file");
        System.clearProperty("has_header");
    }
}