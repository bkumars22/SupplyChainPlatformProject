/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.itemPlatform;

import java.util.ArrayList;
import java.util.List;

public class ItemPlatformGenerator<T extends ItemPlatform> extends ItemPlatformBuilder<T> {
	protected List<T> dataSet; 
	
	
	protected ItemPlatformGenerator(Class<T> messageClazz, long numMessages) {
		super(messageClazz, numMessages);
	}
	
	
	public List<T> build()	{
		dataSet = new ArrayList<T>();
		
		List<String> previousItems = new ArrayList<String>();
		
		for (int index = 0; index < itemAVLList.size(); index++)	{
			if (previousItems.contains(itemAVLList.get(index).getItemIdentifier()))	{
				// item already processed
				continue;
			} else	{
				previousItems.add(itemAVLList.get(index).getItemIdentifier());
				
				T itemPlatform = (T) T.Factory.newInstance();
				
	            itemPlatform.setItemIdentifier(itemAVLList.get(index).getItemIdentifier());
	            itemPlatform.setBusinessEntity(itemAVLList.get(index).getBusinessEntity());
	            itemPlatform.setBusinessEntityType(itemAVLList.get(index).getBusinessEntityType());
	            itemPlatform.setPlatformName(this.platformName);
	            itemPlatform.setDescription(this.platformDescription);
	            itemPlatform.setEffectiveFromDate(itemAVLList.get(index).getEffectiveFromDate());
	            itemPlatform.setEffectiveToDate(itemAVLList.get(index).getEffectiveToDate());
	            
	            if (itemPlatform.getItemIdentifier().contains(" BOM"))	{
	            	// if item is BOM, do not make Platform data
	            	continue;
	            }
	            
	            this.dataSet.add(itemPlatform);
			}
		}
		
		return dataSet;
	}

}
