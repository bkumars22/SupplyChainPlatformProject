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

/**
 * This is a marker interface that is used by any JPA or Hibernate
 * persisted class that needs to track for delta extracts.
 * The interceptor will check for this marker interface
 * and update the flags as needed
 */
public interface TrackDelta extends TrackChange
{
	public abstract Date getInsertDate();
	public abstract void setInsertDate(Date insertDt);

	public abstract Date getUpdateDate();
	public abstract void setUpdateDate(Date updateDt);

	public abstract Boolean getDeleteFlag();
	public abstract void setDeleteFlag(Boolean deleteFlag);
}