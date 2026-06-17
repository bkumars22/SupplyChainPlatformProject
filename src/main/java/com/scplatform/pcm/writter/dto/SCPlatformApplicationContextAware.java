/**
 *	SCPlatformApplicationContextAware.java
 *	Created on Dec 3, 2014
 *     
 *	Copyright (c) 2010 E2open, Inc.
 *	All Rights Reserved.
 *
 *	THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *	The copyright notice above does not evidence any
 *	actual or intended publication of such source code. 
 *	
 *	Author: sgupta
 */
package com.scplatform.pcm.writter.dto;

import com.scplatform.pcm.authentication.dto.ApplicationContext;

/**
 * Marks a class as aware of Application Context
 * 
 * @author sgupta
 */
public interface SCPlatformApplicationContextAware {
    
    public void setApplicationContext(ApplicationContext ctx);

}
