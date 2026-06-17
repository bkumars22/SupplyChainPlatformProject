/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.util.stateMachine;
import java.util.Collection;
import java.util.Date;

public interface StateMachineReactor
{
    public Collection<StateMachineReactor> getChildren();
    
    public String getState();
    
    public void setState(String state);
    
    public StateMachineReactor getParent();
 
	public Date getStatusChangeDate();
	public void setStatusChangeDate(Date statusChangeDate);

	public String getStatusLastChangeBy();
	public void setStatusLastChangeBy(String statusLastChangeBy);
    
}
