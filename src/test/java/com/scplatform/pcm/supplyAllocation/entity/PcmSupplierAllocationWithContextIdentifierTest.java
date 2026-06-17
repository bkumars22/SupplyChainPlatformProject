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

class PcmSupplierAllocationWithContextIdentifierTest {

    @Test
    void defaultConstructor_allFieldsNull() {
        PcmSupplierAllocationWithContextIdentifier s = new PcmSupplierAllocationWithContextIdentifier();
        assertNull(s.getSupplierAllocationKey());
        assertNull(s.getAllocation());
        assertNull(s.getDescription());
        assertNull(s.getContextIdentifier());
        assertFalse(s.isCurrentFlag());
    }

    @Test
    void destinationSite_alwaysReturnsNull() {
        PcmSupplierAllocationWithContextIdentifier s = new PcmSupplierAllocationWithContextIdentifier();
        assertNull(s.getDestinationSite());
    }

    @Test
    void allSettersAndGetters_work() {
        PcmSupplierAllocationWithContextIdentifier s = new PcmSupplierAllocationWithContextIdentifier();
        Date d = new Date();
        s.setSupplierAllocationKey(10L);
        s.setCustomerItemGroupItem(new Item());
        s.setCustomerItem(new Item());
        s.setCustomerSite(new Site());
        s.setSupplierItem(new Item());
        s.setSupplierSite(new Site());
        s.setSupplierBusinessEntity(new BusinessEntity());
        s.setAllocation(new BigDecimal("5"));
        s.setDescription("d");
        s.setSupplyAllocationComment("c");
        s.setDataSource("MCM");
        s.setInsertDate(d);
        s.setUpdateDate(d);
        s.setDeleteFlag(Boolean.FALSE);
        s.setCurrentFlag(true);
        s.setEffectiveFromDt(d);
        s.setEffectiveToDt(d);
        s.setStatus("OK");
        s.setContextName("ctx");
        s.setContextType("T");
        s.setContextIdentifier("CID-1");

        assertEquals(10L, s.getSupplierAllocationKey());
        assertNotNull(s.getCustomerItemGroupItem());
        assertNotNull(s.getCustomerItem());
        assertNotNull(s.getCustomerSite());
        assertNotNull(s.getSupplierItem());
        assertNotNull(s.getSupplierSite());
        assertNotNull(s.getSupplierBusinessEntity());
        assertEquals(new BigDecimal("5"), s.getAllocation());
        assertEquals("d", s.getDescription());
        assertEquals("c", s.getSupplyAllocationComment());
        assertEquals("MCM", s.getDataSource());
        assertEquals(d, s.getInsertDate());
        assertEquals(d, s.getUpdateDate());
        assertEquals(Boolean.FALSE, s.getDeleteFlag());
        assertTrue(s.isCurrentFlag());
        assertEquals(d, s.getEffectiveFromDt());
        assertEquals(d, s.getEffectiveToDt());
        assertEquals("OK", s.getStatus());
        assertEquals("ctx", s.getContextName());
        assertEquals("T", s.getContextType());
        assertEquals("CID-1", s.getContextIdentifier());
    }

    @Test
    void toString_includesContextIdAndKey() {
        PcmSupplierAllocationWithContextIdentifier s = new PcmSupplierAllocationWithContextIdentifier();
        s.setSupplierAllocationKey(99L);
        s.setContextIdentifier("X");
        String str = s.toString();
        assertTrue(str.contains("X"));
        assertTrue(str.contains("99"));
    }
}
