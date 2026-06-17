/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity mapped to the existing PCM_ALERT_SUBSCRIPTION_OPTIONS table.
 *
 * <p>Stores key-value option pairs for an alert subscription.
 * Common option IDs include:</p>
 * <ul>
 *   <li>{@code excludeOwnActions} → "Y" or "N"</li>
 *   <li>{@code responsibilityKey} → numeric key as string</li>
 *   <li>{@code costTypeKey} → numeric key as string</li>
 *   <li>{@code itemKey} → numeric key as string</li>
 * </ul>
 *
 * <p>DB Schema:</p>
 * <pre>
 *   OPTION_KEY        NUMBER(19) PK
 *   SUBSCRIPTION_KEY  NUMBER(19) FK → PCM_ALERT_SUBSCRIPTION
 *   OPTION_ID         VARCHAR2(255) NOT NULL
 *   OPTION_VALUE      VARCHAR2(1024)
 * </pre>
 */
@Entity
@Table(name = "PCM_ALERT_SUBSCRIPTION_OPTIONS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PcmAlertSubscriptionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pcm_alert_sub_opt_seq")
    @SequenceGenerator(
            name = "pcm_alert_sub_opt_seq",
            sequenceName = "PCM_ALERT_SUBSCRIPTION_OPT_SEQ",
            allocationSize = 1
    )
    @Column(name = "OPTION_KEY")
    private Long optionKey;

    /** Parent subscription */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSCRIPTION_KEY", nullable = false)
    private PcmAlertSubscription subscription;

    /** Option identifier (e.g., "excludeOwnActions", "responsibilityKey") */
    @Column(name = "OPTION_ID", length = 255, nullable = false)
    private String optionId;

    /** Option value (e.g., "Y", "12345") */
    @Column(name = "OPTION_VALUE", length = 1024)
    private String optionValue;

    // ── Well-known option IDs ──
    public static final String OPT_EXCLUDE_OWN_ACTIONS = "excludeOwnActions";
    public static final String OPT_RESPONSIBILITY_KEY = "responsibilityKey";
    public static final String OPT_COST_TYPE_KEY = "costTypeKey";
    public static final String OPT_ITEM_KEY = "itemKey";
}

