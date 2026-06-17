/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class BomGroupLinkTest {

    @Test
    void noArgConstructorLeavesFieldsNull() {
        BomGroupLink link = new BomGroupLink();
        assertNull(link.getBom());
        assertNull(link.getBomGroup());
    }

    @Test
    void twoArgConstructorAssignsFields() {
        Bom bom = new Bom();
        BomGroup group = new BomGroup();
        BomGroupLink link = new BomGroupLink(bom, group);
        assertSame(bom, link.getBom());
        assertSame(group, link.getBomGroup());
    }

    @Test
    void settersUpdateFields() {
        BomGroupLink link = new BomGroupLink();
        Bom bom = new Bom();
        BomGroup group = new BomGroup();
        link.setBom(bom);
        link.setBomGroup(group);
        assertSame(bom, link.getBom());
        assertSame(group, link.getBomGroup());
    }

    @Test
    void equalsReflexiveAndRejectsOtherTypes() {
        BomGroupLink link = new BomGroupLink();
        assertEquals(link, link);
        assertNotEquals(link, null);
        assertNotEquals(link, "string");
    }
}
