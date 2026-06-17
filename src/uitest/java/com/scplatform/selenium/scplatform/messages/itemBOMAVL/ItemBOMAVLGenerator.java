/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.itemBOMAVL;

import java.util.ArrayList;
import java.util.List;

import com.test.selenium.common.RandomUtils;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.messages.item.subClasses.BomLine;

public class ItemBOMAVLGenerator<T extends ItemBOMAVL> extends ItemBOMAVLBuilder<T> {

	protected List<T> dataSet; 
	
	
	
	protected ItemBOMAVLGenerator(Class<T> messageClazz, long numMessages) {
		super(messageClazz, numMessages);
	}

	public List<T> build()	{
		dataSet = new ArrayList<T>();
		
		int total = itemList.size()-1;
		
		for (int row = total; row >= 0; row--){
			this.dataSet.addAll(getBaseData (itemList.get(row)));
		}
		
		return dataSet;
	}
	
	
	protected List<T> getBaseData(Item data) {
		List<T> itemList = new ArrayList<T>();
		
    	if ( (data.getBom() != null) && (!data.getBom().isEmpty()) )	{
    		T messageLine = (T) T.Factory.newInstance();
    		
            messageLine.setBOMLevel(data.getLevel());
            messageLine.setItemIdentifier(data.getItemIdentifier());
            messageLine.setBusinessEntity(data.getBusinessEntity());
            messageLine.setBusinessEntityType(data.getBusinessEntityType());
            messageLine.setSite(enterprise.getUdf1());
            messageLine.setVendorItemIdentifier(null);
            messageLine.setVendorBusinessEntity(null);
            messageLine.setVendorBusinessEntityType(null);
            messageLine.setVendorSite(null);
            messageLine.setBillOfMaterialTypeCode(data.getBom().get(0).getBillOfMaterialTypeCode());
            messageLine.setNotes(data.getBom().get(0).getBomName());
            messageLine.setDescription(data.getDescription());
            messageLine.setItemQuantity(RandomUtils.rand(1, 10));
            messageLine.setProductQuantityTypeCode("PerAssembly");
            messageLine.setRevision(data.getRevision());
            messageLine.setVersion(data.getVersion());
            messageLine.setItemClassification(data.getItemClassification());
            messageLine.setCommodityCode(data.getCommodityCode());
            //messageLine.setManagedBy(data.getManagedBy());
            messageLine.setManagedBy("DELL");
            messageLine.setProductUnitOfMeasureCode(data.getProductUnitOfMeasureCode());
            messageLine.setMakeBuy(data.getMakeBuy());
            messageLine.setOwnerName(data.getDataSource());
            messageLine.setContactName(data.getContactName());
            messageLine.setEffectiveFromDate(data.getEffectiveFromDate());
            messageLine.setEffectiveToDate(data.getEffectiveToDate());
            
            itemList.add(messageLine);
            
            
			// add in the items for the BOM
			int level = Integer.parseInt(data.getLevel()) + 1;
			for (int bomLine = 0; bomLine < data.getBom().get(0).getBomLine().size(); bomLine++)	{
				T itemLine = (T) T.Factory.newInstance();
				
				BomLine line = data.getBom().get(0).getBomLine().get(bomLine);
				
				
	            itemLine.setBOMLevel(Integer.toString(level));
	            itemLine.setItemIdentifier(line.getItemIdentifier());
	            itemLine.setBusinessEntity(data.getBusinessEntity());
	            itemLine.setBusinessEntityType(data.getBusinessEntityType());
	            itemLine.setSite(enterprise.getUdf1());
	            itemLine.setVendorItemIdentifier(null);
	            itemLine.setVendorBusinessEntity(null);
	            itemLine.setVendorBusinessEntityType(null);
	            itemLine.setVendorSite(null);
	            itemLine.setBillOfMaterialTypeCode(data.getBom().get(0).getBillOfMaterialTypeCode());
	            itemLine.setNotes("Notes for Item " + itemLine.getItemIdentifier());
	            itemLine.setDescription(line.getDescription());
	            itemLine.setItemQuantity(line.getItemQuantity());
	            itemLine.setProductQuantityTypeCode("PerAssembly");
	            itemLine.setRevision(line.getItemRevision());
	            itemLine.setVersion(line.getItemVersion());
	            itemLine.setItemClassification(null);
	            itemLine.setCommodityCode(null);
	           // itemLine.setManagedBy(data.getManagedBy());
	            itemLine.setManagedBy(null);
	            itemLine.setProductUnitOfMeasureCode(null);
	            itemLine.setMakeBuy(null);
	            itemLine.setOwnerName(null);
	            itemLine.setContactName(null);
	            itemLine.setEffectiveFromDate(line.getEffectiveFromDate());
	            itemLine.setEffectiveToDate(line.getEffectiveToDate());
				
	            itemList.add(itemLine);
			}
            
    	}
    	return itemList;
	}
	
	
}
