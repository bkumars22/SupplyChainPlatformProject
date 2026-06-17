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

/**
 * Track change flag interface.  Interceptors use this marker interface to determine
 * if it has to set the update flag.
 */
public interface TrackChange
{
	public abstract boolean getCurrentFlag();
	public abstract void setCurrentFlag(boolean currentFlag);
}