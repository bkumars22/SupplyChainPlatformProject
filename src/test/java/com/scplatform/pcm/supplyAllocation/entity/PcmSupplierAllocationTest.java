/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.supplyAllocation.entity;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PcmSupplierAllocationTest {

    @Test
    void defaultConstructor_setsDefaults() {
        PcmSupplierAllocation s = new PcmSupplierAllocation();
        assertEquals("MCM", s.getDataSource());
        assertNotNull(s.getInsertDate());
        assertEquals(Boolean.FALSE, s.getDeleteFlag());
        assertTrue(s.getCurrentFlag());
    }

    @Test
    void shortConstructor_setsCoreFields() {
        Date d = new Date();
        PcmSupplierAllocation s = new PcmSupplierAllocation(
                1L, null, null, null, null, null, null, null,
                BigDecimal.TEN, "desc", "ACTIVE", d, true);
        assertEquals(1L, s.getSupplierAllocationKey());
        assertEquals(BigDecimal.TEN, s.getAllocation());
        assertEquals("desc", s.getDescription());
        assertEquals("ACTIVE", s.getStatus());
        assertEquals(d, s.getInsertDate());
        assertTrue(s.getCurrentFlag());
    }

    @Test
    void fullConstructor_setsAllFields() {
        Date d = new Date();
        PcmSupplierAllocation s = new PcmSupplierAllocation(
                2L, null, null, null, null, null, null, null,
                BigDecimal.ONE, "desc", "OPEN", d, "user", d, d, d, d,
                Boolean.TRUE, false, 99L, d, "ETL");
        assertEquals(2L, s.getSupplierAllocationKey());
        assertEquals("ETL", s.getDataSource());
        assertEquals(99L, s.getAuditRev());
        assertEquals(Boolean.TRUE, s.getDeleteFlag());
        assertFalse(s.getCurrentFlag());
        assertEquals("user", s.getStatusLastChangeBy());
    }

    @Test
    void setters_work() {
        PcmSupplierAllocation s = new PcmSupplierAllocation();
        s.setSupplierAllocationKey(7L);
        s.setAllocation(new BigDecimal("3.14"));
        s.setDescription("d");
        s.setSupplyAllocationComment("c");
        s.setDataSource("X");
        s.setDeleteFlag(Boolean.TRUE);
        s.setCurrentFlag(false);
        s.setContextName("CTX");
        s.setContextType("T");
        s.setEffectiveFromDt(new Date());
        s.setEffectiveToDt(new Date());
        s.setUpdateDate(new Date());
        s.setCustomerItem(new Item());
        s.setSupplierItem(new Item());
        s.setCustomerSite(new Site());
        s.setSupplierSite(new Site());
        s.setDestinationSite(new Site());
        s.setSupplierBusinessEntity(new BusinessEntity());
        s.setCustomerItemGroupItem(new Item());

        assertEquals(7L, s.getSupplierAllocationKey());
        assertEquals(new BigDecimal("3.14"), s.getAllocation());
        assertEquals("d", s.getDescription());
        assertEquals("c", s.getSupplyAllocationComment());
        assertEquals("X", s.getDataSource());
        assertEquals(Boolean.TRUE, s.getDeleteFlag());
        assertFalse(s.getCurrentFlag());
        assertEquals("CTX", s.getContextName());
        assertEquals("T", s.getContextType());
        assertNotNull(s.getEffectiveFromDt());
        assertNotNull(s.getEffectiveToDt());
        assertNotNull(s.getUpdateDate());
        assertNotNull(s.getCustomerItem());
        assertNotNull(s.getSupplierItem());
        assertNotNull(s.getCustomerSite());
        assertNotNull(s.getSupplierSite());
        assertNotNull(s.getDestinationSite());
        assertNotNull(s.getSupplierBusinessEntity());
        assertNotNull(s.getCustomerItemGroupItem());
    }

    @Test
    void nullable_descriptionAllowed() {
        PcmSupplierAllocation s = new PcmSupplierAllocation();
        s.setDescription(null);
        assertNull(s.getDescription());
    }
}
