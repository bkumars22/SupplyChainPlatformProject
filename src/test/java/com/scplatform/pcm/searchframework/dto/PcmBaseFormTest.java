/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class PcmBaseFormTest {

    @Test
    void requestTypeFallsBackToDefaultWhenUnset() {
        PcmBaseForm f = new PcmBaseForm();
        f.setDefaultRequestType("DEFAULT");
        assertEquals("DEFAULT", f.getRequestType());
        assertEquals("DEFAULT", f.getDefaultRequestType());
    }

    @Test
    void requestTypeOverridesDefault() {
        PcmBaseForm f = new PcmBaseForm();
        f.setDefaultRequestType("DEFAULT");
        f.setRequestType("ACTUAL");
        assertEquals("ACTUAL", f.getRequestType());
    }

    @Test
    void requestTypeIsTrimmedToNullForBlank() {
        PcmBaseForm f = new PcmBaseForm();
        f.setRequestType("   ");
        assertNull(f.getRequestType());
    }

    @Test
    void requestTypeIsTrimmed() {
        PcmBaseForm f = new PcmBaseForm();
        f.setRequestType("  abc  ");
        assertEquals("abc", f.getRequestType());
    }

    @Test
    void appContextNullByDefault() {
        PcmBaseForm f = new PcmBaseForm();
        assertNull(f.getAppContext());
    }
}
