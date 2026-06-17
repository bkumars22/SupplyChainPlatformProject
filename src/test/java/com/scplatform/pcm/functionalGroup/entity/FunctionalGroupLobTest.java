/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.functionalGroup.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionalGroupLobTest {

    @Test
    void defaultConstructor() {
        FunctionalGroupLob lob = new FunctionalGroupLob();
        assertEquals(0L, lob.getId());
        assertNull(lob.getLobValue());
        assertNull(lob.getFunctionalGroup());
    }

    @Test
    void constructorWithFunctionalGroup() {
        FunctionalGroup fg = new FunctionalGroup();
        fg.setName("FG1");
        FunctionalGroupLob lob = new FunctionalGroupLob(fg);
        assertSame(fg, lob.getFunctionalGroup());
    }

    @Test
    void constructorWithFunctionalGroupAndValue() {
        FunctionalGroup fg = new FunctionalGroup();
        FunctionalGroupLob lob = new FunctionalGroupLob(fg, "myLobValue");
        assertSame(fg, lob.getFunctionalGroup());
        assertEquals("myLobValue", lob.getLobValue());
    }

    @Test
    void settersAndGetters() {
        FunctionalGroupLob lob = new FunctionalGroupLob();
        lob.setId(99L);
        lob.setLobValue("lobData");
        FunctionalGroup fg = new FunctionalGroup();
        lob.setFunctionalGroup(fg);

        assertEquals(99L, lob.getId());
        assertEquals("lobData", lob.getLobValue());
        assertSame(fg, lob.getFunctionalGroup());
    }
}
