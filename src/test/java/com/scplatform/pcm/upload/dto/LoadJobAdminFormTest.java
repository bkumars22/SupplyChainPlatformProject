/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.upload.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.scplatform.pcm.upload.entity.LoadJob;

class LoadJobAdminFormTest {

    @Test
    void defaultsAreSane() {
        LoadJobAdminForm f = new LoadJobAdminForm();
        assertNull(f.getSelectedLoadJob());
        assertNull(f.getSelectedLoadJobKey());
        assertNull(f.getAssignToBusinessKey());
        assertNull(f.getPreserveSearchValues());
        assertNull(f.getErrorFileName());
        assertNull(f.getSelectedEventKeys());
        assertNull(f.getJobErrorDetails());
        assertFalse(f.isUnsavedData());
        assertFalse(f.isClearAll());
        assertFalse(f.isReplayAllowed());
    }

    @Test
    void settersAndLombokWork() {
        LoadJobAdminForm f = new LoadJobAdminForm();
        LoadJob job = new LoadJob();
        f.setSelectedLoadJob(job);
        f.setSelectedLoadJobKey("J1");
        f.setUnsavedData(true);
        f.setClearAll(true);
        f.setReplayAllowed(true);
        f.setAssignToBusinessKey("B1");
        f.setPreserveSearchValues("v=1");
        f.setErrorFileName("err.csv");
        f.setSelectedEventKeys(Arrays.asList(1L, 2L));
        f.setJobErrorDetails("boom");

        assertEquals(job, f.getSelectedLoadJob());
        assertEquals("J1", f.getSelectedLoadJobKey());
        assertTrue(f.isUnsavedData());
        assertTrue(f.isClearAll());
        assertTrue(f.isReplayAllowed());
        assertEquals("B1", f.getAssignToBusinessKey());
        assertEquals("v=1", f.getPreserveSearchValues());
        assertEquals("err.csv", f.getErrorFileName());
        assertEquals(2, f.getSelectedEventKeys().size());
        assertEquals("boom", f.getJobErrorDetails());
        assertNotNull(f.toString());
    }
}
