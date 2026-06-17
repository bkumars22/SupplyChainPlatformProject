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

import java.util.Map;
import java.util.Set;

/**
 * Interface all transition rules must implement
 */
public interface StateMachineTransitionRule
{
	/**
	 * Returns values for stop on child rules
	 */
	public static int CONTINUE = 0;
	public static int STOP_ON_FIRST_CHANGE = 1;
	public static int STOP_AFTER_FIRST_CHILD = 2;
	/**
	 * Implementation should return true if it wants the returned
	 * state to be used on the dependent object.  If false
	 * changeState will be invoked.
	 * @return
	 */
	public boolean overrideState();
	
	/**
	 * Return one the the above values when
	 * processing children.
	 * @return 
	 */
	public int stopOnChild();
	
    public String evaluate(String event, StateMachineReactor obj, StateMachineState state,
                           int direction, String currentState)
    	throws InvalidStateException;
	
    public void addIgnoreState(String stateName);
    
    public Set getIgnoreStates();
    
	public String getRestriction(String restriction);
	public String setRestriction(String restriction, String value);
	public boolean hasRestriction(String restriction);
    
    /**
     * Set the state that should be pushed to the dependent object.
     * This is used when you want for instance the parent state to
     * get set to a different state then the child.  Not all
     * rules will use this value
     */     
    public void setPushState(String state);
    public String getPushState();
    
    public boolean equals(Object other);

	public String getProperty(String propertyName);
	public String setProperty(String propertyName, String value);
	public Map<String,String> getProperties();
}
