/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.restriction.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestrictionDtoTest {

    // --- Element ---
    @Test
    void elementDefaultConstructor() {
        Element e = new Element();
        assertNull(e.getName());
        assertNull(e.getValue());
    }

    @Test
    void elementConstructorAndSetters() {
        Element e = new Element("n", "v");
        assertEquals("n", e.getName());
        assertEquals("v", e.getValue());
        e.setName("n2");
        e.setValue("v2");
        assertEquals("n2", e.getName());
        assertEquals("v2", e.getValue());
        assertTrue(e.toString().contains("n2"));
    }

    // --- IfExist ---
    @Test
    void ifExistDefaultConstructor() {
        IfExist i = new IfExist();
        assertNull(i.getElement());
    }

    @Test
    void ifExistConstructorAndSetter() {
        List<Element> list = new ArrayList<>();
        list.add(new Element("k", "v"));
        IfExist i = new IfExist(list);
        assertSame(list, i.getElement());
        List<Element> list2 = Collections.emptyList();
        i.setElement(list2);
        assertSame(list2, i.getElement());
        assertNotNull(i.toString());
    }

    // --- Require ---
    @Test
    void requireDefaultConstructor() {
        Require r = new Require();
        assertNull(r.getElement());
    }

    @Test
    void requireConstructorAndSetter() {
        List<Element> list = new ArrayList<>();
        Require r = new Require(list);
        assertSame(list, r.getElement());
        List<Element> list2 = Collections.emptyList();
        r.setElement(list2);
        assertSame(list2, r.getElement());
        assertNotNull(r.toString());
    }

    // --- Condition ---
    @Test
    void conditionDefaultConstructor() {
        Condition c = new Condition();
        assertNull(c.getIfExist());
        assertNull(c.getRequire());
    }

    @Test
    void conditionConstructorAndSetters() {
        IfExist ie = new IfExist();
        Require req = new Require();
        Condition c = new Condition(ie, req);
        assertSame(ie, c.getIfExist());
        assertSame(req, c.getRequire());

        IfExist ie2 = new IfExist();
        Require req2 = new Require();
        c.setIfExist(ie2);
        c.setRequire(req2);
        assertSame(ie2, c.getIfExist());
        assertSame(req2, c.getRequire());
        assertNotNull(c.toString());
    }

    // --- Screen ---
    @Test
    void screenDefaultConstructor() {
        Screen s = new Screen();
        assertNull(s.getName());
        assertNull(s.getCondition());
    }

    @Test
    void screenConstructorAndSetters() {
        List<Condition> conds = new ArrayList<>();
        Screen s = new Screen("ScreenA", conds);
        assertEquals("ScreenA", s.getName());
        assertSame(conds, s.getCondition());

        s.setName("ScreenB");
        List<Condition> conds2 = Collections.emptyList();
        s.setCondition(conds2);
        assertEquals("ScreenB", s.getName());
        assertSame(conds2, s.getCondition());
        assertTrue(s.toString().contains("ScreenB"));
    }

    // --- Restriction ---
    @Test
    void restrictionDefaultConstructor() {
        Restriction r = new Restriction();
        assertNull(r.getScreen());
    }

    @Test
    void restrictionConstructorAndSetter() {
        List<Screen> screens = new ArrayList<>();
        screens.add(new Screen());
        Restriction r = new Restriction(screens);
        assertSame(screens, r.getScreen());

        List<Screen> screens2 = Collections.emptyList();
        r.setScreen(screens2);
        assertSame(screens2, r.getScreen());
        assertNotNull(r.toString());
    }
}
