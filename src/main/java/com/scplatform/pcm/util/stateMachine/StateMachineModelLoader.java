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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
public class StateMachineModelLoader extends DefaultHandler
{
	private static final String MODEL = "PCMModel";
	private static final String BP_ATTR = "businessProcess";	
	private static final String NEXT_STATE_ATTR = "nextState";
	private static final String EVENT = "Event";	
	private static final String PROPERTY = "Property";
	private static final String RESTRICTION = "Restriction";	
	private static final String UI_MULTI_TARGET_ATTR = "uiMultiTargetAllowed";
	private static final String UI_LABEL_ATTR = "uiButtonLabel";
	private static final String UI_PROMPT_ATTR = "uiMessagePrompt";	
	private static final String UI_ACK_ATTR = "uiAckMessage";
	private static final String EVENT_REQUIRES_ASSIGNMENT_ATTR = "requiresAssignment";	
	private static final String STATE_TRANSITION = "StateTransition";
	private static final String CHILD_RULE = "ChildRule";
	private static final String REACTOR_ATTR = "reactor";
	private static final String TYPE_ATTR = "type";
	private static final String PARENT_RULE = "ParentRule";
	private static final String PARENT_STATE_ATTR = "parentState";		
	private static final String LABEL_ATTR = "label";
	private static final String NAME_ATTR = "name";
	private static final String VALUE_ATTR = "value";
	private static final String STATE = "State";
	private static final String STATE_ATTR = "state";	
	private static final String WEIGHT_ATTR = "weight";
	private static final String IGNORE_STATE = "IgnoreState";
	private static final String INIT_STATE_ATTR = "initial";
	private static final String BP_APPLIES_TO_ATTR = "appliesToBusinessObjects";
	
	private final static Logger logger = LogManager.getLogger(StateMachineModelLoader.class);
	
	private Map<String, StateMachine> machines;
	private StateMachine stateMachine;
	private StateMachineState currentState;
	private StateMachineEvent currentEvent;
	private StateMachineTransitionRule currentRule;
	public Map<String, StateMachine> loadStateMachine(String xmlModel)
		throws ParserConfigurationException, SAXException, IOException
	{
		parse(new FileInputStream(xmlModel));
		return machines;
	}
	
	public Map<String, StateMachine> loadStateMachine(InputStream xmlModelStream)
		throws ParserConfigurationException, SAXException, IOException
	{
		parse(xmlModelStream);
		return machines;
	}
	
    public void parse(InputStream modelStream) 
    	throws ParserConfigurationException, SAXException, IOException
    {
        long t = System.currentTimeMillis();
        logger.info("Starting parse of Model");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser;
        parser = factory.newSAXParser();
        parser.parse(modelStream, this);
        logger.info("Parse complete in " + (System.currentTimeMillis() - t)
                + " ms");
    }
	
	public void startDocument()
		throws SAXException
	{
		machines = new HashMap<String, StateMachine>();
		super.startDocument();
	}

