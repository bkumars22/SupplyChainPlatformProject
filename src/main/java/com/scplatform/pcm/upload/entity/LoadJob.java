/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LOAD_JOB")
@Data
@NoArgsConstructor
public class LoadJob {

    /** UUID primary key — set by the service before {@code persist()}. */
    @Id
    @Column(name = "LOAD_JOB_KEY", length = 32, nullable = false)
    private String loadJobKey;

    @Column(name = "LOAD_DATASOURCE", length = 255)
    private String datasource;

    @Column(name = "LOADED_BY", length = 64)
    private String loadedBy;

    @Column(name = "LOAD_STATUS", length = 64)
    private String status;

    @Column(name = "LOAD_STATE", length = 64)
    private String state;

    @Column(name = "LOAD_JOB_TYPE", length = 128)
    private String loadJobType;

    @Column(name = "LOAD_EXTERNAL_ID", length = 128)
    private String externalId;

    /** Replaces legacy {@code Date} + {@code @Temporal(TIMESTAMP)}. */
    @Column(name = "LOAD_DATE", nullable = false)
    private LocalDateTime loadDate = LocalDateTime.now();

    /**
     * Child load events.
     * {@code orphanRemoval = true} replaces the legacy Hibernate
     * {@code @Cascade(DELETE_ORPHAN)} annotation.
     */
    @OneToMany(mappedBy = "loadJob", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("insertDate ASC, loadEventKey ASC")
    @lombok.ToString.Exclude
    private List<LoadEvent> loadEvents = new ArrayList<>();

}
