/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.security.filter;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the RestApiAuthFilter class.
 * 
 * @author PCM Security Team
 */
@DisplayName("REST API Auth Filter Tests")
class RestApiAuthFilterTest {

    private RestApiAuthFilter filter;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PcmConfigUtil configUtil;

    @Mock
    private UsersRepository usersRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Default: auth enabled
        when(configUtil.getBooleanValue("scplatform.restapi.auth.enabled", true)).thenReturn(true);
        // Default: return valid user for any userId
        when(usersRepository.findByUserId(anyString())).thenReturn(Optional.of(new Users()));
        filter = new RestApiAuthFilter(configUtil, usersRepository);
    }

    @Nested
    @DisplayName("Internal Request Tests")
    class InternalRequestTests {

        @Test
        @DisplayName("Should pass through internal requests without authentication")
        void shouldPassThroughInternalRequests() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            // No x-forwarded-host header = internal request

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertEquals(200, response.getStatus());
        }

        @Test
        @DisplayName("Should pass through when x-forwarded-host is null")
        void shouldPassThroughWhenNoProxyHost() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("External Request Tests")
    class ExternalRequestTests {

        @Test
        @DisplayName("Should allow external request with valid iv-user")
        void shouldAllowExternalRequestWithValidUser() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("x-forwarded-host", "proxy.example.com");
            request.addHeader("iv-user", "validuser");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(usersRepository).findByUserId("validuser");
            verify(filterChain).doFilter(request, response);
            assertEquals(200, response.getStatus());
        }

        @Test
        @DisplayName("Should reject external request with invalid iv-user")
        void shouldRejectExternalRequestWithInvalidUser() throws ServletException, IOException {
            when(usersRepository.findByUserId("invaliduser")).thenReturn(Optional.empty());
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("x-forwarded-host", "proxy.example.com");
            request.addHeader("iv-user", "invaliduser");
            request.setRequestURI("/mcm/api/test");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(usersRepository).findByUserId("invaliduser");
            verify(filterChain, never()).doFilter(request, response);
            assertEquals(403, response.getStatus());
        }

        @Test
        @DisplayName("Should reject external request without iv-user header")
        void shouldRejectExternalRequestWithoutIvUser() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("x-forwarded-host", "proxy.example.com");
            request.setRequestURI("/api/test");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain, never()).doFilter(request, response);
            assertEquals(403, response.getStatus());
        }

        @Test
        @DisplayName("Should reject external request with empty iv-user")
        void shouldRejectExternalRequestWithEmptyIvUser() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("x-forwarded-host", "proxy.example.com");
            request.addHeader("iv-user", "");
            request.setRequestURI("/api/test");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain, never()).doFilter(request, response);
            assertEquals(403, response.getStatus());
        }
    }

    @Nested
    @DisplayName("Auth Disabled Tests")
    class AuthDisabledTests {

        @Test
        @DisplayName("Should pass through external request when auth is disabled")
        void shouldPassThroughWhenAuthDisabled() throws ServletException, IOException {
            when(configUtil.getBooleanValue("scplatform.restapi.auth.enabled", true)).thenReturn(false);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("x-forwarded-host", "proxy.example.com");
            // No iv-user header
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertEquals(200, response.getStatus());
        }

        @Test
        @DisplayName("Should not require iv-user when auth is disabled")
        void shouldNotRequireIvUserWhenAuthDisabled() throws ServletException, IOException {
            when(configUtil.getBooleanValue("scplatform.restapi.auth.enabled", true)).thenReturn(false);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("x-forwarded-host", "proxy.example.com");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Combined Scenario Tests")
    class CombinedScenarioTests {

        @Test
        @DisplayName("Should handle multiple headers correctly")
        void shouldHandleMultipleHeaders() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("x-forwarded-host", "proxy.example.com");
            request.addHeader("x-forwarded-for", "192.168.1.1");
            request.addHeader("iv-user", "testuser");
            request.addHeader("Content-Type", "application/json");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should handle request with only x-forwarded-host and iv-user")
        void shouldHandleMinimalExternalRequest() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("x-forwarded-host", "proxy.example.com");
            request.addHeader("iv-user", "user123");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertEquals(200, response.getStatus());
        }

        @Test
        @DisplayName("Should reject and not call filter chain for forbidden request")
        void shouldNotCallFilterChainForForbiddenRequest() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("x-forwarded-host", "proxy.example.com");
            request.setRequestURI("/api/secure");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain, never()).doFilter(any(), any());
            assertEquals(403, response.getStatus());
        }
    }

    @Nested
    @DisplayName("Should Not Filter Tests")
    class ShouldNotFilterTests {

        @Test
        @DisplayName("Should filter requests matching /mcm/api/ pattern")
        void shouldFilterApiRequests() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/mcm/api/users");
            assertFalse(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should not filter requests not matching /mcm/api/ pattern")
        void shouldNotFilterNonApiRequests() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/other/path");
            assertTrue(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should not filter when URI is null")
        void shouldNotFilterWhenUriIsNull() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            assertTrue(filter.shouldNotFilter(request));
        }
    }
}
