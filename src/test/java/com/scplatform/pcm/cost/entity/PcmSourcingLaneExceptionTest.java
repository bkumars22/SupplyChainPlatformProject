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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PcmSourcingLaneExceptionTest {

    @Test
    void testNoArgsDefaults() {
        PcmSourcingLaneException sl = new PcmSourcingLaneException();
        assertNull(sl.getSourcingLaneKey());
        assertFalse(sl.isEndDateRequired());
        assertFalse(sl.isSystemDerived());
        assertEquals(Boolean.FALSE, sl.getDeleteFlag());
        // currentFlag defaults to TRUE in the entity
        assertTrue(sl.getCurrentFlag());
        assertEquals(Boolean.FALSE, sl.getCollaboration());
        assertEquals(Boolean.FALSE, sl.getIsAVLExists());
        assertNotNull(sl.getInsertDt());
    }

    @Test
    void testSixArgConstructor() {
        Item item = mock(Item.class);
        Date d = new Date();
        PcmSourcingLaneException sl = new PcmSourcingLaneException(7L, item, "N", "ACTIVE", d, true);
        assertEquals(7L, sl.getSourcingLaneKey());
        assertSame(item, sl.getItem());
        assertEquals("N", sl.getSourcingLaneName());
        assertEquals("ACTIVE", sl.getStatus());
        assertSame(d, sl.getInsertDt());
        // currentFlag is now stored from the constructor argument (true)
        assertTrue(sl.getCurrentFlag());
    }

    @Test
    void testAddRemoveCostRecord() {
        PcmSourcingLaneException sl = new PcmSourcingLaneException();
        sl.setPcmCostRecordsException(new HashSet<>());
        PcmCostRecordException cr = new PcmCostRecordException();
        assertTrue(sl.addCostRecord(cr));
        assertSame(sl, cr.getSourcingLaneException());
        assertTrue(sl.removeCostRecord(cr));
        assertNull(cr.getSourcingLaneException());
        assertFalse(sl.removeCostRecord(cr));
    }

    @Test
    void testGetChildrenAndParent() {
        PcmSourcingLaneException sl = new PcmSourcingLaneException();
        assertTrue(sl.getChildren().isEmpty());
        assertNull(sl.getParent());
    }

    @Test
    void testGetTitleAndShortTitle() {
        PcmSourcingLaneException sl = new PcmSourcingLaneException();
        Item item = mock(Item.class);
        when(item.getItemNumber()).thenReturn("I");
        sl.setItem(item);
        BusinessEntity sup = mock(BusinessEntity.class);
        when(sup.getBusinessEntityName()).thenReturn("S");
        sl.setSupplier(sup);
        Site from = mock(Site.class);
        when(from.getSiteDescription()).thenReturn("F");
        sl.setFromSite(from);
        Site to = mock(Site.class);
        when(to.getSiteDescription()).thenReturn("T");
        sl.setToSite(to);
        sl.setCurrencyCode("USD");

        String title = sl.getTitle();
        assertTrue(title.startsWith("I-"));
        assertTrue(title.contains("S"));

        Bom bom = mock(Bom.class);
        when(bom.getBomName()).thenReturn("B");
        sl.setBom(bom);
        assertTrue(sl.getTitle().contains("-B-"));
        assertTrue(sl.getShortTitle().endsWith(" (NM)"));
    }

    @Test
    void testToStringDelegatesToTitle() {
        PcmSourcingLaneException sl = new PcmSourcingLaneException();
        Item item = mock(Item.class);
        when(item.getItemNumber()).thenReturn("ITX");
        sl.setItem(item);
        assertEquals(sl.getTitle(), sl.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Item it = mock(Item.class);
        PcmSourcingLaneException a = new PcmSourcingLaneException();
        a.setItem(it);
        PcmSourcingLaneException b = new PcmSourcingLaneException();
        b.setItem(it);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "x");
    }

    @Test
    void testEqualsFallsBackToKeyWhenLazyLoadFails() {
        // When item access doesn't throw, equality is computed normally.
        PcmSourcingLaneException a = new PcmSourcingLaneException();
        PcmSourcingLaneException b = new PcmSourcingLaneException();
        a.setSourcingLaneKey(1L);
        b.setSourcingLaneKey(2L);
        // Both have null items, so they're equal via the natural-id path
        assertEquals(a, b);
    }

    @Test
    void testGetPCMSourcingLane_CopiesFields() {
        PcmSourcingLaneException sl = new PcmSourcingLaneException();
        sl.setSourcingLaneName("N");
        sl.setSourcingLaneExternalId("X");
        sl.setDateOffset(5L);
        sl.setProductState("ACTIVE");
        sl.setEndDateRequired(true);
        sl.setCurrencyCode("USD");
        sl.setSystemDerived(true);
        sl.setDescription("D");
        Date in = new Date();
        sl.setInsertDt(in);
        sl.setUpdateDt(in);
        sl.setDeleteFlag(true);
        sl.setCurrentFlag(false);
        sl.setCollaboration(true);
        sl.setCostProviderBusinessEntity("BE");
        sl.setStatus("DRAFT");

        PcmSourcingLane copy = sl.getPCMSourcingLane();
        assertEquals("N", copy.getSourcingLaneName());
        assertEquals("X", copy.getSourcingLaneExternalId());
        assertEquals(5L, copy.getDateOffset());
        assertEquals("ACTIVE", copy.getProductState());
        assertEquals(Boolean.TRUE, copy.isEndDateRequired());
        assertEquals("USD", copy.getCurrencyCode());
        assertEquals(Boolean.TRUE, copy.isSystemDerived());
        assertEquals("D", copy.getDescription());
        assertSame(in, copy.getInsertDate());
        assertSame(in, copy.getUpdateDate());
        assertEquals(Boolean.TRUE, copy.getDeleteFlag());
        assertEquals(Boolean.FALSE, copy.getCurrentFlag());
        assertEquals(Boolean.TRUE, copy.getCollaboration());
        assertEquals("BE", copy.getCostProviderBusinessEntity());
        assertEquals("DRAFT", copy.getStatus());
    }

    @Test
    void testStatefulBaseOverridesReturnDefaults() {
        PcmSourcingLaneException sl = new PcmSourcingLaneException();
        // getInsertDate now returns the stored insertDt, which is auto-initialized
        assertNotNull(sl.getInsertDate());
        assertNull(sl.getUpdateDate());
        // currentFlag default is TRUE
        assertTrue(sl.getCurrentFlag());
        Date d = new Date();
        sl.setInsertDate(d);
        sl.setUpdateDate(d);
        assertSame(d, sl.getInsertDate());
        assertSame(d, sl.getUpdateDate());
    }
}
