/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.site.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SiteFacilityTest {

    private SiteFacility build(Long id, String facility, Boolean dflt, Site site) {
        SiteFacility f = new SiteFacility();
        f.setId(id);
        f.setFacility(facility);
        f.setDefaultFacilityFlag(dflt);
        f.setSite(site);
        return f;
    }

    @Test
    void noArgsConstructor_allDefaultsAreNull() {
        SiteFacility f = new SiteFacility();
        assertNull(f.getId());
        assertNull(f.getFacility());
        assertNull(f.getDefaultFacilityFlag());
        assertNull(f.getSite());
    }

    @Test
    void settersAndGetters() {
        Site s = new Site(1L);
        SiteFacility f = build(7L, "FAC-1", Boolean.TRUE, s);
        assertEquals(7L, f.getId());
        assertEquals("FAC-1", f.getFacility());
        assertTrue(f.getDefaultFacilityFlag());
        assertSame(s, f.getSite());
    }

    @Test
    void equals_sameValues() {
        Site s = new Site(1L);
        SiteFacility a = build(1L, "F", Boolean.TRUE, s);
        SiteFacility b = build(1L, "F", Boolean.TRUE, s);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_reflexiveAndNullAndOtherClass() {
        SiteFacility f = new SiteFacility();
        assertEquals(f, f);
        assertNotEquals(null, f);
        assertNotEquals("x", f);
    }

    @Test
    void equals_differentFacility() {
        SiteFacility a = build(1L, "A", null, null);
        SiteFacility b = build(1L, "B", null, null);
        assertNotEquals(a, b);
    }

    @Test
    void equals_differentId() {
        SiteFacility a = build(1L, "F", null, null);
        SiteFacility b = build(2L, "F", null, null);
        assertNotEquals(a, b);
    }

    @Test
    void equals_differentDefaultFlag() {
        SiteFacility a = build(1L, "F", Boolean.TRUE, null);
        SiteFacility b = build(1L, "F", Boolean.FALSE, null);
        assertNotEquals(a, b);
    }

    @Test
    void equals_differentSite() {
        Site s1 = new Site(1L); s1.setSiteName("S1"); s1.setSiteType("REGION");
        Site s2 = new Site(2L); s2.setSiteName("S2"); s2.setSiteType("SITE");
        SiteFacility a = build(1L, "F", null, s1);
        SiteFacility b = build(1L, "F", null, s2);
        assertNotEquals(a, b);
    }

    @Test
    void hashCode_doesNotThrowOnAllNullFields() {
        assertDoesNotThrow(() -> new SiteFacility().hashCode());
    }
}
