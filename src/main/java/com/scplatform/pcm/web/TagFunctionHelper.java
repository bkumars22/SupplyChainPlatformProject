/*
 * Copyright (c) 2007 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2007, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;


import com.scplatform.pcm.util.stateMachine.StateMachine;
import com.scplatform.pcm.util.stateMachine.StateMachineEvent;
import com.scplatform.pcm.util.stateMachine.StateMachineHelper;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.item.entity.ItemCategory;
import com.scplatform.pcm.item.service.ItemService;
import com.scplatform.pcm.util.message.SCPlatformMessages;


/**
 * Provides a set of static functions exposed as EL functions for use
 * in the JSP pages
 * @author suvasish.bhoi
 *
 */
public class TagFunctionHelper
{
	protected static Log logger = LogFactory.getLog(TagFunctionHelper.class);
	protected static String PRODUCT_VERSION = null;

	public static boolean arrayContains(Object[] array, Object value)
	{
		return ArrayUtils.contains(array, value);
	}

	private static Object invokeMethod(Object target, String methodName, Class<?>[] parameterTypes, Object... args)
	{
		if (target == null)
		{
			return null;
		}

		try
		{
			Method method = target.getClass().getMethod(methodName, parameterTypes);
			return method.invoke(target, args);
		}
		catch (ReflectiveOperationException e)
		{
			logger.debug("Unable to invoke method " + methodName + " on " + target.getClass().getName(), e);
			return null;
		}
	}

	private static String getReactorState(Object reactor)
	{
		Object state = invokeMethod(reactor, "getState", new Class<?>[0]);
		return state != null ? state.toString() : null;
	}

	private static boolean isUiEnabled(Object event)
	{
		Object enabled = invokeMethod(event, "getIsUiEnabled", new Class<?>[0]);
		return enabled instanceof Boolean && ((Boolean) enabled).booleanValue();
	}

	private static boolean prohibitForTarget(Object event, String target)
	{
		Object prohibited = invokeMethod(event, "prohibitForTarget", new Class<?>[] { String.class }, target);
		return prohibited instanceof Boolean && ((Boolean) prohibited).booleanValue();
	}

	private static String getEventName(Object event)
	{
		Object eventName = invokeMethod(event, "getEventName", new Class<?>[0]);
		return eventName != null ? eventName.toString() : null;
	}

	public static boolean contains(Collection collection, Object value)
	{
		return (collection != null) ? collection.contains(value) : false;
	}

	public static Collection addAll(Collection target, Collection source)
	{
		if (target != null)
		{
			target.addAll(source);
			return target;
		}
		else
		{
			return source;
		}
	}

	public static Collection getMapValues(Map map)
	{
		return (map != null) ? map.values():null;
	}

	public static Set getMapKeys(Map map)
	{
		return (map != null) ? map.keySet():null;
	}

	// Stack helpers
	public static Object peek(Stack stack)
	{
		return stack.peek();
	}

	public static Object pop(Stack stack)
	{
		return stack.pop();
	}

	public static Object push(Stack stack, Object value)
	{
		return stack.push(value);
	}

	public static Set getItemAvl(String itemKey)
	{
		logger.debug("getItemAvl is not available in the Spring Boot migration for itemKey=" + itemKey);
		return Collections.emptySet();
	}

	public static Map getStates(String stateMachineType)
	{
		StateMachine sm = StateMachineHelper.getStateMachine(stateMachineType);
		return sm.getStates();
	}

	public static boolean getStateAllowsOperation(String stateMachineType, String stateName, String operation)
	{
		StateMachine sm =  StateMachineHelper.getStateMachine(stateMachineType);
		Object result = invokeMethod(sm, "operationAllowed", new Class<?>[] { String.class, String.class }, stateName, operation);
		return !(result instanceof Boolean) || ((Boolean) result).booleanValue();
	}

    /**
     * allValidEvents
     * @param stateMachineType
     * @param line
     * @return
     * @throws Throwable
     */
	public static Set allValidEvents(String stateMachineType,
			Object line) throws Throwable
	{
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(line);
		return allValidEventsForList(stateMachineType,list);
	}

	public static Set allValidEventsForList(String stateMachineType,
			Collection lines) throws Throwable
			{
		Set result = new HashSet();
		if (stateMachineType != null && lines != null)
		{

			StateMachine sm = StateMachineHelper.getStateMachine(stateMachineType);
			if (sm == null)
			{
				throw new Throwable("Invalid State Machine Type:" + stateMachineType);
			}
			for (Object line: lines)
			{
				if (line != null)
				{
					String state = getReactorState(line);
					Set<StateMachineEvent> validEvents = sm.getValidEventsForState(state);
					if (!(validEvents instanceof Collection))
					{
						continue;
					}
					Iterator eitr = ((Collection) validEvents).iterator();
					while (eitr.hasNext())
					{
						Object event = eitr.next();
						if (isUiEnabled(event))
						{
							String target = line.getClass().getSimpleName();

							// If a ui is set, we have to skip it
							if (prohibitForTarget(event, target))
							{
								continue;
							}
							result.add(event);
						}
					}
				}
			}
		}
		return result;

	}

	public static Map allEventsForList(String stateMachineType,
			Collection lines,
			ApplicationContext cxt)
	{
		Map<Object, String> results = new IdentityHashMap<Object,String>();
		if (lines != null)
		{
			for (Object line: lines)
			{
				results.put(line, allEvents(stateMachineType,line,cxt));
			}
		}
		return results;
	}

