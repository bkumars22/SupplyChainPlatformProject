/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.xlob.entity;

import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.site.entity.Site;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class XLOBAllocationTest {

    @Test
    void defaultConstructor_allFieldsNull() {
        XLOBAllocation a = new XLOBAllocation();
        assertNull(a.getId());
        assertNull(a.getXlob());
        assertNull(a.getSite());
        assertNull(a.getExtractFlag());
        assertNull(a.getLastChangedOn());
        assertNull(a.getLastChangedBy());
        assertNull(a.getLastSystemUpdatedOn());
        assertNull(a.getLastSystemChangedBy());
        assertNull(a.getCreatedOn());
        assertNull(a.getCreatedBy());
        assertNull(a.getRecordSource());
        assertNull(a.getRollOverCount());
        assertNull(a.getNextRolloverDate());
        assertNull(a.getCurrentDataDeleted());
        assertNull(a.getIsCurrentDataDeleted());
        assertNull(a.getItemAllocations());
    }

    @Test
    void settersAndGetters() {
        XLOBAllocation a = new XLOBAllocation();
        FunctionalGroup fg = mock(FunctionalGroup.class);
        Site s = mock(Site.class);
        Timestamp ts = new Timestamp(1L);
        Set<XLOBItemAllocation> items = new HashSet<>();
        a.setId(5L);
        a.setXlob(fg);
        a.setSite(s);
        a.setExtractFlag("Y");
        a.setLastChangedOn(ts);
        a.setLastChangedBy("u1");
        a.setLastSystemUpdatedOn(ts);
        a.setLastSystemChangedBy("sys");
        a.setCreatedOn(ts);
        a.setCreatedBy("creator");
        a.setRecordSource("SRC");
        a.setRollOverCount(3);
        a.setNextRolloverDate(ts);
        a.setIsCurrentDataDeleted(Boolean.TRUE);
        a.setItemAllocations(items);

        assertEquals(5L, a.getId());
        assertSame(fg, a.getXlob());
        assertSame(s, a.getSite());
        assertEquals("Y", a.getExtractFlag());
        assertSame(ts, a.getLastChangedOn());
        assertEquals("u1", a.getLastChangedBy());
        assertSame(ts, a.getLastSystemUpdatedOn());
        assertEquals("sys", a.getLastSystemChangedBy());
        assertSame(ts, a.getCreatedOn());
        assertEquals("creator", a.getCreatedBy());
        assertEquals("SRC", a.getRecordSource());
        assertEquals(3, a.getRollOverCount());
        assertSame(ts, a.getNextRolloverDate());
        assertEquals(Boolean.TRUE, a.getCurrentDataDeleted());
        assertEquals(Boolean.TRUE, a.getIsCurrentDataDeleted());
        assertSame(items, a.getItemAllocations());
    }

    @Test
    void setCurrentDataDeleted_aliasMatchesGetter() {
        XLOBAllocation a = new XLOBAllocation();
        a.setCurrentDataDeleted(Boolean.FALSE);
        assertEquals(Boolean.FALSE, a.getIsCurrentDataDeleted());
    }

    @Test
    void equals_reflexive() {
        XLOBAllocation a = new XLOBAllocation();
        assertEquals(a, a);
    }

    @Test
    void equals_null_returnsFalse() {
        assertNotEquals(null, new XLOBAllocation());
    }

    @Test
    void equals_otherType_returnsFalse() {
        assertNotEquals("string", new XLOBAllocation());
    }

    @Test
    void equals_sameSiteAndXlob_isEqual() {
        FunctionalGroup fg = mock(FunctionalGroup.class);
        Site s = mock(Site.class);
        XLOBAllocation a = new XLOBAllocation();
        a.setXlob(fg); a.setSite(s);
        XLOBAllocation b = new XLOBAllocation();
        b.setXlob(fg); b.setSite(s);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_differentSite_notEqual() {
        FunctionalGroup fg = mock(FunctionalGroup.class);
        XLOBAllocation a = new XLOBAllocation();
        a.setXlob(fg); a.setSite(mock(Site.class));
        XLOBAllocation b = new XLOBAllocation();
        b.setXlob(fg); b.setSite(mock(Site.class));
        assertNotEquals(a, b);
    }

    @Test
    void equals_bothSitesNull_compareXlob() {
        FunctionalGroup fg = mock(FunctionalGroup.class);
        XLOBAllocation a = new XLOBAllocation();
        a.setXlob(fg);
        XLOBAllocation b = new XLOBAllocation();
        b.setXlob(fg);
        assertEquals(a, b);
    }

    @Test
    void equals_bothXlobNull_compareSite() {
        Site s = mock(Site.class);
        XLOBAllocation a = new XLOBAllocation();
        a.setSite(s);
        XLOBAllocation b = new XLOBAllocation();
        b.setSite(s);
        assertEquals(a, b);
    }

    @Test
    void hashCode_consistentForEmptyInstance() {
        assertEquals(new XLOBAllocation().hashCode(), new XLOBAllocation().hashCode());
    }

    @Test
    void itemAllocationsCanBeEmpty() {
        XLOBAllocation a = new XLOBAllocation();
        a.setItemAllocations(Collections.emptySet());
        assertTrue(a.getItemAllocations().isEmpty());
    }
}
