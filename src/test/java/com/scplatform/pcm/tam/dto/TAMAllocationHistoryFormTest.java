/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.dto;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TAMAllocationHistoryFormTest {

    @Test
    void defaultConstructor_initializesCollections() {
        TAMAllocationHistoryForm form = new TAMAllocationHistoryForm();
        assertNotNull(form);
        assertNotNull(form.getRegionList());
        assertTrue(form.getRegionList().isEmpty());
        assertNotNull(form.getCalenderMonthHeader());
        assertNotNull(form.getCalenderDateHeader());
        assertNotNull(form.getHeader());
        assertNotNull(form.getBusinessEntityItemList());
        assertNotNull(form.getInheritValues());
        assertNotNull(form.getAllSites());
        assertNotNull(form.getSitesList());
        assertNotNull(form.getFgTypeOption());
    }

    @Test
    void settersAndGetters_workForKeyFields() {
        TAMAllocationHistoryForm form = new TAMAllocationHistoryForm();
        Date now = new Date();

        form.setItemNumber("ITEM-001");
        form.setGroupName("FG-ALPHA");
        form.setSiteType("Regional");
        form.setRegion("APAC");
        form.setSiteDescription("Asia Pacific");
        form.setMinRange(0.1);
        form.setMaxRange(1.0);
        form.setAllocationStatus("ACTIVE");
        form.setCurrentDate(now);
        form.setDataLocation("/data/tam");
        form.setUnsavedData(true);
        form.setFreshSearch(false);
        form.setCopyType("FULL");
        form.setStartDate("2025-01-01");
        form.setEndDate("2025-12-31");
        form.setHideSupplierWithNoAllocationPref(true);
        form.setHideItemPref(false);
        form.setCacheRegion("REG1");
        form.setSelectedFgType("TYPE_A");
        form.setXlobDisableSiteLevel("Y");
        form.setSearchStartDate("2025-01-01");
        form.setFiscalPeriodStartDate(now);
        form.setCurrentSearchDate(now);
        form.setPastScreen(true);

        assertEquals("ITEM-001", form.getItemNumber());
        assertEquals("FG-ALPHA", form.getGroupName());
        assertEquals("Regional", form.getSiteType());
        assertEquals("APAC", form.getRegion());
        assertEquals("Asia Pacific", form.getSiteDescription());
        assertEquals(0.1, form.getMinRange());
        assertEquals(1.0, form.getMaxRange());
        assertEquals("ACTIVE", form.getAllocationStatus());
        assertEquals(now, form.getCurrentDate());
        assertEquals("/data/tam", form.getDataLocation());
        assertTrue(form.getUnsavedData());
        assertFalse(form.getFreshSearch());
        assertEquals("FULL", form.getCopyType());
        assertEquals("2025-01-01", form.getStartDate());
        assertEquals("2025-12-31", form.getEndDate());
        assertTrue(form.getHideSupplierWithNoAllocationPref());
        assertFalse(form.getHideItemPref());
        assertEquals("REG1", form.getCacheRegion());
        assertEquals("TYPE_A", form.getSelectedFgType());
        assertEquals("Y", form.getXlobDisableSiteLevel());
        assertEquals("2025-01-01", form.getSearchStartDate());
        assertEquals(now, form.getFiscalPeriodStartDate());
        assertEquals(now, form.getCurrentSearchDate());
        assertTrue(form.getPastScreen());
    }

    @Test
    void reset_clearsKeyFields() {
        TAMAllocationHistoryForm form = new TAMAllocationHistoryForm();
        form.setGroupName("FG-TEST");
        form.setItemNumber("ITEM-A");
        form.setUnsavedData(true);
        form.setFreshSearch(false);
        form.setCopyType("PARTIAL");
        form.setSearchStartDate("2025-06-01");
        form.setPastScreen(false);

        form.reset();

        assertNull(form.getGroupName());
        assertNull(form.getItemNumber());
        assertNull(form.getCopyType());
        assertNull(form.getSearchStartDate());
        assertNull(form.getFiscalPeriodStartDate());
        assertNull(form.getCurrentSearchDate());
        assertNull(form.getPastScreen());
        assertFalse(form.getUnsavedData());
        assertTrue(form.getFreshSearch());
    }

    @Test
    void siteList_setterAndGetter() {
        TAMAllocationHistoryForm form = new TAMAllocationHistoryForm();
        String[] sites = {"SITE-A", "SITE-B"};
        form.setSiteList(sites);
        assertArrayEquals(sites, form.getSiteList());
    }
}
