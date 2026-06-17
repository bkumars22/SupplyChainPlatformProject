/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SearchParameterSelectTest {

    @Test
    void defaultsAreSingleSelect() {
        SearchParameterSelect s = new SearchParameterSelect("n", "l");
        assertEquals("SINGLESELECT", s.getType());
        assertFalse(s.getMultiSelect());
        assertFalse(s.getMultiValue());
        assertFalse(s.isValueArray());
        assertNotNull(s.getSelectValues());
        assertTrue(s.getSelectValues().isEmpty());
    }

    @Test
    void setMultiSelectChangesType() {
        SearchParameterSelect s = new SearchParameterSelect("n", "l");
        assertSame(s, s.setMultiSelect(true));
        assertTrue(s.getMultiSelect());
        assertEquals("MULTISELECT", s.getType());
        assertTrue(s.isValueArray());
    }

    @Test
    void setMultiValue() {
        SearchParameterSelect s = new SearchParameterSelect("n", "l");
        assertSame(s, s.setMultiValue(true));
        assertTrue(s.getMultiValue());
    }

    @Test
    void addSelectValueStoresMappingValueToLabel() {
        SearchParameterSelect s = new SearchParameterSelect("n", "l");
        s.addSelectValue("LabelA", "valA");
        s.addSelectValue("LabelB", "valB");
        // Map is value -> label per implementation
        assertEquals("LabelA", s.getSelectValues().get("valA"));
        assertEquals("LabelB", s.getSelectValues().get("valB"));
        assertEquals(2, s.getSelectValueEntries().size());
    }
}
