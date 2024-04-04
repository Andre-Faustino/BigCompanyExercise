package com.big.company.analytics.util;

import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public final class AssertThrows {

    public static <T extends Throwable> void assertThrows(String message, Class<T> expectedType, Executable executable){
        T exception = assertThrowsExactly(expectedType, executable);
        assertEquals(message, exception.getMessage());
    }
}
