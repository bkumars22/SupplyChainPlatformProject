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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.*;


/**
 * A state machine for processing the request states
 * 
 */
public class StateMachine
{
	protected final static Logger logger = LogManager.getLogger(StateMachine.class);
    
    private Map<String, StateMachineState> states = new HashMap<String,StateMachineState>();
    private String name;
	private StateMachineState initialState;
	private URL modelPath;
	public static int DIR_NONE = 0;
    public static int DIR_UP = 1;
    public static int DIR_DOWN = 2;   
    
    private Set<String> appliesToBusinessObjects = new HashSet<String>();
    private final ThreadLocal<String> activeUser = new ThreadLocal<String>();    
    private final ThreadLocal<StateMachineVisitor> visitor = new ThreadLocal<StateMachineVisitor>();
    
    
    public StateMachine(String name)
    {
    	this.name = name;
    }

    public String getName()
    {	
    	return name;
    }

    public URL getModelPath()
	{
		return modelPath;
	}

	public void setModelPath(URL modelPath)
	{
		this.modelPath = modelPath;
	}

    
	private void setActiveUser(String active)
	{
		activeUser.set(active);
	}
	
	public String getActiveUser()
	{
		return activeUser.get();
	}

	public void setVisitor(StateMachineVisitor visitor)
	{
		this.visitor.set(visitor);
	}
	
	public StateMachineVisitor getVisitor()
	{
		return visitor.get();
	}
	
    protected void addState(StateMachineState state)
    {
        states.put(state.stateName, state);
    }

    public StateMachineState getState(String stateName)
    {
        return states.get(stateName);
    }

    public Map<String, StateMachineState> getStates()
    {
    	return Collections.unmodifiableMap(states);
    }
    
    public Collection<StateMachineState> getNextStates(String currentState)
    {
        return getState(currentState).nextStates.values();
    }

    /**
     * Given the current state, and the desired, state, find the event that will perform the transition
     * @param currentStateName 
     * @param nextStateName
     * @return Event that would perform this transition
     */
    public String getEventForStateTransition(String currentStateName, String nextStateName)
    {
    	StateMachineState state = getState(currentStateName);
    	if (state != null && state.nextStates != null)
    	{
    		Iterator<Map.Entry<StateMachineEvent, StateMachineState>> itr = state.nextStates.entrySet().iterator();
    		while (itr.hasNext())
    		{
    			Map.Entry<StateMachineEvent, StateMachineState> entry = itr.next();
    			
    			if (nextStateName.equals(((StateMachineState)entry.getValue()).stateName))
    			{
    				return ((StateMachineEvent)entry.getKey()).getEventName();
    			}
    		}
    	}
    	return null;
    }
    
    public boolean getIsEventValid(String currentStateName, String event)
    {
        StateMachineState next = null;
        StateMachineState currentState = getState(currentStateName);
        if (currentState != null)
        {
            next = (StateMachineState) currentState.getNextState(event);
            if (next != null)
            {
                return true;
            }
        }
        return false;
    }

    public StateMachineEvent getEvent(String currentStateName, String eventName)
    {
        StateMachineState currentState = getState(currentStateName);
        if (currentState != null)
        {
        	Iterator<StateMachineEvent> itr = currentState.nextStates.keySet().iterator();          
            while (itr.hasNext())
            {
            	StateMachineEvent event = itr.next();
            	// StateMachineEvent.equals handles using the event name as string
            	if (event.equals(eventName))
            	{
            		return event;
            	}
            }
        }
        return null;
    }
    
