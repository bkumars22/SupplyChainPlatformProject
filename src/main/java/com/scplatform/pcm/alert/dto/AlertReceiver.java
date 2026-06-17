/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a user who should receive an alert.
 * Used during the publish step to determine which users get the alert.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertReceiver implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** User's primary key (PCM_USER.USER_KEY) */
    private Long userId;

    /** User's login ID (PCM_USER.USER_ID) — used in SC_ALERT_DETAIL.USER_LOGIN_ID */
    private String userLoginId;

    /** User's display name */
    private String userName;

    /** User's email address (for optional email notifications) */
    private String email;
}

