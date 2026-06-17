/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps.ui;

import java.util.ArrayList;
import java.util.List;

import com.test.selenium.common.JLog;
import com.test.selenium.common.Partner;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.cucumber.CukeHelper;
import com.test.selenium.scplatform.cucumber.Preprocessing;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.messages.item.ItemUtils;
import com.test.selenium.scplatform.messages.itemAVL.ItemAVL;
import com.test.selenium.scplatform.messages.itemBOMAVL.ItemBOMAVL;
import com.test.selenium.scplatform.navigation.SCPlatformNavigation;
import com.test.selenium.scplatform.ui.masterDataManagement.itemAssignment.ItemAssignmentResultsController;
import com.test.selenium.scplatform.ui.search.boms.SearchBOMsResultsController;
import com.test.selenium.scplatform.ui.search.itemAVL.SearchItemAVLResultsController;
import com.test.selenium.scplatform.ui.search.items.SearchItemsResultsController;
import com.test.selenium.scplatform.utilities.MessageIO;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.java.Scenario;

public class ItemSteps {
	protected SCPlatformNavigation nav;
	
	@Before
	public void beforeMethod(Scenario scenario){
		JLog.setScenarioForCucumber(scenario);
		JLog.resetErrorCount();
		nav = new SCPlatformNavigation();
	}

	private void checkForErrors()	{
		if (JLog.getErrorCount() > 0){
			JLog.fail(JLog.getErrorCount() + " errors occured in the test.  Check log.", TakeScreenshot.True);
		}
	}
	
	/**
	 * TODO: DOCUMENT
	 * @param itemMasterSaveKey
	 * @param partialItemIdentifier
	 * @param saveSingleItemKey
	 */
	@And("I find uploaded {string} Item data where ItemIdentifier like {string} and save to {string}")
	public void findSingleItemData(String itemMasterSaveKey, String partialItemIdentifier, String saveSingleItemKey)	{
		MessageIO<Item> messageIO = new MessageIO<Item>(Item.class);
		List<Item> itemData = messageIO.load(itemMasterSaveKey);
		Item singleItem = null;
		
		for (int i = 0; i < itemData.size(); i++)	{
			if (itemData.get(i).getItemIdentifier().startsWith(partialItemIdentifier))	{
				singleItem = itemData.get(i);
				break;
			}
		}
		
		itemData.clear();
		itemData.add(singleItem);
		messageIO.save(itemData, saveSingleItemKey);
		
		CukeHelper.setMessageClass(saveSingleItemKey,  Item.class);
		Preprocessing.addPreprocessingClass(saveSingleItemKey, saveSingleItemKey);
	}
	
	/**
	 * TODO: DOCUMENT
	 */
	@And("I find uploaded {string} Item data where Partner is {string} and save to {string}")
	public void findItemDataForPartner(String itemMasterSaveKey, String partnerKey, String saveItemKey)	{
		MessageIO<Item> messageIO = new MessageIO<Item>(Item.class);
		List<Item> itemData = messageIO.load(itemMasterSaveKey);
		Partner partner = (Partner) CukeHelper.findSavedClass(partnerKey);
		
		ItemUtils itemUtils = new ItemUtils();
		List<Item> itemsForPartner = itemUtils.findByPartner(itemData, partner);

		messageIO.save(itemsForPartner, saveItemKey);
		
		CukeHelper.setMessageClass(saveItemKey,  Item.class);
		Preprocessing.addPreprocessingClass(saveItemKey, saveItemKey);
	}
	
	public Item getSingleItemData(String saveSingleItemKey)	{
		MessageIO<Item> messageIO = new MessageIO<Item>(Item.class);
		if (messageIO.doesMessageExist(saveSingleItemKey))	{
			List<Item> itemData = messageIO.load(saveSingleItemKey);
			if ( (itemData != null) && (!itemData.isEmpty()) )	{
				return itemData.get(0);
			}
		} 
		return null;
	}
	
