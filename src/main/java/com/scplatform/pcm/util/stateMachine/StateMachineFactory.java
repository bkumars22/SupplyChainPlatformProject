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


import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.scplatform.pcm.common.service.LocatorService;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;

/**
 * Loads the state machines
 * @author bblasko
 *
 */
@Component
public class StateMachineFactory
{
	private final static Logger logger = LogManager.getLogger(StateMachineFactory.class);
	
	private static Map<String, StateMachine> machines;
	
	private final LocatorService locatorService;
	private final PcmConfigUtil pcmConfigUtil;
	
	public StateMachineFactory(LocatorService locatorService, PcmConfigUtil pcmConfigUtil) {
		this.locatorService = locatorService;
		this.pcmConfigUtil = pcmConfigUtil;
	}

	protected synchronized Map<String, StateMachine> getMachines()
	{
		if (machines == null || machines.isEmpty())
		{
			
			try
			{
				StateMachineModelLoader loader = new StateMachineModelLoader();
				List<String> models = pcmConfigUtil.getList("pcm.state_machine.models",Arrays.asList(""));
				List<String> dirs = pcmConfigUtil.getList("pcm.state_machine.dirs",Arrays.asList(""));
				
				if (models == null || models.size() == 0)
				{					
					models = Arrays.asList("PCMStateModel.xml");					
				}
				machines = new HashMap<String, StateMachine>();
				
				Properties configProps;
                            configProps = pcmConfigUtil.getProperties();
				for (String model: models)
				{	
					if (logger.isInfoEnabled())
					{
						logger.info("Loading state machine model:"+model);
					}
					URL url = locatorService.locateResource(model, dirs, true);												
					if (url != null )
					{

						Map<String, StateMachine> loaded = loadMachine(loader,url,configProps);
						for (Map.Entry<String, StateMachine> entry: loaded.entrySet())
						{
							StateMachine newMachine = entry.getValue();
							newMachine.setModelPath(url);
							if (logger.isInfoEnabled())
							{
								logger.info("Loaded " + newMachine);								
							}
							StateMachine oldMachine = machines.put(entry.getKey(), newMachine);
							if (oldMachine != null)
							{
								logger.warn("State machine " + oldMachine + " overridden by " + newMachine);
							}
						}
					}
					else
					{
						logger.warn("Unable to locate machine model:"+model);
					}
				}
			}
			catch(Exception e)
			{				
				logger.error("Unable to configure state machine",e);
			}	
		}
		return machines;		
	}
	
	private static Map<String, StateMachine> loadMachine(StateMachineModelLoader loader, URL url, Map<?, ?> vars)
		throws ParserConfigurationException, SAXException, IOException
	{	
		InputStream stream = null;
		try		
		{
			stream = url.openStream();
			String xmlContent = IOUtils.toString(stream, StandardCharsets.UTF_8);
			
			PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}");
			Properties properties = new Properties();
			vars.forEach((key, value) -> properties.put(key, value));
			String resolvedXml = helper.replacePlaceholders(xmlContent, properties);
			
			return loader.loadStateMachine(new ByteArrayInputStream(resolvedXml.getBytes(StandardCharsets.UTF_8)));
		}
		finally
		{
			IOUtils.closeQuietly(stream);
		}
		
	}
	
	/**
	 * Returns a set of all the machine types
	 * @return set of the names
	 */
	public synchronized Set<String> getAllStateMachineTypes()
	{
		if (getMachines() != null)
		{
			return getMachines().keySet();
		}
		return null;
	}
	
	/**
	 * Returns a machine of type
	 * @param requestType type of machine to get
	 * @return machine or null if not found
	 */
	public synchronized StateMachine getStateMachine(String requestType)
	{
		if (getMachines() != null)
		{
			return (StateMachine)getMachines().get(requestType);
		}
		return null;
	}
	
	public static StateMachineTransitionRule createRule(String ruleName)
	{		
		if ("NOOP".equals(ruleName))
		{
			return null;
		}
		else if ("PUSH_PARENT_ALWAYS".equals(ruleName))
		{
			return new PushParentAlwaysRule();
		}
		else if ("PUSH_PARENT_IF_ALL".equals(ruleName))
		{
			return new PushParentIfAllRule();
		}
		else if ("PUSH_PARENT_IF_ANY".equals(ruleName))
		{
			return new PushParentIfAnyRule();
		}
		else if ("PUSH_PARENT_IF_ALL_OTHER".equals(ruleName))
		{
			return new PushParentIfAllOther();
		}
		else if ("PUSH_PARENT_LEAST_CHILD".equals(ruleName))
		{
			return new PushParentLeastChild();
		}
		else if ("PUSH_PARENT_MAX_CHILD".equals(ruleName))
		{
			return new PushParentMaxChild();
		}		
		else if ("PUSH_CHILD_IF_SAME".equals(ruleName))
		{
			return new PushChildIfSameRule(StateMachineTransitionRule.CONTINUE);
		}
		else if ("PUSH_FIRST_CHILD_IF_SAME".equals(ruleName))
		{
			return new PushChildIfSameRule(StateMachineTransitionRule.STOP_ON_FIRST_CHANGE);
		}
		else if ("PUSH_CHILD_ALWAYS".equals(ruleName))
		{
			return new PushChildAlwaysRule(StateMachineTransitionRule.CONTINUE);
		}
		else if ("PUSH_FIRST_CHILD_ALWAYS".equals(ruleName))
		{
			return new PushChildAlwaysRule(StateMachineTransitionRule.STOP_AFTER_FIRST_CHILD);
		}
		
		else
		{
			try
			{
				Class c = Class.forName(ruleName);
				Object rule = c.newInstance();
				if (rule instanceof StateMachineTransitionRule)
				{
					return (StateMachineTransitionRule)rule;
				}
			}
			catch (ClassNotFoundException e)
			{
				logger.error("Unable to load rule class " + ruleName, e );
			}
			catch (InstantiationException e)
			{				
				logger.error("Unable to create rule class " + ruleName ,e);				
			}
			catch (IllegalAccessException e)
			{				
				logger.error("No access to rule class " + ruleName,e );
			}
		}
		return null;
	}
	
}
