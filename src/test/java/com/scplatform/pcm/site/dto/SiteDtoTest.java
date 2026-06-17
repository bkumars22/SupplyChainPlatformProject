/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.site.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SiteDtoTest {

    @Test
    void noArgsConstructor_allFieldsNull() {
        SiteDto dto = new SiteDto();
        assertNull(dto.getSiteKey());
        assertNull(dto.getSiteDescription());
        assertNull(dto.getSiteType());
        assertNull(dto.getStatus());
        assertNull(dto.getBusinessEntityKey());
        assertNull(dto.getRegion());
    }

    @Test
    void twoArgsConstructor_setsKeyAndDescription() {
        SiteDto dto = new SiteDto(7L, "EAST");
        assertEquals(7L, dto.getSiteKey());
        assertEquals("EAST", dto.getSiteDescription());
    }

    @Test
    void settersAndGetters() {
        SiteDto dto = new SiteDto();
        dto.setSiteKey(1L);
        dto.setSiteDescription("desc");
        dto.setSiteType("REGION");
        dto.setStatus("A");
        dto.setBusinessEntityKey(99L);
        dto.setRegion("NA");

        assertEquals(1L, dto.getSiteKey());
        assertEquals("desc", dto.getSiteDescription());
        assertEquals("REGION", dto.getSiteType());
        assertEquals("A", dto.getStatus());
        assertEquals(99L, dto.getBusinessEntityKey());
        assertEquals("NA", dto.getRegion());
    }

    @Test
    void equalsAndHashCode_lombokGenerated() {
        SiteDto a = new SiteDto(1L, "X");
        SiteDto b = new SiteDto(1L, "X");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toString_containsKeyAndDescription() {
        SiteDto dto = new SiteDto(42L, "ABC");
        String s = dto.toString();
        assertTrue(s.contains("42"));
        assertTrue(s.contains("ABC"));
    }
}
