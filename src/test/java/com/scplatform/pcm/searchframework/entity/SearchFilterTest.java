/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.scplatform.pcm.user.entity.Users;

class SearchFilterTest {

    @Test
    void noArgConstructorDefaults() {
        SearchFilter f = new SearchFilter();
        assertNull(f.getFilterKey());
        assertNull(f.getName());
        assertNull(f.getCreator());
        assertNull(f.getFilterType());
        assertFalse(f.getIsPublic());
        assertNotNull(f.getFilterValues());
        assertTrue(f.getFilterValues().isEmpty());
    }

    @Test
    void keyConstructorAssignsKey() {
        SearchFilter f = new SearchFilter(42L);
        assertEquals(42L, f.getFilterKey());
    }

    @Test
    void settersAndGetters() {
        SearchFilter f = new SearchFilter();
        Users u = new Users();
        f.setFilterKey(7L);
        f.setName("MyFilter");
        f.setIsPublic(true);
        f.setCreator(u);
        f.setFilterType("BOM");
        assertEquals(7L, f.getFilterKey());
        assertEquals("MyFilter", f.getName());
        assertTrue(f.getIsPublic());
        assertSame(u, f.getCreator());
        assertEquals("BOM", f.getFilterType());
    }

    @Test
    void addFilterValueScalar() {
        SearchFilter f = new SearchFilter();
        f.addFilterValue("name", "abc");
        assertEquals(1, f.getFilterValues().size());
    }

    @Test
    void addFilterValueArrayAddsAllNonNullNonEmptyEntries() {
        SearchFilter f = new SearchFilter();
        f.addFilterValue("col", new Object[]{"a", null, "", "b"});
        // null and empty string should be skipped
        assertEquals(2, f.getFilterValues().size());
    }

    @Test
    void clearFilterValuesEmptiesSet() {
        SearchFilter f = new SearchFilter();
        f.addFilterValue("c", "v");
        assertEquals(1, f.getFilterValues().size());
        f.clearFilterValues();
        assertTrue(f.getFilterValues().isEmpty());
    }

    @Test
    void setFilterValuesReplacesSet() {
        SearchFilter f = new SearchFilter();
        Set<SearchFilterValue> sfvs = new HashSet<>();
        sfvs.add(new SearchFilterValue("k", "v"));
        f.setFilterValues(sfvs);
        assertEquals(1, f.getFilterValues().size());
    }

    @Test
    void getFilterValueMapGroupsByFieldName() {
        SearchFilter f = new SearchFilter();
        f.addFilterValue("a", "1");
        f.addFilterValue("a", "2");
        f.addFilterValue("b", "x");
        Map<String, List<Object>> map = f.getFilterValueMap();
        assertEquals(2, map.size());
        assertEquals(2, map.get("a").size());
        assertEquals(1, map.get("b").size());
        assertEquals("x", map.get("b").get(0));
    }
}
