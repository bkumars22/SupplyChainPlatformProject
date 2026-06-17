/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.aml.entity;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.Item;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AmlTest {

    @Test
    void defaultConstructor() {
        Aml aml = new Aml();
        assertNotNull(aml.getAmlId());
        assertNull(aml.getPartStatusCode());
        assertNull(aml.getPreferredStatusCode());
        assertNull(aml.getDescription());
        assertFalse(aml.getCurrentFlag());
        assertFalse(aml.getDeleteFlag());
        assertNotNull(aml.getInsertDate());
    }

    @Test
    void constructorWithAmlId() {
        AmlId id = new AmlId();
        Aml aml = new Aml(id);
        assertSame(id, aml.getAmlId());
    }

    @Test
    void amlIdSetterGetter() {
        Aml aml = new Aml();
        AmlId id = new AmlId();
        aml.setAmlId(id);
        assertSame(id, aml.getAmlId());
    }

    @Test
    void partStatusCodeSetterGetter() {
        Aml aml = new Aml();
        aml.setPartStatusCode("ACTIVE");
        assertEquals("ACTIVE", aml.getPartStatusCode());
    }

    @Test
    void partStatusCodeOtherSetterGetter() {
        Aml aml = new Aml();
        aml.setPartStatusCodeOther("OTHER");
        assertEquals("OTHER", aml.getPartStatusCodeOther());
    }

    @Test
    void preferredStatusCodeSetterGetter() {
        Aml aml = new Aml();
        aml.setPreferredStatusCode("PREF");
        assertEquals("PREF", aml.getPreferredStatusCode());
    }

    @Test
    void descriptionSetterGetter() {
        Aml aml = new Aml();
        aml.setDescription("test desc");
        assertEquals("test desc", aml.getDescription());
    }

    @Test
    void currentFlagSetterGetter() {
        Aml aml = new Aml();
        aml.setCurrentFlag(true);
        assertTrue(aml.getCurrentFlag());
    }

    @Test
    void deleteFlagSetterGetter() {
        Aml aml = new Aml();
        aml.setDeleteFlag(true);
        assertTrue(aml.getDeleteFlag());
    }

    @Test
    void insertDateSetterGetter() {
        Aml aml = new Aml();
        Date d = new Date(12345L);
        aml.setInsertDate(d);
        assertEquals(d, aml.getInsertDate());
    }

    @Test
    void updateDateSetterGetter() {
        Aml aml = new Aml();
        Date d = new Date(99999L);
        aml.setUpdateDate(d);
        assertEquals(d, aml.getUpdateDate());
    }

    @Test
    void mfgBySetterGetter() {
        Aml aml = new Aml();
        aml.setMfgBy("ManufacturerName");
        assertEquals("ManufacturerName", aml.getMfgBy());
    }

    @Test
    void setMfgSetsNameInMfgBy() {
        Aml aml = new Aml();
        BusinessEntity be = new BusinessEntity();
        be.setBusinessEntityName("MFG Corp");
        aml.setMfg(be);
        assertEquals("MFG Corp", aml.getMfgBy());
        assertSame(be, aml.getMfg());
    }

    @Test
    void mfgItemDelegationThroughAmlId() {
        Aml aml = new Aml();
        Item mfgItem = new Item();
        mfgItem.setItemKey(5L);
        aml.setMfgItem(mfgItem);
        assertSame(mfgItem, aml.getMfgItem());
    }

    @Test
    void itemDelegationThroughAmlId() {
        Aml aml = new Aml();
        Item item = new Item();
        item.setItemKey(7L);
        aml.setItem(item);
        assertSame(item, aml.getItem());
    }

    @Test
    void equalsReflexive() {
        AmlId id = new AmlId();
        Aml aml = new Aml(id);
        assertEquals(aml, aml);
    }

    @Test
    void equalsNull() {
        Aml aml = new Aml();
        assertNotEquals(null, aml);
    }

    @Test
    void equalsDifferentType() {
        Aml aml = new Aml();
        assertNotEquals("string", aml);
    }

    @Test
    void equalsSameAmlId() {
        Item item = new Item();
        item.setItemKey(1L);
        Item mfg = new Item();
        mfg.setItemKey(2L);
        AmlId id1 = new AmlId(item, mfg);
        AmlId id2 = new AmlId(item, mfg);
        Aml a = new Aml(id1);
        Aml b = new Aml(id2);
        assertEquals(a, b);
    }

    @Test
    void hashCodeDelegatesToAmlId() {
        Item item = new Item();
        item.setItemKey(1L);
        Item mfg = new Item();
        mfg.setItemKey(2L);
        AmlId id = new AmlId(item, mfg);
        Aml aml = new Aml(id);
        assertEquals(id.hashCode(), aml.hashCode());
    }
}
