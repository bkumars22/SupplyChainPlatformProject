
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.user.service;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.user.entity.PcmUserSessionInfo;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.PcmUserSessionInfoRepository;
import com.scplatform.pcm.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Service for managing user sessions and concurrent login detection.
 * 
 * <p>This service handles:
 * <ul>
 *   <li>Tracking active user sessions</li>
 *   <li>Detecting concurrent logins (same user from different browsers/sessions)</li>
 *   <li>Session expiry based on inactivity</li>
 *   <li>Updating session access times</li>
 * </ul>
 * 
 * <p>Configuration is retrieved from database via PcmConfigUtil:
 * <ul>
 *   <li>{@code pcm.session.active.check.limit} - session timeout in minutes (default: 10)</li>
 * </ul>
 * 
 * @author PCM Team
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserSessionService {

    private static final Logger logger = LoggerFactory.getLogger(UserSessionService.class);

    private static final String CONFIG_SESSION_TIMEOUT = "pcm.session.active.check.limit";
    private static final int DEFAULT_SESSION_TIMEOUT_MINUTES = 10;
    private final PcmUserSessionInfoRepository sessionInfoRepository;
    private final UsersRepository userRepository;
    private final PcmConfigUtil configUtil;

    /**
     * Result of session validation check.
     */
    public enum SessionCheckResult {
        /** Session is valid, proceed normally */
        VALID,
        /** No session existed, new one was created */
        NEW_SESSION_CREATED,
        /** Session was expired, recreated with new client */
        SESSION_EXPIRED_RECREATED,
        /** Different client detected, user chose to continue */
        CONCURRENT_LOGIN_CONTINUED,
        /** Different client detected, user chose to cancel */
        CONCURRENT_LOGIN_CANCELLED,
        /** Different client detected, needs user decision */
        CONCURRENT_LOGIN_DETECTED
    }

    /**
     * Check and manage user session.
     * 
     * <p>This is the main entry point for session validation. It handles:
     * <ul>
     *   <li>Creating new sessions for first-time users</li>
     *   <li>Recreating expired sessions</li>
     *   <li>Detecting and handling concurrent logins</li>
     *   <li>Updating session access times</li>
     * </ul>
     * 
     * @param userId          the user ID
     * @param clientUniqueId  unique identifier for the client (browser + OS + session)
     * @param actionType      optional action type for handling concurrent logins
     * @return result indicating what action was taken
     */
    public SessionCheckResult checkAndManageSession(String userId, String clientUniqueId, String actionType) {
        logger.debug("Checking session for user: {}, clientId: {}", userId, clientUniqueId);

        int timeLimit = getSessionTimeoutMinutes();
        Optional<PcmUserSessionInfo> existingSession = sessionInfoRepository.findByUserId(userId);

        if (existingSession.isEmpty()) {
            // No session exists - create new one
            createSession(userId, clientUniqueId);
            return SessionCheckResult.NEW_SESSION_CREATED;
        }

        PcmUserSessionInfo sessionInfo = existingSession.get();
        Timestamp expiryThreshold = new Timestamp(
                System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(timeLimit));

        // Check if session is expired
        if (sessionInfo.getLastUpdateOn().getTime() <= expiryThreshold.getTime()) {
            logger.debug("Session expired for user: {}, recreating", userId);
            deleteSession(userId);
            createSession(userId, clientUniqueId);
            return SessionCheckResult.SESSION_EXPIRED_RECREATED;
        }

        // Check if same client
        if (sessionInfo.getSessionId().equals(clientUniqueId)) {
            // Same client - update last access time
            updateSessionLastAccessTime(userId);
            return SessionCheckResult.VALID;
        }

        // Different client detected - concurrent login
        logger.info("Concurrent login detected for user: {} from different client", userId);

        if ("continueWithSameId".equals(actionType)) {
            // User chose to continue with this session
            deleteSession(userId);
            createSession(userId, clientUniqueId);
            return SessionCheckResult.CONCURRENT_LOGIN_CONTINUED;
        } else if ("Cancel".equals(actionType)) {
            return SessionCheckResult.CONCURRENT_LOGIN_CANCELLED;
        } else {
            // Needs user decision
            return SessionCheckResult.CONCURRENT_LOGIN_DETECTED;
        }
    }

    /**
     * Create a new session for the user.
     * 
     * @param userId         the user ID
     * @param clientUniqueId unique client identifier
     */
    public void createSession(String userId, String clientUniqueId) {
        PcmUserSessionInfo sessionInfo = PcmUserSessionInfo.builder()
                .userId(userId)
                .sessionId(clientUniqueId)
                .lastUpdateOn(new Timestamp(System.currentTimeMillis()))
                .build();

        sessionInfoRepository.save(sessionInfo);
        logger.debug("Created session for user: {}", userId);
    }

    /**
     * Delete the session for a user.
     * 
     * @param userId the user ID
     */
    public void deleteSession(String userId) {
        sessionInfoRepository.deleteByUserId(userId);
        logger.debug("Deleted session for user: {}", userId);
    }

    /**
     * Update the last access time for a user's session.
     * 
     * @param userId the user ID
     */
    public void updateSessionLastAccessTime(String userId) {
        sessionInfoRepository.updateLastAccessTime(userId, new Timestamp(System.currentTimeMillis()));
        logger.trace("Updated last access time for user: {}", userId);
    }

    /**
     * Check if a session exists for the user.
     * 
     * @param userId the user ID
     * @return true if session exists
     */
    @Transactional(readOnly = true)
    public boolean isSessionPresent(String userId) {
        return sessionInfoRepository.existsByUserId(userId);
    }

    /**
     * Get session info for a user.
     * 
     * @param userId the user ID
     * @return Optional containing session info if found
     */
    @Transactional(readOnly = true)
    public Optional<PcmUserSessionInfo> getSessionInfo(String userId) {
        return sessionInfoRepository.findByUserId(userId);
    }

    /**
     * Get the session timeout in minutes from database configuration.
     * 
     * @return timeout in minutes (defaults to 10 if not configured)
     */
    private int getSessionTimeoutMinutes() {
        try {
            String configValue = configUtil.getString(CONFIG_SESSION_TIMEOUT);
            if (configValue != null && !configValue.isEmpty()) {
                return Integer.parseInt(configValue);
            }
        } catch (Exception e) {
            logger.debug("Error reading session timeout config, using default: {}", DEFAULT_SESSION_TIMEOUT_MINUTES);
        }
        return DEFAULT_SESSION_TIMEOUT_MINUTES;
    }

    @Transactional
    public Users saveOrUpdate(Users user) {
        logger.info("Saving or updating user: {}", user.getUserId());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Users findUserByKey(Long userKey) {
        logger.debug("Finding user by userKey: {}", userKey);
        Optional<Users> result = userRepository.findById(userKey);
        
        if (result.isPresent()) {
            Users user = result.get();
            // Access the lazy-loaded properties to initialize them within the transaction
            if (user.getContact() != null) {
                user.getContact().toString(); // trigger lazy loading
            }
            if (user.getBusinessEntity() != null) {
                user.getBusinessEntity().toString(); // trigger lazy loading
            }
            if (user.getPreferences() != null) {
                user.getPreferences().size(); // trigger lazy loading
            }
            return user;
        }
        return null;
    }

    /**
     * Find all users by userId (case-insensitive).
     *
     * @param userId the user ID to search for
     * @return list of users matching the userId
     */
    @Transactional(readOnly = true)
    public List<Users> findAllUsersById(String userId) {
        logger.debug("Finding all users by userId: {}", userId);
        if (userId == null) {
            return Collections.emptyList();
        }
        return userRepository.findByUserIdIgnoreCase(userId);
    }

    /**
     * Find users by email.
     *
     * @param email email address
     * @return list of users matching the email, empty list when input is null
     */
    @Transactional(readOnly = true)
    public List<Users> findUserByEMail(String email) {
        if (email == null) {
            return Collections.emptyList();
        }
        return userRepository.findByContact_EMail(email);
    }

    /**
     * Find a user by userId with optional eager relationship loading.
     *
     * @param userId user ID
     * @param initAll true to fetch relationships eagerly
     * @return matching user or null
     */
    @Transactional(readOnly = true)
    public Users findUserByUserId(String userId, boolean initAll) {
        if (userId == null) {
            return null;
        }
        if (initAll) {
            return userRepository.findUserByUserIdWithRelationships(userId);
        }
        return userRepository.findByUserId(userId).orElse(null);
    }

    @Transactional
    public boolean setUserLastAccess(Long userKey) {
        logger.info("Updating last access date for user key: {}", userKey);
        int updatedRows = userRepository.updateUserLastAccessDate(userKey);
        return updatedRows > 0;
    }
}
