/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.cache;

import com.scplatform.pcm.searchframework.dto.SearchDefinition;
import com.scplatform.pcm.searchframework.dto.SearchForm;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Session-level cache for SearchDefinition and SearchForm objects.
 * This cache avoids costly re-initialization of search forms on every request.
 * 
 * Cache Key Format: "SEARCH_CACHE_{formName}_{userId}"
 * This ensures proper isolation between users and different search forms.
 */
@Log4j2
@Component
public class SessionSearchCache {

    private static final String CACHE_PREFIX = "SEARCH_CACHE_";
    private static final String SEPARATOR = "_";

    /**
     * Store SearchDefinition and SearchForm in session cache
     * 
     * @param session HttpSession to store cache
     * @param formName Name of the search form (e.g., "CommodityProfileSearch")
     * @param userId User ID for cache isolation
     * @param searchDefinition SearchDefinition object to cache
     * @param searchForm SearchForm object to cache
     */
    public void cacheSearchForm(HttpSession session, String formName, Long userId,
                                SearchDefinition searchDefinition, SearchForm searchForm) {
        if (session == null || formName == null || userId == null) {
            log.warn("Cannot cache: session, formName, or userId is null");
            return;
        }

        String cacheKey = buildCacheKey(formName, userId);
        Map<String, Object> cacheData = new HashMap<>();
        cacheData.put("searchDefinition", searchDefinition);
        cacheData.put("searchForm", searchForm);
        cacheData.put("timestamp", System.currentTimeMillis());

        session.setAttribute(cacheKey, cacheData);
        log.debug("Cached search form for key: {}", cacheKey);
    }

    /**
     * Retrieve cached SearchDefinition from session
     * 
     * @param session HttpSession to retrieve cache from
     * @param formName Name of the search form
     * @param userId User ID
     * @return Cached SearchDefinition or null if not found
     */
    public SearchDefinition getCachedSearchDefinition(HttpSession session, String formName, Long userId) {
        if (session == null || formName == null || userId == null) {
            return null;
        }

        String cacheKey = buildCacheKey(formName, userId);
        Map<String, Object> cacheData = (Map<String, Object>) session.getAttribute(cacheKey);

        if (cacheData != null) {
            SearchDefinition sd = (SearchDefinition) cacheData.get("searchDefinition");
            log.debug("Retrieved cached SearchDefinition for key: {}", cacheKey);
            return sd;
        }

        return null;
    }

    /**
     * Retrieve cached SearchForm from session
     * 
     * @param session HttpSession to retrieve cache from
     * @param formName Name of the search form
     * @param userId User ID
     * @return Cached SearchForm or null if not found
     */
    public <T extends SearchForm> T getCachedSearchForm(HttpSession session, String formName, Long userId) {
        if (session == null || formName == null || userId == null) {
            return null;
        }

        String cacheKey = buildCacheKey(formName, userId);
        Map<String, Object> cacheData = (Map<String, Object>) session.getAttribute(cacheKey);

        if (cacheData != null) {
            T sf = (T) cacheData.get("searchForm");
            log.debug("Retrieved cached SearchForm for key: {}", cacheKey);
            return sf;
        }

        return null;
    }

    /**
     * Get entire cache data (both SearchDefinition and SearchForm)
     * 
     * @param session HttpSession to retrieve cache from
     * @param formName Name of the search form
     * @param userId User ID
     * @return Map containing cached objects or null if not found
     */
    public Map<String, Object> getCachedFormData(HttpSession session, String formName, Long userId) {
        if (session == null || formName == null || userId == null) {
            return null;
        }

        String cacheKey = buildCacheKey(formName, userId);
        Map<String, Object> cacheData = (Map<String, Object>) session.getAttribute(cacheKey);

        if (cacheData != null) {
            log.debug("Retrieved cached form data for key: {}", cacheKey);
        }

        return cacheData;
    }

    /**
     * Clear cache for specific search form
     * Called during init() to force fresh initialization
     * 
     * @param session HttpSession containing cache
     * @param formName Name of the search form
     * @param userId User ID
     */
    public void clearCache(HttpSession session, String formName, Long userId) {
        if (session == null || formName == null || userId == null) {
            return;
        }

        String cacheKey = buildCacheKey(formName, userId);
        session.removeAttribute(cacheKey);
        log.debug("Cleared cache for key: {}", cacheKey);
    }

    /**
     * Clear all search caches for a specific user
     * Useful for logout or session cleanup
     * 
     * @param session HttpSession containing cache
     * @param userId User ID
     */
    public void clearAllUserCaches(HttpSession session, Long userId) {
        if (session == null || userId == null) {
            return;
        }

        java.util.Enumeration<String> attributeNames = session.getAttributeNames();
        String userCachePrefix = CACHE_PREFIX + userId;

        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            if (attributeName.endsWith(userCachePrefix)) {
                session.removeAttribute(attributeName);
                log.debug("Cleared cache attribute: {}", attributeName);
            }
        }
    }

    /**
     * Check if cache exists for the form
     * 
     * @param session HttpSession containing cache
     * @param formName Name of the search form
     * @param userId User ID
     * @return true if cache exists, false otherwise
     */
    public boolean isCached(HttpSession session, String formName, Long userId) {
        if (session == null || formName == null || userId == null) {
            return false;
        }

        String cacheKey = buildCacheKey(formName, userId);
        Map<String, Object> cacheData = (Map<String, Object>) session.getAttribute(cacheKey);
        return cacheData != null;
    }

    /**
     * Get cache creation timestamp (useful for cache validation)
     * 
     * @param session HttpSession containing cache
     * @param formName Name of the search form
     * @param userId User ID
     * @return Timestamp in milliseconds or 0 if not cached
     */
    public long getCacheTimestamp(HttpSession session, String formName, Long userId) {
        Map<String, Object> cacheData = getCachedFormData(session, formName, userId);
        if (cacheData != null && cacheData.containsKey("timestamp")) {
            return (long) cacheData.get("timestamp");
        }
        return 0;
    }

    /**
     * Build cache key from form name and user ID
     * 
     * @param formName Name of the search form
     * @param userId User ID
     * @return Cache key string
     */
    private String buildCacheKey(String formName, Long userId) {
        return CACHE_PREFIX + formName + SEPARATOR + userId;
    }
}

