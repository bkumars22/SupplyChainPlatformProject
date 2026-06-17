/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.upload.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class LoadEventTest {

    @Test
    void noArgConstructorInitialisesInsertDate() {
        LoadEvent e = new LoadEvent();
        assertNull(e.getLoadEventKey());
        assertNull(e.getLoadJob());
        assertNull(e.getType());
        assertNull(e.getLoadEventData());
        assertNull(e.getLoadEventContext());
        assertNotNull(e.getInsertDate());
    }

    @Test
    void setTypeStringStoresVerbatim() {
        LoadEvent e = new LoadEvent();
        e.setType("CUSTOM");
        assertEquals("CUSTOM", e.getType());
    }

    @Test
    void setTypeEnumStoresName() {
        LoadEvent e = new LoadEvent();
        e.setType(LoadEvent.LoadEventType.DATA_ERROR);
        assertEquals("DATA_ERROR", e.getType());
    }

    @Test
    void setLoadEventDataTruncatesTo1024Chars() {
        LoadEvent e = new LoadEvent();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2000; i++) sb.append('x');
        e.setLoadEventData(sb.toString());
        assertEquals(1024, e.getLoadEventData().length());
        assertTrue(e.getLoadEventData().endsWith("..."));
    }

    @Test
    void setLoadEventDataLeavesShortStringUnchanged() {
        LoadEvent e = new LoadEvent();
        e.setLoadEventData("short");
        assertEquals("short", e.getLoadEventData());
    }

    @Test
    void setLoadEventDataAcceptsNull() {
        LoadEvent e = new LoadEvent();
        e.setLoadEventData(null);
        assertNull(e.getLoadEventData());
    }

    @Test
    void canEventBeClearedTrueForUncleared() {
        LoadEvent e = new LoadEvent();
        e.setType("VALIDATION_ERROR");
        assertTrue(e.isCanEventBeCleared());
    }

    @Test
    void canEventBeClearedFalseWhenAlreadyCleared() {
        LoadEvent e = new LoadEvent();
        e.setType("VALIDATION_ERROR" + LoadEvent.CLEAR_CONTEXT);
        assertFalse(e.isCanEventBeCleared());
    }

    @Test
    void canEventBeClearedFalseWhenTypeNull() {
        LoadEvent e = new LoadEvent();
        assertFalse(e.isCanEventBeCleared());
    }

    @Test
    void clearContextConstantValue() {
        assertEquals(":CLEARED", LoadEvent.CLEAR_CONTEXT);
    }

    @Test
    void allLoadEventTypesEnumerated() {
        assertEquals(8, LoadEvent.LoadEventType.values().length);
    }

    @Test
    void settersAndGettersWork() {
        LoadEvent e = new LoadEvent();
        LocalDateTime now = LocalDateTime.now();
        LoadJob job = new LoadJob();
        e.setLoadEventKey(7L);
        e.setLoadJob(job);
        e.setLoadEventContext("ctx");
        e.setInsertDate(now);

        assertEquals(7L, e.getLoadEventKey());
        assertEquals(job, e.getLoadJob());
        assertEquals("ctx", e.getLoadEventContext());
        assertEquals(now, e.getInsertDate());
    }
}
