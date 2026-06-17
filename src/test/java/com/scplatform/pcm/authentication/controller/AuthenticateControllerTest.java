/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.authentication.controller;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.user.repository.UsersRepository;
import com.scplatform.pcm.user.service.UserSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthenticateControllerTest {

    @Mock private UsersRepository usersRepository;
    @Mock private UserSessionService userSessionService;
    @Mock private PcmConfigUtil configUtil;

    private AuthenticateController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthenticateController(usersRepository, userSessionService, configUtil);
    }

    @AfterEach
    void clearProps() {
        System.clearProperty("mtcm.launchpad.enabled");
        System.clearProperty("env.clp.url");
    }

    @Test
    void welcome_returnsWelcomeView() {
        assertEquals("welcome", controller.welcome());
    }

    @Test
    void doSessionExpired_returnsAskUserReLogin() {
        assertEquals("askUserReLogin", controller.doSessionExpired());
    }

    @Test
    void index_returnsLoginWhenNoSession() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession(false)).thenReturn(null);
        assertEquals("login", controller.index(req));
    }

    @Test
    void index_redirectsToAuthenticateWhenValidContext() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(req.getSession(false)).thenReturn(session);
        ApplicationContext ctx = new ApplicationContext();
        com.scplatform.pcm.user.entity.Users u = new com.scplatform.pcm.user.entity.Users();
        ctx.setCurrentUser(u);
        when(session.getAttribute(ApplicationContext.SESSION_ATTR_NAME)).thenReturn(ctx);

        assertEquals("redirect:authenticate", controller.index(req));
    }

    @Test
    void cancleRelogin_returnsLoginWhenNotViaPortal() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("iv-user")).thenReturn(null);
        when(req.getSession(false)).thenReturn(null);
        assertEquals("login", controller.cancleRelogin(req));
    }

    @Test
    void cancleRelogin_returnsRedirectPortalWhenViaPortal() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("iv-user")).thenReturn("ext-user");
        when(req.getSession(false)).thenReturn(null);
        // launchpad disabled by default
        assertEquals("redirect:../common/exit", controller.cancleRelogin(req));
    }

    @Test
    void cancleRelogin_redirectsToClpWhenPortalAndLaunchpadEnabled() {
        System.setProperty("mtcm.launchpad.enabled", "true");
        System.setProperty("env.clp.url", "https://clp.example/home");
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("iv-user")).thenReturn("ext-user");
        when(req.getSession(false)).thenReturn(null);

        assertEquals("redirect:https://clp.example/home", controller.cancleRelogin(req));
    }

    @Test
    void cancleRelogin_clearsSessionAttributesWhenSessionPresent() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(req.getHeader("iv-user")).thenReturn(null);
        when(req.getSession(false)).thenReturn(session);

        controller.cancleRelogin(req);

        verify(session).removeAttribute(ApplicationContext.SESSION_ATTR_NAME);
        verify(session).removeAttribute("userName");
    }

    @Test
    void logout_invalidatesSessionAndDeletesUserSession_whenContextPresent() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(req.getSession(false)).thenReturn(session);

        ApplicationContext ctx = new ApplicationContext();
        com.scplatform.pcm.user.entity.Users u = new com.scplatform.pcm.user.entity.Users();
        u.setUserId("alice");
        ctx.setCurrentUser(u);
        when(session.getAttribute(ApplicationContext.SESSION_ATTR_NAME)).thenReturn(ctx);

        String view = controller.logout(req, resp);

        assertEquals("login", view);
        verify(userSessionService).deleteSession("alice");
        verify(session).removeAttribute(ApplicationContext.SESSION_ATTR_NAME);
        verify(session).invalidate();
    }

    @Test
    void logout_returnsLoginWhenNoSession_andDoesNotInteractWithSessionService() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getSession(false)).thenReturn(null);

        assertEquals("login", controller.logout(req, resp));
        verify(userSessionService, never()).deleteSession(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void logout_redirectsToPortalWhenViaPortalAndLaunchpadDisabled() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getHeader("iv-user")).thenReturn("ext-user");
        when(req.getSession(false)).thenReturn(null);

        assertEquals("redirect:../common/exit", controller.logout(req, resp));
    }

    @Test
    void logout_redirectsToClpWhenViaPortalAndLaunchpadEnabled() {
        System.setProperty("mtcm.launchpad.enabled", "true");
        System.setProperty("env.clp.url", "https://clp.example/lp");
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getHeader("iv-user")).thenReturn("ext-user");
        when(req.getSession(false)).thenReturn(null);

        assertEquals("redirect:https://clp.example/lp", controller.logout(req, resp));
    }

    @Test
    void ackTerms_returnsLoginWhenAckValueIsFalse() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(req.getHeader("iv-user")).thenReturn(null);
        when(req.getSession(false)).thenReturn(session);

        ApplicationContext ctx = new ApplicationContext();
        com.scplatform.pcm.user.entity.Users u = new com.scplatform.pcm.user.entity.Users();
        u.setUserId("alice");
        ctx.setCurrentUser(u);
        when(session.getAttribute(ApplicationContext.SESSION_ATTR_NAME)).thenReturn(ctx);
        when(req.getSession()).thenReturn(session);

        assertEquals("login", controller.ackTerms(req, "false"));
    }

    @Test
    void ackTerms_returnsLoginWhenInvalidUserContext() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("iv-user")).thenReturn(null);
        when(req.getSession(false)).thenReturn(null); // triggers InvalidUserContext

        assertEquals("login", controller.ackTerms(req, "true"));
    }
}
