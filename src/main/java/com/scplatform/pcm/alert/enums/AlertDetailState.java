/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.enums;

/**
 * Lifecycle states for alert detail records in SC_ALERT_DETAIL.
 *
 * <p>Stored in the STATE column (VARCHAR2(64)).</p>
 *
 * <p>IMPORTANT: The existing database and UI use "ACTIVE" (not "PUBLISHED").
 * The AlertAction search filters by state="ACTIVE". Do NOT change this
 * without a data migration.</p>
 *
 * <pre>
 *   ACTIVE    → Alert is active and visible to the user (legacy: Alert.ACTIVE)
 *   DISMISSED → User dismissed the alert (DISMISED_BY column set)
 * </pre>
 */
public enum AlertDetailState {

    /** Alert is active and visible to the user (matches legacy Alert.ACTIVE = "ACTIVE") */
    ACTIVE,

    /** User has dismissed this alert */
    DISMISSED
}
