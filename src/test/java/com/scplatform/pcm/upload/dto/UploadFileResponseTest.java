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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class UploadFileResponseTest {

    @Test
    void defaultsAreSane() {
        UploadFileResponse r = new UploadFileResponse();
        assertNotNull(r.getAvailableMessageTypes());
        assertTrue(r.getAvailableMessageTypes().isEmpty());
        assertNotNull(r.getXlxsType());
        assertTrue(r.getXlxsType().isEmpty());
        assertNotNull(r.getSuccessLinks());
        assertTrue(r.getSuccessLinks().isEmpty());
        assertNull(r.getErrorDetails());
        assertNull(r.getUploadMenuType());
        assertFalse(r.isError());
        assertEquals(1, r.getMaxFiles());
    }

    @Test
    void addAvailableMessageTypes_createsAndAppendsBuckets() {
        UploadFileResponse r = new UploadFileResponse();
        r.addAvailableMessageTypes("xlsx", Arrays.asList("PriceTAMMonthly"));
        r.addAvailableMessageTypes("xlsx", Arrays.asList("CostRecordUI"));
        r.addAvailableMessageTypes("xls", Collections.emptyList());

        Map<String, List<String>> map = r.getAvailableMessageTypes();
        assertEquals(2, map.size());
        assertEquals(Arrays.asList("PriceTAMMonthly", "CostRecordUI"), map.get("xlsx"));
        assertTrue(map.get("xls").isEmpty());
    }

    @Test
    void addSuccessLink_appendsMapWithExpectedKeys() {
        UploadFileResponse r = new UploadFileResponse();
        r.addSuccessLink("file.xlsx", "TX-1", "JOB-9");
        assertEquals(1, r.getSuccessLinks().size());
        Map<String, String> link = r.getSuccessLinks().get(0);
        assertEquals("file.xlsx", link.get("fileName"));
        assertEquals("TX-1", link.get("transactionId"));
        assertEquals("JOB-9", link.get("loadJobKey"));
    }

    @Test
    void settersAndLombokWork() {
        UploadFileResponse r = new UploadFileResponse();
        r.setUploadMenuType("admin");
        r.setError(true);
        r.setMaxFiles(5);
        r.setErrorDetails(Arrays.asList(new LoadMessage()));

        assertEquals("admin", r.getUploadMenuType());
        assertTrue(r.isError());
        assertEquals(5, r.getMaxFiles());
        assertEquals(1, r.getErrorDetails().size());
        assertNotNull(r.toString());
    }
}
