/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.site.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CCNSiteTest {

    private CCNSite build(Long id, String ccn, String global, String iface, Site site) {
        CCNSite c = new CCNSite();
        c.setId(id);
        c.setCcn(ccn);
        c.setGlobalRegion(global);
        c.setInterfaceRegion(iface);
        c.setSite(site);
        return c;
    }

    @Test
    void noArgsConstructor_allDefaultsAreNull() {
        CCNSite c = new CCNSite();
        assertNull(c.getId());
        assertNull(c.getCcn());
        assertNull(c.getGlobalRegion());
        assertNull(c.getInterfaceRegion());
        assertNull(c.getSite());
    }

    @Test
    void settersAndGetters() {
        Site site = new Site(1L);
        CCNSite c = build(2L, "CCN1", "GR", "IR", site);
        assertEquals(2L, c.getId());
        assertEquals("CCN1", c.getCcn());
        assertEquals("GR", c.getGlobalRegion());
        assertEquals("IR", c.getInterfaceRegion());
        assertSame(site, c.getSite());
    }

    @Test
    void equals_sameValues() {
        Site site = new Site(1L);
        CCNSite a = build(1L, "C", "G", "I", site);
        CCNSite b = build(1L, "C", "G", "I", site);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_reflexiveAndNullAndOtherClass() {
        CCNSite c = new CCNSite();
        assertEquals(c, c);
        assertNotEquals(null, c);
        assertNotEquals("x", c);
    }

    @Test
    void equals_differentCcn() {
        CCNSite a = build(1L, "A", null, null, null);
        CCNSite b = build(1L, "B", null, null, null);
        assertNotEquals(a, b);
    }

    @Test
    void equals_differentGlobalRegion() {
        CCNSite a = build(1L, "C", "G1", null, null);
        CCNSite b = build(1L, "C", "G2", null, null);
        assertNotEquals(a, b);
    }

    @Test
    void equals_differentInterfaceRegion() {
        CCNSite a = build(1L, "C", null, "I1", null);
        CCNSite b = build(1L, "C", null, "I2", null);
        assertNotEquals(a, b);
    }

    @Test
    void equals_differentId() {
        CCNSite a = build(1L, "C", null, null, null);
        CCNSite b = build(2L, "C", null, null, null);
        assertNotEquals(a, b);
    }

    @Test
    void equals_differentSite() {
        Site s1 = new Site(1L); s1.setSiteName("S1"); s1.setSiteType("REGION");
        Site s2 = new Site(2L); s2.setSiteName("S2"); s2.setSiteType("SITE");
        CCNSite a = build(1L, "C", null, null, s1);
        CCNSite b = build(1L, "C", null, null, s2);
        assertNotEquals(a, b);
    }

    @Test
    void hashCode_doesNotThrowOnAllNullFields() {
        assertDoesNotThrow(() -> new CCNSite().hashCode());
    }
}
