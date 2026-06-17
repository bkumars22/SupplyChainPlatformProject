/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.priceTam.dto;

import com.scplatform.pcm.fiscalPeriod.entity.FiscalPeriod;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PriceTAMFormTest {

    @Test
    void simpleSettersAndGetters() {
        PriceTAMForm f = new PriceTAMForm();
        f.setMonthlySearch(Boolean.TRUE);
        f.setSearchType("X");
        f.setOffsetConfigValue("ocv");
        f.setMaxOffsetValue(7);
        f.setHeaderDateFormat("MM/yy");
        List<FiscalPeriod> periods = Collections.emptyList();
        f.setPeriods(periods);
        f.setActualFiscalMonth(periods);
        f.setActualFiscalQuarter(periods);
        f.setOffsetValueDataSet(Collections.emptyList());

        assertEquals(Boolean.TRUE, f.getMonthlySearch());
        assertEquals("X", f.getSearchType());
        assertEquals("ocv", f.getOffsetConfigValue());
        assertEquals(Integer.valueOf(7), f.getMaxOffsetValue());
        assertEquals("MM/yy", f.getHeaderDateFormat());
        assertSame(periods, f.getPeriods());
        assertSame(periods, f.getActualFiscalMonth());
        assertSame(periods, f.getActualFiscalQuarter());
        assertNotNull(f.getOffsetValueDataSet());
    }

    @Test
    void timelineForFiscal_monthly() {
        PriceTAMForm f = new PriceTAMForm();
        List<FiscalPeriod> mo = Collections.emptyList();
        List<FiscalPeriod> qu = Collections.emptyList();
        f.setActualFiscalMonth(mo);
        f.setActualFiscalQuarter(qu);
        f.setMonthlySearch(Boolean.TRUE);
        assertSame(mo, f.getTimelineForFiscal());
    }

    @Test
    void timelineForFiscal_quarterly() {
        PriceTAMForm f = new PriceTAMForm();
        List<FiscalPeriod> mo = Collections.emptyList();
        List<FiscalPeriod> qu = Collections.emptyList();
        f.setActualFiscalMonth(mo);
        f.setActualFiscalQuarter(qu);
        f.setMonthlySearch(Boolean.FALSE);
        assertSame(qu, f.getTimelineForFiscal());
    }

    @Test
    void timelineForFiscal_nullMonthlyFlag_returnsNull() {
        PriceTAMForm f = new PriceTAMForm();
        // monthlySearch is null -> NPE caught -> returns null
        assertNull(f.getTimelineForFiscal());
    }

    @Test
    void getDate_static_delegatesToFilterType() {
        PriceTAMForm f = new PriceTAMForm();
        f.setFilterType("FT");
        assertEquals("FT", PriceTAMForm.getDate(f));
    }

    @Test
    void getFiscalPeriodPriceData_nullCostType_returnsEmptyBlock() {
        PriceTAMForm f = new PriceTAMForm();
        f.setMonthlySearch(Boolean.TRUE);
        Map<Date, PriceTAMFiscalData> result =
                f.getFiscalPeriodPriceData(1L, "mpn", 1L, null, 1L, 1L, 1L, null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getFiscalPeriodPriceData_nullFunctionalGroupId_returnsEmptyBlock() {
        PriceTAMForm f = new PriceTAMForm();
        f.setMonthlySearch(Boolean.TRUE);
        Map<Date, PriceTAMFiscalData> result =
                f.getFiscalPeriodPriceData(1L, "mpn", 1L, "STD", null, 1L, 1L, null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getFiscalPeriodTAMData_nullTamSiteKey_returnsEmptyBlock() {
        PriceTAMForm f = new PriceTAMForm();
        f.setMonthlySearch(Boolean.TRUE);
        assertTrue(f.getFiscalPeriodTAMData(1L, "mpn", 1L, 1L, null).isEmpty());
    }

    @Test
    void getFiscalPeriodTAMData_nullFunctionalGroup_returnsEmptyBlock() {
        PriceTAMForm f = new PriceTAMForm();
        f.setMonthlySearch(Boolean.TRUE);
        assertTrue(f.getFiscalPeriodTAMData(1L, "mpn", 1L, null, 1L).isEmpty());
    }

    @Test
    void reset_clearsFieldsAndCaches() {
        PriceTAMForm f = new PriceTAMForm();
        f.setMonthlySearch(Boolean.TRUE);
        f.setOffsetConfigValue("v");
        f.setMaxOffsetValue(10);
        f.setActualFiscalMonth(Collections.emptyList());
        f.setActualFiscalQuarter(Collections.emptyList());

        f.reset();
        assertNull(f.getMonthlySearch());
        assertNull(f.getOffsetConfigValue());
        assertNull(f.getMaxOffsetValue());
        assertNull(f.getActualFiscalMonth());
        assertNull(f.getActualFiscalQuarter());
        assertNull(f.getPeriods());
    }

    @Test
    void clearCache_doesNotThrowOnFreshInstance() {
        PriceTAMForm f = new PriceTAMForm();
        assertDoesNotThrow(f::clearCache);
    }
}
