/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.dto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class PageSelectionSupportTest {

    @Test
    void defaultsAreSensible() {
        PageSelectionSupport p = new PageSelectionSupport();
        assertEquals(-1, p.getPageStartAt());
        assertEquals(-1, p.getPageSize());
        assertEquals(0L, p.getTotalRows());
        assertEquals(1, p.getCurrentPage());
        assertFalse(p.getClearSelection());
        assertTrue(p.getPagingEnabled());
        assertNotNull(p.getSelectedKeys());
        assertNotNull(p.getCurrentPageIds());
        assertEquals("RA", PageSelectionSupport.RESETALL);
        assertEquals("RP", PageSelectionSupport.RESETPAGE);
    }

    @Test
    void selectedPageKeysSetAndGet() {
        PageSelectionSupport p = new PageSelectionSupport();
        p.setSelectedPageKeys(new String[]{"a", "b", "c"});
        String[] got = p.getSelectedPageKeys();
        Arrays.sort(got);
        assertArrayEquals(new String[]{"a", "b", "c"}, got);
    }

    @Test
    void setPageStartAtIgnoresNull() {
        PageSelectionSupport p = new PageSelectionSupport();
        p.setPageStartAt(50);
        assertEquals(50, p.getPageStartAt());
        p.setPageStartAt(null);
        assertEquals(50, p.getPageStartAt());
    }

    @Test
    void setTotalRowsIgnoresNull() {
        PageSelectionSupport p = new PageSelectionSupport();
        p.setTotalRows(100L);
        assertEquals(100L, p.getTotalRows());
        p.setTotalRows(null);
        assertEquals(100L, p.getTotalRows());
    }

    @Test
    void setPageSizeAllowsNull() {
        PageSelectionSupport p = new PageSelectionSupport();
        p.setPageSize(20);
        assertEquals(20, p.getPageSize());
        p.setPageSize(null);
        assertEquals(-1, p.getPageSize());
    }

    @Test
    void getCurrentPageWithSizeAndStart() {
        PageSelectionSupport p = new PageSelectionSupport();
        p.setPageSize(10);
        p.setPageStartAt(20);
        assertEquals(3, p.getCurrentPage());
    }

    @Test
    void getCurrentPageDefaultsToOneWhenSizeZero() {
        PageSelectionSupport p = new PageSelectionSupport();
        assertEquals(1, p.getCurrentPage());
    }

    @Test
    void getMaxPageWithExactDivision() {
        PageSelectionSupport p = new PageSelectionSupport();
        p.setPageSize(10);
        p.setTotalRows(50L);
        assertEquals(5, p.getMaxPage());
    }

    @Test
    void getMaxPageRoundsUpRemainder() {
        PageSelectionSupport p = new PageSelectionSupport();
        p.setPageSize(10);
        p.setTotalRows(53L);
        assertEquals(6, p.getMaxPage());
    }

    @Test
    void getMaxPageReturnsAtLeastOne() {
        PageSelectionSupport p = new PageSelectionSupport();
        p.setPageSize(10);
        p.setTotalRows(0L);
        assertEquals(1, p.getMaxPage());
    }

    @Test
    void getMaxPageReturnsOneWhenSizeZero() {
        PageSelectionSupport p = new PageSelectionSupport();
        assertEquals(1, p.getMaxPage());
    }

    @Test
    void getAtPageWithValidSize() {
        PageSelectionSupport p = new PageSelectionSupport();
        p.setPageSize(10);
        p.setPageStartAt(30);
        assertEquals(4L, p.getAtPage());
    }

    @Test
    void clearSelectionClearsCurrentAndOptionallyAllSelected() {
        PageSelectionSupport p = new PageSelectionSupport();
        p.setSelectedPageKeys(new String[]{"a", "b"});
        Set<String> cur = new HashSet<>();
        cur.add("a");
        p.setCurrentPageIds(cur);

        p.setClearSelection(false);
        p.clearSelection();
        assertEquals(2, p.getSelectedKeys().size());
        assertTrue(p.getCurrentPageIds().isEmpty());

        p.setClearSelection(true);
        p.clearSelection();
        assertTrue(p.getSelectedKeys().isEmpty());
    }

    @Test
    void resetPagingValuesResets() {
        PageSelectionSupport p = new PageSelectionSupport();
        p.setPageStartAt(50);
        p.setTotalRows(100L);
        p.resetPagingValues();
        assertEquals(0, p.getPageStartAt());
        assertEquals(0L, p.getTotalRows());
    }

    @Test
    void pagingEnabledSetterAndGetter() {
        PageSelectionSupport p = new PageSelectionSupport();
        p.setPagingEnabled(false);
        assertFalse(p.getPagingEnabled());
    }

    @Test
    void clearSelectionSetterAndGetter() {
        PageSelectionSupport p = new PageSelectionSupport();
        p.setClearSelection(true);
        assertTrue(p.getClearSelection());
    }
}
