/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoTest {

    @Test
    void testNoArgsConstructor() {
        ItemDto dto = new ItemDto();
        assertNull(dto.getItemKey());
        assertNull(dto.getItemNumber());
        assertNull(dto.getItemId());
        assertNull(dto.getItemType());
        assertNull(dto.getDescription());
        assertNull(dto.getStatus());
        assertNull(dto.getBusinessEntityKey());
    }

    @Test
    void testKeyArgsConstructor() {
        ItemDto dto = new ItemDto(10L, "P001", "ID-1");
        assertEquals(10L, dto.getItemKey());
        assertEquals("P001", dto.getItemNumber());
        assertEquals("ID-1", dto.getItemId());
    }

    @Test
    void testSettersAndGetters() {
        ItemDto dto = new ItemDto();
        dto.setItemKey(5L);
        dto.setItemNumber("PN");
        dto.setItemId("ID");
        dto.setItemType("I");
        dto.setDescription("desc");
        dto.setStatus("ACTIVE");
        dto.setBusinessEntityKey(99L);

        assertEquals(5L, dto.getItemKey());
        assertEquals("PN", dto.getItemNumber());
        assertEquals("ID", dto.getItemId());
        assertEquals("I", dto.getItemType());
        assertEquals("desc", dto.getDescription());
        assertEquals("ACTIVE", dto.getStatus());
        assertEquals(99L, dto.getBusinessEntityKey());
    }

    @Test
    void testEqualsAndHashCodeAndToString() {
        ItemDto a = new ItemDto(1L, "PN", "ID");
        ItemDto b = new ItemDto(1L, "PN", "ID");
        ItemDto c = new ItemDto(2L, "PN", "ID");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
        assertEquals(a, a);
        assertNotNull(a.toString());
        assertTrue(a.toString().contains("PN"));
    }
}
