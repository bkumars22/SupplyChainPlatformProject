/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.security.filter;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the XssRequestWrapper class.
 * 
 * @author PCM Security Team
 */
@DisplayName("XSS Request Wrapper Tests")
class XssRequestWrapperTest {

    private XssSanitizer sanitizer;
    private MockHttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        sanitizer = new XssSanitizer();
        mockRequest = new MockHttpServletRequest();
    }

    @Nested
    @DisplayName("Parameter Sanitization Tests")
    class ParameterSanitizationTests {

        @Test
        @DisplayName("Should sanitize single parameter value")
        void shouldSanitizeSingleParameter() {
            mockRequest.setParameter("name", "<script>alert('xss')</script>");
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            assertEquals("", wrapper.getParameter("name"));
        }

        @Test
        @DisplayName("Should return null for non-existent parameter")
        void shouldReturnNullForNonExistentParameter() {
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            assertNull(wrapper.getParameter("nonexistent"));
        }

        @Test
        @DisplayName("Should sanitize parameter values array")
        void shouldSanitizeParameterValuesArray() {
            mockRequest.addParameter("items", "<script>bad</script>");
            mockRequest.addParameter("items", "good");
            mockRequest.addParameter("items", "onclick=evil");
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            String[] values = wrapper.getParameterValues("items");
            assertNotNull(values);
            assertEquals(3, values.length);
            assertEquals("", values[0]);
            assertEquals("good", values[1]);
            assertFalse(values[2].contains("onclick"));
        }

        @Test
        @DisplayName("Should return null for non-existent parameter values")
        void shouldReturnNullForNonExistentParameterValues() {
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            assertNull(wrapper.getParameterValues("nonexistent"));
        }

        @Test
        @DisplayName("Should sanitize parameter map")
        void shouldSanitizeParameterMap() {
            mockRequest.setParameter("safe", "hello");
            mockRequest.setParameter("unsafe", "<script>alert('xss')</script>");
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            Map<String, String[]> paramMap = wrapper.getParameterMap();
            
            assertNotNull(paramMap);
            assertEquals("hello", paramMap.get("safe")[0]);
            assertEquals("", paramMap.get("unsafe")[0]);
        }

        @Test
        @DisplayName("Should return immutable parameter map")
        void shouldReturnImmutableParameterMap() {
            mockRequest.setParameter("test", "value");
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            Map<String, String[]> paramMap = wrapper.getParameterMap();
            
            assertThrows(UnsupportedOperationException.class, () -> {
                paramMap.put("new", new String[]{"value"});
            });
        }
    }

    @Nested
    @DisplayName("Parameter Exclusion Tests")
    class ParameterExclusionTests {

        @Test
        @DisplayName("Should exclude specified parameters from sanitization")
        void shouldExcludeSpecifiedParameters() {
            mockRequest.setParameter("signature", "<script>alert('xss')</script>");
            mockRequest.setParameter("normal", "<script>alert('xss')</script>");
            
            XssRequestWrapper wrapper = new XssRequestWrapper(
                    mockRequest, sanitizer, true, true,
                    Set.of("signature"), Collections.emptySet());

            // Excluded parameter should not be sanitized
            assertEquals("<script>alert('xss')</script>", wrapper.getParameter("signature"));
            // Normal parameter should be sanitized
            assertEquals("", wrapper.getParameter("normal"));
        }

        @Test
        @DisplayName("Should exclude parameter values array from sanitization")
        void shouldExcludeParameterValuesArray() {
            mockRequest.addParameter("hash", "<script>bad</script>");
            mockRequest.addParameter("hash", "another<script>");
            
            XssRequestWrapper wrapper = new XssRequestWrapper(
                    mockRequest, sanitizer, true, true,
                    Set.of("hash"), Collections.emptySet());

            String[] values = wrapper.getParameterValues("hash");
            assertEquals("<script>bad</script>", values[0]);
            assertEquals("another<script>", values[1]);
        }

        @Test
        @DisplayName("Should exclude parameters from map sanitization")
        void shouldExcludeParametersFromMapSanitization() {
            mockRequest.setParameter("excluded", "<script>test</script>");
            mockRequest.setParameter("included", "<script>test</script>");
            
            XssRequestWrapper wrapper = new XssRequestWrapper(
                    mockRequest, sanitizer, true, true,
                    Set.of("excluded"), Collections.emptySet());

            Map<String, String[]> paramMap = wrapper.getParameterMap();
            assertEquals("<script>test</script>", paramMap.get("excluded")[0]);
            assertEquals("", paramMap.get("included")[0]);
        }
    }

    @Nested
    @DisplayName("Header Sanitization Tests")
    class HeaderSanitizationTests {

        @Test
        @DisplayName("Should sanitize header values when enabled")
        void shouldSanitizeHeadersWhenEnabled() {
            mockRequest.addHeader("X-Custom", "<script>alert('xss')</script>");
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer, true, true,
                    Collections.emptySet(), Collections.emptySet());

            assertEquals("", wrapper.getHeader("X-Custom"));
        }

        @Test
        @DisplayName("Should not sanitize headers when disabled")
        void shouldNotSanitizeHeadersWhenDisabled() {
            mockRequest.addHeader("X-Custom", "<script>alert('xss')</script>");
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer, false, true,
                    Collections.emptySet(), Collections.emptySet());

            assertEquals("<script>alert('xss')</script>", wrapper.getHeader("X-Custom"));
        }

        @Test
        @DisplayName("Should exclude specified headers from sanitization")
        void shouldExcludeSpecifiedHeaders() {
            mockRequest.addHeader("Authorization", "Bearer <script>token</script>");
            mockRequest.addHeader("X-Custom", "<script>alert('xss')</script>");
            
            XssRequestWrapper wrapper = new XssRequestWrapper(
                    mockRequest, sanitizer, true, true,
                    Collections.emptySet(), Set.of("Authorization"));

            assertEquals("Bearer <script>token</script>", wrapper.getHeader("Authorization"));
            assertEquals("", wrapper.getHeader("X-Custom"));
        }

        @Test
        @DisplayName("Should sanitize multiple header values")
        void shouldSanitizeMultipleHeaderValues() {
            mockRequest.addHeader("X-Custom", "<script>first</script>");
            mockRequest.addHeader("X-Custom", "<script>second</script>");
            
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            Enumeration<String> headers = wrapper.getHeaders("X-Custom");
            int count = 0;
            while (headers.hasMoreElements()) {
                String value = headers.nextElement();
                assertEquals("", value);
                count++;
            }
            assertEquals(2, count);
        }

        @Test
        @DisplayName("Should handle null header value")
        void shouldHandleNullHeaderValue() {
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            assertNull(wrapper.getHeader("NonExistent"));
        }
    }

    @Nested
    @DisplayName("Cookie Sanitization Tests")
    class CookieSanitizationTests {

        @Test
        @DisplayName("Should sanitize cookie values when enabled")
        void shouldSanitizeCookiesWhenEnabled() {
            Cookie cookie = new Cookie("session", "<script>alert('xss')</script>");
            mockRequest.setCookies(cookie);
            
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer, true, true,
                    Collections.emptySet(), Collections.emptySet());

            Cookie[] cookies = wrapper.getCookies();
            assertNotNull(cookies);
            assertEquals(1, cookies.length);
            assertEquals("session", cookies[0].getName());
            assertEquals("", cookies[0].getValue());
        }

        @Test
        @DisplayName("Should not sanitize cookies when disabled")
        void shouldNotSanitizeCookiesWhenDisabled() {
            Cookie cookie = new Cookie("session", "<script>alert('xss')</script>");
            mockRequest.setCookies(cookie);
            
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer, true, false,
                    Collections.emptySet(), Collections.emptySet());

            Cookie[] cookies = wrapper.getCookies();
            assertNotNull(cookies);
            assertEquals("<script>alert('xss')</script>", cookies[0].getValue());
        }

        @Test
        @DisplayName("Should handle null cookies")
        void shouldHandleNullCookies() {
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            assertNull(wrapper.getCookies());
        }

        @Test
        @DisplayName("Should preserve cookie properties")
        void shouldPreserveCookieProperties() {
            Cookie cookie = new Cookie("test", "<script>value</script>");
            cookie.setDomain("example.com");
            cookie.setPath("/app");
            cookie.setMaxAge(3600);
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            mockRequest.setCookies(cookie);
            
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            Cookie[] cookies = wrapper.getCookies();
            Cookie sanitizedCookie = cookies[0];
            
            assertEquals("test", sanitizedCookie.getName());
            assertEquals("", sanitizedCookie.getValue()); // Sanitized
            assertEquals("example.com", sanitizedCookie.getDomain());
            assertEquals("/app", sanitizedCookie.getPath());
            assertEquals(3600, sanitizedCookie.getMaxAge());
            assertTrue(sanitizedCookie.getSecure());
            assertTrue(sanitizedCookie.isHttpOnly());
        }
    }

    @Nested
    @DisplayName("Query String Sanitization Tests")
    class QueryStringSanitizationTests {

        @Test
        @DisplayName("Should sanitize query string")
        void shouldSanitizeQueryString() {
            mockRequest.setQueryString("name=<script>alert('xss')</script>&id=123");
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            String queryString = wrapper.getQueryString();
            assertNotNull(queryString);
            assertFalse(queryString.contains("<script>"));
        }

        @Test
        @DisplayName("Should handle null query string")
        void shouldHandleNullQueryString() {
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            assertNull(wrapper.getQueryString());
        }

        @Test
        @DisplayName("Should preserve safe query string")
        void shouldPreserveSafeQueryString() {
            mockRequest.setQueryString("name=John&id=123&active=true");
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            assertEquals("name=John&id=123&active=true", wrapper.getQueryString());
        }
    }

    @Nested
    @DisplayName("Complex Scenario Tests")
    class ComplexScenarioTests {

        @Test
        @DisplayName("Should handle mixed safe and unsafe content")
        void shouldHandleMixedContent() {
            mockRequest.setParameter("message", "Hello <script>alert('xss')</script> World!");
            mockRequest.setParameter("name", "John Doe");
            mockRequest.addHeader("User-Agent", "Mozilla/5.0 Chrome");
            mockRequest.addHeader("X-Custom", "javascript:void(0)");
            
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            assertEquals("Hello  World!", wrapper.getParameter("message"));
            assertEquals("John Doe", wrapper.getParameter("name"));
            assertEquals("Mozilla/5.0 Chrome", wrapper.getHeader("User-Agent"));
            assertFalse(wrapper.getHeader("X-Custom").contains("javascript:"));
        }

        @Test
        @DisplayName("Should handle empty parameters")
        void shouldHandleEmptyParameters() {
            mockRequest.setParameter("empty", "");
            mockRequest.setParameter("whitespace", "   ");
            
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            assertEquals("", wrapper.getParameter("empty"));
            assertEquals("   ", wrapper.getParameter("whitespace"));
        }

        @Test
        @DisplayName("Should handle unicode content")
        void shouldHandleUnicodeContent() {
            mockRequest.setParameter("name", "日本語<script>alert(1)</script>テスト");
            XssRequestWrapper wrapper = new XssRequestWrapper(mockRequest, sanitizer);

            String result = wrapper.getParameter("name");
            assertTrue(result.contains("日本語"));
            assertTrue(result.contains("テスト"));
            assertFalse(result.contains("script"));
        }
    }
}
