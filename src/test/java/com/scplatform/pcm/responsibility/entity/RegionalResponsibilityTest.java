/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.responsibility.entity;

import com.scplatform.pcm.assignment.entity.Assignment;
import com.scplatform.pcm.assignment.entity.ItemAssignment;
import com.scplatform.pcm.site.entity.Site;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegionalResponsibilityTest {

    @Test
    void defaultConstructor() {
        RegionalResponsibility r = new RegionalResponsibility();
        assertNull(r.getRegionId());
        assertNull(r.getAssignment());
        assertNull(r.getSite());
    }

    @Test
    void parameterizedConstructor() {
        Site site = new Site();
        Assignment a = new ItemAssignment();
        RegionalResponsibility r = new RegionalResponsibility(site, a);
        assertSame(site, r.getSite());
        assertSame(a, r.getAssignment());
    }

    @Test
    void settersAndGetters() {
        RegionalResponsibility r = new RegionalResponsibility();
        Site site = new Site();
        Assignment a = new ItemAssignment();
        r.setRegionId(99L);
        r.setSite(site);
        r.setAssignment(a);

        assertEquals(99L, r.getRegionId());
        assertSame(site, r.getSite());
        assertSame(a, r.getAssignment());
    }

    @Test
    void equalsReflexive() {
        RegionalResponsibility r = new RegionalResponsibility();
        r.setRegionId(1L);
        assertEquals(r, r);
    }

    @Test
    void equalsNull() {
        RegionalResponsibility r = new RegionalResponsibility();
        assertNotEquals(null, r);
    }

    @Test
    void equalsDifferentType() {
        RegionalResponsibility r = new RegionalResponsibility();
        assertNotEquals("str", r);
    }

    @Test
    void equalsSameRegionId() {
        Long id = 42L;
        RegionalResponsibility a = new RegionalResponsibility();
        a.setRegionId(id);
        RegionalResponsibility b = new RegionalResponsibility();
        b.setRegionId(id);
        assertEquals(a, b);
    }

    @Test
    void equalsNullRegionIdReturnsFalse() {
        RegionalResponsibility a = new RegionalResponsibility();
        RegionalResponsibility b = new RegionalResponsibility();
        assertNotEquals(a, b);
    }

    @Test
    void toStringContainsRegionId() {
        Site site = new Site();
        site.setSiteName("SITE1");
        Assignment a = new ItemAssignment();
        a.setAssignmentKey(7L);
        RegionalResponsibility r = new RegionalResponsibility(site, a);
        r.setRegionId(100L);
        String s = r.toString();
        assertTrue(s.contains("100"));
        assertTrue(s.contains("SITE1"));
    }
}
