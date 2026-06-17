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

public class PushParentLeastChild extends StateMachineAbstractRule implements StateMachineTransitionRule
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
        StateMachine stateMachine = state.getStateMachine();
        StateMachineState leastState = null;
        Iterator itr = obj.getChildren().iterator();
        while (itr.hasNext())
        {
        	StateMachineReactor line = (StateMachineReactor)itr.next();
            if (getIgnoreStates().contains(line.getState()) == false)
            {
            	if (leastState == null || 
            		leastState.getStateWeight() > stateMachine.getState(line.getState()).getStateWeight())
            	{
            		leastState = stateMachine.getState(line.getState());
            	}
            }
        }
        
        StateMachineState parentState = stateMachine.getState(obj.getState());
        leastState = evaluateConditions(stateMachine,parentState,leastState);
        
        return (leastState != null) ? leastState.getName():null;    	
    }
	
	public boolean isEqual(Object other)
	{
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof PushParentLeastChild))
            return false;
        return true;
	}
    
}

