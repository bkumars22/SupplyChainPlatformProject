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

class UserCommodityProfileFormTest {

    @Test
    void noArgConstructorYieldsNullSelection() {
        UserCommodityProfileForm f = new UserCommodityProfileForm();
        assertNull(f.getSelectedPageKeys());
    }

    @Test
    void allArgConstructorAssignsKeys() {
        String[] keys = {"k1"};
        UserCommodityProfileForm f = new UserCommodityProfileForm(keys);
        assertArrayEquals(keys, f.getSelectedPageKeys());
    }

    @Test
    void setterUpdatesField() {
        UserCommodityProfileForm f = new UserCommodityProfileForm();
        f.setSelectedPageKeys(new String[]{"a", "b"});
        assertEquals(2, f.getSelectedPageKeys().length);
    }

    @Test
    void extendsSearchForm() {
        assertNotNull(new UserCommodityProfileForm());
        assertEquals(true, SearchForm.class.isAssignableFrom(UserCommodityProfileForm.class));
    }
}
