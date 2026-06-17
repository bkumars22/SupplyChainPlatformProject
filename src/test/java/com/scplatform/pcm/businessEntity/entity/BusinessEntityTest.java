/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.businessEntity.entity;

import com.scplatform.pcm.common.entity.Attribute;
import com.scplatform.pcm.contact.entity.Contact;
import com.scplatform.pcm.currency.entity.Currency;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessEntityTest {

    @Test
    void defaultConstructor() {
        BusinessEntity be = new BusinessEntity();
        assertNull(be.getBusinessEntityKey());
        assertNull(be.getBusinessEntityIdentifier());
        assertNull(be.getBusinessEntityName());
        assertEquals(1L, be.getBusinessEntityTypeKey());
        assertNotNull(be.getAttributes());
        assertNotNull(be.getAlternates());
        assertNotNull(be.getCurrencies());
        assertNotNull(be.getSites());
    }

    @Test
    void constructorWithKey() {
        BusinessEntity be = new BusinessEntity(42L);
        assertEquals(42L, be.getBusinessEntityKey());
    }

    @Test
    void settersAndGetters() {
        BusinessEntity be = new BusinessEntity();
        be.setBusinessEntityKey(1L);
        be.setBusinessEntityIdentifier("ID1");
        be.setBusinessEntityName("BE Name");
        be.setBusinessEntityDesc("Description");
        be.setBusinessEntityTypeKey(BusinessEntity.MFG_TYPE);
        be.setExternalId("EXT-1");

        assertEquals(1L, be.getBusinessEntityKey());
        assertEquals("ID1", be.getBusinessEntityIdentifier());
        assertEquals("BE Name", be.getBusinessEntityName());
        assertEquals("Description", be.getBusinessEntityDesc());
        assertEquals(BusinessEntity.MFG_TYPE, be.getBusinessEntityTypeKey());
        assertEquals("EXT-1", be.getExternalId());
    }

    @Test
    void typeConstants() {
        assertEquals(-1, BusinessEntity.UNKNOWN_TYPE);
        assertEquals(0, BusinessEntity.OPERATOR_TYPE);
        assertEquals(1, BusinessEntity.ENTERPRISE_TYPE);
        assertEquals(2, BusinessEntity.MFG_TYPE);
        assertEquals(3, BusinessEntity.SUPPLIER_TYPE);
    }

    @Test
    void getTypeFromName() {
        assertEquals(BusinessEntity.MFG_TYPE, BusinessEntity.getTypeFromName("MANUFACTURER"));
        assertEquals(BusinessEntity.MFG_TYPE, BusinessEntity.getTypeFromName("manufacturer"));
        assertEquals(BusinessEntity.SUPPLIER_TYPE, BusinessEntity.getTypeFromName("SUPPLIER"));
        assertEquals(BusinessEntity.OPERATOR_TYPE, BusinessEntity.getTypeFromName("OPERATOR"));
        assertEquals(BusinessEntity.ENTERPRISE_TYPE, BusinessEntity.getTypeFromName("ENTERPRISE"));
        assertEquals(BusinessEntity.UNKNOWN_TYPE, BusinessEntity.getTypeFromName("UNKNOWN"));
    }

    @Test
    void getNameFromTypeKey() {
        assertEquals("MANUFACTURER", BusinessEntity.getNameFromTypeKey(BusinessEntity.MFG_TYPE));
        assertEquals("SUPPLIER", BusinessEntity.getNameFromTypeKey(BusinessEntity.SUPPLIER_TYPE));
        assertEquals("OPERATOR", BusinessEntity.getNameFromTypeKey(BusinessEntity.OPERATOR_TYPE));
        assertEquals("ENTERPRISE", BusinessEntity.getNameFromTypeKey(BusinessEntity.ENTERPRISE_TYPE));
        assertNull(BusinessEntity.getNameFromTypeKey(BusinessEntity.UNKNOWN_TYPE));
    }

    @Test
    void businessEntityTypeName() {
        BusinessEntity be = new BusinessEntity();
        be.setBusinessEntityTypeKey(BusinessEntity.SUPPLIER_TYPE);
        assertEquals("SUPPLIER", be.getBusinessEntityTypeName());
    }

    @Test
    void addAttribute() {
        BusinessEntity be = new BusinessEntity();
        Attribute attr = new Attribute();
        attr.setAttrName("color");
        attr.setAttrValue("red");
        assertTrue(be.addAttribute(attr));
        assertFalse(be.addAttribute(attr));
        assertEquals("red", be.getAttribute("color"));
        assertNull(be.getAttribute("missing"));
    }

    @Test
    void addAlternate() {
        BusinessEntity be = new BusinessEntity();
        BusinessEntityAlternate alt = new BusinessEntityAlternate();
        alt.setBusinessEntityName("Alt1");
        assertTrue(be.addAlternate(alt));
        assertSame(be, alt.getBusinessEntity());
        assertFalse(be.addAlternate(alt));
    }

    @Test
    void addRemoveCurrency() {
        BusinessEntity be = new BusinessEntity();
        Currency c = new Currency();
        assertTrue(be.addCurrency(c));
        assertTrue(be.removeCurrency(c));
    }

    @Test
    void contactSetterGetter() {
        BusinessEntity be = new BusinessEntity();
        Contact contact = new Contact();
        be.setContact(contact);
        assertSame(contact, be.getContact());
    }

    @Test
    void equalsReflexive() {
        BusinessEntity be = new BusinessEntity();
        be.setBusinessEntityIdentifier("ID1");
        assertEquals(be, be);
    }

    @Test
    void equalsNull() {
        BusinessEntity be = new BusinessEntity();
        assertNotEquals(null, be);
    }

    @Test
    void equalsDifferentType() {
        BusinessEntity be = new BusinessEntity();
        assertNotEquals("string", be);
    }

    @Test
    void equalsSameIdentifierAndType() {
        BusinessEntity a = new BusinessEntity();
        a.setBusinessEntityIdentifier("ID1");
        a.setBusinessEntityTypeKey(BusinessEntity.SUPPLIER_TYPE);

        BusinessEntity b = new BusinessEntity();
        b.setBusinessEntityIdentifier("ID1");
        b.setBusinessEntityTypeKey(BusinessEntity.SUPPLIER_TYPE);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void compareToOrders() {
        BusinessEntity a = new BusinessEntity();
        a.setBusinessEntityIdentifier("AAA");
        a.setBusinessEntityName("A");
        BusinessEntity b = new BusinessEntity();
        b.setBusinessEntityIdentifier("BBB");
        b.setBusinessEntityName("B");

        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    @Test
    void toStringContainsIdentifier() {
        BusinessEntity be = new BusinessEntity();
        be.setBusinessEntityIdentifier("MY-ID");
        be.setBusinessEntityName("MY-NAME");
        String s = be.toString();
        assertTrue(s.contains("MY-ID"));
        assertTrue(s.contains("MY-NAME"));
    }

    @Test
    void getNaturalKeyAsJSON() {
        BusinessEntity be = new BusinessEntity();
        be.setBusinessEntityIdentifier("ID1");
        be.setBusinessEntityTypeKey(BusinessEntity.SUPPLIER_TYPE);
        var json = be.getNaturalKeyAsJSON();
        assertNotNull(json);
        assertEquals("ID1", json.get("identifier").asText());
        assertEquals("SUPPLIER", json.get("type").asText());
    }

    @Test
    void getCurrentStateAsJSON() {
        BusinessEntity be = new BusinessEntity();
        be.setBusinessEntityIdentifier("ID1");
        be.setBusinessEntityDesc("desc");
        be.setBusinessEntityTypeKey(BusinessEntity.MFG_TYPE);
        be.setExternalId("EXT-1");
        var json = be.getCurrentStateAsJSON();
        assertEquals("ID1", json.get("identifier").asText());
        assertEquals("desc", json.get("description").asText());
        assertEquals("MANUFACTURER", json.get("type").asText());
        assertEquals("EXT-1", json.get("externalId").asText());
    }

    @Test
    void nameSorterComparator() {
        BusinessEntity a = new BusinessEntity();
        a.setBusinessEntityName("AAA");
        BusinessEntity b = new BusinessEntity();
        b.setBusinessEntityName("BBB");

        BusinessEntity.NameSorter sorter = new BusinessEntity.NameSorter();
        assertTrue(sorter.compare(a, b) < 0);
        assertTrue(sorter.compare(b, a) > 0);
        assertEquals(0, sorter.compare(a, a));
    }
}
