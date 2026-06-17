/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.scplatform.pcm.ums.dto.UMSProvisionUserErrorResponse.UMSUserErrorResponse;
import com.scplatform.pcm.ums.dto.UMSProvisionUserErrorResponse.UMSUserErrorResponse.AccessControl;

class UMSProvisionUserErrorResponseTest {

    @Test
    void defaultsAreEmpty() {
        UMSProvisionUserErrorResponse r = new UMSProvisionUserErrorResponse();
        assertNotNull(r.getErrors());
        assertTrue(r.getErrors().getUser().isEmpty());
        assertTrue(r.getErrors().getAccessControls().isEmpty());
    }

    @Test
    void addAccessControlObject() {
        UMSUserErrorResponse u = new UMSUserErrorResponse();
        AccessControl ac = new AccessControl();
        ac.setAccessControlId("ac");
        u.addAccessControl(ac);
        assertEquals(1, u.getAccessControls().size());
    }

    @Test
    void addAccessControlByFields() {
        UMSUserErrorResponse u = new UMSUserErrorResponse();
        u.addAccessControl("ac", "ag", Arrays.asList("err"));
        AccessControl added = u.getAccessControls().get(0);
        assertEquals("ac", added.getAccessControlId());
        assertEquals("ag", added.getAccessGroupId());
        assertEquals(Collections.singletonList("err"), added.getErrors());
    }

    @Test
    void accessControlSettersAndEquals() {
        AccessControl a = new AccessControl();
        a.setAccessControlId("ac");
        a.setAccessGroupId("ag");
        a.setErrors(Arrays.asList("e"));
        AccessControl b = new AccessControl();
        b.setAccessControlId("ac");
        b.setAccessGroupId("ag");
        b.setErrors(Arrays.asList("e"));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }

    @Test
    void equalsAndHashCode() {
        UMSProvisionUserErrorResponse a = new UMSProvisionUserErrorResponse();
        UMSProvisionUserErrorResponse b = new UMSProvisionUserErrorResponse();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotNull(a.toString());
    }
}
