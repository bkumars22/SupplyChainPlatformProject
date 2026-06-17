/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */package com.scplatform.pcm.util.stateMachine;

import java.util.Iterator;

public class PushParentIfAllOther extends StateMachineAbstractRule implements StateMachineTransitionRule
{
	public boolean overrideState()
	{
		return true;
	}
	
    public String evaluate(String event, StateMachineReactor obj, StateMachineState state,
                           int direction, String currentState)
    	throws InvalidStateException
    {
        if (obj.getChildren() == null || obj.getChildren().size() == 0)
        {
            return null;
        }
        String otherState = null;
        Iterator itr = obj.getChildren().iterator();
        while (itr.hasNext())
        {
        	StateMachineReactor line = (StateMachineReactor)itr.next();
            if (getIgnoreStates().contains(line.getState()) == false)
            {
            	if (otherState == null)
            	{
            		otherState = line.getState();
            	}
            	else
            	{
            		if (otherState.equals(line.getState()) == false)
            		{
            			return null;
            		}
            	}
            }
        }
        return otherState;    	
    }	
    
	public boolean equals(Object other)
	{
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof PushParentIfAllOther))
            return false;
        return true;
	}
    
}
