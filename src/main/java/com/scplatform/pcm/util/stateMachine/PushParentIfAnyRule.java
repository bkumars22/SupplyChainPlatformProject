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

public class PushParentIfAnyRule extends StateMachineAbstractRule implements StateMachineTransitionRule
{
	
    public String evaluate(String event, StateMachineReactor obj, StateMachineState state,
                           int direction, String currentState)
		throws InvalidStateException
	{
        if (obj.getChildren() == null || obj.getChildren().size() == 0)
        {
            return state.getName();
        }
        Iterator itr = obj.getChildren().iterator();
        while (itr.hasNext())
        {
        	StateMachineReactor line = (StateMachineReactor)itr.next();
            if (state.getName().equals(line.getState()))
            {
            	return state.getName();
            }
        }
        return null;
	}
	
	public boolean equals(Object other)
	{
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof PushParentIfAnyRule))
            return false;
        return true;
	}
    
}
