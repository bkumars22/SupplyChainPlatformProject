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

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.util.Date;

@MappedSuperclass
public abstract class AuditRevisionBase
{
    // Audit revision for optimistic locking
	@Version()
	@Column(name="AUDIT_REV")
    protected Long auditRev = -1L;
	
	@Column(name="LAST_REV_CHANGE_DATE")
    protected Date lastRevChangeDate;    
    // Constructors

    /**
     * @param auditRev
     *            The auditRev to set.
     */
    public void setAuditRev(Long auditRev)
    {
        this.auditRev = auditRev;
    }

    /**
     * @return Returns the auditRev.
     */
    public long getAuditRev()
    {
        return auditRev;
    }

	/**
	 * @param lastRevChangeDate The lastRevChangeDate to set.
	 */
	public void setLastRevChangeDate(Date lastRevChangeDate)
	{
		this.lastRevChangeDate = lastRevChangeDate;
	}

	/**
	 * @return Returns the lastRevChangeDate.
	 */
	public Date getLastRevChangeDate()
	{
		return lastRevChangeDate;
	}
}
