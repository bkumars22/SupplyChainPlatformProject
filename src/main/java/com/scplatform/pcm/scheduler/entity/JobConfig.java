/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.scheduler.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "APP_JOB_CONFIG")
public class JobConfig {

    @Id
    @SequenceGenerator(name="JOB_CONFIG_SEQ", sequenceName = "JOB_CONFIG_SEQ",allocationSize = 1)
    @GeneratedValue(generator = "JOB_CONFIG_SEQ")
    @Column(name = "JOB_ID")
    private Long jobId;

    @Column(name = "JOB_NAME", length = 100, nullable = false)
    private String jobName;

    @Column(name = "JOB_GROUP", length = 100, nullable = false)
    private String jobGroup;

    @Column(name = "JOB_CLASS", length = 200, nullable = false)
    private String jobClass;

    @Column(name = "CRON_EXPRESSION", length = 100)
    private String cron;

    @Column(name = "JOB_TYPE", length = 20)
    private String jobType;

    @Column(name = "REPEAT_INTERVAL")
    private Integer repeatInterval;

    @Column(name = "ENABLED")
    private boolean enabled;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "UPDATED_AT")
    private Date updatedAt;

}
