/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.upload.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class LoadJobTest {

    @Test
    void noArgConstructorInitialisesDateAndEmptyEvents() {
        LoadJob j = new LoadJob();
        assertNull(j.getLoadJobKey());
        assertNull(j.getDatasource());
        assertNull(j.getLoadedBy());
        assertNull(j.getStatus());
        assertNull(j.getState());
        assertNull(j.getLoadJobType());
        assertNull(j.getExternalId());
        assertNotNull(j.getLoadDate());
        assertNotNull(j.getLoadEvents());
        assertTrue(j.getLoadEvents().isEmpty());
    }

    @Test
    void settersAssignFields() {
        LoadJob j = new LoadJob();
        LocalDateTime now = LocalDateTime.now();
        j.setLoadJobKey("uuid-1");
        j.setDatasource("ds");
        j.setLoadedBy("u1");
        j.setStatus("RUNNING");
        j.setState("S");
        j.setLoadJobType("LJT");
        j.setExternalId("ext-1");
        j.setLoadDate(now);
        j.setLoadEvents(Arrays.asList(new LoadEvent()));

        assertEquals("uuid-1", j.getLoadJobKey());
        assertEquals("ds", j.getDatasource());
        assertEquals("u1", j.getLoadedBy());
        assertEquals("RUNNING", j.getStatus());
        assertEquals("S", j.getState());
        assertEquals("LJT", j.getLoadJobType());
        assertEquals("ext-1", j.getExternalId());
        assertEquals(now, j.getLoadDate());
        assertEquals(1, j.getLoadEvents().size());
    }

    @Test
    void lombokDataEqualsHashCodeAndToStringWork() {
        LoadJob a = new LoadJob();
        a.setLoadJobKey("k");
        LoadJob b = new LoadJob();
        b.setLoadJobKey("k");
        b.setLoadDate(a.getLoadDate());
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }
}
