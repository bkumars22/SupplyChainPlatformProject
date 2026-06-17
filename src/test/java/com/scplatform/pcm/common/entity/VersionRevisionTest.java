/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.common.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

class VersionRevisionTest {

    @Test
    void noArgConstructorLeavesFieldsNull() {
        VersionRevision v = new VersionRevision();
        assertNull(v.getVersion());
        assertNull(v.getRevision());
        assertNull(v.getVersionDate());
        assertNull(v.getRevisionDate());
    }

    @Test
    void twoArgConstructorAssignsRevisionThenVersion() {
        VersionRevision v = new VersionRevision("R1", "V1");
        assertEquals("R1", v.getRevision());
        assertEquals("V1", v.getVersion());
    }

    @Test
    void settersAndGetters() {
        VersionRevision v = new VersionRevision();
        Date a = new Date(1000L);
        Date b = new Date(2000L);
        v.setVersion("V");
        v.setRevision("R");
        v.setVersionDate(a);
        v.setRevisionDate(b);
        assertEquals("V", v.getVersion());
        assertEquals("R", v.getRevision());
        assertEquals(a, v.getVersionDate());
        assertEquals(b, v.getRevisionDate());
    }

    @Test
    void compareToOrdersByVersionThenRevision() {
        VersionRevision a = new VersionRevision("R1", "V1");
        VersionRevision b = new VersionRevision("R1", "V2");
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);

        VersionRevision c = new VersionRevision("R1", "V1");
        VersionRevision d = new VersionRevision("R2", "V1");
        assertTrue(c.compareTo(d) < 0);

        VersionRevision e = new VersionRevision("R1", "V1");
        VersionRevision f = new VersionRevision("R1", "V1");
        assertEquals(0, e.compareTo(f));
    }

    @Test
    void compareToWithNullsReturnsZeroAtThatLevel() {
        VersionRevision a = new VersionRevision();
        VersionRevision b = new VersionRevision();
        assertEquals(0, a.compareTo(b));
    }
}
