/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.assignment.entity;

import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.responsibility.entity.PcmResponsibility;
import com.scplatform.pcm.site.entity.Site;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemAssignmentTest {

    @Test
    void defaultConstructor() {
        ItemAssignment a = new ItemAssignment();
        assertNull(a.getAssignmentKey());
        assertNull(a.getUserId());
        assertNull(a.getItem());
    }

    @Test
    void isInstanceOfAssignment() {
        ItemAssignment a = new ItemAssignment();
        assertInstanceOf(Assignment.class, a);
    }

    @Test
    void itemSetterGetter() {
        ItemAssignment a = new ItemAssignment();
        Item item = new Item();
        item.setItemKey(5L);
        a.setItem(item);
        assertSame(item, a.getItem());
    }

    @Test
    void inheritedSetters() {
        ItemAssignment a = new ItemAssignment();
        a.setAssignmentKey(10L);
        a.setUserId("iUser");
        a.setRegion("APAC");
        a.setCurrentFlag(true);

        assertEquals(10L, a.getAssignmentKey());
        assertEquals("iUser", a.getUserId());
        assertEquals("APAC", a.getRegion());
        assertTrue(a.getCurrentFlag());
    }

    @Test
    void equalsReflexive() {
        ItemAssignment a = new ItemAssignment();
        Item item = new Item();
        item.setItemKey(1L);
        a.setItem(item);
        PcmResponsibility resp = new PcmResponsibility("KEY1", "Resp1");
        a.setResponsibility(resp);
        assertEquals(a, a);
    }

    @Test
    void equalsNull() {
        ItemAssignment a = new ItemAssignment();
        assertNotEquals(null, a);
    }

    @Test
    void equalsDifferentType() {
        ItemAssignment a = new ItemAssignment();
        assertNotEquals("string", a);
    }

    @Test
    void equalsSameItemAndResponsibility() {
        Item item = new Item();
        item.setItemKey(1L);
        PcmResponsibility resp = new PcmResponsibility("KEY1", "Resp1");
        Site site = new Site();

        ItemAssignment a = new ItemAssignment();
        a.setItem(item);
        a.setResponsibility(resp);
        a.setUserId("user1");
        a.setSite(site);

        ItemAssignment b = new ItemAssignment();
        b.setItem(item);
        b.setResponsibility(resp);
        b.setUserId("user1");
        b.setSite(site);

        assertEquals(a, b);
    }

    @Test
    void responsibilityComparatorCompares() {
        Item item1 = new Item();
        item1.setItemKey(1L);
        Item item2 = new Item();
        item2.setItemKey(2L);

        PcmResponsibility r1 = new PcmResponsibility("KEY1", "Resp1");
        r1.setDisplayOrder(1L);
        PcmResponsibility r2 = new PcmResponsibility("KEY2", "Resp2");
        r2.setDisplayOrder(2L);

        ItemAssignment a = new ItemAssignment();
        a.setItem(item1);
        a.setResponsibility(r1);
        a.setUserId("aUser");

        ItemAssignment b = new ItemAssignment();
        b.setItem(item2);
        b.setResponsibility(r2);
        b.setUserId("bUser");

        ItemAssignment.ItemAssignmentResponsibilityComparator comp =
                new ItemAssignment.ItemAssignmentResponsibilityComparator();
        assertTrue(comp.compare(a, b) < 0);
        assertTrue(comp.compare(b, a) > 0);
        assertEquals(0, comp.compare(a, a));
    }
}
