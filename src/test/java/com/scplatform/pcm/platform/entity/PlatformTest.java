/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.platform.entity;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PlatformTest {

    @Test
    void defaultConstructor_initializesNullsAndZero() {
        Platform p = new Platform();
        assertEquals(0L, p.getPlatformKey());
        assertNull(p.getPlatformName());
        assertNull(p.getPlatformType());
        assertNull(p.getPlatformDescription());
        assertNull(p.getPlatformExternalId());
        assertNull(p.getBusinessEntity());
    }

    @Test
    void settersAndGetters() {
        Platform p = new Platform();
        BusinessEntity be = mock(BusinessEntity.class);
        p.setPlatformKey(7L);
        p.setPlatformName("PLAT");
        p.setPlatformType("HW");
        p.setPlatformDescription("desc");
        p.setPlatformExternalId("ext-1");
        p.setBusinessEntity(be);

        assertEquals(7L, p.getPlatformKey());
        assertEquals("PLAT", p.getPlatformName());
        assertEquals("HW", p.getPlatformType());
        assertEquals("desc", p.getPlatformDescription());
        assertEquals("ext-1", p.getPlatformExternalId());
        assertSame(be, p.getBusinessEntity());
    }

    @Test
    void noPlatformConstantInitialized() {
        assertNotNull(Platform.NO_PLATFORM);
        assertEquals("NO_PLATFORM", Platform.NO_PLATFORM.getPlatformName());
        assertEquals("No platform", Platform.NO_PLATFORM.getPlatformDescription());
    }

    @Test
    void getPlatformsNaturalKeyAsJSON_containsPlatformName() {
        Platform p = new Platform();
        p.setPlatformName("P1");
        ObjectNode n = p.getPlatformsNaturalKeyAsJSON();
        assertNotNull(n);
        assertEquals("P1", n.get("platformName").asText());
    }

    @Test
    void equals_reflexive() {
        Platform p = new Platform();
        p.setPlatformName("A");
        assertEquals(p, p);
    }

    @Test
    void equals_null_returnsFalse() {
        Platform p = new Platform();
        assertNotEquals(null, p);
    }

    @Test
    void equals_otherType_returnsFalse() {
        Platform p = new Platform();
        assertNotEquals("string", p);
    }

    @Test
    void equals_sameFields_isEqual() {
        Platform a = new Platform();
        a.setPlatformName("X");
        a.setPlatformType("T");
        Platform b = new Platform();
        b.setPlatformName("X");
        b.setPlatformType("T");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_differentName_notEqual() {
        Platform a = new Platform();
        a.setPlatformName("X");
        Platform b = new Platform();
        b.setPlatformName("Y");
        assertNotEquals(a, b);
    }

    @Test
    void hashCode_basedOnPlatformName() {
        Platform a = new Platform();
        a.setPlatformName("Z");
        Platform b = new Platform();
        b.setPlatformName("Z");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void isSerializable() {
        assertTrue(java.io.Serializable.class.isAssignableFrom(Platform.class));
    }
}
