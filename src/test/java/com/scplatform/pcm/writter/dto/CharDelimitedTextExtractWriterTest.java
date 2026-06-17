/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.writter.dto;

import com.scplatform.pcm.searchframework.dto.GenericResultRow;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class CharDelimitedTextExtractWriterTest {

    private CharDelimitedTextExtractWriter newWriter(String col, String row, ByteArrayOutputStream baos) throws Exception {
        CharDelimitedTextExtractWriter w = new CharDelimitedTextExtractWriter(col, row);
        w.setStream(baos, "UTF-8");
        return w;
    }

    @Test
    void defaultConstructor_usesTabAndNewline() {
        CharDelimitedTextExtractWriter w = new CharDelimitedTextExtractWriter();
        assertEquals("\t", w.getColDelimiter());
        assertEquals("\n", w.getRowDelimiter());
    }

    @Test
    void customDelimiters() {
        CharDelimitedTextExtractWriter w = new CharDelimitedTextExtractWriter(",", ";");
        assertEquals(",", w.getColDelimiter());
        assertEquals(";", w.getRowDelimiter());
    }

    @Test
    void start_writesColumnHeaders() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CharDelimitedTextExtractWriter w = newWriter(",", "\n", baos);
        w.start(new String[] {"id", "name"});
        w.close();
        assertEquals("id,name,\n", baos.toString("UTF-8"));
    }

    @Test
    void onRow_writesAllValueTypesAndEscapesHtml() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CharDelimitedTextExtractWriter w = newWriter("|", "\n", baos);

        GenericResultRow row = new GenericResultRow();
        row.getValues().add("a&b");
        row.getValues().add(Integer.valueOf(42));
        row.getValues().add(new BigDecimal("1.5"));
        row.getValues().add(new Date(0));
        row.getValues().add(null);

        assertTrue(w.onRow(row));
        w.close();

        String written = baos.toString("UTF-8");
        assertTrue(written.contains("a&amp;b"));
        assertTrue(written.contains("42"));
        assertTrue(written.contains("1.5"));
        assertTrue(written.endsWith("\n"));
        assertEquals(1, w.getRowsWritten());
        assertTrue(w.getCharsWritten() > 0);
    }

    @Test
    void end_isNoOp() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CharDelimitedTextExtractWriter w = newWriter(",", "\n", baos);
        assertDoesNotThrow(() -> w.end(new String[] {"x"}));
        w.close();
    }

    @Test
    void onRow_doesNotThrowWhenWriterClosed() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CharDelimitedTextExtractWriter w = newWriter(",", "\n", baos);
        w.close();
        GenericResultRow row = new GenericResultRow();
        row.getValues().add("x");
        // writer logs the IOException internally; should still return true
        assertTrue(w.onRow(row));
    }
}
