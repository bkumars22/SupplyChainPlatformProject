/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.scheduler.entity;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JobConfigTest {

    @Test
    void defaults() {
        JobConfig j = new JobConfig();
        assertNull(j.getJobId());
        assertNull(j.getJobName());
        assertNull(j.getJobGroup());
        assertNull(j.getJobClass());
        assertNull(j.getCron());
        assertNull(j.getJobType());
        assertNull(j.getRepeatInterval());
        assertFalse(j.isEnabled());
        assertNull(j.getDescription());
        assertNull(j.getCreatedAt());
        assertNull(j.getUpdatedAt());
    }

    @Test
    void settersAndGetters() {
        Date created = new Date(1000L);
        Date updated = new Date(2000L);
        JobConfig j = new JobConfig();
        j.setJobId(1L);
        j.setJobName("ImportJob");
        j.setJobGroup("ETL");
        j.setJobClass("com.example.MyJob");
        j.setCron("0 0 * * *");
        j.setJobType("CRON");
        j.setRepeatInterval(60);
        j.setEnabled(true);
        j.setDescription("desc");
        j.setCreatedAt(created);
        j.setUpdatedAt(updated);

        assertEquals(1L, j.getJobId());
        assertEquals("ImportJob", j.getJobName());
        assertEquals("ETL", j.getJobGroup());
        assertEquals("com.example.MyJob", j.getJobClass());
        assertEquals("0 0 * * *", j.getCron());
        assertEquals("CRON", j.getJobType());
        assertEquals(60, j.getRepeatInterval());
        assertTrue(j.isEnabled());
        assertEquals("desc", j.getDescription());
        assertEquals(created, j.getCreatedAt());
        assertEquals(updated, j.getUpdatedAt());
    }

    @Test
    void equalsAndHashCode_lombokGenerated() {
        JobConfig a = new JobConfig(); a.setJobId(1L); a.setJobName("A");
        JobConfig b = new JobConfig(); b.setJobId(1L); b.setJobName("A");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setJobName("B");
        assertNotEquals(a, b);
    }

    @Test
    void toString_containsName() {
        JobConfig j = new JobConfig();
        j.setJobName("MyJob");
        assertTrue(j.toString().contains("MyJob"));
    }
}
