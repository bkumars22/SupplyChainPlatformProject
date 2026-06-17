/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;

import org.junit.jupiter.api.Test;

class BomDtoTest {

    @Test
    void noArgConstructorLeavesFieldsNull() {
        BomDto dto = new BomDto();
        assertNull(dto.getBomKey());
        assertNull(dto.getBomNumber());
        assertNull(dto.getStatus());
        assertNull(dto.getEffectiveFrom());
        assertNull(dto.getEffectiveTo());
        assertNull(dto.getDescription());
        assertNull(dto.getItemKey());
        assertNull(dto.getBusinessEntityKey());
    }

    @Test
    void threeArgConstructorAssignsKeyFields() {
        BomDto dto = new BomDto(7L, "BOM-1", "ACTIVE");
        assertEquals(7L, dto.getBomKey());
        assertEquals("BOM-1", dto.getBomNumber());
        assertEquals("ACTIVE", dto.getStatus());
    }

    @Test
    void allSettersWork() {
        BomDto dto = new BomDto();
        Date from = new Date(1000L);
        Date to = new Date(2000L);
        dto.setBomKey(1L);
        dto.setBomNumber("BN");
        dto.setStatus("S");
        dto.setEffectiveFrom(from);
        dto.setEffectiveTo(to);
        dto.setDescription("desc");
        dto.setItemKey(11L);
        dto.setBusinessEntityKey(22L);

        assertEquals(1L, dto.getBomKey());
        assertEquals("BN", dto.getBomNumber());
        assertEquals("S", dto.getStatus());
        assertEquals(from, dto.getEffectiveFrom());
        assertEquals(to, dto.getEffectiveTo());
        assertEquals("desc", dto.getDescription());
        assertEquals(11L, dto.getItemKey());
        assertEquals(22L, dto.getBusinessEntityKey());
    }

    @Test
    void lombokEqualsAndHashCodeWork() {
        BomDto a = new BomDto(1L, "BN", "S");
        BomDto b = new BomDto(1L, "BN", "S");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }
}
