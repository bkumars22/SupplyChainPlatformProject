/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.util.stateMachine;


public class PushChildIfSameRule extends StateMachineAbstractRule implements StateMachineTransitionRule
{	
	protected int stopOnChild = CONTINUE;
	public PushChildIfSameRule(int stopOn)
	{
		stopOnChild = stopOn;
	}
	
    public String evaluate(String event, StateMachineReactor obj, StateMachineState state,
                           int direction, String currentState)
    	throws InvalidStateException
    {
    	return (obj.getState().equals(currentState)) ? state.getName():null;
    }
	
	public boolean equals(Object other)
	{
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof PushChildIfSameRule))
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

