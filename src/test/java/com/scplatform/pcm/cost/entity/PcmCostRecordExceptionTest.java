/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.entity;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.costexception.entity.CostException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PcmCostRecordExceptionTest {

    @Test
    void testNoArgsConstructorDefaults() {
        PcmCostRecordException cr = new PcmCostRecordException();
        assertNull(cr.getCostRecordKey());
        assertEquals(Boolean.FALSE, cr.getDeleteFlag());
        // getCurrentFlag is overridden to always return false (StatefulBase override)
        assertFalse(cr.getCurrentFlag());
        assertNotNull(cr.getInsertDt());
        assertNotNull(cr.getCostRecordRanges());
        assertTrue(cr.getCostRecordRanges().isEmpty());
        assertEquals("EA", PcmCostRecordException.DEFAULT_UOM);
    }

    @Test
    void testGettersSetters() {
        PcmCostRecordException cr = new PcmCostRecordException();
        cr.setCostRecordKey(1L);
        CostException ce = mock(CostException.class);
        cr.setCostException(ce);
        PcmSourcingLane sl = mock(PcmSourcingLane.class);
        cr.setSourcingLane(sl);
        PcmSourcingLaneException slex = mock(PcmSourcingLaneException.class);
        cr.setSourcingLaneException(slex);
        PcmCostType ct = new PcmCostType();
        cr.setCostType(ct);
        Date d = new Date();
        cr.setEffectiveFromDt(d);
        cr.setEffectiveToDt(d);
        cr.setInsertDt(d);
        cr.setUpdateDt(d);
        cr.setDeleteFlag(true);
        cr.setCurrentFlag(false);
        BusinessEntity be = mock(BusinessEntity.class);
        cr.setCostProvider(be);
        cr.setDescription("D");
        cr.setReasonCode("RC");
        cr.setCostRecordExternalId("EXT");
        cr.setSystemAction("A");
        cr.setProjectName("PN");
        PcmPricingScenario ps = new PcmPricingScenario();
        cr.setPricingScenario(ps);
        cr.setCostRecordRanges(new TreeSet<>());

        assertEquals(1L, cr.getCostRecordKey());
        assertSame(ce, cr.getCostException());
        assertSame(sl, cr.getSourcingLane());
        assertSame(slex, cr.getSourcingLaneException());
        assertSame(ct, cr.getCostType());
        assertSame(d, cr.getEffectiveFromDt());
        assertSame(d, cr.getEffectiveToDt());
        assertSame(d, cr.getInsertDt());
        assertSame(d, cr.getUpdateDt());
        assertEquals(Boolean.TRUE, cr.getDeleteFlag());
        assertFalse(cr.getCurrentFlag());
        assertSame(be, cr.getCostProvider());
        assertEquals("D", cr.getDescription());
        assertEquals("RC", cr.getReasonCode());
        assertEquals("EXT", cr.getCostRecordExternalId());
        assertEquals("A", cr.getSystemAction());
        assertEquals("PN", cr.getProjectName());
        assertSame(ps, cr.getPricingScenario());
        assertNotNull(cr.getCostRecordRanges());
    }

    @Test
    void testFlexAttributes() {
        PcmCostRecordException cr = new PcmCostRecordException();
        cr.setStringAttribute1("s1"); cr.setStringAttribute2("s2");
        cr.setStringAttribute3("s3"); cr.setStringAttribute4("s4");
        cr.setStringAttribute5("s5"); cr.setStringAttribute6("s6");
        cr.setStringAttribute7("s7"); cr.setStringAttribute8("s8");
        cr.setStringAttribute9("s9"); cr.setStringAttribute10("s10");
        cr.setNumberAttribute1(1); cr.setNumberAttribute2(2);
        cr.setNumberAttribute3(3); cr.setNumberAttribute4(4);
        cr.setNumberAttribute5(5); cr.setNumberAttribute6(6);
        cr.setNumberAttribute7(7); cr.setNumberAttribute8(8);
        cr.setNumberAttribute9(9); cr.setNumberAttribute10(10);
        cr.setFloatAttribute1(new BigDecimal("1.1"));
        cr.setFloatAttribute2(new BigDecimal("2.2"));
        cr.setFloatAttribute3(new BigDecimal("3.3"));
        cr.setFloatAttribute4(new BigDecimal("4.4"));
        cr.setFloatAttribute5(new BigDecimal("5.5"));
        cr.setFloatAttribute6(new BigDecimal("6.6"));
        cr.setFloatAttribute7(new BigDecimal("7.7"));
        cr.setFloatAttribute8(new BigDecimal("8.8"));
        cr.setFloatAttribute9(new BigDecimal("9.9"));
        cr.setFloatAttribute10(new BigDecimal("10.1"));
        Date d = new Date();
        cr.setDateAttribute1(d); cr.setDateAttribute2(d);
        cr.setDateAttribute3(d); cr.setDateAttribute4(d);
        cr.setDateAttribute5(d); cr.setDateAttribute6(d);
        cr.setDateAttribute7(d); cr.setDateAttribute8(d);
        cr.setDateAttribute9(d); cr.setDateAttribute10(d);

        assertEquals("s1", cr.getStringAttribute1());
        assertEquals("s10", cr.getStringAttribute10());
        assertEquals(1, cr.getNumberAttribute1());
        assertEquals(10, cr.getNumberAttribute10());
        assertEquals(new BigDecimal("1.1"), cr.getFloatAttribute1());
        assertEquals(new BigDecimal("10.1"), cr.getFloatAttribute10());
        assertSame(d, cr.getDateAttribute1());
        assertSame(d, cr.getDateAttribute10());
    }

    @Test
    void testStatefulBaseOverrides() {
        PcmCostRecordException cr = new PcmCostRecordException();
        assertNull(cr.getInsertDate());
        assertNull(cr.getUpdateDate());
        assertFalse(cr.getCurrentFlag());
        cr.setInsertDate(new Date()); // no-op
        cr.setUpdateDate(new Date()); // no-op
        assertNull(cr.getInsertDate());
    }

    @Test
    void testGetChildrenAndParent() {
        PcmCostRecordException cr = new PcmCostRecordException();
        assertTrue(cr.getChildren().isEmpty());
        assertNull(cr.getParent());
    }

    @Test
    void testCompareToReturnsZero() {
        PcmCostRecordException cr = new PcmCostRecordException();
        assertEquals(0, cr.compareTo(new PcmCostRecordException()));
        assertEquals(0, cr.compareTo("anything"));
    }
}
