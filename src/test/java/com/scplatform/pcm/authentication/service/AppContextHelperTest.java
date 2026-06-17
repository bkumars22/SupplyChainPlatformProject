/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.authentication.service;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.dto.InvalidUserContext;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.user.entity.Users;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppContextHelperTest {

    private HttpServletRequest mockRequestWithSessionContaining(ApplicationContext ctx) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute(ApplicationContext.SESSION_ATTR_NAME)).thenReturn(ctx);
        return req;
    }

    @Test
    void getValidContext_throwsWhenSessionMissing() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession(false)).thenReturn(null);
        InvalidUserContext ex = assertThrows(InvalidUserContext.class,
                () -> AppContextHelper.getValidContext(req));
        assertEquals("NoSession", ex.getMessage());
    }

    @Test
    void getValidContext_throwsWhenContextMissing() {
        HttpServletRequest req = mockRequestWithSessionContaining(null);
        InvalidUserContext ex = assertThrows(InvalidUserContext.class,
                () -> AppContextHelper.getValidContext(req));
        assertEquals("NoApplicationContext", ex.getMessage());
    }

    @Test
    void getValidContext_throwsWhenUserMissing() {
        ApplicationContext ctx = new ApplicationContext();
        // currentUser is null
        HttpServletRequest req = mockRequestWithSessionContaining(ctx);
        InvalidUserContext ex = assertThrows(InvalidUserContext.class,
                () -> AppContextHelper.getValidContext(req));
        assertEquals("NoAuthenticatedUserFoundInContext", ex.getMessage());
    }

    @Test
    void getValidContext_returnsContextWhenUserPresent() throws Exception {
        ApplicationContext ctx = new ApplicationContext();
        Users u = new Users();
        ctx.setCurrentUser(u);
        HttpServletRequest req = mockRequestWithSessionContaining(ctx);
        assertSame(ctx, AppContextHelper.getValidContext(req));
    }

    @Test
    void getContextOrNull_returnsNullWhenNoSession() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession(false)).thenReturn(null);
        assertNull(AppContextHelper.getContextOrNull(req));
    }

    @Test
    void getContextOrNull_returnsContextFromSession() {
        ApplicationContext ctx = new ApplicationContext();
        HttpServletRequest req = mockRequestWithSessionContaining(ctx);
        assertSame(ctx, AppContextHelper.getContextOrNull(req));
    }

    @Test
    void hasValidContext_trueWhenContextWithUser() {
        ApplicationContext ctx = new ApplicationContext();
        ctx.setCurrentUser(new Users());
        HttpServletRequest req = mockRequestWithSessionContaining(ctx);
        assertTrue(AppContextHelper.hasValidContext(req));
    }

    @Test
    void hasValidContext_falseWhenInvalid() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession(false)).thenReturn(null);
        assertFalse(AppContextHelper.hasValidContext(req));
    }

    @Test
    void clearContext_removesAttributeWhenSessionPresent() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(req.getSession(false)).thenReturn(session);
        AppContextHelper.clearContext(req);
        verify(session).removeAttribute(ApplicationContext.SESSION_ATTR_NAME);
    }

    @Test
    void clearContext_isNoOpWhenSessionMissing() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession(false)).thenReturn(null);
        AppContextHelper.clearContext(req); // should not throw
    }

    @Test
    void invalidateSession_invalidatesWhenPresent() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(req.getSession(false)).thenReturn(session);
        AppContextHelper.invalidateSession(req);
        verify(session).invalidate();
    }

    @Test
    void invalidateSession_isNoOpWhenSessionMissing() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession(false)).thenReturn(null);
        AppContextHelper.invalidateSession(req); // should not throw
        verify(req, never()).getSession(true);
    }

    @Test
    void getUserId_returnsNullForNullContext() {
        assertNull(AppContextHelper.getUserId(null));
    }

    @Test
    void getUserId_returnsNullWhenUserMissing() {
        assertNull(AppContextHelper.getUserId(new ApplicationContext()));
    }

    @Test
    void getUserId_returnsUserId() {
        ApplicationContext ctx = new ApplicationContext();
        Users u = new Users();
        u.setUserId("bob");
        ctx.setCurrentUser(u);
        assertEquals("bob", AppContextHelper.getUserId(ctx));
    }

    @Test
    void isAuthenticated_falseForNullOrMissingUser() {
        assertFalse(AppContextHelper.isAuthenticated(null));
        assertFalse(AppContextHelper.isAuthenticated(new ApplicationContext()));
    }

    @Test
    void isAuthenticated_trueWhenUserPresent() {
        ApplicationContext ctx = new ApplicationContext();
        ctx.setCurrentUser(new Users());
        assertTrue(AppContextHelper.isAuthenticated(ctx));
    }

    @Test
    void getActiveBusinessEntityKey_returnsNullWhenNoBusinessEntity() {
        Users u = new Users();
        assertNull(AppContextHelper.getActiveBusinessEntityKey(u));
    }

    @Test
    void getActiveBusinessEntityKey_returnsKey() {
        Users u = new Users();
        BusinessEntity be = new BusinessEntity();
        be.setBusinessEntityKey(123L);
        u.setBusinessEntity(be);
        assertEquals(123L, AppContextHelper.getActiveBusinessEntityKey(u));
    }

    @Test
    void setDataFilterKeys_storesUnderDataType() {
        ApplicationContext ctx = new ApplicationContext();
        Set<String> keys = new HashSet<>();
        keys.add("1");
        AppContextHelper.setDataFilterKeys(ctx, "CATEGORY", keys);
        assertNotNull(ctx.getDataFilterKeys().get("CATEGORY"));
        assertTrue(ctx.getDataFilterKeys().get("CATEGORY").contains("1"));
    }

    @Test
    void getSystemProperty_readsSystemProperty() {
        String key = "appctx.helper.test." + System.nanoTime();
        try {
            System.setProperty(key, "value-1");
            assertEquals("value-1", AppContextHelper.getSystemProperty(key));
        } finally {
            System.clearProperty(key);
        }
    }

    @Test
    void getSystemProperty_returnsNullForUnknown() {
        assertNull(AppContextHelper.getSystemProperty("appctx.helper.absent." + System.nanoTime()));
    }
}
