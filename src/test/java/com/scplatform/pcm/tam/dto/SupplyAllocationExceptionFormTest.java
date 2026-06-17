/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.dto;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class SupplyAllocationExceptionFormTest {

    @Test
    void defaultConstructor_createsInstance() {
        SupplyAllocationExceptionForm form = new SupplyAllocationExceptionForm();
        assertNotNull(form);
    }

    @Test
    void settersAndGetters_workCorrectly() {
        SupplyAllocationExceptionForm form = new SupplyAllocationExceptionForm();
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        form.setFgName("FG-001");
        form.setStartDate("2025-01-01");
        form.setEndDate("2025-12-31");
        form.setAllocation(0.75);
        form.setUpdateDate(ts);
        form.setUpdateBY("admin");
        form.setCheackRows(true);
        form.setMessagePopup("alert message");

        assertEquals("FG-001", form.getFgName());
        assertEquals("2025-01-01", form.getStartDate());
        assertEquals("2025-12-31", form.getEndDate());
        assertEquals(0.75, form.getAllocation());
        assertEquals(ts, form.getUpdateDate());
        assertEquals("admin", form.getUpdateBY());
        assertTrue(form.isCheackRows());
        assertEquals("alert message", form.getMessagePopup());
    }

    @Test
    void equalsAndHashCode_twoEqualInstances() {
        SupplyAllocationExceptionForm a = new SupplyAllocationExceptionForm();
        a.setFgName("FG-A");
        SupplyAllocationExceptionForm b = new SupplyAllocationExceptionForm();
        b.setFgName("FG-A");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notEquals_whenDifferentFgName() {
        SupplyAllocationExceptionForm a = new SupplyAllocationExceptionForm();
        a.setFgName("FG-A");
        SupplyAllocationExceptionForm b = new SupplyAllocationExceptionForm();
        b.setFgName("FG-B");
        assertNotEquals(a, b);
    }
}
