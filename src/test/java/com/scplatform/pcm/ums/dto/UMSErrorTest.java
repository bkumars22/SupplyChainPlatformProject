/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class UMSErrorTest {

    @Test
    void getMessageReturnsTemplate() {
        assertEquals("AccessGroup {0} not found", UMSError.AccessGroup.getMessage());
        assertEquals("UserName {0} not found", UMSError.UserName.getMessage());
    }

    @Test
    void getErrorSubstitutesSingleArg() {
        assertEquals("AccessGroup ADMIN not found", UMSError.AccessGroup.getError("ADMIN"));
        assertEquals("AccessControl X not found", UMSError.Role.getError("X"));
    }

    @Test
    void getErrorSubstitutesMultipleArgs() {
        // Internal template is "{0}" — multi-arg works
        assertEquals("hello", UMSError.Internal.getError(new String[]{"hello"}));
    }

    @Test
    void notImplementedHasFixedMessage() {
        assertEquals("This api is not implemented for MTCM", UMSError.NotImplemented.getMessage());
    }

    @Test
    void setMessageOverridesValue() {
        String orig = UMSError.AccessGroupRole.getMessage();
        try {
            UMSError.AccessGroupRole.setMessage("custom {0}");
            assertEquals("custom Z", UMSError.AccessGroupRole.getError("Z"));
        } finally {
            UMSError.AccessGroupRole.setMessage(orig);
        }
    }

    @Test
    void allEnumValuesPresent() {
        assertNotNull(UMSError.values());
        assertEquals(6, UMSError.values().length);
        assertNotNull(UMSError.valueOf("AccessGroup"));
    }
}
