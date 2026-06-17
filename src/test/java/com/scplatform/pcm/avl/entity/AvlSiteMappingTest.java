/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.avl.entity;

import com.scplatform.pcm.site.entity.Site;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AvlSiteMappingTest {

    @Test
    void defaultConstructor() {
        AvlSiteMapping m = new AvlSiteMapping();
        assertNull(m.getId());
        assertNull(m.getAvl());
        assertNull(m.getSourceSite());
        assertNull(m.getDestSite());
    }

    @Test
    void settersAndGetters() {
        AvlSiteMapping m = new AvlSiteMapping();
        Avl avl = new Avl(1L);
        Site src = new Site();
        Site dst = new Site();

        m.setId(10L);
        m.setAvl(avl);
        m.setSourceSite(src);
        m.setDestSite(dst);

        assertEquals(10L, m.getId());
        assertSame(avl, m.getAvl());
        assertSame(src, m.getSourceSite());
        assertSame(dst, m.getDestSite());
    }

    @Test
    void equalsReflexive() {
        AvlSiteMapping m = new AvlSiteMapping();
        assertEquals(m, m);
    }

    @Test
    void equalsNull() {
        AvlSiteMapping m = new AvlSiteMapping();
        assertNotEquals(null, m);
    }

    @Test
    void equalsDifferentType() {
        AvlSiteMapping m = new AvlSiteMapping();
        assertNotEquals("string", m);
    }

    @Test
    void equalsSameFields() {
        Avl avl = new Avl(1L);
        Site src = new Site();
        Site dst = new Site();

        AvlSiteMapping a = new AvlSiteMapping();
        a.setAvl(avl);
        a.setSourceSite(src);
        a.setDestSite(dst);

        AvlSiteMapping b = new AvlSiteMapping();
        b.setAvl(avl);
        b.setSourceSite(src);
        b.setDestSite(dst);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
