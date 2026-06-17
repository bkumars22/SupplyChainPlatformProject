/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.scplatform.pcm.searchframework.dto.SearchDefinition;
import com.scplatform.pcm.searchframework.dto.SearchForm;

import jakarta.servlet.http.HttpSession;

class SessionSearchCacheTest {

    private SessionSearchCache cache;

    @BeforeEach
    void setUp() {
        cache = new SessionSearchCache();
    }

    @Test
    void cacheSearchFormStoresAttribute() {
        HttpSession session = org.mockito.Mockito.mock(HttpSession.class);
        SearchDefinition sd = new SearchDefinition();
        SearchForm sf = new SearchForm();

        cache.cacheSearchForm(session, "MyForm", 42L, sd, sf);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map> valCaptor = ArgumentCaptor.forClass(Map.class);
        verify(session).setAttribute(keyCaptor.capture(), valCaptor.capture());

        assertEquals("SEARCH_CACHE_MyForm_42", keyCaptor.getValue());
        Map<String, Object> data = valCaptor.getValue();
        assertEquals(sd, data.get("searchDefinition"));
        assertEquals(sf, data.get("searchForm"));
        assertNotNull(data.get("timestamp"));
    }

    @Test
    void cacheSearchFormSkipsOnNulls() {
        HttpSession session = org.mockito.Mockito.mock(HttpSession.class);
        cache.cacheSearchForm(null, "F", 1L, null, null);
        cache.cacheSearchForm(session, null, 1L, null, null);
        cache.cacheSearchForm(session, "F", null, null, null);
        verify(session, never()).setAttribute(anyString(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void getCachedSearchDefinitionReturnsStoredValue() {
        HttpSession session = org.mockito.Mockito.mock(HttpSession.class);
        SearchDefinition sd = new SearchDefinition();
        Map<String, Object> data = new HashMap<>();
        data.put("searchDefinition", sd);
        when(session.getAttribute("SEARCH_CACHE_F_5")).thenReturn(data);

        assertEquals(sd, cache.getCachedSearchDefinition(session, "F", 5L));
    }

    @Test
    void getCachedSearchDefinitionNullForMissingCache() {
        HttpSession session = org.mockito.Mockito.mock(HttpSession.class);
        assertNull(cache.getCachedSearchDefinition(session, "F", 5L));
        assertNull(cache.getCachedSearchDefinition(null, "F", 5L));
        assertNull(cache.getCachedSearchDefinition(session, null, 5L));
        assertNull(cache.getCachedSearchDefinition(session, "F", null));
    }

    @Test
    void getCachedSearchFormReturnsStoredValue() {
        HttpSession session = org.mockito.Mockito.mock(HttpSession.class);
        SearchForm sf = new SearchForm();
        Map<String, Object> data = new HashMap<>();
        data.put("searchForm", sf);
        when(session.getAttribute("SEARCH_CACHE_F_5")).thenReturn(data);

        SearchForm result = cache.getCachedSearchForm(session, "F", 5L);
        assertEquals(sf, result);
    }

    @Test
    void getCachedSearchFormNullForMissing() {
        HttpSession session = org.mockito.Mockito.mock(HttpSession.class);
        assertNull(cache.getCachedSearchForm(session, "F", 5L));
        assertNull(cache.getCachedSearchForm(null, "F", 5L));
    }

    @Test
    void getCachedFormDataReturnsStoredMap() {
        HttpSession session = org.mockito.Mockito.mock(HttpSession.class);
        Map<String, Object> data = new HashMap<>();
        when(session.getAttribute("SEARCH_CACHE_F_5")).thenReturn(data);
        assertEquals(data, cache.getCachedFormData(session, "F", 5L));
        assertNull(cache.getCachedFormData(null, "F", 5L));
    }

    @Test
    void clearCacheRemovesAttribute() {
        HttpSession session = org.mockito.Mockito.mock(HttpSession.class);
        cache.clearCache(session, "F", 5L);
        verify(session).removeAttribute("SEARCH_CACHE_F_5");
    }

    @Test
    void clearCacheNoopOnNulls() {
        HttpSession session = org.mockito.Mockito.mock(HttpSession.class);
        cache.clearCache(null, "F", 5L);
        cache.clearCache(session, null, 5L);
        cache.clearCache(session, "F", null);
        verify(session, never()).removeAttribute(anyString());
    }

    @Test
    void clearAllUserCachesRemovesAttributesEndingInUserPrefix() {
        // Implementation removes attributes whose name endsWith("SEARCH_CACHE_" + userId).
        HttpSession session = org.mockito.Mockito.mock(HttpSession.class);
        when(session.getAttributeNames()).thenReturn(
                Collections.enumeration(java.util.Arrays.asList(
                        "PREFIX_SEARCH_CACHE_5", "X_SEARCH_CACHE_5", "SEARCH_CACHE_F_6", "OTHER")));

        cache.clearAllUserCaches(session, 5L);

        verify(session).removeAttribute("PREFIX_SEARCH_CACHE_5");
        verify(session).removeAttribute("X_SEARCH_CACHE_5");
        verify(session, never()).removeAttribute("SEARCH_CACHE_F_6");
        verify(session, never()).removeAttribute("OTHER");
    }

    @Test
    void clearAllUserCachesNoopOnNulls() {
        HttpSession session = org.mockito.Mockito.mock(HttpSession.class);
        cache.clearAllUserCaches(null, 5L);
        cache.clearAllUserCaches(session, null);
        verify(session, never()).removeAttribute(anyString());
    }

    @Test
    void isCachedReturnsTrueWhenAttributePresent() {
        HttpSession session = org.mockito.Mockito.mock(HttpSession.class);
        when(session.getAttribute("SEARCH_CACHE_F_5")).thenReturn(new HashMap<>());
        assertTrue(cache.isCached(session, "F", 5L));
    }

    @Test
    void isCachedReturnsFalseWhenAbsentOrNullArgs() {
        HttpSession session = org.mockito.Mockito.mock(HttpSession.class);
        assertFalse(cache.isCached(session, "F", 5L));
        assertFalse(cache.isCached(null, "F", 5L));
        assertFalse(cache.isCached(session, null, 5L));
        assertFalse(cache.isCached(session, "F", null));
    }

    @Test
    void getCacheTimestampReturnsValueOrZero() {
        HttpSession session = org.mockito.Mockito.mock(HttpSession.class);
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", 12345L);
        when(session.getAttribute("SEARCH_CACHE_F_5")).thenReturn(data);
        assertEquals(12345L, cache.getCacheTimestamp(session, "F", 5L));

        when(session.getAttribute("SEARCH_CACHE_X_5")).thenReturn(null);
        assertEquals(0L, cache.getCacheTimestamp(session, "X", 5L));
    }
}
