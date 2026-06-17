/*
 * Copyright (c) 2006 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2006, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.util.stateMachine;


public class PushChildAlwaysRule extends StateMachineAbstractRule implements StateMachineTransitionRule
{	
	protected int stopOnChild = CONTINUE;
	public PushChildAlwaysRule(int stopOn)
	{
		this.stopOnChild = stopOn;
	}
	
    public String evaluate(String event, StateMachineReactor obj, StateMachineState state,
                           int direction, String currentState)
    	throws InvalidStateException
    {
    	return state.getName();
    }
	
	public boolean equals(Object other)
	{
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof PushChildAlwaysRule))
            return false;
        return true;
	}

	/* (non-Javadoc)
	 * @see com.scplatform.mcd.util.sm.StateMachineAbstractRule#stopOnChild()
	 */
	public int stopOnChild()
	{
		return stopOnChild;
	}
    
}