	public static String allEvents(String stateMachineType,
			Object reactor,
			ApplicationContext cxt)
	{

		if (reactor == null)
		{
			return null;
		}
		StringBuilder result = new StringBuilder();
		StateMachine sm = StateMachineHelper.getStateMachine(stateMachineType);
		if (sm == null)
		{
			logger.error("Unable to locate state machine for type:" + stateMachineType);
			return result.toString();
		}

		String state = getReactorState(reactor);
		Object validEvents = invokeMethod(sm, "getValidEventsForState", new Class<?>[] { String.class }, state);
		if (!(validEvents instanceof Collection))
		{
			return result.toString();
		}
		for (Object event :(Collection) validEvents)
		{
			if (!isUiEnabled(event))
			{
				continue;
			}
			String eventName = getEventName(event);
			if (eventName != null)
			{
				result.append("|").append(eventName).append("|");
			}
		}

		return result.toString();
	}

	public static Object getConfigValue(String key)
	{
		try
		{
			return SpringContextHolder.getBean(PcmConfigUtil.class).getString(key);
		}
		catch (IllegalStateException e)
		{
			throw new IllegalStateException("Unable to access PcmConfigUtil from Spring context", e);
		}
	}

	public static String getMessage(ResourceBundle bundle, String key)
	{
		String label = null;
		try
		{
			label = bundle.getString(key);
		}
		catch(Exception e)
		{
			label = "???"+key+"???";
		}
		return label;
	}

	public static Date dateOnly(Date date)
	{
		if (date == null)
		{
			return null;
		}
		return DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
	}

	public static Object getBeanProperty(Object bean, String name)
		throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		return (PropertyUtils.getProperty(bean, name));
	}

	public static String getMessage(String key, String values, Locale locale) {

		String[] arg =  new String[1];
		arg[0] = values;

		return SCPlatformMessages.INSTANCE.getMessage(key, arg, locale);
	}

	public static String getProductVersion() {
		if(PRODUCT_VERSION == null) {
			String productDirectory = System.getProperty("e2.deploy.dir");
			productDirectory = productDirectory+File.separator+"app"+File.separator+"mainline"+File.separator+"config"+File.separator+"productregistry.xml";
			File componentVersion = new File(productDirectory);
			try {
				String versionStartElement = "<version>";
				String versionEndElement = "</version>";
				BufferedReader br = new BufferedReader(new FileReader(componentVersion));
				String line = br.readLine();
				while(line != null) {
					line = line.trim();
					if(line.startsWith(versionStartElement) && line.endsWith(versionEndElement)) {
						PRODUCT_VERSION = line.replaceAll(versionStartElement,"").replaceAll(versionEndElement,"");
						break;
					}
					line = br.readLine();
				}
			} catch (IOException e) {
				logger.error("Error while reading productregistry from "+productDirectory +" path",e);
			}catch (Exception e) {
				logger.error("Error while fetching product version",e);
			}
		}
		return PRODUCT_VERSION;
	}

	    /**
     * Validates if user can edit an item based on business entity matching and validation settings.
     * @param item - The item's business entity
     * @return true if user can edit the item, false otherwise
     */
	public static boolean canEditItem(ApplicationContext ac, Item item) {
		boolean validationForCreateSupplyAllocation = SpringContextHolder.getBean(PcmConfigUtil.class).getBooleanValue("pcm.restrict.non.enterprise.to.other.business", false);
		if (validationForCreateSupplyAllocation) {
		   BusinessEntity currentEntity = ac.getCurrentUser() != null ? ac.getCurrentUser().getBusinessEntity() : null;
		   if (currentEntity != null
				 && !(currentEntity.getBusinessEntityTypeKey() == BusinessEntity.ENTERPRISE_TYPE)) {
			  if (!Objects.equals(item.getBusinessEntity(), currentEntity)) {
				 return false;
			  }
		   }
		}
		return true;
	}

	/**
	 * Get comma-separated item category names for use in JSP EL.
	 * Delegates to ItemService.getItemCategoryNames(item).
	 *
	 * @param item the item entity
	 * @return category names as comma-separated string
	 */
	public static String getItemCategoryNames(Item item) {
		return SpringContextHolder.getBean(ItemService.class).getItemCategoryNames(item);
	}

	/**
	 * Formats a cost value using HALF_UP rounding.
	 * Uses BigDecimal.setScale() for exact rounding — no double precision loss.
	 * Usage in JSP: ${e2ofn:formatCost(value, maxFractionDigits, minFractionDigits)}
	 */
	public static String formatCost(Object value, Object maxFractionDigits, Object minFractionDigits) {
		if (value == null) {
			return "";
		}
		try {
			BigDecimal bd;
			if (value instanceof BigDecimal) {
				bd = (BigDecimal) value;
			} else {
				String str = value.toString().trim();
				if (str.isEmpty()) return "";
				bd = new BigDecimal(str);
			}
			int maxFD = Integer.parseInt(maxFractionDigits.toString().trim());
			int minFD = Integer.parseInt(minFractionDigits.toString().trim());
			bd = bd.setScale(maxFD, RoundingMode.HALF_UP);
			String result = bd.toPlainString();
			int dotIndex = result.indexOf('.');
			int currentDecimals = (dotIndex >= 0) ? result.length() - dotIndex - 1 : 0;
			if (currentDecimals < minFD) {
				StringBuilder sb = new StringBuilder(result);
				if (dotIndex < 0) sb.append('.');
				for (int i = currentDecimals; i < minFD; i++) sb.append('0');
				result = sb.toString();
			}
			return result;
		} catch (Exception e) {
			return value.toString();
		}
	}

	private static int parseDigits(Object o, int fallback) {
		if (o == null) return fallback;
		try {
			String s = o.toString().trim();
			return s.isEmpty() ? fallback : Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return fallback;
		}
	}
}