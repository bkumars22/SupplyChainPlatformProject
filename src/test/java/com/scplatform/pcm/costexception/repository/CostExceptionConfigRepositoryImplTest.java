/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.repository;

import com.scplatform.pcm.costexception.entity.CostException;
import com.scplatform.pcm.role.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CostExceptionConfigRepositoryImplTest {

    private CostExceptionConfigRepositoryImpl impl;

    @BeforeEach
    void setUp() {
        impl = new CostExceptionConfigRepositoryImpl();
    }

    @Test
    void isActionAllowedForRole_alwaysReturnsFalse() {
        CostException ex = new CostException();
        Role role = new Role();
        role.setRoleId("ADMIN");
        assertFalse(impl.isActionAllowedForRole(ex, "Approve", role));
        assertFalse(impl.isActionAllowedForRole(ex, "Reject", role));
        assertFalse(impl.isActionAllowedForRole(null, "Approve", null));
    }

    @Test
    void getRolesForCostExceptionAction_alwaysReturnsEmptyList() {
        List<String> roles = impl.getRolesForCostExceptionAction("BUY", "BACKDATE", "Approve");
        assertNotNull(roles);
        assertTrue(roles.isEmpty());

        roles = impl.getRolesForCostExceptionAction("SELL", "MIDMONTH", "Reject");
        assertTrue(roles.isEmpty());
    }

    @Test
    void getRolesForCostExceptionAction_resultIsUnmodifiable() {
        List<String> roles = impl.getRolesForCostExceptionAction("BUY", "BACKDATE", "Approve");
        assertThrows(UnsupportedOperationException.class, () -> roles.add("ADMIN"));
    }

    @Test
    void getEmailForCostExceptionAction_alwaysReturnsEmptyMap() {
        Map<String, List<String>> emails = impl.getEmailForCostExceptionAction("BUY", "BACKDATE", "Approve");
        assertNotNull(emails);
        assertTrue(emails.isEmpty());
    }

    @Test
    void getEmailForCostExceptionAction_resultIsUnmodifiable() {
        Map<String, List<String>> emails = impl.getEmailForCostExceptionAction("BUY", "BACKDATE", "Approve");
        assertThrows(UnsupportedOperationException.class,
                () -> emails.put("key", Collections.emptyList()));
    }

    @Test
    void checkODMEmailAttachmentValidation_alwaysReturnsFalse() {
        assertFalse(impl.checkODMEmailAttachmentValidation("BUY", "BACKDATE", "Approve"));
        assertFalse(impl.checkODMEmailAttachmentValidation("SELL", "MIDMONTH", "Submit"));
        assertFalse(impl.checkODMEmailAttachmentValidation(null, null, null));
    }

    @Test
    void getSuperRoleFromExceptionConfigFile_alwaysReturnsEmptyList() {
        List<String> superRoles = impl.getSuperRoleFromExceptionConfigFile("SomeParam");
        assertNotNull(superRoles);
        assertTrue(superRoles.isEmpty());
    }

    @Test
    void getSuperRoleFromExceptionConfigFile_resultIsUnmodifiable() {
        List<String> superRoles = impl.getSuperRoleFromExceptionConfigFile("param");
        assertThrows(UnsupportedOperationException.class, () -> superRoles.add("SUPER"));
    }

    @Test
    void getRolesForCostExceptionActionExceptSuperRoles_alwaysReturnsEmptySet() {
        Set<String> roles = impl.getRolesForCostExceptionActionExceptSuperRoles("BUY", "BACKDATE", "Approve");
        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }

    @Test
    void getRolesForCostExceptionActionExceptSuperRoles_resultIsUnmodifiable() {
        Set<String> roles = impl.getRolesForCostExceptionActionExceptSuperRoles("BUY", "BACKDATE", "Approve");
        assertThrows(UnsupportedOperationException.class, () -> roles.add("ADMIN"));
    }

    @Test
    void implementsCostExceptionConfigRepository() {
        assertTrue(impl instanceof CostExceptionConfigRepository);
    }
}
