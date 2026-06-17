/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.security.filter;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UnicodeFilter class.
 * 
 * @author PCM Security Team
 */
@DisplayName("Unicode Filter Tests")
class UnicodeFilterTest {

    private UnicodeFilter filter;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new UnicodeFilter();
        filter.setEncoding("UTF-8");
        filter.setEnabled(true);
    }

    @Nested
    @DisplayName("Filter Enabled/Disabled Tests")
    class FilterEnabledTests {

        @Test
        @DisplayName("Should set encoding when filter is enabled")
        void shouldSetEncodingWhenEnabled() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            assertEquals("UTF-8", request.getCharacterEncoding());
            assertEquals("UTF-8", response.getCharacterEncoding());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should pass through when filter is disabled")
        void shouldPassThroughWhenDisabled() throws ServletException, IOException {
            filter.setEnabled(false);
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should not filter when disabled")
        void shouldNotFilterWhenDisabled() {
            filter.setEnabled(false);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/saveExceptionRequest.do");

            assertTrue(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should filter when enabled and URL matches")
        void shouldFilterWhenEnabledAndUrlMatches() {
            filter.setEnabled(true);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/saveExceptionRequest.do");

            assertFalse(filter.shouldNotFilter(request));
        }
    }

    @Nested
    @DisplayName("URL Pattern Matching Tests")
    class UrlPatternMatchingTests {

        @Test
        @DisplayName("Should filter saveExceptionRequest.do")
        void shouldFilterSaveExceptionRequest() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/saveExceptionRequest.do");

            assertFalse(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should filter downloadODMEmail.do")
        void shouldFilterDownloadODMEmail() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/downloadODMEmail.do");

            assertFalse(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should filter processExceptionRequest")
        void shouldFilterProcessExceptionRequest() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/processExceptionRequest");

            assertFalse(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should filter submitExceptionRequest")
        void shouldFilterSubmitExceptionRequest() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/submitExceptionRequest");

            assertFalse(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should filter URL with context path prefix")
        void shouldFilterUrlWithContextPath() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/myapp/saveExceptionRequest.do");

            assertFalse(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should not filter unmatched URL")
        void shouldNotFilterUnmatchedUrl() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/someOtherEndpoint.do");

            assertTrue(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should not filter when URI is null")
        void shouldNotFilterWhenUriIsNull() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            // Don't set request URI, it will be null

            assertTrue(filter.shouldNotFilter(request));
        }

        @Test
        @DisplayName("Should not filter root URL")
        void shouldNotFilterRootUrl() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/");

            assertTrue(filter.shouldNotFilter(request));
        }
    }

    @Nested
    @DisplayName("Encoding Configuration Tests")
    class EncodingConfigurationTests {

        @Test
        @DisplayName("Should use configured encoding")
        void shouldUseConfiguredEncoding() throws ServletException, IOException {
            filter.setEncoding("ISO-8859-1");
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            assertEquals("ISO-8859-1", request.getCharacterEncoding());
            assertEquals("ISO-8859-1", response.getCharacterEncoding());
        }

        @Test
        @DisplayName("Should not override existing request encoding")
        void shouldNotOverrideExistingRequestEncoding() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setCharacterEncoding("ISO-8859-1");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            // Request encoding should remain unchanged
            assertEquals("ISO-8859-1", request.getCharacterEncoding());
            // Response encoding should be set to filter's encoding
            assertEquals("UTF-8", response.getCharacterEncoding());
        }

        @Test
        @DisplayName("Should always set response encoding")
        void shouldAlwaysSetResponseEncoding() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            response.setCharacterEncoding("ISO-8859-1");

            filter.doFilterInternal(request, response, filterChain);

            assertEquals("UTF-8", response.getCharacterEncoding());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set encoding")
        void shouldGetAndSetEncoding() {
            filter.setEncoding("UTF-16");
            assertEquals("UTF-16", filter.getEncoding());
        }

        @Test
        @DisplayName("Should get and set enabled")
        void shouldGetAndSetEnabled() {
            filter.setEnabled(false);
            assertFalse(filter.isEnabled());

            filter.setEnabled(true);
            assertTrue(filter.isEnabled());
        }
    }

    @Nested
    @DisplayName("Filter Chain Tests")
    class FilterChainTests {

        @Test
        @DisplayName("Should always call filter chain")
        void shouldAlwaysCallFilterChain() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("Should call filter chain even when disabled")
        void shouldCallFilterChainWhenDisabled() throws ServletException, IOException {
            filter.setEnabled(false);
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Different Encoding Tests")
    class DifferentEncodingTests {

        @Test
        @DisplayName("Should handle UTF-16 encoding")
        void shouldHandleUtf16Encoding() throws ServletException, IOException {
            filter.setEncoding("UTF-16");
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            assertEquals("UTF-16", request.getCharacterEncoding());
            assertEquals("UTF-16", response.getCharacterEncoding());
        }

        @Test
        @DisplayName("Should handle ISO-8859-1 encoding")
        void shouldHandleIso88591Encoding() throws ServletException, IOException {
            filter.setEncoding("ISO-8859-1");
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            assertEquals("ISO-8859-1", request.getCharacterEncoding());
            assertEquals("ISO-8859-1", response.getCharacterEncoding());
        }
    }
}
