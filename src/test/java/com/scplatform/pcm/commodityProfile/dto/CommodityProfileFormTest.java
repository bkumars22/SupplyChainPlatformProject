/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.commodityProfile.dto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.scplatform.pcm.searchframework.dto.SearchForm;

class CommodityProfileFormTest {

    @Test
    void noArgConstructorYieldsNullSelection() {
        CommodityProfileForm f = new CommodityProfileForm();
        assertNull(f.getSelectedPageKeys());
    }

    @Test
    void allArgConstructorSetsField() {
        String[] keys = {"a", "b"};
        CommodityProfileForm f = new CommodityProfileForm(keys);
        assertArrayEquals(keys, f.getSelectedPageKeys());
    }

    @Test
    void setterUpdatesField() {
        CommodityProfileForm f = new CommodityProfileForm();
        f.setSelectedPageKeys(new String[]{"x"});
        assertEquals(1, f.getSelectedPageKeys().length);
        assertEquals("x", f.getSelectedPageKeys()[0]);
    }

    @Test
    void extendsSearchForm() {
        assertNotNull(new CommodityProfileForm());
        assertEquals(true, SearchForm.class.isAssignableFrom(CommodityProfileForm.class));
    }
}
