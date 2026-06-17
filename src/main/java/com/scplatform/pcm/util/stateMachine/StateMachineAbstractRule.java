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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;


public abstract class StateMachineAbstractRule
{
	//We intentionally use StateMachine log class because we want this log whenever state machine log is enabled
	private final static Logger logger = LogManager.getLogger(StateMachineAbstractRule.class);
	protected Set<String> ignoreStates = new HashSet<String>();
	protected String pushState;
	protected Map<String, String> properties = new HashMap<String,String>();
	protected Map<String, String> restrictions = new HashMap<String,String>();
	
	public boolean overrideState()
	{
		return false;
	}
	
	/**
	 * Override this in rules that should stop on the first
	 * child that processes the event.  Ignored for parent rules
	 * @return true to stop on first processed child
	 */
	public int stopOnChild()
	{
		return StateMachineTransitionRule.CONTINUE;
	}
	
    public void addIgnoreState(String stateName)
    {
    	ignoreStates.add(stateName);
    }
    
    public Set<String> getIgnoreStates()
    {
    	return ignoreStates;
    }
	
    public void setPushState(String state)
    {
    	this.pushState = state;
    }
    public String getPushState()
    {
    	return pushState;
    }
	public String getProperty(String propertyName)
	{
		return properties.get(propertyName);	
	}
	
	public String setProperty(String propertyName, String value)
	{
		return properties.put(propertyName,value);
	}
	 
	public Map<String,String> getProperties()
	{
		return Collections.unmodifiableMap(properties);
	}    
    

	public String getRestriction(String restriction)
	{
		return restrictions.get(restriction);	
	}
	
	public String setRestriction(String restriction, String value)
	{
		return restrictions.put(restriction,value);
	}
	
	public boolean hasRestriction(String restriction)
	{
		return restrictions.containsKey(restriction);
	}
	
	public StateMachineState evaluateConditions(StateMachine stateMachine,
                                                StateMachineState targetState, StateMachineState newState)
	{
		// Nothing to do
		if (newState == null)
		{
			return newState;
		}
		
    	// This restriction is to only set the parent if the parent state weight is 
		// less than the new state weight
    	if (hasRestriction("OnlyIfLess"))
    	{
	        if (logger.isDebugEnabled())
	        {                
	            logger.debug("Rule evaluateCondition is:" + targetState.getStateWeight() + " OnlyIfLess " + newState.getStateWeight());
	        }
    		// If the new state is greater than then the target, we failed the condition
    		if (newState.getStateWeight() > targetState.getStateWeight())
    		{
    			return null;    			
    		}
    	}
    	
    	// This restriction is to only set the parent if the parent state weight is 
		// greater than the new state weight
    	if (hasRestriction("OnlyIfGreater"))
    	{
	        if (logger.isDebugEnabled())
	        {                
	            logger.debug("Rule evaluateCondition is:" + targetState.getStateWeight() + " OnlyIfGreater " + newState.getStateWeight());
	        }    		
    		// If the new state is less than then the target, we failed the condition	        
    		if (newState.getStateWeight() < targetState.getStateWeight())
    		{
    			return null;    			
    		}
    	}
    	if (hasRestriction("OnlyIfInState"))
    	{
    		String states = getRestriction("OnlyIfInState");
	        if (logger.isDebugEnabled())
	        {                
	            logger.debug("Rule evaluateCondition is:" + targetState.getName() + " OnlyIfInState " + states);
	        }    	
	        // If the state is not contained in the list, we failed the condition
    		if (StringUtils.contains(states, targetState.getName()) == false)
    		{
    			return null;    			
    		}
    	}        	
    	
		return newState;
	}
}
