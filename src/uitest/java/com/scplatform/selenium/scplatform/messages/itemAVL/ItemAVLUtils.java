/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.itemAVL;

import java.util.List;

import com.test.selenium.common.JLog;
import com.test.selenium.scplatform.messages.itemPlatform.ItemPlatform;

public class ItemAVLUtils {

	public List<ItemAVL> updateWithItemPlatform(List<ItemAVL> itemAVLData, List<ItemPlatform> itemPlatformData){
		
		for (int platformIndex = 0; platformIndex < itemPlatformData.size(); platformIndex++)	{
			int itemAVLIndex = findItemAVLIndex(itemAVLData, itemPlatformData.get(platformIndex));
			if (itemAVLIndex == -1)	{
				JLog.error("ItemAVLUtils.updateWithItemPlatform() - unable to find ItemAVL data for item: " + itemPlatformData.get(platformIndex).getItemIdentifier());
				continue;
			}
			
			itemAVLData.get(itemAVLIndex).setVerification_Platform(itemPlatformData.get(platformIndex).getPlatformName());
			itemAVLData.get(itemAVLIndex).setRevision("*");
		}
		
		
		return itemAVLData;
	}

	protected int findItemAVLIndex(List<ItemAVL> itemAVLData, ItemPlatform itemPlatform) {
		int itemAVLIndex = -1;
		
		for (int i = 0; i < itemAVLData.size(); i++)	{
			if (itemAVLData.get(i).getItemIdentifier().equals(itemPlatform.getItemIdentifier()))	{
				if (itemAVLData.get(i).getBusinessEntity().equals(itemPlatform.getBusinessEntity()))	{
					itemAVLIndex = i;
					break;
				} 
			}
		}
		return itemAVLIndex;
	}
}
