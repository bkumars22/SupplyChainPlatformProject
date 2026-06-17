/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.writter.dto;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AbstractExtractWriterTest {

    private static class StubWriter extends AbstractExtractWriter {
        @Override public boolean onRow(com.scplatform.pcm.searchframework.dto.GenericResultRow row) { return true; }
        @Override public void start(String[] columnNames) {}
        @Override public void end(String[] columnNames) {}
    }

    @Test
    void initialCounters_areZero() {
        StubWriter w = new StubWriter();
        assertEquals(0, w.getCharsWritten());
        assertEquals(0, w.getRowsWritten());
    }

    @Test
    void setStream_initializesUnderlyingWriter() throws Exception {
        StubWriter w = new StubWriter();
        w.setStream(new ByteArrayOutputStream(), "UTF-8");
        // close should now succeed
        assertTrue(w.close());
    }

    @Test
    void setStream_invalidEncoding_throws() {
        StubWriter w = new StubWriter();
        assertThrows(java.io.UnsupportedEncodingException.class,
                () -> w.setStream(new ByteArrayOutputStream(), "no-such-encoding"));
    }

    @Test
    void close_returnsFalseWhenUnderlyingThrows() throws Exception {
        StubWriter w = new StubWriter();
        OutputStream throwing = new OutputStream() {
            @Override public void write(int b) throws IOException { throw new IOException("boom"); }
            @Override public void close() throws IOException { throw new IOException("boom"); }
        };
        w.setStream(throwing, "UTF-8");
        assertFalse(w.close());
    }

    @Test
    void initialize_isNoOp() {
        StubWriter w = new StubWriter();
        assertDoesNotThrow(() -> w.inititalize(Collections.emptyMap()));
    }
}
