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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StateMachineState
{
	/**
	 * 
	 */
	private final static String defaultRule = "DEFAULT";
	private final StateMachine stateMachine;
	private int stateWeight = 10;
	
    String stateName;
    String stateLabel;
    boolean initialState = false;
    Map<StateMachineEvent,StateMachineState> nextStates = new HashMap<StateMachineEvent,StateMachineState>();
    Map<String, StateMachineTransitionRule> parentActionMap = new HashMap<String, StateMachineTransitionRule>();
    Map<String, StateMachineTransitionRule> childActionMap = new HashMap<String, StateMachineTransitionRule>();
    protected Map<String,String> properties = new HashMap<String,String>();
    
    public String toString()
    {
        return "State:" + stateName;
    }

    protected StateMachine getStateMachine()
    {
    	return stateMachine;
    }
    
    public StateMachineState(StateMachine machine2, String name, String label)
    {
        this.stateMachine = machine2;
		stateName = name;
        stateLabel = label;
    }
    
    public StateMachineState(StateMachine machine2, String name, String label,
                             StateMachineTransitionRule parentAction, StateMachineTransitionRule childAction)
    {
        this.stateMachine = machine2;
		stateName = name;
        stateLabel = label;
        parentActionMap.put(defaultRule,parentAction);
        childActionMap.put(defaultRule,childAction);
    }

    public StateMachineEvent addNextState(String eventName, StateMachineState s)
    {
    	StateMachineEvent event = new StateMachineEvent(eventName);
        nextStates.put(event, s);
        return event;
    }

    public String getName()
    {
        return stateName;
    }

    public String getLabel()
    {
        return stateLabel;
    }

    /**
     * Set the state weight, the higher the value, the more weight it has
     * This is used by some rules in determining what the parent
     * state should be.   
     * @param stateWeight as an int
     */
    public void setStateWeight(int stateWeight)
	{
		this.stateWeight = stateWeight;
	}

    /**
     * Returns the states weigth
     * @return weight as an int
     */
	public int getStateWeight()
	{
		return stateWeight;
	}

	public StateMachineState getNextState(String event)
    {
        return (StateMachineState) nextStates.get(new StateMachineEvent(event));
    }

    public void addDefaultParentRule(StateMachineTransitionRule rule)
    {
    	parentActionMap.put(defaultRule,rule);
    }
    
    public void addParentRule(Class obj, StateMachineTransitionRule rule)
    {
    	parentActionMap.put(obj.getName(),rule);
    }
    
    public StateMachineTransitionRule getParentRule(StateMachineReactor obj)
    {
    	if (parentActionMap.containsKey(obj.getClass().getName()))
    	{
    		return (StateMachineTransitionRule)parentActionMap.get(obj.getClass().getName());
    	}
    	return (StateMachineTransitionRule)parentActionMap.get(defaultRule);
    }

    public void addDefaultChildRule(StateMachineTransitionRule rule)
    {
    	childActionMap.put(defaultRule,rule);
    }
    
    public void addChildRule(Class obj, StateMachineTransitionRule rule)
    {
    	childActionMap.put(obj.getName(),rule);
    }
    
    public StateMachineTransitionRule getChildRule(StateMachineReactor obj)
    {
    	if (childActionMap.containsKey(obj.getClass().getName()))
    	{
    		return (StateMachineTransitionRule)childActionMap.get(obj.getClass().getName());
    	}
    	return (StateMachineTransitionRule)childActionMap.get(defaultRule);
    }
    
    public boolean isInitialState()
	{
		return initialState;
	}

	public void setInitialState(boolean initialState)
	{
		this.initialState = initialState;
		if (this.stateMachine != null && initialState)
		{
			stateMachine.setInitialState(this);
		}
	}

	public String getProperty(String propertyName)
	{
		return properties.get(propertyName);	
	}
	
	public Object setProperty(String propertyName, String value)
	{
		return properties.put(propertyName,value);
	}
	 
	public Map<String,String> getProperties()
	{
		return Collections.unmodifiableMap(properties);
	}
	
	/**
	 * Return true if the operation is not in the prohibitOperation property
	 */
	public boolean operationAllowed(String operation)
	{
		String ops = getProperty("prohibitOperation"); 
		if (ops != null)
		{
			return !StringUtils.contains(ops, operation);
		}
		return true;
	}
	/**
     * Returns true if other is a StateMachineState with the same name
     * or if other is a string of the same name as this StateMachineState's state name
     */
    public boolean equals(Object other)
    {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (other instanceof StateMachineState)
        {
        	StateMachineState otherState = (StateMachineState)other;
        	EqualsBuilder eb = new EqualsBuilder();
        	return eb.append(this.getName(),otherState.getName()).isEquals();
        }
        if (other instanceof String)
        {
        	EqualsBuilder eb = new EqualsBuilder();
        	return eb.append(this.getName(),(String)other).isEquals();
        }
        
        return false;
    }
}