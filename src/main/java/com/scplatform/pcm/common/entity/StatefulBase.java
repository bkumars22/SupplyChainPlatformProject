/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.common.entity;



import java.util.Date;

import com.scplatform.pcm.common.dto.ChangeTracker;
import com.scplatform.pcm.util.stateMachine.StateMachineReactor;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

@MappedSuperclass
public abstract class StatefulBase extends AuditRevisionBase
        implements StateMachineReactor
{
    @Column(name="STATUS")
    protected String status;

    @Column(name="STATUS_CHANGE_DATE")
    protected Date statusChangeDate;

    @Column(name="STATUS_LAST_CHANGE_BY")
    protected String statusLastChangeBy;

    @Transient
    transient ChangeTracker<StatefulBase> statusChangeTracker = new ChangeTracker<StatefulBase>();

    public StatefulBase()
    {
        super();
        this.statusChangeTracker.setObservedObject(this);
    }

    public String getStatus()
    {
        return this.status;
    }

    public void setStatus(String status)
    {
        this.statusChangeTracker.firePropertyChangeEvent("status", this.status, status);
        this.status = status;
    }

    public Date getStatusChangeDate()
    {
        return this.statusChangeDate;
    }

    public void setStatusChangeDate(Date statusChangeDate)
    {
        this.statusChangeDate = statusChangeDate;
    }

    public String getStatusLastChangeBy()
    {
        return this.statusLastChangeBy;
    }

    public void setStatusLastChangeBy(String statusLastChangeBy)
    {
        this.statusLastChangeBy = statusLastChangeBy;
    }

    public void setState(String state)
    {
        this.statusChangeTracker.firePropertyChangeEvent("status", this.status, state);
        this.status = state;
    }

    public String getState()
    {
        return this.status;
    }

    public ChangeTracker<StatefulBase> getStatusChangeTracker() {
        return this.statusChangeTracker;
    }

}