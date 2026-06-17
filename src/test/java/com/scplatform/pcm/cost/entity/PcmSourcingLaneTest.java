/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.entity;

import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PcmSourcingLaneTest {

    @Test
    void testNoArgsConstructorDefaults() {
        PcmSourcingLane sl = new PcmSourcingLane();
        assertNull(sl.getSourcingLaneKey());
        assertEquals(Boolean.TRUE, sl.getCurrentFlag());
        assertEquals(Boolean.FALSE, sl.getDeleteFlag());
        assertEquals(Boolean.FALSE, sl.isSystemDerived());
        assertEquals(Boolean.FALSE, sl.isEndDateRequired());
        assertNotNull(sl.getCostRecords());
        assertTrue(sl.getCostRecords().isEmpty());
        assertNotNull(sl.getPcmCostRecordsException());
        assertTrue(sl.getPcmCostRecordsException().isEmpty());
        assertNotNull(sl.getInsertDate());
    }

    @Test
    void testFiveArgConstructor() {
        Date d = new Date();
        PcmSourcingLane sl = new PcmSourcingLane(5L, "N", "ACTIVE", d, true);
        assertEquals(5L, sl.getSourcingLaneKey());
        assertEquals("N", sl.getSourcingLaneName());
        assertEquals("ACTIVE", sl.getStatus());
        assertSame(d, sl.getInsertDate());
        assertEquals(Boolean.TRUE, sl.getCurrentFlag());
    }

    @Test
    void testAddRemoveCostRecord() {
        PcmSourcingLane sl = new PcmSourcingLane();
        PcmCostRecord cr = new PcmCostRecord();
        assertTrue(sl.addCostRecord(cr));
        assertSame(sl, cr.getSourcingLane());
        assertTrue(sl.getCostRecords().contains(cr));
        assertTrue(sl.removeCostRecord(cr));
        assertNull(cr.getSourcingLane());
        assertFalse(sl.getCostRecords().contains(cr));
        assertFalse(sl.removeCostRecord(cr));
    }

    @Test
    void testAddRemoveCostRecordException() {
        PcmSourcingLane sl = new PcmSourcingLane();
        PcmCostRecordException crex = new PcmCostRecordException();
        assertTrue(sl.addCostRecord(crex));
        assertSame(sl, crex.getSourcingLane());
        assertTrue(sl.removeCostRecord(crex));
        assertNull(crex.getSourcingLane());
        assertFalse(sl.removeCostRecord(crex));
    }

    @Test
    void testGetCostTypesInLane() {
        PcmSourcingLane sl = new PcmSourcingLane();
        PcmCostType ct1 = new PcmCostType();
        ct1.setCostTypeKey("CT1");
        PcmCostType ct2 = new PcmCostType();
        ct2.setCostTypeKey("CT2");
        PcmCostRecord cr1 = new PcmCostRecord();
        cr1.setCostType(ct1);
        PcmCostRecord cr2 = new PcmCostRecord();
        cr2.setCostType(ct2);
        sl.addCostRecord(cr1);
        sl.addCostRecord(cr2);
        Set<PcmCostType> types = sl.getCostTypesInLane();
        assertEquals(2, types.size());
        assertTrue(types.contains(ct1));
        assertTrue(types.contains(ct2));
    }

    @Test
    void testGetLaneContainsCostType() {
        PcmSourcingLane sl = new PcmSourcingLane();
        PcmCostType ct = new PcmCostType();
        ct.setCostTypeKey("CT1");
        PcmCostRecord cr = new PcmCostRecord();
        cr.setCostType(ct);
        sl.addCostRecord(cr);
        assertTrue(sl.getLaneContainsCostType("CT1"));
        assertFalse(sl.getLaneContainsCostType("OTHER"));
    }

    @Test
    void testGetChildrenAndParent() {
        PcmSourcingLane sl = new PcmSourcingLane();
        assertSame(sl.getCostRecords(), sl.getChildren());
        assertNull(sl.getParent());
    }

    @Test
    void testGetTitleAndShortTitle() {
        PcmSourcingLane sl = new PcmSourcingLane();

        Item item = mock(Item.class);
        when(item.getItemNumber()).thenReturn("ITEM-1");
        sl.setItem(item);

        BusinessEntity sup = mock(BusinessEntity.class);
        when(sup.getBusinessEntityName()).thenReturn("SUP");
        sl.setSupplier(sup);

        Site from = mock(Site.class);
        when(from.getSiteDescription()).thenReturn("F");
        sl.setFromSite(from);

        Site to = mock(Site.class);
        when(to.getSiteDescription()).thenReturn("T");
        sl.setToSite(to);

        sl.setCurrencyCode("USD");

        String title = sl.getTitle();
        assertTrue(title.startsWith("ITEM-1-"));
        assertTrue(title.contains("SUP"));
        assertTrue(title.contains("F"));
        assertTrue(title.contains("T"));
        assertTrue(title.contains("USD"));

        Bom bom = mock(Bom.class);
        when(bom.getBomName()).thenReturn("B1");
        sl.setBom(bom);
        String withBom = sl.getTitle();
        assertTrue(withBom.contains("-B1-"));
        assertTrue(sl.getShortTitle().endsWith(" (NM)"));
    }

    @Test
    void testGettersSettersBasic() {
        PcmSourcingLane sl = new PcmSourcingLane();
        sl.setSourcingLaneKey(1L);
        sl.setSourcingLaneName("L");
        sl.setSourcingLaneExternalId("X");
        sl.setDescription("D");
        sl.setCurrencyCode("USD");
        sl.setProductState("ACTIVE");
        sl.setCostProviderBusinessEntity("BE");
        sl.setStatus("DRAFT");
        sl.setCurrentFlag(false);
        sl.setDeleteFlag(true);
        sl.setSystemDerived(true);
        sl.setEndDateRequired(true);
        sl.setCollaboration(true);
        sl.setDateOffset(99L);
        Set<PcmCostRecord> crs = new HashSet<>();
        sl.setCostRecords(crs);
        assertEquals(1L, sl.getSourcingLaneKey());
        assertEquals("L", sl.getSourcingLaneName());
        assertEquals("X", sl.getSourcingLaneExternalId());
        assertEquals("D", sl.getDescription());
        assertEquals("USD", sl.getCurrencyCode());
        assertEquals("ACTIVE", sl.getProductState());
        assertEquals("BE", sl.getCostProviderBusinessEntity());
        assertEquals("DRAFT", sl.getStatus());
        assertEquals(Boolean.FALSE, sl.getCurrentFlag());
        assertEquals(Boolean.TRUE, sl.getDeleteFlag());
        assertEquals(Boolean.TRUE, sl.isSystemDerived());
        assertEquals(Boolean.TRUE, sl.isEndDateRequired());
        assertEquals(Boolean.TRUE, sl.getCollaboration());
        assertEquals(99L, sl.getDateOffset());
        assertSame(crs, sl.getCostRecords());
    }
}
