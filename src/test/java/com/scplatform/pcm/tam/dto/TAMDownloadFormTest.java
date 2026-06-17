/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.dto;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TAMDownloadFormTest {

    @Test
    void defaultConstructor_initializesCollections() {
        TAMDownloadForm form = new TAMDownloadForm();
        assertNotNull(form);
        assertNotNull(form.getHeader());
        assertNotNull(form.getBusinessEntityItemList());
    }

    @Test
    void settersAndGetters_workCorrectly() {
        TAMDownloadForm form = new TAMDownloadForm();
        form.setDownloadOption("FULL");
        form.setSupplierCount(5L);
        form.setItemCount(10L);
        form.setGlobalRegionCheck(true);
        form.setFileLocation("/tmp/tam");
        form.setSearchStartDate("2025-01-01");
        Date d = new Date();
        form.setFiscalPeriodStartDate(d);
        form.setCurrentSearchDate(d);

        String[] keys = {"k1", "k2"};
        form.setSelectedPageKeys(keys);

        assertEquals("FULL", form.getDownloadOption());
        assertEquals(5L, form.getSupplierCount());
        assertEquals(10L, form.getItemCount());
        assertTrue(form.getGlobalRegionCheck());
        assertEquals("/tmp/tam", form.getFileLocation());
        assertEquals("2025-01-01", form.getSearchStartDate());
        assertEquals(d, form.getFiscalPeriodStartDate());
        assertEquals(d, form.getCurrentSearchDate());
        assertArrayEquals(keys, form.getSelectedPageKeys());
    }

    @Test
    void header_canBePopulated() {
        TAMDownloadForm form = new TAMDownloadForm();
        Map<String, List<TAMHeader>> header = new LinkedHashMap<>();
        form.setHeader(header);
        assertSame(header, form.getHeader());
    }
}
