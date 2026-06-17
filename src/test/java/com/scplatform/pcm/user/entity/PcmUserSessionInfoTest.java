/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.user.entity;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class PcmUserSessionInfoTest {

    @Test
    void testNoArgsAndAllArgsConstructorsAndAccessors() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        PcmUserSessionInfo s = new PcmUserSessionInfo("u1", "sid", ts);
        assertEquals("u1", s.getUserId());
        assertEquals("sid", s.getSessionId());
        assertEquals(ts, s.getLastUpdateOn());

        PcmUserSessionInfo def = new PcmUserSessionInfo();
        def.setUserId("u2");
        def.setSessionId("sid2");
        def.setLastUpdateOn(ts);
        assertEquals("u2", def.getUserId());
        assertEquals("sid2", def.getSessionId());
        assertEquals(ts, def.getLastUpdateOn());
    }

    @Test
    void testBuilder() {
        Timestamp ts = new Timestamp(0);
        PcmUserSessionInfo s = PcmUserSessionInfo.builder()
                .userId("u").sessionId("s").lastUpdateOn(ts).build();
        assertEquals("u", s.getUserId());
        assertEquals("s", s.getSessionId());
        assertEquals(ts, s.getLastUpdateOn());
    }

    @Test
    void testEqualsAndHashCode() {
        PcmUserSessionInfo a = new PcmUserSessionInfo("u", "s1", new Timestamp(1));
        PcmUserSessionInfo b = new PcmUserSessionInfo("u", "s2", new Timestamp(2));
        PcmUserSessionInfo c = new PcmUserSessionInfo("v", "s1", new Timestamp(1));
        PcmUserSessionInfo nullId = new PcmUserSessionInfo();

        assertEquals(a, a);
        assertEquals(a, b);          // userId equal => equal
        assertNotEquals(a, c);       // different userId
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
        assertNotEquals(nullId, a);  // null userId branch

        // hashCode is class-based and deterministic
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(a.hashCode(), c.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        PcmUserSessionInfo s = new PcmUserSessionInfo("uid", "sess", new Timestamp(0));
        String str = s.toString();
        assertTrue(str.contains("uid"));
        assertTrue(str.contains("sess"));
        assertTrue(str.startsWith("PcmUserSessionInfo{"));
    }
}
