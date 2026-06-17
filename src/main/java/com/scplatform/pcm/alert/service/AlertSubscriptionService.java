/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.entity.PcmAlertSubscription;
import com.scplatform.pcm.alert.entity.PcmAlertSubscriptionOption;
import com.scplatform.pcm.alert.dto.AlertEvent;
import com.scplatform.pcm.alert.dto.AlertReceiver;
import com.scplatform.pcm.alert.repository.AlertSubscriptionRepository;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Service that queries the PCM_ALERT_SUBSCRIPTION table (with options)
 * to determine which users should receive a specific alert event.
 *
 * <h3>Real DB Structure:</h3>
 * <pre>
 *   PCM_ALERT_SUBSCRIPTION → SUBSCRIPTION_KEY, ALERT_TYPE, USER_KEY, SUBSCRIBE_FLAG
 *   PCM_ALERT_SUBSCRIPTION_OPTIONS → OPTION_KEY, SUBSCRIPTION_KEY, OPTION_ID, OPTION_VALUE
 * </pre>
 *
 * <h3>Filter Logic:</h3>
 * <pre>
 *   1. Match alert type + subscribeFlag = 1
 *   2. Load options (key-value pairs) for each subscription
 *   3. In Java: filter by excludeOwnActions, responsibilityKey, costTypeKey, itemKey
 *   4. Lookup PCM_USER to get login ID, name, email for each subscriber
 * </pre>
 */
@Service
@ConditionalOnProperty(name = "pcm.alert.artemis.enabled", havingValue = "true", matchIfMissing = false)
public class AlertSubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(AlertSubscriptionService.class);

    private final AlertSubscriptionRepository subscriptionRepository;
    private final UsersRepository usersRepository;

    public AlertSubscriptionService(AlertSubscriptionRepository subscriptionRepository,
                                    UsersRepository usersRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.usersRepository = usersRepository;
    }

    /**
     * Finds all users subscribed to receive the given alert event.
     *
     * @param alertEvent the alert event to find subscribers for
     * @return list of AlertReceiver representing subscribed users
     */
    @Transactional(readOnly = true)
    public List<AlertReceiver> findSubscribers(AlertEvent alertEvent) {
        String alertTypeName = alertEvent.getAlertType().name();
        Long actor = alertEvent.getActor();
        Map<String, Object> filters = alertEvent.getFilters();

        log.debug("Finding subscribers for alertType=[{}], actor=[{}]", alertTypeName, actor);

        // Step 1: Get all active subscriptions for this alert type (with options eagerly loaded)
        List<PcmAlertSubscription> subscriptions = subscriptionRepository.findActiveSubscriptions(alertTypeName);

        // Step 2: Filter in Java based on options
        List<PcmAlertSubscription> filtered = subscriptions.stream()
                .filter(sub -> {
                    // Exclude actor's own actions if the option is set
                    if (sub.isExcludeOwnActions() && actor != null && actor.equals(sub.getUserKey())) {
                        return false;
                    }
                    // Match optional responsibility filter
                    if (!matchesFilter(sub, PcmAlertSubscriptionOption.OPT_RESPONSIBILITY_KEY,
                            extractString(filters, "responsibilityKey"))) {
                        return false;
                    }
                    // Match optional cost type filter
                    if (!matchesFilter(sub, PcmAlertSubscriptionOption.OPT_COST_TYPE_KEY,
                            extractString(filters, "costTypeKey"))) {
                        return false;
                    }
                    // Match optional item filter
                    String itemKeyStr = alertEvent.getReferenceKey() != null
                            ? alertEvent.getReferenceKey().toString() : null;
                    return matchesFilter(sub, PcmAlertSubscriptionOption.OPT_ITEM_KEY, itemKeyStr);
                })
                .toList();

        // Step 3: Lookup PCM_USER for each subscriber to get login ID, name, email
        // Deduplicate by userKey
        List<AlertReceiver> receivers = filtered.stream()
                .map(PcmAlertSubscription::getUserKey)
                .distinct()
                .map(this::buildReceiverFromUser)
                .filter(Objects::nonNull)
                .toList();

        log.debug("Found {} subscriber(s) for alertType=[{}]", receivers.size(), alertTypeName);
        return receivers;
    }

    /**
     * Checks if a subscription's option matches the event's filter value.
     * If the subscription has no value for this option (NULL), it means "match all" → return true.
     * If the event has no filter value, the subscription option is irrelevant → return true.
     */
    private boolean matchesFilter(PcmAlertSubscription sub, String optionId, String eventValue) {
        String subValue = sub.getOptionValue(optionId);
        if (subValue == null || subValue.isBlank()) {
            return true; // subscription has no filter → matches all
        }
        if (eventValue == null || eventValue.isBlank()) {
            return true; // event has no value → cannot filter, allow through
        }
        return subValue.equals(eventValue);
    }

    /**
     * Builds an AlertReceiver by looking up the PCM_USER table.
     */
    private AlertReceiver buildReceiverFromUser(Long userKey) {
        Optional<Users> userOpt = usersRepository.findById(userKey);
        if (userOpt.isEmpty()) {
            log.warn("User with key [{}] not found in PCM_USER table. Skipping.", userKey);
            return null;
        }
        Users user = userOpt.get();
        return AlertReceiver.builder()
                .userId(user.getUserKey())
                .userLoginId(user.getUserId())   // PCM_USER.USER_ID = login string
                .userName(user.getUserName())
                .email(user.getEmailAddress())
                .build();
    }

    private String extractString(Map<String, Object> filters, String key) {
        if (filters == null || !filters.containsKey(key)) {
            return null;
        }
        Object value = filters.get(key);
        return value != null ? value.toString() : null;
    }
}

