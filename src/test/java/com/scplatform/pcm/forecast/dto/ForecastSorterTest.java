/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.dto;

import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.enums.ForecastModel;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ForecastSorterTest {

    private PcmForecast makeForecast(String forecastType, String itemNumber,
                                     String siteDesc, Long key) {
        PcmForecast fc = mock(PcmForecast.class);
        when(fc.getForecastType()).thenReturn(forecastType);
        when(fc.getForecastKey()).thenReturn(key);
        when(fc.getStatus()).thenReturn("ACTIVE");

        Item item = mock(Item.class);
        when(item.getItemNumber()).thenReturn(itemNumber);
        when(fc.getItem()).thenReturn(item);

        if (siteDesc != null) {
            Site site = mock(Site.class);
            when(site.getSiteDescription()).thenReturn(siteDesc);
            when(fc.getSite()).thenReturn(site);
        } else {
            when(fc.getSite()).thenReturn(null);
        }
        return fc;
    }

    @Test
    void testCompare_SameOrderReturnZero() {
        ForecastSorter sorter = new ForecastSorter();
        PcmForecast f1 = makeForecast("COST", "PN-100", "SITE-A", 1L);
        PcmForecast f2 = makeForecast("COST", "PN-100", "SITE-A", 1L);
        // Only key null-ness differs the 4th criterion — both have non-null keys here
        assertEquals(0, sorter.compare(f1, f2));
    }

    @Test
    void testCompare_DifferentForecastType() {
        ForecastSorter sorter = new ForecastSorter();
        PcmForecast f1 = makeForecast("COST",   "PN-100", "SITE-A", 1L);
        PcmForecast f2 = makeForecast("DEMAND", "PN-100", "SITE-A", 2L);
        // "COST" < "DEMAND"
        assertTrue(sorter.compare(f1, f2) < 0);
        assertTrue(sorter.compare(f2, f1) > 0);
    }

    @Test
    void testCompare_DifferentItemNumber() {
        ForecastSorter sorter = new ForecastSorter();
        PcmForecast f1 = makeForecast("COST", "AAA", "SITE", 1L);
        PcmForecast f2 = makeForecast("COST", "BBB", "SITE", 2L);
        assertTrue(sorter.compare(f1, f2) < 0);
        assertTrue(sorter.compare(f2, f1) > 0);
    }

    @Test
    void testCompare_NullKeyComesLast() {
        ForecastSorter sorter = new ForecastSorter();
        PcmForecast withKey    = makeForecast("COST", "PN", "SITE", 1L);
        PcmForecast withoutKey = makeForecast("COST", "PN", "SITE", null);
        // null key → goes last (true > false in Boolean compare)
        assertTrue(sorter.compare(withKey, withoutKey) < 0);
        assertTrue(sorter.compare(withoutKey, withKey) > 0);
    }

    @Test
    void testCompare_NullSite() {
        ForecastSorter sorter = new ForecastSorter();
        PcmForecast withSite    = makeForecast("COST", "PN", "SITE-A", 1L);
        PcmForecast withoutSite = makeForecast("COST", "PN", null, 2L);
        // null vs non-null site — result is defined (null < "SITE-A" in CompareToBuilder)
        int result = sorter.compare(withoutSite, withSite);
        assertTrue(result < 0, "null site description should sort before non-null");
    }

    @Test
    void testSortList() {
        ForecastSorter sorter = new ForecastSorter();
        PcmForecast f1 = makeForecast("COST", "ZZZ", "SITE", 1L);
        PcmForecast f2 = makeForecast("COST", "AAA", "SITE", 2L);
        PcmForecast f3 = makeForecast("COST", "MMM", "SITE", 3L);
        List<PcmForecast> list = Arrays.asList(f1, f2, f3);
        list.sort(sorter);
        assertEquals("AAA", list.get(0).getItem().getItemNumber());
        assertEquals("MMM", list.get(1).getItem().getItemNumber());
        assertEquals("ZZZ", list.get(2).getItem().getItemNumber());
    }

    @Test
    void testCompareEmptyList() {
        ForecastSorter sorter = new ForecastSorter();
        List<PcmForecast> list = Collections.emptyList();
        list.sort(sorter); // must not throw
        assertTrue(list.isEmpty());
    }
}
