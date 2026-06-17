/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.security.filter;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.service.UserSessionService;
import com.scplatform.pcm.authentication.dto.ApplicationContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SessionHandlerFilter.
 */
@DisplayName("Session Handler Filter Tests")
@ExtendWith(MockitoExtension.class)
class SessionHandlerFilterTest {

    private SessionHandlerFilter filter;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserSessionService userSessionService;

    @Mock
    private PcmConfigUtil configUtil;

    @BeforeEach
    void setUp() {
        filter = new SessionHandlerFilter(userSessionService, configUtil);
        filter.setEnabled(true);
        filter.setConcurrentLoginCheckEnabled(true);
    }

    private void setupValidApplicationContext(MockHttpServletRequest request, String userId) {
        Users user = Users.builder().userId(userId).build();
        ApplicationContext context = new ApplicationContext();
        context.setCurrentUser(user);
        request.getSession().setAttribute(ApplicationContext.SESSION_ATTR_NAME, context);
    }

    @Nested
    @DisplayName("Basic Filter Tests")
    class BasicFilterTests {

        @Test
        @DisplayName("Should call filter chain for authenticated user with valid session")
        void shouldCallFilterChainForValidSession() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/action.do");
            setupValidApplicationContext(request, "testUser");
            MockHttpServletResponse response = new MockHttpServletResponse();

            when(userSessionService.checkAndManageSession(anyString(), anyString(), any()))
                    .thenReturn(UserSessionService.SessionCheckResult.VALID);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should process request without errors")
        void shouldProcessRequestWithoutErrors() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/test");
            setupValidApplicationContext(request, "testUser");
            MockHttpServletResponse response = new MockHttpServletResponse();

            when(userSessionService.checkAndManageSession(anyString(), anyString(), any()))
                    .thenReturn(UserSessionService.SessionCheckResult.VALID);

            assertDoesNotThrow(() -> filter.doFilterInternal(request, response, filterChain));
        }

