/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PcmDefectTypeTest {

    @Test
    void defaultsAreNull() {
        PcmDefectType dt = new PcmDefectType();
        assertNull(dt.getDefectTypeKey());
        assertNull(dt.getDefectName());
    }

    @Test
    void settersUpdateFields() {
        PcmDefectType dt = new PcmDefectType();
        dt.setDefectTypeKey(5L);
        dt.setDefectName("Scrap");
        assertEquals(5L, dt.getDefectTypeKey());
        assertEquals("Scrap", dt.getDefectName());
    }

    @Test
    void equalsAndHashCodeBasedOnDefectName() {
        PcmDefectType a = new PcmDefectType();
        a.setDefectName("X");
        PcmDefectType b = new PcmDefectType();
        b.setDefectName("X");
        PcmDefectType c = new PcmDefectType();
        c.setDefectName("Y");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "x");
    }

    @Test
    void compareToOrdersByDefectName() {
        PcmDefectType a = new PcmDefectType();
        a.setDefectName("AA");
        PcmDefectType b = new PcmDefectType();
        b.setDefectName("BB");
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        PcmDefectType a2 = new PcmDefectType();
        a2.setDefectName("AA");
        assertEquals(0, a.compareTo(a2));
    }

    @Test
    void toStringContainsDefectName() {
        PcmDefectType dt = new PcmDefectType();
        dt.setDefectName("Yield");
        assertTrue(dt.toString().contains("Yield"));
    }
}
