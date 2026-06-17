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

import java.util.*;
/**
 * Models an event of the state machine
 * @author bblasko
 *
 */
public class StateMachineEvent
{
	protected String eventName;
	protected String uiButtonLabel = null;
	protected String uiPrompt = null;
	protected String uiAckMessage = null;
	protected boolean uiMultiTargetAllowed = true;
	protected Set<String> requiresAssignedResps = null;
	protected Map<String,String> properties = new HashMap<String,String>();
	
	public StateMachineEvent(String eventName)
	{
		this.eventName = eventName;
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
	 * Returns the event name;
	 * @return eventName
	 */
	public String getEventName()
	{
		return eventName;
	}
	
	/**
	 * Returns true if this event can be initiated from a UI
	 * Returns true if the UIButtonLabel is set
	 * @return true if event can be initiated by user via UI
	 */
	public boolean getIsUiEnabled()
	{
		return !StringUtils.isBlank(uiButtonLabel);
	}
	
	/**
	 * Set the resource key to use when displaying button
	 * Also used to key when the event should be displayed on UI
	 * @param uiLabel
	 */
	public void setUiButtonLabel(String uiLabel)
	{
		this.uiButtonLabel = uiLabel; 
	}

	/**
	 * Returns the resource key to use for a button or label
	 * @return
	 */
	public String getUiButtonLabel()
	{
		return uiButtonLabel;
	}

	/**
	 * Set a resource key to use for displaying a prompt
	 * when this event is invoked from the ui
	 * A prompt asking for user input will be presented
	 * @param uiPrompt
	 */
	public void setUiPrompt(String uiPrompt)
	{
		this.uiPrompt = uiPrompt;
	}

	public String getUiPrompt()
	{
		return uiPrompt;
	}

	
	/**
	 * Returns true if the uiPrompt is set
	 * @return
	 */
	public boolean getIsUiPromptRequired()
	{
		return StringUtils.isBlank(uiPrompt);
	}
	
	/**
	 * Set a resource key to use for an acknologment message
	 * when this event is invoked from the ui.  The user will
	 * be presented with a message
	 * @param uiAckMessage
	 */
	public void setUiAckMessage(String uiAckMessage)
	{
		this.uiAckMessage = uiAckMessage;
	}

	public String getUiAckMessage()
	{
		return uiAckMessage;
	}

	public boolean getIsUiAckRequired()
	{
		return StringUtils.isBlank(uiAckMessage);
	}

	/**
	 * Set if this event should be included in UIs that allow multiple items to
	 * be processed.
	 * @param uiMultiSelect
	 */
	public void setUiMultiTargetAllowed(boolean uiMultiSelect)
	{
		this.uiMultiTargetAllowed = uiMultiSelect;
	}

	/**
	 * Returns true if this event should support multiple targets at once.
	 * For instance, some UIs allow the event to be invoked on a list of targets.
	 * If this value is false, the UI should not allow this.
	 * @return
	 */
	public boolean getUiMultiTargetAllowed()
	{
		return uiMultiTargetAllowed;
	}
	
	/**
	 * Optional list of responsibilities.  These can then be used
	 * to determine if the user needs to be assigned to the target
	 * object with a responsibility.   It is used by the ui
	 * to determine when a event action should be presented to the user
	 * @param responsibilities Collection of values.  Since this is a set, duplicate will be ignored
	 */
	public void setRequiresAssignment(Collection<String> responsibilities)
	{
		this.requiresAssignedResps = new HashSet<String>(responsibilities);
	}

	public Set<String> getRequiresAssignment()
	{
		return requiresAssignedResps;
	}

	/**
	 * check property prohibit propery for target, return true if found
	 * @param target
	 * @return
	 */
	public boolean prohibitForTarget(String target)
	{
		return StringUtils.contains(getProperty("prohibitTarget"), target);
	}
	
	public String toString()
	{
		return eventName;
	}
	
    public boolean equals(Object other)
    {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (other instanceof StateMachineEvent)
        {
        	return (this.eventName.equals(((StateMachineEvent)other).eventName));
        }
        if (other instanceof String)
        {
        	return (this.eventName.equals((String)other));
        }
        return false;
    }
    
    public int hashCode()
    {
    	return (eventName != null) ? eventName.hashCode():-1;
    }

}
