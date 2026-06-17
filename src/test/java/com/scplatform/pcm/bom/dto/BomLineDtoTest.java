/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class BomLineDtoTest {

    @Test
    void noArgConstructorInitialisesCollections() {
        BomLineDto dto = new BomLineDto();
        assertEquals(0, dto.getLevel());
        assertNull(dto.getLine());
        assertNotNull(dto.getChildList());
        assertTrue(dto.getChildList().isEmpty());
        assertNotNull(dto.getAttritionRates());
        assertTrue(dto.getAttritionRates().isEmpty());
        assertNull(dto.getBomLineKey());
        assertNull(dto.getBomKey());
        assertNull(dto.getLineNumber());
    }

    @Test
    void allArgConstructorAssignsFields() {
        BomLineDto dto = new BomLineDto(2, null, Collections.emptyList(), new HashMap<>(),
                10L, 20L, 5, "1.0", "EA", "ACT", "2024-01-01", "2024-12-31");
        assertEquals(2, dto.getLevel());
        assertEquals(10L, dto.getBomLineKey());
        assertEquals(20L, dto.getBomKey());
        assertEquals(5, dto.getLineNumber());
        assertEquals("1.0", dto.getQuantity());
        assertEquals("EA", dto.getUnitOfMeasure());
        assertEquals("ACT", dto.getStatus());
        assertEquals("2024-01-01", dto.getEffectiveFromDate());
        assertEquals("2024-12-31", dto.getEffectiveToDate());
    }

    @Test
    void settersUpdateFields() {
        BomLineDto dto = new BomLineDto();
        dto.setLevel(3);
        dto.setBomLineKey(99L);
        dto.setBomKey(100L);
        dto.setLineNumber(1);
        dto.setQuantity("2.5");
        dto.setUnitOfMeasure("PC");
        dto.setStatus("S");
        dto.setEffectiveFromDate("from");
        dto.setEffectiveToDate("to");
        dto.setChildList(Arrays.asList(new BomLineDto()));

        assertEquals(3, dto.getLevel());
        assertEquals(99L, dto.getBomLineKey());
        assertEquals(100L, dto.getBomKey());
        assertEquals(1, dto.getLineNumber());
        assertEquals("2.5", dto.getQuantity());
        assertEquals("PC", dto.getUnitOfMeasure());
        assertEquals("S", dto.getStatus());
        assertEquals("from", dto.getEffectiveFromDate());
        assertEquals("to", dto.getEffectiveToDate());
        assertEquals(1, dto.getChildList().size());
    }

    @Test
    void lombokDataEqualsHashCodeWork() {
        BomLineDto a = new BomLineDto();
        a.setBomLineKey(1L);
        BomLineDto b = new BomLineDto();
        b.setBomLineKey(1L);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }

    @Test
    void attritionRatesMapMutableViaSetter() {
        BomLineDto dto = new BomLineDto();
        Map<com.scplatform.pcm.bom.entity.PcmDefectType, com.scplatform.pcm.bom.entity.PcmBomLineAttritionRate> map = new HashMap<>();
        dto.setAttritionRates(map);
        assertEquals(map, dto.getAttritionRates());
    }
}
