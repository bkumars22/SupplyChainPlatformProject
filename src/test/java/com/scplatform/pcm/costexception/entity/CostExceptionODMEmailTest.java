/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.entity;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class CostExceptionODMEmailTest {

    @Test
    void noArgsConstructor_defaultsAreNull() {
        CostExceptionODMEmail e = new CostExceptionODMEmail();
        assertNull(e.getId());
        assertNull(e.getException());
        assertNull(e.getFileName());
        assertNull(e.getFileContent());
        assertNull(e.getUploadedBy());
        assertNull(e.getUploadedOn());
    }

    @Test
    void settersAndGetters_roundTrip() {
        CostExceptionODMEmail e = new CostExceptionODMEmail();
        CostException ex = new CostException();
        ex.setExceptionKey(6L);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        byte[] content = "email-data".getBytes();

        e.setId(30L);
        e.setException(ex);
        e.setFileName("email.pdf");
        e.setFileContent(content);
        e.setUploadedBy("user1");
        e.setUploadedOn(ts);

        assertEquals(30L, e.getId());
        assertSame(ex, e.getException());
        assertEquals("email.pdf", e.getFileName());
        assertArrayEquals(content, e.getFileContent());
        assertEquals("user1", e.getUploadedBy());
        assertSame(ts, e.getUploadedOn());
    }

    @Test
    void allArgsConstructor_roundTrip() {
        CostException ex = new CostException();
        Timestamp ts = new Timestamp(0L);
        byte[] content = new byte[]{1, 2, 3};
        CostExceptionODMEmail e = new CostExceptionODMEmail(9L, ex, "file.pdf", content, "uploader", ts);

        assertEquals(9L, e.getId());
        assertSame(ex, e.getException());
        assertEquals("file.pdf", e.getFileName());
        assertArrayEquals(content, e.getFileContent());
        assertEquals("uploader", e.getUploadedBy());
        assertSame(ts, e.getUploadedOn());
    }

    @Test
    void equalsAndHashCode_symmetric() {
        Timestamp ts = new Timestamp(1000L);
        byte[] c = new byte[]{5};
        CostExceptionODMEmail a = new CostExceptionODMEmail(1L, null, "f.pdf", c, "u", ts);
        CostExceptionODMEmail b = new CostExceptionODMEmail(1L, null, "f.pdf", c, "u", ts);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toString_excludesException() {
        CostExceptionODMEmail e = new CostExceptionODMEmail();
        e.setFileName("attachment.pdf");
        e.setUploadedBy("uploader");
        String s = e.toString();
        assertNotNull(s);
        assertTrue(s.contains("attachment.pdf"));
    }
}
