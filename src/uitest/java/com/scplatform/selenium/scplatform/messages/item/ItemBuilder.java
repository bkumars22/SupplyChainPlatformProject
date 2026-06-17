/**
 * @ItemBuilder.java@
 *
 * Created on Tue Oct 21 09:06:46 PDT 2014
 *
 *      Copyright (c) 2014 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.messages.item;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scplatform.qa.iris.factory.DefaultMessageFactory;
import com.scplatform.qa.iris.factory.MessageLineEnricher;
import com.test.selenium.common.Partner;
import com.test.selenium.scplatform.messages.businessEntity.BusinessEntity;
import com.test.selenium.scplatform.messages.commodityCode.CommodityCode;
import com.test.selenium.scplatform.messages.item.parser.ItemData;
import com.test.selenium.scplatform.messages.item.parser.ItemParser;
import com.test.selenium.scplatform.messages.item.parser.Variables;
import com.test.selenium.scplatform.messages.item.subClasses.AlternateItem;
import com.test.selenium.scplatform.messages.item.subClasses.ApprovedManufacturerListItem;
import com.test.selenium.scplatform.messages.item.subClasses.ApprovedVendorListItem;
import com.test.selenium.scplatform.messages.item.subClasses.Bom;
import com.test.selenium.scplatform.messages.item.subClasses.BomLine;
import com.test.selenium.scplatform.messages.item.subClasses.BuyerCode;
import com.test.selenium.scplatform.messages.item.subClasses.ItemPlatform;
import com.test.selenium.scplatform.messages.item.subClasses.Responsibility;

/**
 * <b>SPEC:</b> http://confluence.dev.scplatform.local/display/PUBT/Item
 *
 * Used to build default {@link Item} message data.  
 *
 * Default Data:
 * <UL>
 * <LI> buildAction = null.  Change using {@link #withBuildAction(String)}
 * <LI> itemAppendString = null.  Change using {@link #withItemAppendString(String)}
 * </UL>
 * <br><br>
 * Chained Call Example
 * <pre>
 * ItemBuilder<Item> builder = 
 * 				new ItemBuilder<Item>(Item.class, businessEntityData, commodityCodeData, allCompanies, itemDataFile, replacementVariables);
 * Iterable<Item> data = builder.build();
 * </pre>
 * 
 *
 */
public class ItemBuilder<T extends Item> extends DefaultMessageFactory<T> {
    protected final static long defaultNumMessages = 1;
    protected File itemDataFile = null;
    protected List<Variables> replacementVariables;
    protected Iterable<BusinessEntity> businessEntityData;
    protected Iterable<CommodityCode> commodityCodeData;
    protected List<T> itemData;
    protected List<Partner> allCompanies;
    
    protected ItemBuilder(Class<T> messageClazz, long numMessages) {
        super(messageClazz, numMessages);
        this.setMessageLineEnricher(new ItemEnricher());
    }

    /**
     * Builds the complete Item Data Set
     * 
     * @param messageClazz
     * 		The Item Message class, typically Item.class, but can be any class that extends it.
     * 
     * @param businessEntityData
     * 		{@link BusinessEntity} data.  Used to get details of the partners
     * 
     * @param itemDataFile
     * 		The {@link ItemData} Excel file containing the details of the items to make
     * 
     * @param replacementVariables
     * 		List of {@link Variables} used to do search/replace within the data of the itemDataFile
     */
    public ItemBuilder(
    		Class<T> messageClazz, 
    		Iterable<BusinessEntity> businessEntityData, 
    		Iterable<CommodityCode> commodityCodeData, 
    		List<Partner> allCompanies,
    		File itemDataFile, 
    		List<Variables> replacementVariables) {
    	
        this(messageClazz, defaultNumMessages);
        this.itemDataFile = itemDataFile;
        this.replacementVariables = replacementVariables;
        this.businessEntityData = businessEntityData;
        this.commodityCodeData = commodityCodeData;
        this.allCompanies = allCompanies;
    }

    @SuppressWarnings("unchecked")
	@Override
    public Iterable<T> build() {
    	ItemParser itemParser = new ItemParser();
    	if (forceBuildActions != null) itemParser.forceAllBuildActionsTo(forceBuildActions);
    	if (itemAppendString != null) itemParser.setAppendString(itemAppendString);
    	
    	List<ItemData> items = itemParser.parse(itemDataFile, replacementVariables);
    	
    	ItemGenerator<Item> generator = new ItemGenerator<Item>();
    	itemData= (List<T>) generator.build(this.businessEntityData, this.commodityCodeData, items, this.allCompanies);
    	
     	setNumMessages(itemData.size());
        return super.build();
    }
    
    protected class ItemEnricher implements MessageLineEnricher<T> {

    	/**
    	 * Sets the data for a single line.  
    	 * 
    	 * @param messageLine	The message line
    	 * @param lineNumber	The line number
    	 * @return
    	 */
        @Override
        public T enrichMessageLine(T messageLine, long lineNumber) {
        	messageLine = (T) itemData.get((int) lineNumber);
            return messageLine;
        }
        
    }
    
   
	
    
    
    
    
    //===========================================
    // CHAINED CALLS
    //===========================================
    protected String forceBuildActions = null;
    protected String itemAppendString = null;
    
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
    public ItemBuilder<T> withBuildAction(String buildAction) {
    	forceBuildActions = buildAction;
        return this;
    }
    

    /**
     * Adds a append string to the item name
     */
    public ItemBuilder<T> withItemAppendString(String value) {
    	itemAppendString = value;
        return this;
    }
    
    
	public static Map<String, Class> getSubClasses() {
		Map<String, Class> listClasses = new HashMap<String, Class>();
		listClasses.put("AlternateItem", AlternateItem.class);
		listClasses.put("ApprovedManufacturerListItem", ApprovedManufacturerListItem.class);
		listClasses.put("ApprovedVendorListItem", ApprovedVendorListItem.class);
		listClasses.put("Bom", Bom.class);
		listClasses.put("BomLine", BomLine.class);
		listClasses.put("BuyerCode", BuyerCode.class);
		listClasses.put("ItemPlatform", ItemPlatform.class);
		listClasses.put("Responsibility", Responsibility.class);
		return listClasses;
	}
}
