/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.userAlert.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;


@Entity
@Table(name = "PCM_USER_ALERTS")
public class UserAlert implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pcm_user_alerts_seq")
    @SequenceGenerator(name = "pcm_user_alerts_seq", sequenceName = "PCM_USER_ALERTS_SEQ", allocationSize = 1)
    @Column(name = "ALERT_KEY")
    private long alertKey = -1;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ALERT_DATE", nullable = false)
    private Date alertDate;

    @Column(name = "ALERT_TITLE", length = 100, nullable = false)
    private String alertTitle;

    @Column(name = "ALERT_URL", length = 512)
    private String alertURL;

    @Column(name = "ALERT_TARGET")
    private String alertTarget;

    @Column(name = "ALERT_FILTER")
    private String alertFilter;

    public long getAlertKey() {
        return alertKey;
    }

    public void setAlertKey(long alertKey) {
        this.alertKey = alertKey;
    }

    public Date getAlertDate() {
        return alertDate;
    }

    public void setAlertDate(Date alertDate) {
        this.alertDate = alertDate;
    }

    public String getAlertTitle() {
        return alertTitle;
    }

    public void setAlertTitle(String alertTitle) {
        this.alertTitle = alertTitle;
    }

    public String getAlertURL() {
        return alertURL;
    }

    public void setAlertURL(String alertURL) {
        this.alertURL = alertURL;
    }

    public String getAlertTarget() {
        return alertTarget;
    }

    public void setAlertTarget(String alertTarget) {
        this.alertTarget = alertTarget;
    }

    public String getAlertFilter() {
        return alertFilter;
    }

    public void setAlertFilter(String alertFilter) {
        this.alertFilter = alertFilter;
    }
}

