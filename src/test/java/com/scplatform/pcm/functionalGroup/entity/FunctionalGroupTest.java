/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.functionalGroup.entity;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class FunctionalGroupTest {

    @Test
    void defaultConstructor() {
        FunctionalGroup fg = new FunctionalGroup();
        assertNull(fg.getFunctionalGroupId());
        assertNull(fg.getName());
        assertNull(fg.getDescription());
        assertNull(fg.getType());
        assertNull(fg.getStatus());
    }

    @Test
    void settersAndGetters() {
        FunctionalGroup fg = new FunctionalGroup();
        fg.setFunctionalGroupId(1L);
        fg.setFunctionalGroupExternalId("EXT-001");
        fg.setName("FG-TEST");
        fg.setDescription("Test FG");
        fg.setType(FunctionalGroup.CFG);
        fg.setStatus("ACTIVE");
        fg.setFgPlatform("PLATFORM1");
        fg.setCreatedBy("admin");
        fg.setLastChangedBy("user1");
        fg.setExtractFlag("Y");
        fg.setRollOverCount(3L);
        fg.setAliasName("alias1");

        Date created = new Date(1000L);
        Date changed = new Date(2000L);
        fg.setCreatedOn(created);
        fg.setLastChangedOn(changed);

        assertEquals(1L, fg.getFunctionalGroupId());
        assertEquals("EXT-001", fg.getFunctionalGroupExternalId());
        assertEquals("FG-TEST", fg.getName());
        assertEquals("Test FG", fg.getDescription());
        assertEquals(FunctionalGroup.CFG, fg.getType());
        assertEquals("ACTIVE", fg.getStatus());
        assertEquals("PLATFORM1", fg.getFgPlatform());
        assertEquals("admin", fg.getCreatedBy());
        assertEquals("user1", fg.getLastChangedBy());
        assertEquals("Y", fg.getExtractFlag());
        assertEquals(3L, fg.getRollOverCount());
        assertEquals("alias1", fg.getAliasName());
        assertEquals(created, fg.getCreatedOn());
        assertEquals(changed, fg.getLastChangedOn());
    }

    @Test
    void constantsAreCorrect() {
        assertEquals("CFG", FunctionalGroup.CFG);
        assertEquals("EM", FunctionalGroup.EM);
        assertEquals("NFG", FunctionalGroup.NFG);
        assertEquals("XLOB", FunctionalGroup.XLOB);
        assertEquals("OneToMany", FunctionalGroup.ONE_TO_MANY);
        assertEquals("ManyToMany", FunctionalGroup.MANY_TO_MANY);
    }

    @Test
    void equalsReflexive() {
        FunctionalGroup fg = new FunctionalGroup();
        fg.setFunctionalGroupId(1L);
        assertEquals(fg, fg);
    }

    @Test
    void equalsNull() {
        FunctionalGroup fg = new FunctionalGroup();
        assertNotEquals(null, fg);
    }

    @Test
    void equalsDifferentType() {
        FunctionalGroup fg = new FunctionalGroup();
        assertNotEquals("string", fg);
    }

    @Test
    void equalsSameId() {
        FunctionalGroup a = new FunctionalGroup();
        a.setFunctionalGroupId(5L);
        FunctionalGroup b = new FunctionalGroup();
        b.setFunctionalGroupId(5L);
        assertEquals(a, b);
    }

    @Test
    void notEqualsDifferentId() {
        FunctionalGroup a = new FunctionalGroup();
        a.setFunctionalGroupId(1L);
        FunctionalGroup b = new FunctionalGroup();
        b.setFunctionalGroupId(2L);
        assertNotEquals(a, b);
    }

    @Test
    void hashCodeConsistency() {
        FunctionalGroup a = new FunctionalGroup();
        a.setFunctionalGroupId(5L);
        FunctionalGroup b = new FunctionalGroup();
        b.setFunctionalGroupId(5L);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringContainsName() {
        FunctionalGroup fg = new FunctionalGroup();
        fg.setName("MY-FG");
        String s = fg.toString();
        assertTrue(s.contains("MY-FG"));
    }

    @Test
    void equalsNullIdBothNull() {
        FunctionalGroup a = new FunctionalGroup();
        FunctionalGroup b = new FunctionalGroup();
        assertEquals(a, b);
    }

    @Test
    void equalsOneNullId() {
        FunctionalGroup a = new FunctionalGroup();
        FunctionalGroup b = new FunctionalGroup();
        b.setFunctionalGroupId(1L);
        assertNotEquals(a, b);
    }
}
