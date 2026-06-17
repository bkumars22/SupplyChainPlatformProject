/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.common.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class CompareDefnTest {

    @Test
    void defaultListsAreEmptyButNonNull() {
        CompareDefn d = new CompareDefn();
        assertNotNull(d.getUniqueCriteria());
        assertNotNull(d.getCompareCriteria());
        assertNotNull(d.getSortCriteria());
        assertTrue(d.getUniqueCriteria().isEmpty());
        assertTrue(d.getCompareCriteria().isEmpty());
        assertTrue(d.getSortCriteria().isEmpty());
    }

    @Test
    void settersAndGetters() {
        CompareDefn d = new CompareDefn();
        d.setUniqueCriteria(Arrays.asList("a"));
        d.setCompareCriteria(Arrays.asList("b", "c"));
        d.setSortCriteria(Arrays.asList("d", "e", "f"));

        assertEquals(1, d.getUniqueCriteria().size());
        assertEquals(2, d.getCompareCriteria().size());
        assertEquals(3, d.getSortCriteria().size());
        assertEquals("a", d.getUniqueCriteria().get(0));
    }

    @Test
    void publicFieldsRemainAccessible() {
        CompareDefn d = new CompareDefn();
        d.uniqueCriteria.add("u");
        assertEquals(1, d.getUniqueCriteria().size());
    }
}
