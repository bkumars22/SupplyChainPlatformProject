/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.util.jpa;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.dto.InvalidUserContext;
import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Spring component for managing Hibernate/JPA dynamic filters.
 * Provides centralized methods for enabling/disabling database query filters.
 */
@Component
public class JPAFilterUtil {

    private static final Logger logger = LogManager.getLogger(JPAFilterUtil.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Enable filter with multiple parameters
     */
    public void enableFilter(String filterName, Map<String, ?> parameters) {
        if (filterName == null || filterName.isEmpty()) {
            throw new IllegalArgumentException("Filter name cannot be null or empty");
        }

        try {
            Session session = getSession();
            Filter filter = session.enableFilter(filterName);
            
            if (parameters != null && !parameters.isEmpty()) {
                for (Map.Entry<String, ?> entry : parameters.entrySet()) {
                    filter.setParameter(entry.getKey(), entry.getValue());
                    logger.debug("Filter '{}' parameter '{}' set to '{}'", 
                            filterName, entry.getKey(), entry.getValue());
                }
            }
            
            logger.info("Filter '{}' enabled ({} params)", filterName, 
                    parameters != null ? parameters.size() : 0);
        } catch (Exception e) {
            logger.error("Error enabling filter '{}': {}", filterName, e.getMessage(), e);
            throw new RuntimeException("Failed to enable filter: " + filterName, e);
        }
    }

    /**
     * Enable filter without parameters
     */
    public void enableFilter(String filterName) {
        enableFilter(filterName, null);
    }

    /**
     * Enable filter with single parameter (convenience)
     */
    public void enableFilter(String filterName, String paramName, Object paramValue) {
        Map<String, Object> params = new HashMap<>();
        params.put(paramName, paramValue);
        enableFilter(filterName, params);
    }

    /**
     * Disable specific filter
     */
    public void disableFilter(String filterName) {
        if (filterName == null || filterName.isEmpty()) {
            throw new IllegalArgumentException("Filter name cannot be null or empty");
        }

        try {
            Session session = getSession();
            session.disableFilter(filterName);
            logger.info("Filter '{}' disabled", filterName);
        } catch (Exception e) {
            logger.error("Error disabling filter '{}': {}", filterName, e.getMessage(), e);
            throw new RuntimeException("Failed to disable filter: " + filterName, e);
        }
    }

    /**
     * Get Hibernate session from EntityManager
     */
    private Session getSession() {
        if (entityManager == null) {
            throw new IllegalStateException("EntityManager not available");
        }
        Session session = entityManager.unwrap(Session.class);
        if (session == null) {
            throw new IllegalStateException("Cannot unwrap Session from EntityManager");
        }
        return session;
    }
}