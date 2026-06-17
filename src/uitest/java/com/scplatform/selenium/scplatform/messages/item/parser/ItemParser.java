/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.item.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.scplatform.qa.e2Messages.utilities.MessageReader;
import com.scplatform.qa.e2Messages.utilities.MessageReader.MESSAGE_FORMAT;
import com.test.selenium.common.JLog;
import com.test.selenium.common.MathUtils;
import com.test.selenium.common.RandomUtils;
import com.test.selenium.scplatform.utilities.MessageIO;
import com.google.common.collect.Lists;

public class ItemParser {
	protected int delta;
	protected String forceBuildActions = null;
	protected String appendString = "";
	
	public ItemParser()	{
		delta = RandomUtils.rand(1, 5);
	}
	
	public List<ItemData> parse(File itemDataFile, List<Variables> replacementVariables)	{
		MessageReader<ItemData> messageReader = new MessageReader<ItemData>();
		List<ItemData> itemData = null;
		try {
			Iterable<ItemData> message = messageReader.convertToMessage(ItemData.class, MESSAGE_FORMAT.Excel, itemDataFile, "*");
			itemData = Lists.newArrayList(message);
			
			for (int i = 0; i < itemData.size(); i++)	{
				if (forceBuildActions != null){
					itemData.get(i).setBuildAction(forceBuildActions);
				}
				
				itemData.get(i).setPrice(range(itemData.get(i).getPrice()));
				itemData.get(i).setItemNumber(itemData.get(i).getItemNumber() + appendString);
				
				itemData.get(i).setBomName(replaceKeys(itemData.get(i).getBomName(), replacementVariables, true));
				itemData.get(i).setParentBOM(replaceKeys(itemData.get(i).getParentBOM(), replacementVariables, true));
				itemData.get(i).setItemNumber(replaceKeys(itemData.get(i).getItemNumber(), replacementVariables, true));
				itemData.get(i).setDescription(replaceKeys(itemData.get(i).getDescription(), replacementVariables, false));
				
			}
		} catch (IOException e) {
			JLog.fail(e);
		}
		
		
		MessageIO<ItemData> itemDataMessageIO = new MessageIO<ItemData>(ItemData.class);
		itemDataMessageIO.save(itemData);
		
		return itemData;
	}
	
	
	/**
	 * The default data is set with a build action to do a memory and display addition.
	 * If loading for the first time, force all build actions to be "ItemAndBOM".
	 * If wanting to do a custom load, set "DoNotCreate" (or some other value), 
	 * then change the Build Action via some other logic.
	 * 
	 * @param buildAction The  to force all buildAction values to.
	 * <ul>
	 * <li> ItemAndBOM - Create Item and BOM
	 * <li> BOMOnly - Create BOM only
	 * <li> ItemOnly - Create Item Only
	 * <li> DoNotCreate - Do Not Create
	 * </ul>
	 */
	public void forceAllBuildActionsTo(String buildAction){
		forceBuildActions = buildAction;
	}
	
	protected float range (float originalValue)	{
		float min = MathUtils.deductPercentage(originalValue, delta * -1, 4);
		float max = MathUtils.deductPercentage(originalValue, delta, 4);
		float randomValue = RandomUtils.randomFloat(min, max);
		return MathUtils.Round(randomValue, 2);
	}
	

	protected String replaceKeys (String data, List<Variables> variables, boolean addAppendString){
		if (data == null){
			return data;
		}
		String newLine = data;
		
		for (Variables var : variables)	{
			String key = "<" + var.getKeyName() + ">";
			
			if (newLine.contains(key))	{
				newLine = newLine.replace(key, var.getKeyValue());
			}
		}
		
		if (addAppendString){
			if (StringUtils.isNotBlank(newLine)){
				if (!newLine.endsWith(appendString))	{
					newLine = newLine + appendString;
				}
			}
		}
		return newLine;
	}
	
	public void setAppendString (String append){
		appendString = append;
	}
}
