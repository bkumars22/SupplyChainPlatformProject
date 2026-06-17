/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.entity;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PcmCostRecordTest {

    private static PcmCostElement element(String key) {
        PcmCostElement el = new PcmCostElement();
        el.setId(new PcmCostElementId(key, "CT"));
        el.setCostElementValueType("S");
        return el;
    }

    @Test
    void testNoArgsDefaults() {
        PcmCostRecord cr = new PcmCostRecord();
        assertNull(cr.getCostRecordKey());
        assertEquals(Boolean.FALSE, cr.getDeleteFlag());
        assertTrue(cr.getCurrentFlag());
        assertNotNull(cr.getInsertDt());
        assertNotNull(cr.getCostRecordRanges());
        assertTrue(cr.getCostRecordRanges().isEmpty());
        assertEquals("EA", PcmCostRecord.DEFAULT_UOM);
    }

    @Test
    void testSixArgConstructor() {
        PcmSourcingLane sl = new PcmSourcingLane();
        PcmCostType ct = new PcmCostType();
        Date d = new Date();
        PcmCostRecord cr = new PcmCostRecord(99L, sl, ct, "ACTIVE", d, true);
        assertEquals(99L, cr.getCostRecordKey());
        assertSame(sl, cr.getSourcingLane());
        assertSame(ct, cr.getCostType());
        assertEquals("ACTIVE", cr.getStatus());
        assertSame(d, cr.getInsertDt());
        assertTrue(cr.getCurrentFlag());
    }

    @Test
    void testTrackDeltaOverrides() {
        PcmCostRecord cr = new PcmCostRecord();
        Date in = new Date(1000);
        Date up = new Date(2000);
        cr.setInsertDate(in);
        cr.setUpdateDate(up);
        cr.setDeleteFlag(true);
        cr.setCurrentFlag(false);
        assertSame(in, cr.getInsertDate());
        assertSame(up, cr.getUpdateDate());
        assertEquals(Boolean.TRUE, cr.getDeleteFlag());
        assertFalse(cr.getCurrentFlag());
    }

    @Test
    void testGetCostRecordValues_ReturnsNullWhenNoRanges() {
        PcmCostRecord cr = new PcmCostRecord();
        assertNull(cr.getActiveCostRecordRange());
        assertNull(cr.getActiveCostRecordValues());
        assertNull(cr.getCostRecordValues());
    }

    @Test
    void testSetCostRecordValuesThrowsWhenNoActiveRange() {
        PcmCostRecord cr = new PcmCostRecord();
        assertThrows(IllegalStateException.class, () -> cr.setCostRecordValues(Map.of()));
    }

    @Test
    void testAddDefaultCostRecordRange_CreatesActiveZeroRange() {
        PcmCostRecord cr = new PcmCostRecord();
        PcmCostRecordRange range = cr.addDefaultCostRecordRange();
        assertNotNull(range);
        assertEquals(0, new BigDecimal(0).compareTo(range.getFromRange()));
        assertEquals(Boolean.TRUE, range.getActive());
        assertSame(cr, range.getCostRecord());
        assertSame(range, cr.getActiveCostRecordRange());
    }

    @Test
    void testAddDefaultCostRecordRange_ReturnsExistingZeroRange() {
        PcmCostRecord cr = new PcmCostRecord();
        PcmCostRecordRange first = cr.addDefaultCostRecordRange();
        PcmCostRecordRange second = cr.addDefaultCostRecordRange();
        assertSame(first, second);
    }

    @Test
    void testAddDefaultCostRecordRange_ThrowsWhenOtherRangesExist() {
        PcmCostRecord cr = new PcmCostRecord();
        PcmCostRecordRange r = new PcmCostRecordRange(new BigDecimal("5"));
        cr.addCostRecordRange(r);
        assertThrows(IllegalStateException.class, cr::addDefaultCostRecordRange);
    }

    @Test
    void testAddCostRecordRange_CreatesAndReusesByValues() {
        PcmCostRecord cr = new PcmCostRecord();
        PcmCostRecordRange a = cr.addCostRecordRange(new BigDecimal("0"), new BigDecimal("10"), Boolean.TRUE);
        assertNotNull(a);
        assertEquals(Boolean.TRUE, a.getActive());
        // Reuse with same values, flipping active
        PcmCostRecordRange b = cr.addCostRecordRange(new BigDecimal("0"), new BigDecimal("10"), Boolean.FALSE);
        assertSame(a, b);
        assertEquals(Boolean.FALSE, b.getActive());
        assertEquals(1, cr.getCostRecordRanges().size());
    }

    @Test
    void testAddCostRecordRangeObject_BacklinksParent() {
        PcmCostRecord cr = new PcmCostRecord();
        PcmCostRecordRange r = new PcmCostRecordRange(new BigDecimal("0"));
        cr.addCostRecordRange(r);
        assertSame(cr, r.getCostRecord());
        assertTrue(cr.getCostRecordRanges().contains(r));
    }

    @Test
    void testGetCostRecordRangeByKey() {
        PcmCostRecord cr = new PcmCostRecord();
        PcmCostRecordRange a = new PcmCostRecordRange(new BigDecimal("0"));
        a.setCostRecordRangeKey(5L);
        cr.addCostRecordRange(a);
        assertSame(a, cr.getCostRecordRangeByKey(5L));
        assertNull(cr.getCostRecordRangeByKey(99L));
        assertNull(cr.getCostRecordRangeByKey(null));
    }

    @Test
    void testGetCostRecordRange_MatchesAndMissingValues() {
        PcmCostRecord cr = new PcmCostRecord();
        PcmCostRecordRange r1 = new PcmCostRecordRange(new BigDecimal("0"));
        r1.setToRange(new BigDecimal("10"));
        cr.addCostRecordRange(r1);
        PcmCostRecordRange r2 = new PcmCostRecordRange(new BigDecimal("10"));
        r2.setToRange(new BigDecimal("20"));
        cr.addCostRecordRange(r2);
        assertSame(r1, cr.getCostRecordRange(new BigDecimal("0"), new BigDecimal("10")));
        assertSame(r2, cr.getCostRecordRange(new BigDecimal("10"), new BigDecimal("20")));
        assertNull(cr.getCostRecordRange(new BigDecimal("99"), new BigDecimal("100")));
        // Null from on lookup against non-null fromRange ranges returns null
        assertNull(cr.getCostRecordRange(null, new BigDecimal("10")));
    }

    @Test
    void testAddAndGetCostRecordValue_DelegatesToActiveRange() {
        PcmCostRecord cr = new PcmCostRecord();
        cr.addDefaultCostRecordRange();
        PcmCostElement el = element("E1");
        PcmCostRecordValue v = cr.addCostRecordValue(el, new BigDecimal("3"), "EA");
        assertNotNull(v);
        assertSame(v, cr.getCostRecordValue(el));
        assertSame(v, cr.getCostRecordValue("E1"));
        assertNull(cr.getCostRecordValue("NOPE"));
    }

    @Test
    void testGetActiveCostRecordRange_PicksActiveOverFirst() {
        PcmCostRecord cr = new PcmCostRecord();
        PcmCostRecordRange low = new PcmCostRecordRange(new BigDecimal("0"));
        low.setActive(Boolean.FALSE);
        PcmCostRecordRange high = new PcmCostRecordRange(new BigDecimal("10"));
        high.setActive(Boolean.TRUE);
        cr.addCostRecordRange(low);
        cr.addCostRecordRange(high);
        assertSame(high, cr.getActiveCostRecordRange());
    }

    @Test
    void testGetActiveCostRecordRange_FallsBackToLowestWhenNoneActive() {
        PcmCostRecord cr = new PcmCostRecord();
        PcmCostRecordRange low = new PcmCostRecordRange(new BigDecimal("0"));
        PcmCostRecordRange high = new PcmCostRecordRange(new BigDecimal("10"));
        cr.addCostRecordRange(low);
        cr.addCostRecordRange(high);
        assertSame(low, cr.getActiveCostRecordRange());
    }

    @Test
    void testGetChildrenAndParent() {
        PcmCostRecord cr = new PcmCostRecord();
        assertNull(cr.getChildren());
        PcmSourcingLane sl = new PcmSourcingLane();
        cr.setSourcingLane(sl);
        assertSame(sl, cr.getParent());
    }

    @Test
    void testEqualsContract_NullAndType() {
        PcmCostRecord a = new PcmCostRecord();
        assertEquals(a, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "x");
    }

    @Test
    void testEqualsCompares_SameValues() {
        PcmSourcingLane sl = new PcmSourcingLane();
        PcmCostType ct = new PcmCostType();
        ct.setCostTypeKey("CT");
        PcmCostRecord a = new PcmCostRecord();
        a.setSourcingLane(sl);
        a.setCostType(ct);
        a.setStatus("ACTIVE");
        PcmCostRecord b = new PcmCostRecord();
        b.setSourcingLane(sl);
        b.setCostType(ct);
        b.setStatus("ACTIVE");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testCompareTo_OrdersByCostType() {
        PcmCostType ct1 = new PcmCostType();
        ct1.setCostTypeKey("AAA");
        PcmCostType ct2 = new PcmCostType();
        ct2.setCostTypeKey("BBB");
        PcmCostRecord a = new PcmCostRecord();
        a.setCostType(ct1);
        a.setStatus("ACTIVE");
        PcmCostRecord b = new PcmCostRecord();
        b.setCostType(ct2);
        b.setStatus("ACTIVE");
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    @Test
    void testCopiedExceptionObjectAndExtraAttr() {
        PcmCostRecord cr = new PcmCostRecord();
        cr.setStringAttribute1("s1");
        cr.setNumberAttribute2(42);
        cr.setFloatAttribute3(new BigDecimal("3.14"));
        Date d = new Date(0);
        cr.setDateAttribute4(d);
        PcmCostRecordException blank = cr.getCopiedExceptionObject();
        assertNotNull(blank);
        assertNull(blank.getStringAttribute1());
        PcmCostRecordException filled = cr.getExtraAttrCopiedExceptionObject(new PcmCostRecordException());
        assertEquals("s1", filled.getStringAttribute1());
        assertEquals(42, filled.getNumberAttribute2());
        assertEquals(new BigDecimal("3.14"), filled.getFloatAttribute3());
        assertSame(d, filled.getDateAttribute4());
    }

    @Test
    void testCopy_ClonesFieldsAndRanges() {
        PcmCostRecord cr = new PcmCostRecord();
        PcmSourcingLane sl = new PcmSourcingLane();
        cr.setSourcingLane(sl);
        PcmCostType ct = new PcmCostType();
        cr.setCostType(ct);
        cr.setStatus("ACTIVE");
        BusinessEntity be = mock(BusinessEntity.class);
        cr.setCostProvider(be);
        cr.setDescription("D");
        cr.setReasonCode("R");
        cr.setPricingScenario(new PcmPricingScenario());
        cr.setStringAttribute1("s1");
        cr.setNumberAttribute1(5);
        cr.addDefaultCostRecordRange();

        PcmCostRecord copy = cr.copy();
        assertNotSame(cr, copy);
        assertSame(sl, copy.getSourcingLane());
        assertSame(ct, copy.getCostType());
        assertEquals("ACTIVE", copy.getStatus());
        assertSame(be, copy.getCostProvider());
        assertEquals("D", copy.getDescription());
        assertEquals("R", copy.getReasonCode());
        assertEquals("s1", copy.getStringAttribute1());
        assertEquals(5, copy.getNumberAttribute1());
        assertEquals(1, copy.getCostRecordRanges().size());
        // Each copied range is linked to the new cost record
        PcmCostRecordRange copiedRange = copy.getCostRecordRanges().first();
        assertSame(copy, copiedRange.getCostRecord());
    }

    @Test
    void testCopyOnUIAction_OmitsReasonCode() {
        PcmCostRecord cr = new PcmCostRecord();
        cr.setSourcingLane(new PcmSourcingLane());
        cr.setCostType(new PcmCostType());
        cr.setReasonCode("R");
        cr.setDescription("D");
        PcmCostRecord copy = cr.copyOnUIAction();
        assertEquals("D", copy.getDescription());
        assertNull(copy.getReasonCode());
    }

    @Test
    void testUseMPNCompareFlag() {
        PcmCostRecord cr = new PcmCostRecord();
        assertFalse(cr.isUseMPNCompare());
        cr.setUseMPNCompare(true);
        assertTrue(cr.isUseMPNCompare());
    }

    @Test
    void testFlexAttributeAccessors() {
        PcmCostRecord cr = new PcmCostRecord();
        cr.setStringAttribute10("s10");
        cr.setNumberAttribute10(10);
        cr.setFloatAttribute10(new BigDecimal("10.10"));
        Date d = new Date();
        cr.setDateAttribute10(d);
        cr.setProjectName("P");
        cr.setLastLoadedByUser("L");
        cr.setCreatedBy("C");
        cr.setLastUpdatedBy("U");
        cr.setSystemAction("SA");
        cr.setCostRecordExternalId("X");
        cr.setCostRecordRanges(new TreeSet<>());
        assertEquals("s10", cr.getStringAttribute10());
        assertEquals(10, cr.getNumberAttribute10());
        assertEquals(new BigDecimal("10.10"), cr.getFloatAttribute10());
        assertSame(d, cr.getDateAttribute10());
        assertEquals("P", cr.getProjectName());
        assertEquals("L", cr.getLastLoadedByUser());
        assertEquals("C", cr.getCreatedBy());
        assertEquals("U", cr.getLastUpdatedBy());
        assertEquals("SA", cr.getSystemAction());
        assertEquals("X", cr.getCostRecordExternalId());
        assertTrue(cr.getCostRecordRanges().isEmpty());
    }
}