	/**
	 * DOCUMENTATION: http://confluence.dev.scplatform.local/display/QA/Items
	 */
	@Then("I validate the {string} Item Master Data")
	public void validItemMaster(String itemMasterSaveKey){
		MessageIO<Item> messageIO = new MessageIO<Item>(Item.class);
		
		nav.SearchItems();
		SearchItemsResultsController c = new SearchItemsResultsController();
		c.validate(messageIO.load(itemMasterSaveKey));
		
		checkForErrors();
	}
	
	/**
	 * DOCUMENTATION: http://confluence.dev.scplatform.local/display/QA/Item+AVL
	 */
	@Then("I validate the {string} ItemAVL Master Data")
	public void validItemAVLMaster(String itemMasterSaveKey){
		MessageIO<ItemAVL> messageIO = new MessageIO<ItemAVL>(ItemAVL.class);
		
		nav.SearchItemAVL();
		SearchItemAVLResultsController c = new SearchItemAVLResultsController();
		c.validate(messageIO.load(itemMasterSaveKey));
		
		checkForErrors();
	}
	
	/**
	 * DOCUMENTATION: http://confluence.dev.scplatform.local/display/QA/BOMs
	 */
	@Then("I validate the {string} ItemBOMAVL Master Data")
	public void validItemBOMAVLMaster(String itemMasterSaveKey){
		MessageIO<ItemBOMAVL> messageIO = new MessageIO<ItemBOMAVL>(ItemBOMAVL.class);
		
		nav.SearchBOMs();
		SearchBOMsResultsController c = new SearchBOMsResultsController();
		c.validateItemBOMAVL(messageIO.load(itemMasterSaveKey));
		
		checkForErrors();
	}
	
	/**
	 * DOCUMENTATION: http://confluence.dev.scplatform.local/display/QA/BOMs
	 */
	@Then("I validate the {string} BOM Master Data")
	public void validItemBOMMaster(String itemMasterSaveKey){
		MessageIO<Item> messageIO = new MessageIO<Item>(Item.class);
		
		nav.SearchBOMs();
		SearchBOMsResultsController c = new SearchBOMsResultsController();
		c.validateItem(messageIO.load(itemMasterSaveKey));
		
		checkForErrors();
	}
	
	/**
	 * DOCUMENTATION: http://confluence.dev.scplatform.local/display/QA/Item+Assignment
	 */
	@Then("I validate the {string} Item Assignment Data")
	public void validItemAssigment(String itemOrItemAVLSaveKey){
		MessageIO<Item> messageIOItem = new MessageIO<Item>(Item.class);
		MessageIO<ItemAVL> messageIOItemAVL = new MessageIO<ItemAVL>(ItemAVL.class);
		
		nav.ItemAssignment();
		ItemAssignmentResultsController c = new ItemAssignmentResultsController();
		
		
		if (messageIOItemAVL.doesMessageExist(itemOrItemAVLSaveKey))	{
			c.validateItemAVL(messageIOItemAVL.load(itemOrItemAVLSaveKey));
		} else	{
			c.validateItem(messageIOItem.load(itemOrItemAVLSaveKey));
		}
		checkForErrors();
	}
	
	/**
	 * DOCUMENTATION: http://confluence.dev.scplatform.local/display/QA/Item+Assignment
	 */
	@Then("I validate Item Details in Item Assignment for the {string} Item Assignment Data")
	public void validItemAssigmentItemDetails(String itemOrItemAVLSaveKey){
		MessageIO<Item> messageIOItem = new MessageIO<Item>(Item.class);
		MessageIO<ItemAVL> messageIOItemAVL = new MessageIO<ItemAVL>(ItemAVL.class);
		
		nav.ItemAssignment();
		ItemAssignmentResultsController c = new ItemAssignmentResultsController();
		
		
		if (messageIOItemAVL.doesMessageExist(itemOrItemAVLSaveKey))	{
			c.validateItemAVLDetails(messageIOItemAVL.load(itemOrItemAVLSaveKey));
		} else	{
			c.validateItemDetails(messageIOItem.load(itemOrItemAVLSaveKey));
		}
		checkForErrors();
	}
	
}
