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

/**
 * Interface that can used to register a "listener" on an instance of a StateMachine.
 * Useful for tracing what instances are being acted apon or when you
 * need to control state for particular business reasons.
 */
public interface StateMachineVisitor
{
	/**
	 * Invoked for each target the state machine processes.  Return false to stop processing
	 * @param target
	 * @param machineState
	 * @param direction
	 * @param currentStateName
	 * @return false to stop processing.
	 */
	public boolean visit(StateMachineReactor target, StateMachineState machineState,
                         int direction, String currentStateName);

	/**
	 * Invoked for each target that a state change is being proposed.  Return false to stop processing.
	 * NOTE: The state change is performed after invocation, so use proposedStateName to see
	 * what state the target will be set to if you return true..
	 * @param target
	 * @param event
	 * @param proposedStateName
	 * @param direction
	 * @param startNode
	 * @return true to allow the state to be set and processing to continue.  Return false to stop processing
	 */
	public boolean allowStateChange(StateMachineReactor target, String event, String proposedStateName,
                                    int direction, boolean startNode);
 
}
