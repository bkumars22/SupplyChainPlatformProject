/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.launchpad.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class ChildTest {

    @Test
    void defaultsAreNullExceptAdditionalProps() {
        Child child = new Child();
        assertNull(child.getTitle());
        assertNull(child.getApp());
        assertNull(child.getLabel());
        assertNull(child.getName());
        assertNull(child.getChildren());
        assertNotNull(child.getAdditionalProperties());
    }

    @Test
    void scalarSettersUpdateValues() {
        Child child = new Child();
        child.setTitle("t");
        child.setApp("a");
        child.setLabel("l");
        child.setName("n");

        assertEquals("t", child.getTitle());
        assertEquals("a", child.getApp());
        assertEquals("l", child.getLabel());
        assertEquals("n", child.getName());
    }

    @Test
    void childrenListIsRetained() {
        Child child = new Child();
        Child_ inner = new Child_();
        inner.setName("leaf");
        List<List<Child_>> nested = new ArrayList<>();
        nested.add(Collections.singletonList(inner));
        child.setChildren(nested);

        assertEquals(1, child.getChildren().size());
        assertEquals("leaf", child.getChildren().get(0).get(0).getName());
    }

    @Test
    void additionalPropertyStored() {
        Child child = new Child();
        child.setAdditionalProperty("k", "v");
        assertEquals("v", child.getAdditionalProperties().get("k"));
    }
}
