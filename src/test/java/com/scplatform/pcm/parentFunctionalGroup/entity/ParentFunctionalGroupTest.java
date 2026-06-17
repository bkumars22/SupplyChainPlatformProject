/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.parentFunctionalGroup.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ParentFunctionalGroupTest {

    @Test
    void defaultConstructor() {
        ParentFunctionalGroup pfg = new ParentFunctionalGroup();
        assertNull(pfg.getParentFunctionalGroupId());
        assertNull(pfg.getName());
        assertNull(pfg.getDescription());
        assertNull(pfg.getType());
        assertNull(pfg.getPurpose());
    }

    @Test
    void settersAndGetters() {
        ParentFunctionalGroup pfg = new ParentFunctionalGroup();
        LocalDateTime now = LocalDateTime.now();

        pfg.setParentFunctionalGroupId(10L);
        pfg.setName("PFG-TEST");
        pfg.setDescription("A parent FG");
        pfg.setType("AGGREGATION");
        pfg.setPurpose(ParentFunctionalGroup.PURPOSE_AGGREGATION);
        pfg.setLastChangedOn(now);
        pfg.setLastChangedBy("user1");
        pfg.setCreatedOn(now);
        pfg.setCreatedBy("admin");

        assertEquals(10L, pfg.getParentFunctionalGroupId());
        assertEquals("PFG-TEST", pfg.getName());
        assertEquals("A parent FG", pfg.getDescription());
        assertEquals("AGGREGATION", pfg.getType());
        assertEquals(ParentFunctionalGroup.PURPOSE_AGGREGATION, pfg.getPurpose());
        assertEquals(now, pfg.getLastChangedOn());
        assertEquals("user1", pfg.getLastChangedBy());
        assertEquals(now, pfg.getCreatedOn());
        assertEquals("admin", pfg.getCreatedBy());
    }

    @Test
    void purposeConstants() {
        assertEquals("MASSUPDATE", ParentFunctionalGroup.PURPOSE_MASS_UPDATE);
        assertEquals("AGGREGATION", ParentFunctionalGroup.PURPOSE_AGGREGATION);
        assertEquals("ALL", ParentFunctionalGroup.PURPOSE_ALL);
        assertEquals("", ParentFunctionalGroup.PURPOSE_NONE);
    }

    @Test
    void equalsReflexive() {
        ParentFunctionalGroup pfg = new ParentFunctionalGroup();
        pfg.setName("PFG1");
        assertEquals(pfg, pfg);
    }

    @Test
    void equalsNull() {
        ParentFunctionalGroup pfg = new ParentFunctionalGroup();
        assertNotEquals(null, pfg);
    }

    @Test
    void equalsSameFields() {
        LocalDateTime now = LocalDateTime.now();
        ParentFunctionalGroup a = new ParentFunctionalGroup();
        a.setParentFunctionalGroupId(1L);
        a.setName("PFG1");
        a.setDescription("desc");
        a.setType("T1");
        a.setPurpose("P1");
        a.setLastChangedOn(now);
        a.setLastChangedBy("u1");
        a.setCreatedOn(now);
        a.setCreatedBy("admin");

        ParentFunctionalGroup b = new ParentFunctionalGroup();
        b.setParentFunctionalGroupId(1L);
        b.setName("PFG1");
        b.setDescription("desc");
        b.setType("T1");
        b.setPurpose("P1");
        b.setLastChangedOn(now);
        b.setLastChangedBy("u1");
        b.setCreatedOn(now);
        b.setCreatedBy("admin");

        assertEquals(a, b);
    }

    @Test
    void toStringContainsName() {
        ParentFunctionalGroup pfg = new ParentFunctionalGroup();
        pfg.setName("MY-PFG");
        String s = pfg.toString();
        assertTrue(s.contains("MY-PFG"));
    }

    @Test
    void getPfgNaturalKeyAsJSON() {
        ParentFunctionalGroup pfg = new ParentFunctionalGroup();
        pfg.setName("JSON-PFG");
        var json = pfg.getPfgNaturalKeyAsJSON();
        assertNotNull(json);
        assertEquals("JSON-PFG", json.get("parentName").asText());
    }
}