	public void startElement(String uri, String localName, String qName, Attributes attr)
		throws SAXException
	{
		if (MODEL.equals(qName))
		{
			String name = attr.getValue(BP_ATTR);
			String appliesTo = StringUtils.trimToNull(attr.getValue(BP_APPLIES_TO_ATTR));			
			stateMachine = new StateMachine(name);
			if (appliesTo != null)
			{
				String[] bolist = appliesTo.split(",");
				Set<String> boset = new HashSet<String>();
				for (int idx=0; idx < bolist.length; idx++)
				{
					boset.add(bolist[idx]);
				}
				stateMachine.setAppliesToBusinessObjects(boset);
			}
			machines.put(name,stateMachine);
		}
		else if (STATE.equals(qName))
		{
			String name = attr.getValue(NAME_ATTR);
			if (name.length() > 20)
			{
				throw new SAXException("State Name:"+name+" too long.  Shorten name to 20 or less characters");
			}
			String label = attr.getValue(LABEL_ATTR);
			String weight = attr.getValue(WEIGHT_ATTR);
			currentState = new StateMachineState(stateMachine,name,label);
			stateMachine.addState(currentState);
			if (weight != null)
			{
				try
				{
					currentState.setStateWeight(Integer.parseInt(weight));
				}
				catch(Exception e)
				{
					logger.warn("Invalid weight set for state:" + qName +". Default was used");
				}
			}
			currentState.setInitialState(BooleanUtils.toBoolean(attr.getValue(INIT_STATE_ATTR)));
		}
		else if (PARENT_RULE.equals(qName))
		{
			String type = attr.getValue(TYPE_ATTR);
			currentRule = StateMachineFactory.createRule(type);
			if (currentRule == null && "NOOP".equals(type) == false)
			{
				throw new SAXException("Invalid rule type " + type);
			}
			if (currentRule != null)
			{
				currentRule.setPushState(attr.getValue(PARENT_STATE_ATTR));
			}
			String reactor = attr.getValue(REACTOR_ATTR);
			if (reactor == null || reactor.length() == 0)
			{
				currentState.addDefaultParentRule(currentRule);
			}
			else
			{				
				currentState.addParentRule(loadReactorClass(reactor),currentRule);
			}
		}
		else if (CHILD_RULE.equals(qName))
		{
			String type = attr.getValue(TYPE_ATTR);
			currentRule = StateMachineFactory.createRule(type);
			if (currentRule == null && "NOOP".equals(type) == false)
			{
				throw new SAXException("Invalid rule type " + type);
			}
			
			String reactor = attr.getValue(REACTOR_ATTR);
			if (reactor == null || reactor.length() == 0)
			{
				currentState.addDefaultChildRule(currentRule);
			}
			else
			{				
				currentState.addChildRule(loadReactorClass(reactor),currentRule);
			}
		}
		else if (IGNORE_STATE.equals(qName))
		{
			if (currentRule != null)
			{
				currentRule.addIgnoreState(attr.getValue(STATE_ATTR));
			}
		}

		else if (STATE_TRANSITION.equals(qName))
		{
			String stateName = attr.getValue(STATE_ATTR);
			currentState = stateMachine.getState(stateName);
			if (currentState == null)
			{
				throw new SAXException("Invalid state in StateTransition, State:" + stateName);
			}
		}		
		else if (EVENT.equals(qName))
		{
			String eventName = attr.getValue(NAME_ATTR);
			String nextStateName = attr.getValue(NEXT_STATE_ATTR);
			StateMachineState nextState = stateMachine.getState(nextStateName);
			if (nextState == null)
			{
				throw new SAXException("Invalid nextState in Event:" + eventName + ", State:" + nextStateName);
			}
			StateMachineEvent event = currentState.addNextState(eventName,nextState);
			event.setUiButtonLabel(attr.getValue(UI_LABEL_ATTR));
			event.setUiPrompt(attr.getValue(UI_PROMPT_ATTR));
			event.setUiAckMessage(attr.getValue(UI_ACK_ATTR));
			if (StringUtils.isBlank(attr.getValue(UI_MULTI_TARGET_ATTR)) == false)
			{
				event.setUiMultiTargetAllowed(BooleanUtils.toBoolean(attr.getValue(UI_MULTI_TARGET_ATTR)));
			}
			if (StringUtils.isBlank(attr.getValue(EVENT_REQUIRES_ASSIGNMENT_ATTR)) == false)
			{
				String[] set = attr.getValue(EVENT_REQUIRES_ASSIGNMENT_ATTR).split(",");
				event.setRequiresAssignment(Arrays.asList(set));
			}
			currentEvent = event;
		}
		else if (PROPERTY.equals(qName))
		{
			// Rules are in states, so check for rule first
			if (currentRule != null)
			{
				String name = StringUtils.trimToNull(attr.getValue(NAME_ATTR));
				String value = StringUtils.trimToNull(attr.getValue(VALUE_ATTR));
				currentRule.setProperty(name, value);				
			}
			else if (currentEvent != null)
			{
				String name = StringUtils.trimToNull(attr.getValue(NAME_ATTR));
				String value = StringUtils.trimToNull(attr.getValue(VALUE_ATTR));
				currentEvent.setProperty(name, value);
			}			
			else if (currentState != null)
			{
				String name = StringUtils.trimToNull(attr.getValue(NAME_ATTR));
				String value = StringUtils.trimToNull(attr.getValue(VALUE_ATTR));
				currentState.setProperty(name, value);				
			}
			
		}
		else if (RESTRICTION.equals(qName))
		{
			if (currentRule != null)
			{
				String name = StringUtils.trimToNull(attr.getValue(NAME_ATTR));
				String value = StringUtils.trimToNull(attr.getValue(VALUE_ATTR));
				currentRule.setRestriction(name,value);				
			}

		}
	}
	
	public void endElement(String uri, String localName, String qName)
		throws SAXException
	{
		if (STATE_TRANSITION.equals(qName))
		{
			currentState = null;
		}		
		else if (EVENT.equals(qName))
		{
			currentEvent = null;
		}
		else if (STATE.equals(qName))
		{
			currentState = null;
		}
		else if (PARENT_RULE.equals(qName))
		{
			currentRule = null;
		}
		else if (CHILD_RULE.equals(qName))
		{
			currentRule = null;
		}
		
	}

	
	protected Class loadReactorClass(String name) 
		throws SAXException
	{
		try
		{
			return getClass().getClassLoader().loadClass(name);
		}
		catch (ClassNotFoundException e)
		{
			throw new SAXException(e);
		}
	}
	
}
