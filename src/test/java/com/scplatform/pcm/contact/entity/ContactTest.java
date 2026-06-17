/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.contact.entity;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ContactTest {

    @Test
    void defaultConstructor() {
        Contact c = new Contact();
        assertNull(c.getContactKey());
        assertNull(c.getContactName());
        assertNull(c.getContactId());
        assertNull(c.getContactUniqueId());
        assertNull(c.getBusinessEntity());
    }

    @Test
    void keyConstructor() {
        Contact c = new Contact(99L);
        assertEquals(99L, c.getContactKey());
    }

    @Test
    void scalarSettersAndGetters() {
        Contact c = new Contact();
        c.setContactKey(1L);
        c.setContactName("name");
        c.setContactId("cid");
        c.setContactUniqueId("uid");
        c.setAddressL1("a1"); c.setAddressL2("a2"); c.setAddressL3("a3");
        c.setCity("city"); c.setRegion("r"); c.setCountryCode("US"); c.setCountry("USA");
        c.setPostalCode("12345"); c.setPostOfficeBox("PO");
        c.setTelephoneNumber("111"); c.setFaxNumber("222");
        c.setDepartment("dept"); c.setBusinessName("bn");
        c.setDuns("D"); c.setEmail("e@x"); c.setCompanyUri("uri");
        c.setStatus("ACTIVE"); c.setPartnerClassCode("PC");
        c.setPartnerClassCodeOther("PCO"); c.setPartnerSubclassCode("PSC");
        c.setDunsPlus4("DP4");

        assertEquals(1L, c.getContactKey());
        assertEquals("name", c.getContactName());
        assertEquals("cid", c.getContactId());
        assertEquals("uid", c.getContactUniqueId());
        assertEquals("a1", c.getAddressL1()); assertEquals("a2", c.getAddressL2());
        assertEquals("a3", c.getAddressL3()); assertEquals("city", c.getCity());
        assertEquals("r", c.getRegion()); assertEquals("US", c.getCountryCode());
        assertEquals("USA", c.getCountry()); assertEquals("12345", c.getPostalCode());
        assertEquals("PO", c.getPostOfficeBox()); assertEquals("111", c.getTelephoneNumber());
        assertEquals("222", c.getFaxNumber()); assertEquals("dept", c.getDepartment());
        assertEquals("bn", c.getBusinessName()); assertEquals("D", c.getDuns());
        assertEquals("e@x", c.getEmail()); assertEquals("uri", c.getCompanyUri());
        assertEquals("ACTIVE", c.getStatus()); assertEquals("PC", c.getPartnerClassCode());
        assertEquals("PCO", c.getPartnerClassCodeOther());
        assertEquals("PSC", c.getPartnerSubclassCode());
        assertEquals("DP4", c.getDunsPlus4());
    }

    @Test
    void businessEntityAndItemSetters() {
        Contact c = new Contact();
        BusinessEntity be = mock(BusinessEntity.class);
        Set<Object> sup = Collections.singleton(new Object());
        Set<Object> mfg = Collections.singleton(new Object());
        Set<Object> all = Collections.singleton(new Object());
        c.setBusinessEntity(be);
        c.setSetOfSupplierItem(sup);
        c.setSetOfMfgItem(mfg);
        c.setSetOfItem(all);
        assertSame(be, c.getBusinessEntity());
        assertSame(sup, c.getSetOfSupplierItem());
        assertSame(mfg, c.getSetOfMfgItem());
        assertSame(all, c.getSetOfItem());
    }

    @Test
    void equals_reflexive() {
        Contact c = new Contact();
        assertEquals(c, c);
    }

    @Test
    void equals_null_returnsFalse() {
        assertNotEquals(null, new Contact());
    }

    @Test
    void equals_otherType_returnsFalse() {
        assertNotEquals("string", new Contact());
    }

    @Test
    void equals_sameNameIdAndUniqueId_isEqual() {
        Contact a = new Contact(); a.setContactName("n"); a.setContactId("i"); a.setContactUniqueId("u");
        Contact b = new Contact(); b.setContactName("n"); b.setContactId("i"); b.setContactUniqueId("u");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_differentName_notEqual() {
        Contact a = new Contact(); a.setContactName("n1"); a.setContactId("i"); a.setContactUniqueId("u");
        Contact b = new Contact(); b.setContactName("n2"); b.setContactId("i"); b.setContactUniqueId("u");
        assertNotEquals(a, b);
    }

    @Test
    void isSerializable() {
        assertTrue(java.io.Serializable.class.isAssignableFrom(Contact.class));
    }
}
