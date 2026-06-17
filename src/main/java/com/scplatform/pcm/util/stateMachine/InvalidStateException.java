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

@SuppressWarnings("serial")
public class InvalidStateException extends Exception
{
    public static int INVALID_STATE = 1;
    public static int INVALID_EVENT = 2;
    public static int INVALID_TRANSISTION = 3;
    
    private int reasonCode;
    private String state;
    private String event;
    public InvalidStateException(int reason, String state, String event)
    {
        this.state = state;
        this.event = event;
        this.reasonCode = reason;
        
    }
    
    @Override
    public String getMessage()
    {
    	StringBuffer msg = new StringBuffer();
        if (reasonCode == INVALID_STATE)
        {
        	msg.append("Invalid state '").append(state);
        	msg.append("' encountered when processing event '").append(event).append("'");
        }
        else if (reasonCode == INVALID_EVENT)
        {
        	msg.append("Invalid event '").append(event);
        	msg.append("' submitted for target in state '").append(state).append("'");
        }
        else
        {
        	msg.append(super.getMessage());
        }
        return msg.toString();
    }

    @Override
	public String getLocalizedMessage()
	{
		return getMessage();
	}
    
    
}