        @Test
        @DisplayName("Should continue filter chain when session check disabled")
        void shouldContinueWhenSessionCheckDisabled() throws ServletException, IOException {
            filter.setConcurrentLoginCheckEnabled(false);
            
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/action.do");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            verifyNoInteractions(userSessionService);
        }
    }

    @Nested
    @DisplayName("Session Check Tests")
    class SessionCheckTests {

        @Test
        @DisplayName("Should redirect to authenticate when no session")
        void shouldRedirectWhenNoSession() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/action.do");
            // No session or ApplicationContext set
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            assertEquals("authenticate", response.getRedirectedUrl());
            verify(filterChain, never()).doFilter(any(), any());
        }

        @Test
        @DisplayName("Should redirect to authenticate when no ApplicationContext in session")
        void shouldRedirectWhenNoApplicationContext() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/action.do");
            request.getSession(); // Create session but don't set ApplicationContext
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            assertEquals("authenticate", response.getRedirectedUrl());
            verify(filterChain, never()).doFilter(any(), any());
        }

        @Test
        @DisplayName("Should redirect to authenticate when no user in ApplicationContext")
        void shouldRedirectWhenNoUserInContext() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/action.do");
            ApplicationContext context = new ApplicationContext(); // No user set
            request.getSession().setAttribute(ApplicationContext.SESSION_ATTR_NAME, context);
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            assertEquals("authenticate", response.getRedirectedUrl());
            verify(filterChain, never()).doFilter(any(), any());
        }

        @Test
        @DisplayName("Should continue for NEW_SESSION_CREATED result")
        void shouldContinueForNewSessionCreated() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/action.do");
            setupValidApplicationContext(request, "testUser");
            MockHttpServletResponse response = new MockHttpServletResponse();

            when(userSessionService.checkAndManageSession(anyString(), anyString(), any()))
                    .thenReturn(UserSessionService.SessionCheckResult.NEW_SESSION_CREATED);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should continue for SESSION_EXPIRED_RECREATED result")
        void shouldContinueForSessionExpiredRecreated() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/action.do");
            setupValidApplicationContext(request, "testUser");
            MockHttpServletResponse response = new MockHttpServletResponse();

            when(userSessionService.checkAndManageSession(anyString(), anyString(), any()))
                    .thenReturn(UserSessionService.SessionCheckResult.SESSION_EXPIRED_RECREATED);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should continue for CONCURRENT_LOGIN_CONTINUED result")
        void shouldContinueForConcurrentLoginContinued() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/action.do");
            setupValidApplicationContext(request, "testUser");
            MockHttpServletResponse response = new MockHttpServletResponse();

            when(userSessionService.checkAndManageSession(anyString(), anyString(), any()))
                    .thenReturn(UserSessionService.SessionCheckResult.CONCURRENT_LOGIN_CONTINUED);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should forward to sessionInvalidate for CONCURRENT_LOGIN_DETECTED")
        void shouldForwardForConcurrentLoginDetected() throws ServletException, IOException {
            MockHttpServletRequest request = spy(new MockHttpServletRequest());
            request.setRequestURI("/app/action.do");
            setupValidApplicationContext(request, "testUser");
            MockHttpServletResponse response = new MockHttpServletResponse();
            
            RequestDispatcher dispatcher = mock(RequestDispatcher.class);
            when(request.getRequestDispatcher("sessionInvalidate")).thenReturn(dispatcher);

            when(userSessionService.checkAndManageSession(anyString(), anyString(), any()))
                    .thenReturn(UserSessionService.SessionCheckResult.CONCURRENT_LOGIN_DETECTED);

            filter.doFilterInternal(request, response, filterChain);

            verify(dispatcher).forward(request, response);
            verify(filterChain, never()).doFilter(any(), any());
        }

        @Test
        @DisplayName("Should forward to cancleRelogin for CONCURRENT_LOGIN_CANCELLED")
        void shouldForwardForConcurrentLoginCancelled() throws ServletException, IOException {
            MockHttpServletRequest request = spy(new MockHttpServletRequest());
            request.setRequestURI("/app/action.do");
            setupValidApplicationContext(request, "testUser");
            MockHttpServletResponse response = new MockHttpServletResponse();
            
            RequestDispatcher dispatcher = mock(RequestDispatcher.class);
            when(request.getRequestDispatcher("cancleRelogin")).thenReturn(dispatcher);

            when(userSessionService.checkAndManageSession(anyString(), anyString(), any()))
                    .thenReturn(UserSessionService.SessionCheckResult.CONCURRENT_LOGIN_CANCELLED);

            filter.doFilterInternal(request, response, filterChain);

            verify(dispatcher).forward(request, response);
            verify(filterChain, never()).doFilter(any(), any());
        }
    }

    @Nested
    @DisplayName("Punch-In Path Tests")
    class PunchInPathTests {

        @BeforeEach
        void setUpPunchIn() {
            // Disable concurrent login check to simplify punch-in testing
            filter.setConcurrentLoginCheckEnabled(false);
        }

        @Test
        @DisplayName("Should save punch-in path to session")
        void shouldSavePunchInPath() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/alertDetail.do");
            request.setQueryString("bmAction=pin&id=123");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            assertEquals("/app/alertDetail.do", request.getSession().getAttribute("punchInPath"));
            assertEquals("bmAction=pin&id=123", request.getSession().getAttribute("punchInQuery"));
        }

        @Test
        @DisplayName("Should handle context path in punch-in")
        void shouldHandleContextPathInPunchIn() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setContextPath("/scplatform");
            request.setRequestURI("/scplatform/alertDetail.do");
            request.setQueryString("action=pin");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            assertEquals("/alertDetail.do", request.getSession().getAttribute("punchInPath"));
        }

        @Test
        @DisplayName("Should not save punch-in path without pin reference")
        void shouldNotSavePunchInWithoutPin() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/normal.do");
            request.setQueryString("id=123");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            assertNull(request.getSession(false));
        }

        @Test
        @DisplayName("Should not save punch-in path without query string")
        void shouldNotSavePunchInWithoutQueryString() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/action.do");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            assertNull(request.getSession(false));
        }
    }

    @Nested
    @DisplayName("Should Not Filter Tests")
    class ShouldNotFilterTests {

        @Test
        @DisplayName("Should not filter when disabled")
        void shouldNotFilterWhenDisabled() {
            filter.setEnabled(false);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/test");

            assertTrue(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should not filter CSS files")
        void shouldNotFilterCssFiles() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/css/style.css");

            assertTrue(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should not filter JavaScript files")
        void shouldNotFilterJsFiles() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/js/app.js");

            assertTrue(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should not filter image files")
        void shouldNotFilterImageFiles() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            
            request.setRequestURI("/images/logo.png");
            assertTrue(filter.shouldNotFilter(request));
            
            request.setRequestURI("/images/photo.jpg");
            assertTrue(filter.shouldNotFilter(request));
            
            request.setRequestURI("/images/icon.gif");
            assertTrue(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should not filter font files")
        void shouldNotFilterFontFiles() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            
            request.setRequestURI("/fonts/arial.woff");
            assertTrue(filter.shouldNotFilter(request));
            
            request.setRequestURI("/fonts/arial.woff2");
            assertTrue(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should not filter favicon")
        void shouldNotFilterFavicon() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/favicon.ico");

            assertTrue(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should filter normal requests")
        void shouldFilterNormalRequests() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/dashboard");

            assertFalse(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should filter action requests")
        void shouldFilterActionRequests() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/submitForm.do");

            assertFalse(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should not filter when URI is empty")
        void shouldNotFilterWhenUriIsEmpty() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("");

            assertTrue(filter.shouldNotFilter(request));
        }
    }

    @Nested
    @DisplayName("Custom Ignore Pattern Tests")
    class CustomIgnorePatternTests {

        @BeforeEach
        void setUpPatterns() {
            filter.setConcurrentLoginCheckEnabled(false);
        }

        @Test
        @DisplayName("Should ignore paths matching custom pattern")
        void shouldIgnoreMatchingPattern() throws ServletException, IOException {
            filter.setIgnorePatternConfig(".*health.*");
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/actuator/health");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should ignore multiple patterns")
        void shouldIgnoreMultiplePatterns() throws ServletException, IOException {
            filter.setIgnorePatternConfig(".*health.*;.*metrics.*");
            
            MockHttpServletRequest request1 = new MockHttpServletRequest();
            request1.setRequestURI("/actuator/health");
            MockHttpServletResponse response1 = new MockHttpServletResponse();
            
            MockHttpServletRequest request2 = new MockHttpServletRequest();
            request2.setRequestURI("/actuator/metrics");
            MockHttpServletResponse response2 = new MockHttpServletResponse();

            filter.doFilterInternal(request1, response1, filterChain);
            filter.doFilterInternal(request2, response2, filterChain);

            verify(filterChain, times(2)).doFilter(any(), any());
        }

        @Test
        @DisplayName("Should handle empty pattern config")
        void shouldHandleEmptyPatternConfig() {
            filter.setIgnorePatternConfig("");
            assertFalse(filter.ignorePath("/app/dashboard"));
        }

        @Test
        @DisplayName("Should handle invalid pattern gracefully")
        void shouldHandleInvalidPatternGracefully() {
            // Invalid regex pattern - unmatched brackets
            assertDoesNotThrow(() -> filter.setIgnorePatternConfig("[invalid"));
        }
    }

    @Nested
    @DisplayName("Getter/Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set enabled override")
        void shouldSetEnabledOverride() {
            filter.setEnabled(false);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/test");

            assertTrue(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should set concurrent login check enabled override")
        void shouldSetConcurrentLoginCheckEnabledOverride() throws ServletException, IOException {
            filter.setConcurrentLoginCheckEnabled(false);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/app/action.do");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            // Should proceed without checking session
            verify(filterChain).doFilter(request, response);
            verifyNoInteractions(userSessionService);
        }
    }
}
