/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity mapped to the existing PCM_ALERT_SUBSCRIPTION table.
 *
 * <p>Each row represents a user's subscription to a specific alert type.
 * Additional filter criteria (responsibility, cost type, etc.) are stored
 * as key-value pairs in the related {@link PcmAlertSubscriptionOption} table.</p>
 *
 * <p>DB Schema:</p>
 * <pre>
 *   SUBSCRIPTION_KEY  NUMBER(19) PK
 *   ALERT_TYPE        VARCHAR2(255) NOT NULL
 *   USER_KEY          NUMBER(19) NOT NULL (FK → PCM_USER)
 *   SUBSCRIBE_FLAG    NUMBER(1)
 * </pre>
 */
@Entity
@Table(name = "PCM_ALERT_SUBSCRIPTION")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PcmAlertSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pcm_alert_sub_seq")
    @SequenceGenerator(
            name = "pcm_alert_sub_seq",
            sequenceName = "PCM_ALERT_SUBSCRIPTION_SEQ",
            allocationSize = 1
    )
    @Column(name = "SUBSCRIPTION_KEY")
    private Long subscriptionKey;

    /** Alert type this subscription is for (e.g., "CostChange", "CostPending") */
    @Column(name = "ALERT_TYPE", length = 255, nullable = false)
    private String alertType;

    /** FK to PCM_USER.USER_KEY */
    @Column(name = "USER_KEY", nullable = false)
    private Long userKey;

    /** 1 = subscribed, 0 = unsubscribed */
    @Column(name = "SUBSCRIBE_FLAG")
    private Integer subscribeFlag;

    /** Subscription options (key-value pairs from PCM_ALERT_SUBSCRIPTION_OPTIONS) */
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PcmAlertSubscriptionOption> options = new ArrayList<>();

    // ── Convenience methods for reading options ──

    /**
     * Gets the value of a specific option, or null if not set.
     */
    public String getOptionValue(String optionId) {
        return options.stream()
                .filter(o -> optionId.equals(o.getOptionId()))
                .map(PcmAlertSubscriptionOption::getOptionValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns true if the "excludeOwnActions" option is set to "Y".
     */
    public boolean isExcludeOwnActions() {
        String val = getOptionValue("excludeOwnActions");
        return "Y".equalsIgnoreCase(val) || "true".equalsIgnoreCase(val) || "1".equals(val);
    }

    /** Returns true if subscribeFlag == 1 */
    public boolean isSubscribed() {
        return subscribeFlag != null && subscribeFlag == 1;
    }
}
