/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.commodityProfile.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class RoleCommodityProfileMappingIdTest {

    @Test
    void noArgConstructorLeavesFieldsNull() {
        RoleCommodityProfileMappingId id = new RoleCommodityProfileMappingId();
        assertNull(id.getRole());
        assertNull(id.getCommodityProfile());
        assertNull(id.getBusinessEntity());
    }

    @Test
    void allArgConstructorAssignsAllFields() {
        RoleCommodityProfileMappingId id = new RoleCommodityProfileMappingId(1L, 2L, 3L);
        assertEquals(1L, id.getRole());
        assertEquals(2L, id.getCommodityProfile());
        assertEquals(3L, id.getBusinessEntity());
    }

    @Test
    void settersUpdateValues() {
        RoleCommodityProfileMappingId id = new RoleCommodityProfileMappingId();
        id.setRole(11L);
        id.setCommodityProfile(22L);
        id.setBusinessEntityKey(33L);
        assertEquals(11L, id.getRole());
        assertEquals(22L, id.getCommodityProfile());
        assertEquals(33L, id.getBusinessEntity());
    }

    @Test
    void equalsAndHashCodeWorkOnAllFields() {
        RoleCommodityProfileMappingId a = new RoleCommodityProfileMappingId(1L, 2L, 3L);
        RoleCommodityProfileMappingId b = new RoleCommodityProfileMappingId(1L, 2L, 3L);
        RoleCommodityProfileMappingId c = new RoleCommodityProfileMappingId(9L, 2L, 3L);

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "x");
    }
}
