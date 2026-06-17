/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.allocationAudit.entity;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.item.entity.Item;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AllocationAuditHistoryTest {

    @Test
    void defaultConstructor() {
        AllocationAuditHistory h = new FunctionalGroupAuditHistory();
        assertNull(h.getUserId());
        assertNull(h.getUserRole());
        assertNull(h.getActionPerformed());
        assertNull(h.getOperationCode());
        assertNull(h.getComment());
        assertNull(h.getDatePerformed());
        assertNull(h.getAuditKey());
    }

    @Test
    void constructorWithFunctionalGroup() {
        FunctionalGroup fg = new FunctionalGroup();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        AllocationAuditHistory h = new AllocationAuditHistory("user1", "ROLE_ADMIN",
                "CREATE", "OP001", fg, "a comment", ts);
        assertEquals("user1", h.getUserId());
        assertEquals("ROLE_ADMIN", h.getUserRole());
        assertEquals("CREATE", h.getActionPerformed());
        assertEquals("OP001", h.getOperationCode());
        assertSame(fg, h.getFunctionalGroup());
        assertEquals("a comment", h.getComment());
        assertEquals(ts, h.getDatePerformed());
    }

    @Test
    void constructorWithFunctionalGroupAndItem() {
        FunctionalGroup fg = new FunctionalGroup();
        Item item = new Item();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        AllocationAuditHistory h = new AllocationAuditHistory("user2", "ROLE_BUYER",
                "UPDATE", "OP002", fg, item, "item comment", ts);
        assertEquals("user2", h.getUserId());
        assertSame(item, h.getItem());
        assertEquals("item comment", h.getComment());
    }

    @Test
    void settersAndGetters() {
        AllocationAuditHistory h = new FunctionalGroupAuditHistory();

        h.setAuditKey(100L);
        h.setUserId("testUser");
        h.setUserRole("ROLE_OWNER");
        h.setActionPerformed("DELETE");
        h.setOperationCode("OP999");
        h.setComment("test comment");

        Timestamp ts = new Timestamp(1000000L);
        h.setDatePerformed(ts);

        Date start = new Date(1000L);
        Date end = new Date(2000L);
        h.setBucketStartDate(start);
        h.setBucketEndDate(end);

        assertEquals(100L, h.getAuditKey());
        assertEquals("testUser", h.getUserId());
        assertEquals("ROLE_OWNER", h.getUserRole());
        assertEquals("DELETE", h.getActionPerformed());
        assertEquals("OP999", h.getOperationCode());
        assertEquals("test comment", h.getComment());
        assertEquals(ts, h.getDatePerformed());
        assertEquals(start, h.getBucketStartDate());
        assertEquals(end, h.getBucketEndDate());
    }

    @Test
    void itemSetterGetter() {
        AllocationAuditHistory h = new FunctionalGroupAuditHistory();
        Item item = new Item();
        h.setItem(item);
        assertSame(item, h.getItem());
    }

    @Test
    void supplierSetterGetter() {
        AllocationAuditHistory h = new FunctionalGroupAuditHistory();
        BusinessEntity supplier = new BusinessEntity();
        h.setSupplier(supplier);
        assertSame(supplier, h.getSupplier());
    }

    @Test
    void sourceSetterGetter() {
        AllocationAuditHistory h = new FunctionalGroupAuditHistory();
        h.setSource("FG");
        assertEquals("FG", h.getSource());
    }
}
