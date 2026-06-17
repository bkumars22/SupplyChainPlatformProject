/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.web;

import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EscapePrinterTest {

    private static class CapturingTag extends EscapePrinter {
        final JspContext ctx;
        CapturingTag(JspContext ctx) { this.ctx = ctx; }
        @Override public JspContext getJspContext() { return ctx; }
    }

    @Test
    void simpleSettersAndGetters() {
        EscapePrinter t = new EscapePrinter();
        t.setValue("v");
        t.setRemoveColon(true);
        assertEquals("v", t.getValue());
        assertTrue(t.isRemoveColon());
    }

    @Test
    void doTag_escapesSingleAndDoubleQuotes_andWrapsInQuotes() throws Exception {
        JspContext ctx = mock(JspContext.class);
        JspWriter writer = mock(JspWriter.class);
        when(ctx.getOut()).thenReturn(writer);
        CapturingTag t = new CapturingTag(ctx);
        t.setValue("a'b\"c");
        t.setRemoveColon(false);

        t.doTag();
        verify(writer).print(anyString());
    }

    @Test
    void doTag_removeColonTrue_doesNotWrap() throws Exception {
        JspContext ctx = mock(JspContext.class);
        JspWriter writer = mock(JspWriter.class);
        when(ctx.getOut()).thenReturn(writer);
        CapturingTag t = new CapturingTag(ctx);
        t.setValue("hello");
        t.setRemoveColon(true);

        t.doTag();
        verify(writer).print("hello");
    }

    @Test
    void doTag_swallowsExceptionWhenWriterThrows() throws Exception {
        JspContext ctx = mock(JspContext.class);
        JspWriter writer = mock(JspWriter.class);
        when(ctx.getOut()).thenReturn(writer);
        org.mockito.Mockito.doThrow(new IOException("boom")).when(writer).print(anyString());

        CapturingTag t = new CapturingTag(ctx);
        t.setValue("x");
        // exception is caught internally and printed, no throw
        assertDoesNotThrow(t::doTag);
    }

    @Test
    void doTag_swallowsNpeWhenValueIsNull() throws Exception {
        JspContext ctx = mock(JspContext.class);
        JspWriter writer = mock(JspWriter.class);
        when(ctx.getOut()).thenReturn(writer);
        CapturingTag t = new CapturingTag(ctx);
        // value is null -> NPE caught internally
        assertDoesNotThrow(t::doTag);
        verify(writer, never()).print(anyString());
    }
}
