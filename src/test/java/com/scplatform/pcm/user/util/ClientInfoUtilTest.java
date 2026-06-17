/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.user.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientInfoUtilTest {

    private HttpServletRequest req(String userAgent, String... headerPairs) {
        HttpServletRequest r = mock(HttpServletRequest.class);
        when(r.getHeader("User-Agent")).thenReturn(userAgent);
        for (int i = 0; i + 1 < headerPairs.length; i += 2) {
            when(r.getHeader(headerPairs[i])).thenReturn(headerPairs[i + 1]);
        }
        return r;
    }

    @Test
    void privateConstructor_isPrivate_andCanBeInvokedReflectively() throws Exception {
        Constructor<ClientInfoUtil> c = ClientInfoUtil.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(c.getModifiers()));
        c.setAccessible(true);
        try {
            assertNotNull(c.newInstance());
        } catch (InvocationTargetException e) {
            // Acceptable - some utility classes throw in private constructor
        }
    }

    // ---- getClientOS ----
    @ParameterizedTest
    @CsvSource({
            "'Mozilla/5.0 (Windows NT 10.0)','Windows'",
            "'Mozilla/5.0 (Macintosh; Intel Mac)','Mac'",
            "'Mozilla/5.0 (X11; Linux x86_64)','Unix'",
            "'Mozilla/5.0 (Linux; Android 10)','Android'",
            "'Mozilla/5.0 (iPhone; CPU iPhone OS)','iPhone'",
            "'totally-unknown-agent','Unknown'"
    })
    void getClientOS_detectsByUserAgent(String ua, String expected) {
        assertEquals(expected, ClientInfoUtil.getClientOS(req(ua)));
    }

    @Test
    void getClientOS_returnsUnknownWhenHeaderMissing() {
        assertEquals("Unknown", ClientInfoUtil.getClientOS(req(null)));
    }

    // ---- getClientBrowser ----
    @Test
    void getClientBrowser_detectsEdge() {
        String ua = "Mozilla/5.0 (Windows) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.2210.91";
        String b = ClientInfoUtil.getClientBrowser(req(ua));
        assertTrue(b.startsWith("Edg"), b);
        assertTrue(b.contains("-"));
    }

    @Test
    void getClientBrowser_detectsChrome() {
        String ua = "Mozilla/5.0 Chrome/100.0.0 Safari/537.36";
        String b = ClientInfoUtil.getClientBrowser(req(ua));
        assertTrue(b.startsWith("Chrome"));
    }

    @Test
    void getClientBrowser_detectsFirefox() {
        String ua = "Mozilla/5.0 Firefox/115.0";
        assertEquals("Firefox-115.0", ClientInfoUtil.getClientBrowser(req(ua)));
    }

    @Test
    void getClientBrowser_detectsMSIEAndTrident() {
        String ie = "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2)";
        assertTrue(ClientInfoUtil.getClientBrowser(req(ie)).startsWith("IE-"));
        String trident = "Mozilla/5.0 (Windows NT 10.0; Trident/7.0; rv:11.0) like Gecko";
        assertEquals("IE-11", ClientInfoUtil.getClientBrowser(req(trident)));
    }

    @Test
    void getClientBrowser_detectsOpera() {
        String opr = "Mozilla/5.0 Chrome/100.0 OPR/85.0.4341.60";
        assertTrue(ClientInfoUtil.getClientBrowser(req(opr)).startsWith("Opera"));

        String opera = "Opera/9.80 (Windows NT 6.0)";
        assertTrue(ClientInfoUtil.getClientBrowser(req(opera)).contains("Opera"));
    }

    @Test
    void getClientBrowser_detectsSafari() {
        String safari = "Mozilla/5.0 (Macintosh) AppleWebKit Version/16.1 Safari/605.1.15";
        assertTrue(ClientInfoUtil.getClientBrowser(req(safari)).startsWith("Safari"));
    }

    @Test
    void getClientBrowser_returnsUnknownWhenNoneMatch() {
        assertEquals("Unknown", ClientInfoUtil.getClientBrowser(req("no-known-tokens")));
        assertEquals("Unknown", ClientInfoUtil.getClientBrowser(req(null)));
    }

    // ---- getClientIpAddress ----
    @Test
    void getClientIpAddress_prefersXForwardedFor() {
        HttpServletRequest r = mock(HttpServletRequest.class);
        when(r.getHeader("X-Forwarded-For")).thenReturn("1.2.3.4");
        assertEquals("1.2.3.4", ClientInfoUtil.getClientIpAddress(r));
    }

    @Test
    void getClientIpAddress_fallsThroughHeaderChainToRemoteAddr() {
        HttpServletRequest r = mock(HttpServletRequest.class);
        when(r.getHeader("X-Forwarded-For")).thenReturn(null);
        when(r.getHeader("Proxy-Client-IP")).thenReturn("unknown");      // treated blank
        when(r.getHeader("WL-Proxy-Client-IP")).thenReturn("");          // blank
        when(r.getHeader("HTTP_CLIENT_IP")).thenReturn(null);
        when(r.getHeader("HTTP_X_FORWARDED_FOR")).thenReturn(null);
        when(r.getRemoteAddr()).thenReturn("9.9.9.9");
        assertEquals("9.9.9.9", ClientInfoUtil.getClientIpAddress(r));
    }

    @Test
    void getClientIpAddress_picksMidChainHeader() {
        HttpServletRequest r = mock(HttpServletRequest.class);
        when(r.getHeader("X-Forwarded-For")).thenReturn(null);
        when(r.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(r.getHeader("WL-Proxy-Client-IP")).thenReturn("5.6.7.8");
        assertEquals("5.6.7.8", ClientInfoUtil.getClientIpAddress(r));
    }

    // ---- getSessionId ----
    @Test
    void getSessionId_returnsHttpSessionId() {
        HttpServletRequest r = mock(HttpServletRequest.class);
        HttpSession sess = mock(HttpSession.class);
        when(r.getSession()).thenReturn(sess);
        when(sess.getId()).thenReturn("S123");
        assertEquals("S123", ClientInfoUtil.getSessionId(r));
    }

    // ---- private helper edge cases (force fallback / catch branches) ----

    @Test
    void extractBrowserVersion_returnsBrowserName_whenCaseMismatchYieldsNoIndex() {
        // lowerCaseAgent.contains("edg") true, but userAgent.indexOf("Edg") == -1 -> falls through to bare name
        String ua = "mozilla/5.0 chromium edg/120.0.0.0";
        assertEquals("Edg", ClientInfoUtil.getClientBrowser(req(ua)));
    }

    @Test
    void extractBrowserVersion_catchBranch_viaReflectionWithNullUserAgent() throws Exception {
        java.lang.reflect.Method m = ClientInfoUtil.class.getDeclaredMethod(
                "extractBrowserVersion", String.class, String.class);
        m.setAccessible(true);
        // userAgent.indexOf(...) on null throws NPE -> caught -> returns browserName
        assertEquals("Chrome", m.invoke(null, null, "Chrome"));
    }

    @Test
    void extractIEVersion_catchBranch_whenNoSpaceAfterMSIE() {
        // "MSIE10.0" has no space; substring(idx).split(" ")[1] throws -> catch returns "IE"
        String ua = "Mozilla/5.0 (compatible; MSIE10.0)";
        assertEquals("IE", ClientInfoUtil.getClientBrowser(req(ua)));
    }

    @Test
    void extractOperaVersion_catchBranch_whenLowercaseOprMissingFromOriginal() {
        // lowerCaseAgent.contains("opr") true, indexOf("OPR") == -1 -> substring(-1) throws -> "Opera"
        String ua = "Mozilla/5.0 chromium opr/55.0.2994.61";
        assertEquals("Opera", ClientInfoUtil.getClientBrowser(req(ua)));
    }

    @Test
    void extractSafariVersion_catchBranch_whenVersionLacksSlash() {
        // contains "safari" + contains "version" but Version10 has no slash -> split("/")[1] throws -> "Safari"
        String ua = "Mozilla/5.0 safari Version10";
        assertEquals("Safari", ClientInfoUtil.getClientBrowser(req(ua)));
    }

    // ---- getClientUniqueId ----
    @Test
    void getClientUniqueId_combinesBrowserOsAndSession() {
        HttpServletRequest r = mock(HttpServletRequest.class);
        HttpSession sess = mock(HttpSession.class);
        when(r.getHeader("User-Agent")).thenReturn(
                "Mozilla/5.0 (Windows NT 10.0) Chrome/100.0.0 Safari/537.36");
        when(r.getSession()).thenReturn(sess);
        when(sess.getId()).thenReturn("ABC");
        String uid = ClientInfoUtil.getClientUniqueId(r);
        assertNotNull(uid);
        assertTrue(uid.endsWith("ABC"));
    }
}
