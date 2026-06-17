/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.commodityProfile.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.role.entity.Role;

class RoleCommodityProfileMappingTest {

    private RoleCommodityProfileMapping build(long roleKey, long profileId, long beKey) {
        RoleCommodityProfileMapping m = new RoleCommodityProfileMapping();
        Role r = new Role();
        r.setRoleKey(roleKey);
        m.setRole(r);
        m.setCommodityProfile(new CommodityProfile(profileId));
        m.setBusinessEntity(new BusinessEntity(beKey));
        return m;
    }

    @Test
    void defaultsAreNull() {
        RoleCommodityProfileMapping m = new RoleCommodityProfileMapping();
        assertNull(m.getRole());
        assertNull(m.getCommodityProfile());
        assertNull(m.getBusinessEntity());
    }

    @Test
    void settersStoreReferences() {
        Role r = new Role();
        r.setRoleKey(1L);
        CommodityProfile cp = new CommodityProfile(2L);
        BusinessEntity be = new BusinessEntity(3L);

        RoleCommodityProfileMapping m = new RoleCommodityProfileMapping();
        m.setRole(r);
        m.setCommodityProfile(cp);
        m.setBusinessEntity(be);

        assertSame(r, m.getRole());
        assertSame(cp, m.getCommodityProfile());
        assertSame(be, m.getBusinessEntity());
    }

    @Test
    void equalsAndHashCodeBasedOnAllThreeKeys() {
        RoleCommodityProfileMapping a = build(1, 2, 3);
        RoleCommodityProfileMapping b = build(1, 2, 3);
        RoleCommodityProfileMapping diff = build(9, 2, 3);

        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
        assertFalse(a.equals(diff));
        assertFalse(a.equals("string"));
    }
}
