/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import com.scplatform.pcm.item.entity.Item;

class PcmBomLineAttritionRateIdTest {

    @Test
    void noArgConstructorLeavesFieldsNull() {
        PcmBomLineAttritionRateId id = new PcmBomLineAttritionRateId();
        assertNull(id.getBomLine());
        assertNull(id.getBomItem());
        assertNull(id.getDefectType());
    }

    @Test
    void allArgConstructorAssignsFields() {
        BomLine line = new BomLine();
        Item item = new Item();
        PcmDefectType dt = new PcmDefectType();
        PcmBomLineAttritionRateId id = new PcmBomLineAttritionRateId(line, item, dt);
        assertSame(line, id.getBomLine());
        assertSame(item, id.getBomItem());
        assertSame(dt, id.getDefectType());
    }

    @Test
    void settersUpdateFields() {
        PcmBomLineAttritionRateId id = new PcmBomLineAttritionRateId();
        BomLine line = new BomLine();
        Item item = new Item();
        PcmDefectType dt = new PcmDefectType();
        id.setBomLine(line);
        id.setBomItem(item);
        id.setDefectType(dt);
        assertSame(line, id.getBomLine());
        assertSame(item, id.getBomItem());
        assertSame(dt, id.getDefectType());
    }

    @Test
    void equalsReturnsFalseForNullAndOtherClass() {
        PcmBomLineAttritionRateId id = new PcmBomLineAttritionRateId();
        assertNotEquals(id, null);
        assertNotEquals(id, "string");
    }

    @Test
    void equalsAndHashCodeWorkOnAllNullFields() {
        PcmBomLineAttritionRateId a = new PcmBomLineAttritionRateId();
        PcmBomLineAttritionRateId b = new PcmBomLineAttritionRateId();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a, a);
    }
}
