/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 */
package com.scplatform.pcm.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Entity for tracking user session/connection information.
 * 
 * <p>Used for concurrent login detection - ensures a user can only
 * be logged in from one browser/session at a time.
 * 
 * @author PCM Team
 */
@Entity
@Table(name = "PCM_USER_CONNECTION_INFO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PcmUserSessionInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * User ID - primary key
     */
    @Id
    @Column(name = "USER_ID", length = 60)
    private String userId;

    /**
     * Unique session identifier (combination of browser + OS + session ID)
     */
    @Column(name = "SESSION_ID", length = 255)
    private String sessionId;

    /**
     * Last time the session was accessed/updated
     */
    @Column(name = "LAST_UPDATE_ON")
    private Timestamp lastUpdateOn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PcmUserSessionInfo that = (PcmUserSessionInfo) o;
        return userId != null && userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "PcmUserSessionInfo{" +
                "userId='" + userId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", lastUpdateOn=" + lastUpdateOn +
                '}';
    }
}
