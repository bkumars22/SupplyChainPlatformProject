/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PcmCostRecordAssignmentTest {

    @Test
    void testNoArgsConstructorAndDefaults() {
        PcmCostRecordAssignment a = new PcmCostRecordAssignment();
        assertNull(a.getCostRecord());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        PcmCostRecord cr = mock(PcmCostRecord.class);
        PcmCostRecordAssignment a = new PcmCostRecordAssignment(cr);
        assertSame(cr, a.getCostRecord());
    }

    @Test
    void testSetCostRecord() {
        PcmCostRecordAssignment a = new PcmCostRecordAssignment();
        PcmCostRecord cr = mock(PcmCostRecord.class);
        a.setCostRecord(cr);
        assertSame(cr, a.getCostRecord());
    }

    @Test
    void testDiscriminatorAnnotation() {
        DiscriminatorValue dv = PcmCostRecordAssignment.class.getAnnotation(DiscriminatorValue.class);
        assertNotNull(dv);
        assertEquals("CR", dv.value());
    }

    @Test
    void testIsJpaEntity() {
        assertNotNull(PcmCostRecordAssignment.class.getAnnotation(Entity.class));
    }

    @Test
    void testToStringExcludesCostRecord() {
        PcmCostRecordAssignment a = new PcmCostRecordAssignment();
        a.setCostRecord(mock(PcmCostRecord.class));
        String s = a.toString();
        assertFalse(s.contains("costRecord="), "toString should exclude costRecord per @ToString(exclude=...)");
    }
}
