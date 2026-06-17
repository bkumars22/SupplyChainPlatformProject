/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.site.entity;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SiteTest {

    @Test
    void typeConstants() {
        assertEquals("GLOBAL", Site.GLOBAL_TYPE);
        assertEquals("REGION", Site.REGION_TYPE);
        assertEquals("SITE", Site.SITE_TYPE);
        assertEquals("CCN", Site.CCN_TYPE);
    }

    @Test
    void noArgsConstructor_defaults() {
        Site s = new Site();
        assertNull(s.getSiteKey());
        assertNull(s.getBusinessEntity());
        assertNull(s.getParentSite());
        assertNotNull(s.getCcnSites());
        assertTrue(s.getCcnSites().isEmpty());
        assertNotNull(s.getSiteFacilities());
        assertTrue(s.getSiteFacilities().isEmpty());
    }

    @Test
    void keyConstructor_setsKey() {
        Site s = new Site(123L);
        assertEquals(123L, s.getSiteKey());
    }

    @Test
    void getLevel_rootIsZero() {
        Site root = new Site();
        assertEquals(0, root.getLevel());
    }

    @Test
    void getLevel_childIncrementsParent() throws Exception {
        Site root = new Site();
        Site child = new Site();
        child.setParentSite(root);
        Site grand = new Site();
        grand.setParentSite(child);
        assertEquals(1, child.getLevel());
        assertEquals(2, grand.getLevel());
    }

    @Test
    void setParentSite_selfReferenceThrows() {
        Site s = new Site(1L);
        Exception ex = assertThrows(Exception.class, () -> s.setParentSite(s));
        assertTrue(ex.getMessage().contains("Site cannot be parent of self"));
    }

    @Test
    void toString_containsKeyFields() {
        Site s = new Site();
        s.setSiteName("WEST");
        s.setSiteDescription("West region");
        s.setSiteType(Site.REGION_TYPE);
        String txt = s.toString();
        assertTrue(txt.contains("WEST"));
        assertTrue(txt.contains("West region"));
        assertTrue(txt.contains("REGION"));
    }

    @Test
    void equals_sameNameTypeAndBE() {
        BusinessEntity be = new BusinessEntity(1L);
        Site a = new Site(); a.setSiteName("X"); a.setSiteType("REGION"); a.setBusinessEntity(be);
        Site b = new Site(); b.setSiteName("X"); b.setSiteType("REGION"); b.setBusinessEntity(be);
        assertEquals(a, b);
    }

    @Test
    void equals_differentTypeNotEqual() {
        Site a = new Site(); a.setSiteName("X"); a.setSiteType("REGION");
        Site b = new Site(); b.setSiteName("X"); b.setSiteType("SITE");
        assertNotEquals(a, b);
    }

    @Test
    void equals_handlesNullAndOtherClass() {
        Site a = new Site();
        assertNotEquals(null, a);
        assertNotEquals("string", a);
        assertEquals(a, a);
    }

    @Test
    void hashCode_consistentForSameType() {
        Site a = new Site(); a.setSiteType("REGION");
        Site b = new Site(); b.setSiteType("REGION");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void getNaturalKeyAsJSON_nullBusinessEntity() {
        Site s = new Site();
        s.setSiteName("WW");
        ObjectNode node = s.getNaturalKeyAsJSON();
        assertEquals("WW", node.get("siteName").asText());
        assertTrue(node.get("business").isNull());
    }

    @Test
    void getNaturalKeyAsJSON_withBusinessEntity() {
        Site s = new Site();
        s.setSiteName("WW");
        BusinessEntity be = new BusinessEntity(1L);
        be.setBusinessEntityIdentifier("BE1");
        s.setBusinessEntity(be);
        ObjectNode node = s.getNaturalKeyAsJSON();
        assertEquals("WW", node.get("siteName").asText());
        assertNotNull(node.get("business"));
    }

    @Test
    void settersAndGetters_basicFields() {
        Site s = new Site();
        s.setSiteKey(5L);
        s.setSiteName("N1");
        s.setSiteDescription("D");
        s.setSiteType("SITE");
        s.setDefaultCurrencyCode("USD");
        assertEquals(5L, s.getSiteKey());
        assertEquals("N1", s.getSiteName());
        assertEquals("D", s.getSiteDescription());
        assertEquals("SITE", s.getSiteType());
        assertEquals("USD", s.getDefaultCurrencyCode());
    }
}
