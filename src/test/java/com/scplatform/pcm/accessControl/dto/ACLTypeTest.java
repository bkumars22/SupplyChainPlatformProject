/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.accessControl.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ACLTypeTest {

    @Test
    void constructorSetsFields() {
        ACLType t = new ACLType("view", "admin", "read", "edit");
        assertEquals("view", t.getAction());
        assertEquals("admin", t.getRequiredBy());
        assertEquals("read", t.getRequires());
        assertEquals("edit", t.getXor());
    }

    @Test
    void settersAndGetters() {
        ACLType t = new ACLType(null, null, null, null);
        t.setAction("create");
        t.setRequiredBy("manager");
        t.setRequires("login");
        t.setXor("delete");
        t.setBusinessObjectType("Item");
        assertEquals("create", t.getAction());
        assertEquals("manager", t.getRequiredBy());
        assertEquals("login", t.getRequires());
        assertEquals("delete", t.getXor());
        assertEquals("Item", t.getBusinessObjectType());
    }

    @Test
    void getValidatorWithXor() {
        ACLType t = new ACLType(null, null, null, "delete");
        String v = t.getValidator();
        assertTrue(v.contains("handleXOR"));
        assertTrue(v.contains("delete"));
    }

    @Test
    void getValidatorWithXorAndBusinessObjectType() {
        ACLType t = new ACLType(null, null, null, "delete");
        t.setBusinessObjectType("Order");
        String v = t.getValidator();
        assertTrue(v.contains("handleXOR"));
        assertTrue(v.contains("Order"));
    }

    @Test
    void getValidatorWithRequires() {
        ACLType t = new ACLType(null, null, "login", null);
        String v = t.getValidator();
        assertTrue(v.contains("handleRequires"));
        assertTrue(v.contains("login"));
    }

    @Test
    void getValidatorWithRequiredBy() {
        ACLType t = new ACLType(null, "admin", null, null);
        String v = t.getValidator();
        assertTrue(v.contains("handleRequiredBy"));
        assertTrue(v.contains("admin"));
    }

    @Test
    void getValidatorAllNull() {
        ACLType t = new ACLType(null, null, null, null);
        assertEquals("", t.getValidator());
    }

    @Test
    void getValidatorAllFields() {
        ACLType t = new ACLType("view", "admin", "login", "delete");
        t.setBusinessObjectType("BOM");
        String v = t.getValidator();
        assertTrue(v.contains("handleXOR"));
        assertTrue(v.contains("handleRequires"));
        assertTrue(v.contains("handleRequiredBy"));
        assertTrue(v.contains("BOM"));
    }
}
