/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.user.entity;

import com.scplatform.pcm.site.entity.Site;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserDelegateTest {

    @Test
    void testGettersSettersAndLombokData() {
        UserDelegate ud = new UserDelegate();
        ud.setDelegateKey(42L);
        Users delegator = new Users(1L);
        ud.setDelegator(delegator);
        ud.setDelegateUserId("user2");
        ud.setResponsibility("RESP");
        Site site = new Site();
        ud.setSite(site);
        Date from = new Date(0);
        Date to = new Date(1000);
        ud.setEffectiveFromDate(from);
        ud.setEffectiveToDate(to);

        assertEquals(42L, ud.getDelegateKey());
        assertSame(delegator, ud.getDelegator());
        assertEquals("user2", ud.getDelegateUserId());
        assertEquals("RESP", ud.getResponsibility());
        assertSame(site, ud.getSite());
        assertEquals(from, ud.getEffectiveFromDate());
        assertEquals(to, ud.getEffectiveToDate());
    }

    @Test
    void testEqualsAndHashCodeAndToString() {
        UserDelegate a = new UserDelegate();
        a.setDelegateKey(1L);
        a.setDelegateUserId("u");
        a.setResponsibility("R");

        UserDelegate b = new UserDelegate();
        b.setDelegateKey(1L);
        b.setDelegateUserId("u");
        b.setResponsibility("R");

        UserDelegate c = new UserDelegate();
        c.setDelegateKey(2L);
        c.setDelegateUserId("u");
        c.setResponsibility("R");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotNull(a.toString());
    }
}
