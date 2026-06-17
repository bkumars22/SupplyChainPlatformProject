/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the XssFilter class.
 * 
 * @author PCM Security Team
 */
@DisplayName("XSS Filter Tests")
class XssFilterTest {

    private XssSanitizer sanitizer;
    private XssFilterProperties properties;
    private XssFilter filter;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sanitizer = new XssSanitizer();
        properties = new XssFilterProperties();
        filter = new XssFilter(sanitizer, properties);
    }

    @Nested
    @DisplayName("Filter Enabled/Disabled Tests")
    class FilterEnabledTests {

        @Test
        @DisplayName("Should filter requests when enabled")
        void shouldFilterWhenEnabled() throws ServletException, IOException {
            properties.setEnabled(true);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("test", "<script>alert('xss')</script>");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
            verify(filterChain).doFilter(requestCaptor.capture(), eq(response));
            
            HttpServletRequest wrappedRequest = requestCaptor.getValue();
            assertTrue(wrappedRequest instanceof XssRequestWrapper);
        }

        @Test
        @DisplayName("Should pass through when disabled")
        void shouldPassThroughWhenDisabled() throws ServletException, IOException {
            properties.setEnabled(false);
            filter = new XssFilter(sanitizer, properties);
            
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("test", "<script>alert('xss')</script>");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
            verify(filterChain).doFilter(requestCaptor.capture(), eq(response));
            
            HttpServletRequest passedRequest = requestCaptor.getValue();
            assertFalse(passedRequest instanceof XssRequestWrapper);
        }
    }

    @Nested
    @DisplayName("Security Headers Tests")
    class SecurityHeadersTests {

        @Test
        @DisplayName("Should add security headers when enabled")
        void shouldAddSecurityHeaders() throws ServletException, IOException {
            properties.setAddSecurityHeaders(true);
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            assertEquals("1; mode=block", response.getHeader("X-XSS-Protection"));
            assertEquals("nosniff", response.getHeader("X-Content-Type-Options"));
            assertEquals("strict-origin-when-cross-origin", response.getHeader("Referrer-Policy"));
        }

        @Test
        @DisplayName("Should not add security headers when disabled")
        void shouldNotAddSecurityHeadersWhenDisabled() throws ServletException, IOException {
            properties.setAddSecurityHeaders(false);
            filter = new XssFilter(sanitizer, properties);
            
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            assertNull(response.getHeader("X-XSS-Protection"));
        }

        @Test
        @DisplayName("Should add CSP header when enabled")
        void shouldAddCspHeader() throws ServletException, IOException {
            properties.setAddContentSecurityPolicy(true);
            properties.setContentSecurityPolicy("default-src 'self'");
            filter = new XssFilter(sanitizer, properties);
            
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            assertEquals("default-src 'self'", response.getHeader("Content-Security-Policy"));
        }
    }

    @Nested
    @DisplayName("URL Exclusion Tests")
    class UrlExclusionTests {

        @Test
        @DisplayName("Should exclude URL patterns from filtering")
        void shouldExcludeUrlPatterns() throws ServletException, IOException {
            properties.setExcludedUrlPatterns(Set.of("/api/webhook/**"));
            filter = new XssFilter(sanitizer, properties);
            
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/webhook/github");
            request.setParameter("test", "<script>alert('xss')</script>");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
            verify(filterChain).doFilter(requestCaptor.capture(), eq(response));
            
            HttpServletRequest passedRequest = requestCaptor.getValue();
            assertFalse(passedRequest instanceof XssRequestWrapper);
        }

        @Test
        @DisplayName("Should not exclude non-matching URLs")
        void shouldNotExcludeNonMatchingUrls() throws ServletException, IOException {
            properties.setExcludedUrlPatterns(Set.of("/api/webhook/**"));
            filter = new XssFilter(sanitizer, properties);
            
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/users");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
            verify(filterChain).doFilter(requestCaptor.capture(), eq(response));
            
            assertTrue(requestCaptor.getValue() instanceof XssRequestWrapper);
        }

        @Test
        @DisplayName("Should handle exact URL match")
        void shouldHandleExactUrlMatch() throws ServletException, IOException {
            properties.setExcludedUrlPatterns(Set.of("/api/special"));
            filter = new XssFilter(sanitizer, properties);
            
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/special");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(argThat(req -> !(req instanceof XssRequestWrapper)), eq(response));
        }
    }

    @Nested
    @DisplayName("HTTP Method Exclusion Tests")
    class HttpMethodExclusionTests {

        @Test
        @DisplayName("Should exclude specified HTTP methods")
        void shouldExcludeHttpMethods() throws ServletException, IOException {
            properties.setExcludedMethods(Set.of("OPTIONS"));
            filter = new XssFilter(sanitizer, properties);
            
            MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/test");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(argThat(req -> !(req instanceof XssRequestWrapper)), eq(response));
        }

        @Test
        @DisplayName("Should filter non-excluded HTTP methods")
        void shouldFilterNonExcludedMethods() throws ServletException, IOException {
            properties.setExcludedMethods(Set.of("OPTIONS"));
            filter = new XssFilter(sanitizer, properties);
            
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/test");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(argThat(req -> req instanceof XssRequestWrapper), eq(response));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should sanitize XSS in request parameters")
        void shouldSanitizeXssInParameters() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("name", "<script>alert('xss')</script>John");
            request.setParameter("comment", "Hello <img src=x onerror=alert(1)> World");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
            verify(filterChain).doFilter(requestCaptor.capture(), eq(response));
            
            HttpServletRequest wrappedRequest = requestCaptor.getValue();
            assertEquals("John", wrappedRequest.getParameter("name"));
            // onerror event handler is removed; unquoted src=x is kept (sanitizer only removes quoted src)
            assertEquals("Hello <img src=x > World", wrappedRequest.getParameter("comment"));
        }

        @Test
        @DisplayName("Should handle multiple parameters")
        void shouldHandleMultipleParameters() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addParameter("values", "<script>bad</script>");
            request.addParameter("values", "normal");
            request.addParameter("values", "onclick=bad");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
            verify(filterChain).doFilter(requestCaptor.capture(), eq(response));
            
            HttpServletRequest wrappedRequest = requestCaptor.getValue();
            String[] values = wrappedRequest.getParameterValues("values");
            
            assertNotNull(values);
            assertEquals(3, values.length);
            assertEquals("", values[0]);
            assertEquals("normal", values[1]);
            assertFalse(values[2].contains("onclick"));
        }

        @Test
        @DisplayName("Should preserve safe parameters")
        void shouldPreserveSafeParameters() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("email", "user@example.com");
            request.setParameter("message", "Hello, this is a normal message!");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilterInternal(request, response, filterChain);

            ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
            verify(filterChain).doFilter(requestCaptor.capture(), eq(response));
            
            HttpServletRequest wrappedRequest = requestCaptor.getValue();
            assertEquals("user@example.com", wrappedRequest.getParameter("email"));
            assertEquals("Hello, this is a normal message!", wrappedRequest.getParameter("message"));
        }
    }

    @Nested
    @DisplayName("Filter Configuration Tests")
    class ConfigurationTests {

        @Test
        @DisplayName("Should use correct filter name")
        void shouldUseCorrectFilterName() {
            assertEquals("XssProtectionFilter", filter.getFilterName());
        }

        @Test
        @DisplayName("Should respect shouldNotFilter")
        void shouldRespectShouldNotFilter() {
            properties.setEnabled(true);
            MockHttpServletRequest request = new MockHttpServletRequest();
            assertFalse(filter.shouldNotFilter(request));

            properties.setEnabled(false);
            assertTrue(filter.shouldNotFilter(request));
        }
    }
}
