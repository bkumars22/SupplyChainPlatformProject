/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.itemAVL;

import java.util.ArrayList;
import java.util.List;

import com.test.selenium.scplatform.messages.item.Item;

public class ItemAVLGenerator<T extends ItemAVL> {

	protected List<T> dataSet; 
	
	public List<T> build(List<Item> itemList){
		dataSet = new ArrayList<T>();
		
		for (Item data : itemList){
        	if ( (data.getApprovedVendorListItem() != null) && (!data.getApprovedVendorListItem().isEmpty()) )	{
        		for (int row = 0; row < data.getApprovedVendorListItem().size(); row++)	{
        			T messageLine = (T) ItemAVL.Factory.newInstance();
        			
                    messageLine.setItemIdentifier(data.getItemIdentifier());
                    messageLine.setBusinessEntity(data.getBusinessEntity());
                    messageLine.setBusinessEntityType(data.getBusinessEntityType());
                    messageLine.setVendorItemIdentifier(data.getApprovedVendorListItem().get(row).getVendorItemIdentifier());
                    messageLine.setVendorBusinessEntity(data.getApprovedVendorListItem().get(row).getVendorBusinessEntity());
                    messageLine.setVendorBusinessEntityType(data.getApprovedVendorListItem().get(row).getVendorBusinessEntityType());
                    messageLine.setVendorSite(data.getApprovedVendorListItem().get(row).getSite());
                    messageLine.setDescription(data.getDescription());
                    messageLine.setRevision(data.getRevision());
                    messageLine.setVersion(data.getVersion());
                    messageLine.setItemClassification(data.getItemClassification());
                    messageLine.setCommodityCode(data.getCommodityCode());
                    messageLine.setManagedBy(data.getManagedBy());
                    messageLine.setProductUnitOfMeasureCode(data.getProductUnitOfMeasureCode());
                    messageLine.setMakeBuy(data.getMakeBuy());
                    messageLine.setOwnerName(data.getDataSource());
                    messageLine.setContactName(data.getContactName());
                    messageLine.setEffectiveFromDate(data.getEffectiveFromDate());
                    messageLine.setEffectiveToDate(data.getEffectiveToDate());	
                    
                    messageLine.setVerification_ProductFamily(data.getProprietaryProductFamily());
                    
                    if ( (data.getItemPlatform() != null) && (!data.getItemPlatform().isEmpty()) )	{
                    	messageLine.setVerification_Platform(data.getItemPlatform().get(0).getPlatformName());
                    }
                    
                    if ( (data.getResponsibility() != null) && (!data.getResponsibility().isEmpty()) )	{
                    	messageLine.setVerification_Responsibility(data.getResponsibility().get(0).getResponsibility());
                    }
                    
                    
                    dataSet.add(messageLine);
        		}
        	}
		}
		return dataSet;
	}
	
	
}
