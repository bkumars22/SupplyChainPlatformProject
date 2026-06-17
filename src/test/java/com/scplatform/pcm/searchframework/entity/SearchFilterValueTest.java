/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;

import org.junit.jupiter.api.Test;

class SearchFilterValueTest {

    @Test
    void noArgConstructorLeavesFieldsNull() {
        SearchFilterValue v = new SearchFilterValue();
        assertNull(v.getFieldName());
        assertNull(v.getFieldType());
        assertNull(v.getFieldValue());
    }

    @Test
    void twoArgConstructorAssignsAndDerivesType() {
        SearchFilterValue v = new SearchFilterValue("name", "abc");
        assertEquals("name", v.getFieldName());
        assertEquals("abc", v.getFieldValue());
        assertEquals(String.class.getName(), v.getFieldType());
    }

    @Test
    void setFieldValueDerivesType() {
        SearchFilterValue v = new SearchFilterValue();
        v.setFieldValue(Integer.valueOf(7));
        assertEquals(Integer.class.getName(), v.getFieldType());
        assertEquals(7, v.getFieldValue());
    }

    @Test
    void setFieldValueNullLeavesTypeUnchanged() {
        SearchFilterValue v = new SearchFilterValue();
        v.setFieldValue(null);
        assertNull(v.getFieldType());
        assertNull(v.getFieldValue());
    }

    @Test
    void getFieldValueAsStringForString() {
        SearchFilterValue v = new SearchFilterValue("n", "abc");
        assertEquals("abc", v.getFieldValueAsString());
    }

    @Test
    void getFieldValueAsStringForNullValue() {
        SearchFilterValue v = new SearchFilterValue();
        v.setFieldName("n");
        assertNull(v.getFieldValueAsString());
    }

    @Test
    void getFieldValueAsStringForDateUsesDateTimeFormat() {
        SearchFilterValue v = new SearchFilterValue("d", new Date(0L));
        // Should not throw and should return a non-null formatted string
        org.junit.jupiter.api.Assertions.assertNotNull(v.getFieldValueAsString());
    }

    @Test
    void setFieldValueAsStringDefaultsTypeToStringWhenNull() {
        SearchFilterValue v = new SearchFilterValue();
        v.setFieldValueAsString("hello");
        assertEquals("hello", v.getFieldValue());
        assertEquals(String.class.getName(), v.getFieldType());
    }

    @Test
    void setFieldNameAndType() {
        SearchFilterValue v = new SearchFilterValue();
        v.setFieldName("n");
        v.setFieldType("t");
        assertEquals("n", v.getFieldName());
        assertEquals("t", v.getFieldType());
    }

    @Test
    void equalsAndHashCodeOnFields() {
        SearchFilterValue a = new SearchFilterValue("n", "v");
        SearchFilterValue b = new SearchFilterValue("n", "v");
        SearchFilterValue c = new SearchFilterValue("n", "x");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
        assertEquals(a, a);
    }
}
