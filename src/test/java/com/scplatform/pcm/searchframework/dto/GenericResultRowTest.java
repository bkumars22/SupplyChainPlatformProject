/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class GenericResultRowTest {

    @Test
    void noArgConstructorEmptyValuesNullKey() {
        GenericResultRow r = new GenericResultRow();
        assertNotNull(r.getValues());
        assertTrue(r.getValues().isEmpty());
        assertNull(r.getKey());
    }

    @Test
    void singleObjectConstructorAddsOneValue() {
        GenericResultRow r = new GenericResultRow("hello");
        assertEquals(1, r.getValues().size());
        assertEquals("hello", r.getObject(0));
    }

    @Test
    void arrayConstructorAddsAllValues() {
        GenericResultRow r = new GenericResultRow(new Object[]{"a", 1, 2.5});
        List<Object> values = r.getValues();
        assertEquals(3, values.size());
        assertEquals("a", values.get(0));
        assertEquals(1, values.get(1));
        assertEquals(2.5, values.get(2));
    }

    @Test
    void setKeyAndGetKey() {
        GenericResultRow r = new GenericResultRow();
        r.setKey("K1");
        assertSame("K1", r.getKey());
    }

    @Test
    void addValueAppends() {
        GenericResultRow r = new GenericResultRow();
        r.addValue("a");
        r.addValue(42);
        assertEquals(2, r.getValues().size());
    }

    @Test
    void getValueReturnsToString() {
        GenericResultRow r = new GenericResultRow();
        r.addValue(123);
        assertEquals("123", r.getValue(0));
    }

    @Test
    void getValueReturnsEmptyStringForNullValue() {
        GenericResultRow r = new GenericResultRow();
        r.addValue(null);
        assertEquals("", r.getValue(0));
    }

    @Test
    void getValueWithLengthTruncatesAndAppendsEllipsis() {
        GenericResultRow r = new GenericResultRow();
        r.addValue("0123456789");
        assertEquals("01234...", r.getValue(0, 5));
    }

    @Test
    void getValueWithLengthDoesNotTruncateShorterValues() {
        GenericResultRow r = new GenericResultRow();
        r.addValue("ab");
        assertEquals("ab", r.getValue(0, 5));
    }

    @Test
    void getObjectOutOfBoundsReturnsErrorString() {
        GenericResultRow r = new GenericResultRow();
        Object value = r.getObject(5);
        assertEquals("No value at 5", value);
    }
}
