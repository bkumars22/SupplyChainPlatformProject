/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.commodityProfile.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.scplatform.pcm.item.entity.ItemCategory;

class CommodityProfileTest {

    @Test
    void defaultConstructorLeavesFieldsNull() {
        CommodityProfile cp = new CommodityProfile();
        assertNull(cp.getProfileId());
        assertNull(cp.getProfileName());
        assertNull(cp.getCompanyItemType());
        assertNull(cp.getIncludeExcludeCostRecord());
        assertNull(cp.getIncludeExcludeCostForecast());
        assertNull(cp.getIncludeExcludeRebate());
        assertNull(cp.getIncludeExcludeItem());
        assertNull(cp.getIncludeExcludeBOM());
        assertNull(cp.getIncludeExcludeTAM());
        assertNull(cp.getIncludeExcludePriceTAM());
        assertNull(cp.getItemCategory());
        assertNull(cp.getCostTypes());
    }

    @Test
    void idConstructorAssignsProfileId() {
        CommodityProfile cp = new CommodityProfile(42L);
        assertEquals(42L, cp.getProfileId());
    }

    @Test
    void allSettersStoreValues() {
        CommodityProfile cp = new CommodityProfile();
        ItemCategory cat = new ItemCategory();
        Set<CommodityProfileCostType> cts = new HashSet<>();
        cts.add(new CommodityProfileCostType(1L));

        cp.setProfileId(7L);
        cp.setProfileName("name");
        cp.setCompanyItemType("type");
        cp.setIncludeExcludeCostRecord("INCLUDE");
        cp.setIncludeExcludeCostForecast("EXCLUDE");
        cp.setIncludeExcludeRebate("INCLUDE");
        cp.setIncludeExcludeItem("EXCLUDE");
        cp.setIncludeExcludeBOM("INCLUDE");
        cp.setIncludeExcludeTAM("EXCLUDE");
        cp.setIncludeExcludePriceTAM("INCLUDE");
        cp.setItemCategory(cat);
        cp.setCostTypes(cts);

        assertEquals(7L, cp.getProfileId());
        assertEquals("name", cp.getProfileName());
        assertEquals("type", cp.getCompanyItemType());
        assertEquals("INCLUDE", cp.getIncludeExcludeCostRecord());
        assertEquals("EXCLUDE", cp.getIncludeExcludeCostForecast());
        assertEquals("INCLUDE", cp.getIncludeExcludeRebate());
        assertEquals("EXCLUDE", cp.getIncludeExcludeItem());
        assertEquals("INCLUDE", cp.getIncludeExcludeBOM());
        assertEquals("EXCLUDE", cp.getIncludeExcludeTAM());
        assertEquals("INCLUDE", cp.getIncludeExcludePriceTAM());
        assertSame(cat, cp.getItemCategory());
        assertEquals(1, cp.getCostTypes().size());
    }

    @Test
    void equalsAndHashCodeUseProfileIdOnly() {
        CommodityProfile a = new CommodityProfile(5L);
        CommodityProfile b = new CommodityProfile(5L);
        CommodityProfile c = new CommodityProfile(6L);
        CommodityProfile nullId = new CommodityProfile();

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, nullId);
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
        assertEquals(nullId, new CommodityProfile());
        assertNotNull(Integer.valueOf(nullId.hashCode()));
        // Same-id different content still equal
        b.setProfileName("other");
        assertTrue(a.equals(b));
    }

    @Test
    void emptyCostTypesSetIsRetained() {
        CommodityProfile cp = new CommodityProfile();
        cp.setCostTypes(Collections.emptySet());
        assertEquals(0, cp.getCostTypes().size());
    }
}
