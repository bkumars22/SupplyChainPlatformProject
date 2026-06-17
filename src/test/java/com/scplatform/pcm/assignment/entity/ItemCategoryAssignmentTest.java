/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.assignment.entity;

import com.scplatform.pcm.responsibility.entity.PcmResponsibility;
import com.scplatform.pcm.responsibility.entity.RegionalResponsibility;
import com.scplatform.pcm.site.entity.Site;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemCategoryAssignmentTest {

    @Test
    void defaultConstructor() {
        ItemCategoryAssignment a = new ItemCategoryAssignment();
        assertNull(a.getAssignmentKey());
        assertNull(a.getUserId());
        assertNull(a.getItemCategory());
    }

    @Test
    void isInstanceOfAssignment() {
        ItemCategoryAssignment a = new ItemCategoryAssignment();
        assertInstanceOf(Assignment.class, a);
    }

    @Test
    void categorySetterGetter() {
        ItemCategoryAssignment a = new ItemCategoryAssignment();
        com.scplatform.pcm.item.entity.ItemCategory cat = new com.scplatform.pcm.item.entity.ItemCategory();
        a.setItemCategory(cat);
        assertSame(cat, a.getItemCategory());
    }

    @Test
    void inheritedSettersAndGetters() {
        ItemCategoryAssignment a = new ItemCategoryAssignment();
        a.setAssignmentKey(50L);
        a.setUserId("icUser");
        a.setAssignmentCode("OWNER");
        a.setCurrentFlag(true);
        a.setRegion("US");

        Date from = new Date(1000L);
        Date to = new Date(2000L);
        a.setEffectiveFromDate(from);
        a.setEffectiveToDate(to);

        assertEquals(50L, a.getAssignmentKey());
        assertEquals("icUser", a.getUserId());
        assertEquals("OWNER", a.getAssignmentCode());
        assertTrue(a.getCurrentFlag());
        assertEquals("US", a.getRegion());
        assertEquals(from, a.getEffectiveFromDate());
        assertEquals(to, a.getEffectiveToDate());
    }

    @Test
    void responsibilitySetterGetter() {
        ItemCategoryAssignment a = new ItemCategoryAssignment();
        PcmResponsibility resp = new PcmResponsibility();
        a.setResponsibility(resp);
        assertSame(resp, a.getResponsibility());
    }

    @Test
    void siteSetterGetter() {
        ItemCategoryAssignment a = new ItemCategoryAssignment();
        Site site = new Site();
        a.setSite(site);
        assertSame(site, a.getSite());
    }

    @Test
    void addRegionalResponsibility() {
        ItemCategoryAssignment a = new ItemCategoryAssignment();
        assertNull(a.getRegionalResponsibility());
        RegionalResponsibility rr = new RegionalResponsibility();
        a.addRegionalResponsibility(rr);
        assertNotNull(a.getRegionalResponsibility());
        assertEquals(1, a.getRegionalResponsibility().size());
    }

    @Test
    void removeRegionalResponsibility() {
        ItemCategoryAssignment a = new ItemCategoryAssignment();
        RegionalResponsibility rr = new RegionalResponsibility();
        a.addRegionalResponsibility(rr);
        a.removeRegionalResponsibility(rr);
        assertTrue(a.getRegionalResponsibility().isEmpty());
    }

    @Test
    void removeRegionalResponsibilityNullSet() {
        ItemCategoryAssignment a = new ItemCategoryAssignment();
        // should not throw when set is null
        assertDoesNotThrow(() -> a.removeRegionalResponsibility(new RegionalResponsibility()));
    }

    @Test
    void setRegionalResponsibility() {
        ItemCategoryAssignment a = new ItemCategoryAssignment();
        Set<RegionalResponsibility> set = new HashSet<>();
        a.setRegionalResponsibility(set);
        assertSame(set, a.getRegionalResponsibility());
    }
}
