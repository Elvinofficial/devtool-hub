package com.devtoolshub.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameNormalizerTest {

    @Test
    void shouldTrimName() {
        String result = NameNormalizer.normalize("  Windows  ", 50);

        assertEquals("Windows", result);
    }

    @Test
    void shouldRejectNullName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> NameNormalizer.normalize(null, 50)
        );
    }

    @Test
    void shouldRejectBlankName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> NameNormalizer.normalize("   ", 50)
        );
    }

    @Test
    void shouldRejectTooLongName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> NameNormalizer.normalize("Windows", 5)
        );
    }

    @Test
    void shouldAcceptMaximumLength() {
        String result = NameNormalizer.normalize("Linux", 5);

        assertEquals("Linux", result);
    }
}