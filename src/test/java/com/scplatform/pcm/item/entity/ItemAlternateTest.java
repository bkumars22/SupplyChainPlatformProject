/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ItemAlternateTest {

    @Test
    void testNoArgConstructorAndAccessors() {
        ItemAlternate ia = new ItemAlternate();
        Item item = new Item();
        Item alt = new Item();
        Date start = new Date(10_000L);
        Date end = new Date(20_000L);

        ia.setItem(item);
        ia.setAlternateItem(alt);
        ia.setPreferredStatusCode("PREF");
        ia.setPreferredStartDate(start);
        ia.setPreferredEndDate(end);

        assertSame(item, ia.getItem());
        assertSame(alt, ia.getAlternateItem());
        assertEquals("PREF", ia.getPreferredStatusCode());
        assertEquals(start, ia.getPreferredStartDate());
        assertEquals(end, ia.getPreferredEndDate());
    }

    @Test
    void testTwoArgConstructor() {
        Item item = new Item();
        Item alt = new Item();
        ItemAlternate ia = new ItemAlternate(item, alt);
        assertSame(item, ia.getItem());
        assertSame(alt, ia.getAlternateItem());
    }

    @Test
    void testGetAlternatesNaturalKeyAsJSON() {
        Item alt = new Item();
        alt.setItemNumber("ALT-PN");
        ItemAlternate ia = new ItemAlternate();
        ia.setAlternateItem(alt);

        ObjectNode n = ia.getAlternatesNaturalKeyAsJSON();
        assertEquals("ALT-PN", n.get("alternateItem").asText());
    }

    @Test
    void testEqualsAndHashCode() {
        Item item = new Item();
        item.setItemNumber("PN");
        item.setItemType(Item.ITEM);
        Item alt = new Item();
        alt.setItemNumber("ALT");
        alt.setItemType(Item.ITEM);

        ItemAlternate a = new ItemAlternate(item, alt);
        ItemAlternate b = new ItemAlternate(item, alt);

        Item alt2 = new Item();
        alt2.setItemNumber("ALT2");
        alt2.setItemType(Item.ITEM);
        ItemAlternate c = new ItemAlternate(item, alt2);

        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
    }
}