    public StateMachineState getNextState(String currentStateName, String event)
            throws InvalidStateException
    {
        StateMachineState next = null;
        StateMachineState currentState = getState(currentStateName);
        if (currentState != null)
        {
            next = (StateMachineState) currentState.getNextState(event);
            if (next == null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("GetNextState Event:" + event + " Current " + currentState + " does not exist");
                }
                throw new InvalidStateException(
                        InvalidStateException.INVALID_STATE,currentStateName, event);
            }
        }
        else
        {
            if (logger.isDebugEnabled())
            {            
                logger.debug("GetNextState Event:" + event + " Current " + currentStateName + " does not exist");
            }
            throw new InvalidStateException(InvalidStateException.INVALID_STATE,
                    currentStateName,event);
        }
        return next;
    }

    /**
     * Returns a set of all valid events for the given state
     * @param currentState current state
     * @return Set of valid events, or empty set if none are valid
     */
    public Set<StateMachineEvent> getValidEventsForState(String currentState)
    {
        Set<StateMachineEvent> result = new HashSet<StateMachineEvent>();
        StateMachineState state = getState(currentState);
        if (state != null)
        {
            result.addAll(state.nextStates.keySet());
        }
        return result;
    }

    /**
     * Returns the list of all events modeled
     * @return
     */
    public List<StateMachineEvent> getAllEvents()
    {
        List<StateMachineEvent> result = new ArrayList<StateMachineEvent>();
        Iterator<StateMachineState> itr = getAllStates().iterator();         
        while(itr.hasNext())
        {
        	StateMachineState state = itr.next();
            result.addAll(state.nextStates.keySet());
        }
        return result;
    }
    
    public Map<StateMachineEvent,Set<StateMachineState>> getEventStateTransitions()
    {
        Map<StateMachineEvent, Set<StateMachineState>> result = new HashMap<StateMachineEvent,Set<StateMachineState>>();         
        for (StateMachineState state: getAllStates())
        {
        	for (StateMachineEvent event: state.nextStates.keySet())
        	{
        		Set<StateMachineState> states = result.get(event);
        		if (states == null)
        		{
        			states = new HashSet<StateMachineState>();
        			result.put(event, states);
        		}
        		states.add(state.nextStates.get(event));
        	}
        }
        return result;
    }

    public Set<StateMachineState> getEventStateTransitions(String eventName)
    {
		Set<StateMachineState> result = new HashSet<StateMachineState>();         
        for (StateMachineState state: getAllStates())
        {
        	for (StateMachineEvent event: state.nextStates.keySet())
        	{
        		if (event.getEventName().equals(eventName))
        		{ 
        			result.add(state.nextStates.get(event));
        		}
        	}
        }
        return result;
    }
    
    public List<StateMachineState> getValidStates(String currentState)
    {
        List<StateMachineState> result = new ArrayList<StateMachineState>();
        StateMachineState state = getState(currentState);
        if (state != null)
        {
            result.add(state);
            result.addAll(state.nextStates.values());
        }
        return result;
    }

    public Collection<StateMachineState> getAllStates()
    {
        return states.values();
    }
    
    public Set<String> getAppliesToBusinessObjects()
	{
		return appliesToBusinessObjects;
	}

	public void setAppliesToBusinessObjects(Set<String> appliesToBusinessObjects)
	{
		this.appliesToBusinessObjects = appliesToBusinessObjects;
	}

	public void changeState(String event, StateMachineReactor obj, String activeUserId, boolean startNode)
    	throws InvalidStateException
    {
        if (logger.isDebugEnabled())
        {        
            logger.debug("ChangeState Event:" + event + " ReactorObject " + obj);
        }
        setActiveUser(activeUserId);
        changeState(event,obj,DIR_NONE,obj.getState(),null,null,startNode);
    }
	
	public void changeState(String event, StateMachineReactor obj, String activeUserId)
	throws InvalidStateException
	{
	    if (logger.isDebugEnabled())
	    {        
	        logger.debug("ChangeState Event:" + event + " ReactorObject " + obj);
	    }
	    setActiveUser(activeUserId);
	    changeState(event,obj,DIR_NONE,obj.getState(),null,null,false);
	}
    
    
    /**
     * process the event
     * @param event - name of the event
     * @param target - object to process
     * @param direction - direction of process
     * @param currentState - current state\
     * @param state - state machine state to get the rules from.  If null, state is determined by calling 
     * determineNextState
     * @throws InvalidStateException
     */
    protected void changeState(String event, StateMachineReactor target, int direction, String currentState,
    		StateMachineState machineState, String overrideStateName, boolean startNode)
        throws InvalidStateException
    {
    	
    	if (machineState == null)
    	{
    		machineState = determineNextState(event,target,direction,currentState);
    	}
        if (machineState != null)
        {
        	// If a name was passed in, we use the overrride
        	String nextStateName = (overrideStateName != null) ? overrideStateName: machineState.getName();
        	
            if (logger.isDebugEnabled())
            {
                logger.debug("ChangeState Event:" + event + " MachineState:" + machineState + " State:" + nextStateName);
            }    		
            if (handleAllowStateChangeCallback(target,event,nextStateName,direction,startNode) == false)
            {
            	logger.info("allowStateChange callback returned false, processing stopped");
            	return;            	
            }
           	// Need to set our state before we process parents or children
           	target.setState(nextStateName);	
           	target.setStatusChangeDate(new Date());
           	target.setStatusLastChangeBy(getActiveUser());
            if (direction != DIR_DOWN)
            {
            	processParentRule(event, machineState, target,currentState, startNode);
            }
            if (direction != DIR_UP)
            {
            	processChildRule(event, machineState, target,currentState, startNode);
            }
            
        }
    }


	/**
     * Process the parent rules
     * @param event - event that invoked this proces
     * @param machineState - state
     * @param obj - object to process
     * @param currentStateName - name of the origional state on first object
     * @param startNode - flag indicating we are processing the start node
     * @throws InvalidStateException
     */
    protected void processParentRule(String event, StateMachineState machineState,
    		StateMachineReactor obj, String currentStateName, boolean startNode) 
    	throws InvalidStateException
    {
        StateMachineTransitionRule parentRule = machineState.getParentRule(obj);    	
        if (parentRule != null)
        {
            if (logger.isDebugEnabled())
            {                
                logger.debug("ProcessParentRule PushUp:" + parentRule);
            }
            StateMachineReactor parent = obj.getParent();
            if (handleVisitCallback(parent,machineState,DIR_UP,currentStateName) == false)
            {
            	return;
            }
            if (parent != null && machineState.getName().equals(parent.getState()) == false)
            {
            	String newState = parentRule.evaluate(event,parent,machineState,DIR_UP,currentStateName);
            	if (newState != null)
            	{
            		if (parentRule.getPushState() != null)
            		{
                		// Push state set, so we want to process using the push state
                		// and the existing rule set of the machineState passed in            			
            			changeState(event, parent, DIR_UP,currentStateName,machineState,parentRule.getPushState(),false);            			
            		}            		
            		else if (parentRule.overrideState())
            		{
                		// Override set, so we want to process using the state returned
            			// from the rule and then go up using
                		// the existing rule set of the origional machineState passed in            			
            			changeState(event, parent, DIR_UP,currentStateName,machineState,newState,false);
            		}
            		else
            		{
            			// Normal behavior, so just fire the event on each
            			// object up the hierarchy.
                        changeState(event, parent, DIR_UP,currentStateName,null,null,false);
                    }
                }
            }
        }    	
    }

	/**
     * Process the child rules
     * @param event - event that invoked this proces
     * @param machineState - state
     * @param target - object to process
     * @param currentStateName - name of the origional state on first object 
     * @throws InvalidStateException
     */    
    protected void processChildRule(String event, StateMachineState machineState, 
    		StateMachineReactor target, String currentStateName, boolean startNode) 
    	throws InvalidStateException
    {
        StateMachineTransitionRule childRule = machineState.getChildRule(target);
        if (childRule != null)
        {
        	if (logger.isDebugEnabled())
        	{
        		logger.debug("ProcessChildRule PushDown:PushChild");
        	}                                            
        	Collection<StateMachineReactor> children = target.getChildren();
        	if (children != null)
        	{
        		for (StateMachineReactor child: children)
        		{
        			if (child == null)
        			{
        				logger.warn("NULL child returned from getChildren for object : " + target);
        				continue;
        			}
        			if (logger.isDebugEnabled())
        			{
        				logger.debug("ProcessChildRule PushDown:PushChild ChildAction:" + machineState.getChildRule(target) + " State:" + machineState.getName() + " Child State:" +child.getState());
        			}        
        			if (handleVisitCallback(child,machineState,DIR_DOWN,currentStateName) == false)
        			{
        				continue;
        			}
        			if (machineState.getName().equals(child.getState()) == false)
        			{
        				String newState = childRule.evaluate(event,child,machineState,DIR_DOWN,currentStateName);        				
        				if (newState != null)
        				{
        					if (childRule.overrideState())
        					{
            					changeState(event, child, DIR_DOWN,currentStateName,machineState,newState,false);
        					}
        					else
        					{
        						changeState(event, child, DIR_DOWN,currentStateName,null,null,false);
        					}
        					
        					// Stop on first change
        					if (childRule.stopOnChild() == StateMachineTransitionRule.STOP_ON_FIRST_CHANGE)
        					{
        			        	if (logger.isDebugEnabled())
        			        	{
        			        		logger.debug("ProcessChildRule PushDown:STOP_ON_FIRST_CHANCE");
        			        	}                                                    						
        						break;
        					}
        				}        				
        			}
    				// Stop on first always        			
        			if (childRule.stopOnChild() == StateMachineTransitionRule.STOP_AFTER_FIRST_CHILD)
					{        				
			        	if (logger.isDebugEnabled())
			        	{
			        		logger.debug("ProcessChildRule PushDown:STOP_AFTER_FIRST_CHILD");
			        	}                                                    						
        				
						break;
					}

        		}       		
        	}
        }
    }
    	    
    protected StateMachineState determineNextState(String event, StateMachineReactor target, 
    		int direction, String currentState)
    	throws InvalidStateException
    {
    	StateMachineState state = null;
    	try
    	{
    		state = getNextState(target.getState(), event);
    	}
    	catch(InvalidStateException ise)
    	{
    		// It may be that we need to move the state from the child first
    		if (direction == DIR_UP)
    		{
    			if (logger.isDebugEnabled())
    			{
    				logger.debug("ChangeState PushUp: Forceup parent state, from:" 
    						+ target.getState() + " to:" + currentState);
    			}
    			state = getNextState(currentState, event);
    		}
    		else
    		{
    			throw ise;
    		}
    	}
    	return state;
    }

	private boolean handleVisitCallback(StateMachineReactor target, StateMachineState machineState, 
			int direction, String currentStateName)
	{
		boolean continueProcessing = true;
    	StateMachineVisitor smv = this.getVisitor();
    	if (smv != null)
    	{
    		continueProcessing = smv.visit(target,machineState,direction,currentStateName);
        	if (logger.isInfoEnabled())
        	{
        		logger.info("Visitor callback:visit returned:" + continueProcessing);
        	}    		
    	}			
    	return true;
	}

    private boolean handleAllowStateChangeCallback(StateMachineReactor target, String event, 
			String nextStateName, int direction, boolean startNode)
	{
    	boolean continueProcessing = true;
    	StateMachineVisitor smv = this.getVisitor();
    	if (smv != null)
    	{
    		continueProcessing = smv.allowStateChange(target,event, nextStateName, direction, startNode);
        	if (logger.isInfoEnabled())
        	{
        		logger.info("Visitor callback:allowStateChange returned:" + continueProcessing);
        	}    		
    	}			
    	return continueProcessing;
	}
	
	public void setInitialState(StateMachineState state)
	{
		initialState = state;		
	}  
	
	public StateMachineState getInitialState()
	{
		return initialState;
	}

	/**
	 * Helper that will find the state and check the operation.  If the
	 * state is not found or null, the method returns true.
	 * @param stateName
	 * @param operation
	 * @return true if this operation is allowed on this state
	 */
	public boolean operationAllowed(String stateName, String operation)
	{
		boolean result = true;
		StateMachineState sms = getState(stateName);
		if (sms != null)
		{
			result = sms.operationAllowed(operation);
		}
		return result;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("StateMachine[name:");
		sb.append(name);
		sb.append(", model:");
		sb.append(this.modelPath);
		sb.append("]");
		return sb.toString();
	}
}
