/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.common.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;

class MultiPurposeUsesTest {

    @Test
    void defaultsAreNull() {
        MultiPurposeUses m = new MultiPurposeUses();
        assertNull(m.getId());
        assertNull(m.getObjectType());
        assertNull(m.getStringParam1());
        assertNull(m.getStringParam2());
        assertNull(m.getStringParam3());
        assertNull(m.getLongParam1());
        assertNull(m.getLongParam2());
        assertNull(m.getLongParam3());
        assertNull(m.getDateParam1());
        assertNull(m.getDateParam2());
        assertNull(m.getDateParam3());
        assertNull(m.getClobData());
    }

    @Test
    void allSettersAndGettersWork() {
        MultiPurposeUses m = new MultiPurposeUses();
        Timestamp t1 = new Timestamp(100);
        Timestamp t2 = new Timestamp(200);
        Timestamp t3 = new Timestamp(300);
        m.setId(1L);
        m.setObjectType("GRID_VIEW");
        m.setStringParam1("s1");
        m.setStringParam2("s2");
        m.setStringParam3("s3");
        m.setLongParam1(11L);
        m.setLongParam2(22L);
        m.setLongParam3(33L);
        m.setDateParam1(t1);
        m.setDateParam2(t2);
        m.setDateParam3(t3);
        m.setClobData("clob");

        assertEquals(1L, m.getId());
        assertEquals("GRID_VIEW", m.getObjectType());
        assertEquals("s1", m.getStringParam1());
        assertEquals("s2", m.getStringParam2());
        assertEquals("s3", m.getStringParam3());
        assertEquals(11L, m.getLongParam1());
        assertEquals(22L, m.getLongParam2());
        assertEquals(33L, m.getLongParam3());
        assertEquals(t1, m.getDateParam1());
        assertEquals(t2, m.getDateParam2());
        assertEquals(t3, m.getDateParam3());
        assertEquals("clob", m.getClobData());
    }

    @Test
    void equalsAndHashCodeBasedOnAllFieldsExcludingId() {
        MultiPurposeUses a = newSample();
        MultiPurposeUses b = newSample();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setStringParam1("different");
        assertNotEquals(a, b);
    }

    @Test
    void equalsRejectsNullAndOtherType() {
        MultiPurposeUses a = new MultiPurposeUses();
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
    }

    @Test
    void equalsReflexive() {
        MultiPurposeUses a = new MultiPurposeUses();
        assertEquals(a, a);
    }

    private MultiPurposeUses newSample() {
        MultiPurposeUses m = new MultiPurposeUses();
        m.setObjectType("GRID_VIEW");
        m.setStringParam1("s1");
        m.setStringParam2("s2");
        m.setLongParam1(1L);
        return m;
    }
}
