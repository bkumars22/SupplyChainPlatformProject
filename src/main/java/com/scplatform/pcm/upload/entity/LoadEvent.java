/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.entity;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "LOAD_EVENT")
@Data
@NoArgsConstructor
public class LoadEvent {

    /**
     * Suffix appended to {@link #type} when an event is cleared.
     * {@code @Transient} prevents JPA from treating it as a persistent field.
     */
    @Transient
    public static final String CLEAR_CONTEXT = ":CLEARED";

    public enum LoadEventType {
        MISSING_BUSINESS_ENTITY,
        NEW_BUSINESS_ENTITY,
        VALIDATION_ERROR,
        DATA_ERROR,
        CORRECTION,
        WARNING,
        INFO,
        OTHER
    }

    @Id
    @SequenceGenerator(name = "LOAD_EVENT_SEQ", sequenceName = "LOAD_EVENT_SEQ",
            allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LOAD_EVENT_SEQ")
    @Column(name = "LOAD_EVENT_KEY", nullable = false)
    private Long loadEventKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAD_JOB_KEY", nullable = false)
    @lombok.ToString.Exclude
    private LoadJob loadJob;

    @Column(name = "LOAD_EVENT_TYPE", length = 64, nullable = false)
    private String type;

    @Setter(AccessLevel.NONE)
    @Column(name = "LOAD_EVENT_DATA", length = 1024)
    private String loadEventData;

    @Column(name = "LOAD_EVENT_CONTEXT", length = 255)
    private String loadEventContext;

    /** Replaces legacy {@code Date} + {@code @Temporal(TIMESTAMP)}.
     *  columnDefinition = "DATE" ensures correct Oracle DATE type handling with Hibernate 6. */
    @Column(name = "INSERT_DT", nullable = false, columnDefinition = "DATE")
    private LocalDateTime insertDate = LocalDateTime.now();

    // -----------------------------------------------------------------------
    // Custom setters
    // -----------------------------------------------------------------------

    /** Accepts a raw String type value directly. */
    public void setType(String type) {
        this.type = type;
    }

    /** Convenience overload — stores the enum constant name as the persisted value. */
    public void setType(LoadEventType type) {
        this.type = type.name();
    }

    /**
     * Persists event data, truncating to 1 024 characters.
     * Mirrors the legacy {@code StringUtils.abbreviate(loadEventData, 1024)} call.
     */
    public void setLoadEventData(String loadEventData) {
        this.loadEventData = StringUtils.abbreviate(loadEventData, 1024);
    }

    /**
     * Returns {@code true} if this event is eligible to be cleared —
     * i.e. it has not already been cleared (type does not end with {@value #CLEAR_CONTEXT}).
     * Used by the JSP EL expression {@code ${loadEvent.canEventBeCleared}}.
     */
    @Transient
    public boolean isCanEventBeCleared() {
        return type != null && !type.endsWith(CLEAR_CONTEXT);
    }

}

