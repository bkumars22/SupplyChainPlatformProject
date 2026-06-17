/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.writter.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExtractWriterTest {

    @Test
    void isInterface() {
        assertTrue(ExtractWriter.class.isInterface());
    }

    @Test
    void abstractExtractWriterImplementsIt() {
        assertTrue(ExtractWriter.class.isAssignableFrom(AbstractExtractWriter.class));
    }

    @Test
    void charDelimitedTextExtractWriterImplementsIt() {
        assertTrue(ExtractWriter.class.isAssignableFrom(CharDelimitedTextExtractWriter.class));
    }
}
