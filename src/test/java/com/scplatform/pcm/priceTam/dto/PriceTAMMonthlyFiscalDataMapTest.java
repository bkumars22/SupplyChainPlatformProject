/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.priceTam.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PriceTAMMonthlyFiscalDataMapTest {

    @Test
    void noArgConstructor_allFieldsNull() {
        PriceTAMMonthlyFiscalDataMap m = new PriceTAMMonthlyFiscalDataMap();
        assertNull(m.getFiscalStartDate());
        assertNull(m.getFiscalEndDate());
        assertNull(m.getFunctionalGroupID());
        assertNull(m.getItemKey());
        assertNull(m.getSupplierKey());
        assertNull(m.getToSiteKey());
        assertNull(m.getToSiteDescription());
        assertNull(m.getSourcingLaneKey());
        assertNull(m.getFromSiteKey());
        assertNull(m.getCostTypeName());
        assertNull(m.getCostTypeKey());
        assertNull(m.getMpn());
        assertNull(m.getCostValue());
        assertNull(m.getXlobId());
        assertNull(m.getSiteKey());
        assertNull(m.getSiteDescription());
        assertNull(m.getAllocation());
    }

    @Test
    void allArgsConstructor() {
        Date s = new Date(1L);
        Date e = new Date(2L);
        PriceTAMMonthlyFiscalDataMap m = new PriceTAMMonthlyFiscalDataMap(
                s, e, 100L, 200L, 300L, 400L, "to", 500L, 600L,
                "name", "key", "mpn", 9.5, 700L, 800L, "site", 0.25);
        assertEquals(s, m.getFiscalStartDate());
        assertEquals(e, m.getFiscalEndDate());
        assertEquals(100L, m.getFunctionalGroupID());
        assertEquals(200L, m.getItemKey());
        assertEquals(300L, m.getSupplierKey());
        assertEquals(400L, m.getToSiteKey());
        assertEquals("to", m.getToSiteDescription());
        assertEquals(500L, m.getSourcingLaneKey());
        assertEquals(600L, m.getFromSiteKey());
        assertEquals("name", m.getCostTypeName());
        assertEquals("key", m.getCostTypeKey());
        assertEquals("mpn", m.getMpn());
        assertEquals(9.5, m.getCostValue());
        assertEquals(700L, m.getXlobId());
        assertEquals(800L, m.getSiteKey());
        assertEquals("site", m.getSiteDescription());
        assertEquals(0.25, m.getAllocation());
    }

    @Test
    void setters_basicTypes() {
        PriceTAMMonthlyFiscalDataMap m = new PriceTAMMonthlyFiscalDataMap();
        m.setFISCALSTARTDATE(new Date(1L));
        m.setFISCALENDDATE(new Date(2L));
        m.setFUNCTIONALGROUPID(1L);
        m.setITEMKEY(2L);
        m.setSUPPLIERKEY(3L);
        m.setTOSITEKEY(4L);
        m.setTOSITEDESCRIPTION("td");
        m.setSOURCINGLANEKEY(5L);
        m.setFROMSITEKEY(6L);
        m.setCOSTTYPENAME("ctn");
        m.setCOSTTYPEKEY("ctk");
        m.setMPN("m");
        m.setCOSTVALUE(1.0);
        m.setXLOBID(7L);
        m.setSITEDESCRIPTION("sd");

        assertEquals(new Date(1L), m.getFiscalStartDate());
        assertEquals(new Date(2L), m.getFiscalEndDate());
        assertEquals(1L, m.getFunctionalGroupID());
        assertEquals("td", m.getToSiteDescription());
        assertEquals("sd", m.getSiteDescription());
        assertEquals("ctn", m.getCostTypeName());
        assertEquals("ctk", m.getCostTypeKey());
        assertEquals("m", m.getMpn());
        assertEquals(1.0, m.getCostValue());
    }

    @Test
    void setSiteKey_bigDecimalConversion() {
        PriceTAMMonthlyFiscalDataMap m = new PriceTAMMonthlyFiscalDataMap();
        m.setSITEKEY(new BigDecimal("123"));
        assertEquals(123L, m.getSiteKey());
    }

    @Test
    void setSiteKey_nullStaysNull() {
        PriceTAMMonthlyFiscalDataMap m = new PriceTAMMonthlyFiscalDataMap();
        m.setSITEKEY(null);
        assertNull(m.getSiteKey());
    }

    @Test
    void setAllocation_nullStaysNull() {
        PriceTAMMonthlyFiscalDataMap m = new PriceTAMMonthlyFiscalDataMap();
        m.setALLOCATION(null);
        assertNull(m.getAllocation());
    }

    @Test
    void setAllocation_value() {
        PriceTAMMonthlyFiscalDataMap m = new PriceTAMMonthlyFiscalDataMap();
        m.setALLOCATION(0.75);
        assertEquals(0.75, m.getAllocation());
    }
}
