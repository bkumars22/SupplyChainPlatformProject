/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.site.entity;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class SiteDetailsTest {

    private SiteDetails build(Long id, String mrp, Boolean costFlag) {
        SiteDetails d = new SiteDetails();
        d.setId(id);
        d.setMrpSite(mrp);
        d.setCostVisibleFlag(costFlag);
        return d;
    }

    @Test
    void noArgsConstructor_allDefaultsAreNull() {
        SiteDetails d = new SiteDetails();
        assertNull(d.getId());
        assertNull(d.getMrpSite());
        assertNull(d.getIntefaceSite());
        assertNull(d.getSiteOwner());
        assertNull(d.getEolLastUpdateOn());
        assertNull(d.getCostNegotiationFlag());
        assertNull(d.getDemandForCastFlag());
        assertNull(d.getTamVisibleFlag());
        assertNull(d.getCostVisibleFlag());
    }

    @Test
    void settersAndGetters() {
        Timestamp ts = new Timestamp(1_000L);
        SiteDetails d = new SiteDetails();
        d.setId(1L);
        d.setMrpSite("MRP-A");
        d.setIntefaceSite("IFC-A");
        d.setSiteOwner("owner");
        d.setEolLastUpdateOn(ts);
        d.setCostNegotiationFlag(true);
        d.setDemandForCastFlag(false);
        d.setTamVisibleFlag(true);
        d.setTamProcessingFlag(false);
        d.setCostVisibleFlag(true);
        d.setCostUpdateFlag(false);
        d.setTamUpdateFlag(true);
        d.setSiteState(false);
        d.setIsODMFlag(true);
        d.setDiscpSiteDescription("disc");
        d.setMrpSiteLegacy("legacy");
        d.setSitePurpose("PROD");

        assertEquals(1L, d.getId());
        assertEquals("MRP-A", d.getMrpSite());
        assertEquals("IFC-A", d.getIntefaceSite());
        assertEquals("owner", d.getSiteOwner());
        assertEquals(ts, d.getEolLastUpdateOn());
        assertTrue(d.getCostNegotiationFlag());
        assertFalse(d.getDemandForCastFlag());
        assertTrue(d.getTamVisibleFlag());
        assertFalse(d.getTamProcessingFlag());
        assertTrue(d.getCostVisibleFlag());
        assertFalse(d.getCostUpdateFlag());
        assertTrue(d.getTamUpdateFlag());
        assertFalse(d.getSiteState());
        assertTrue(d.getIsODMFlag());
        assertEquals("disc", d.getDiscpSiteDescription());
        assertEquals("legacy", d.getMrpSiteLegacy());
        assertEquals("PROD", d.getSitePurpose());
    }

    @Test
    void equals_sameValues() {
        SiteDetails a = build(1L, "M", Boolean.TRUE);
        SiteDetails b = build(1L, "M", Boolean.TRUE);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_reflexiveAndNullAndOtherClass() {
        SiteDetails a = new SiteDetails();
        assertEquals(a, a);
        assertNotEquals(null, a);
        assertNotEquals("text", a);
    }

    @Test
    void equals_differentMrpSite() {
        SiteDetails a = build(1L, "A", null);
        SiteDetails b = build(1L, "B", null);
        assertNotEquals(a, b);
    }

    @Test
    void equals_differentId() {
        SiteDetails a = build(1L, "M", null);
        SiteDetails b = build(2L, "M", null);
        assertNotEquals(a, b);
    }

    @Test
    void equals_differentCostVisibleFlag() {
        SiteDetails a = build(1L, "M", Boolean.TRUE);
        SiteDetails b = build(1L, "M", Boolean.FALSE);
        assertNotEquals(a, b);
    }

    @Test
    void hashCode_doesNotThrowOnAllNullFields() {
        assertDoesNotThrow(() -> new SiteDetails().hashCode());
    }
}
