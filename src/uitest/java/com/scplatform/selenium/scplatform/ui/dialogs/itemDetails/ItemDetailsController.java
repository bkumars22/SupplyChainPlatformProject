/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.dialogs.itemDetails;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.messages.itemAVL.ItemAVL;
import com.test.selenium.scplatform.modelViewController.SCPlatformController;
import com.test.selenium.scplatform.utilities.DatabaseUtils;

public class ItemDetailsController extends SCPlatformController {

	@Override
	public PageImpl getView() {
		return new ItemDetailsPage();
	}

	public boolean validate(Item itemData)	{
		ItemDetailsModel expected = new ItemDetailsModel();
		
		String platform = "";
		if (itemData.getItemPlatform() != null)	{
			if (StringUtils.isNotBlank(itemData.getItemPlatform().get(0).getPlatformName())){
				platform = itemData.getItemPlatform().get(0).getPlatformName();
			}
		}
		
		String itemBusiness = String.format("%s %s", itemData.getBusinessEntity(), itemData.getBusinessEntity());
		
		expected.setItemId(itemData.getItemIdentifier());
		expected.setItemDescription(itemData.getDescription());
		expected.setItemType(itemData.getItemPartType());
		expected.setManagedBy(itemData.getManagedBy());
		expected.setRevision(itemData.getRevision());
		expected.setVersion(itemData.getVersion());
		expected.setTopLevelItem(("true".equalsIgnoreCase(itemData.getIsTopLevel()) ? "Yes" : "No"));
		expected.setCostCommodity(itemData.getCommodityCode());
		expected.setClassification(itemData.getItemClassification());
		expected.setPlatform(platform);
		expected.setProductFamily(itemData.getProprietaryProductFamily());
		expected.setState("/");
		expected.setUom(itemData.getProductUnitOfMeasureCode());
		expected.setInventory("");
		expected.setItemBusiness(itemBusiness);
		expected.setSourceSystem(itemData.getDataSource());
//		expected.setFirstLoadedOn(DateTime.parse(firstLoadedOn(), formatter));
//		expected.setLastUpdatedOn(DateTime.parse(lastUpdatedOn(), formatter));
		
		return validate(expected);
	}
	
	public boolean validate(ItemAVL itemAVLData)	{
		ItemDetailsModel expected = new ItemDetailsModel();
		
		String platform = "";	
		String itemBusiness = String.format("%s %s", itemAVLData.getBusinessEntity(), itemAVLData.getBusinessEntity());
		
		expected.setItemId(itemAVLData.getItemIdentifier());
		expected.setItemDescription(itemAVLData.getDescription());
		expected.setItemType("Item");
		expected.setManagedBy(itemAVLData.getManagedBy());
		expected.setRevision(itemAVLData.getRevision());
		expected.setVersion(itemAVLData.getVersion());
		expected.setTopLevelItem("No");
		expected.setCostCommodity(itemAVLData.getCommodityCode());
		expected.setClassification(itemAVLData.getItemClassification());
		expected.setPlatform(platform);
		expected.setProductFamily("");
		expected.setState("/");
		expected.setUom(itemAVLData.getProductUnitOfMeasureCode());
		expected.setInventory("");
		expected.setItemBusiness(itemBusiness);
		expected.setSourceSystem("MCM");
//		expected.setFirstLoadedOn(DateTime.parse(firstLoadedOn(), formatter));
//		expected.setLastUpdatedOn(DateTime.parse(lastUpdatedOn(), formatter));
		
		return validate(expected);
	}
	
	protected boolean validate(ItemDetailsModel expected){
		boolean success = true;
		boolean verified = true;
		
		openItemDetails(expected);
		
		ItemDetailsPage page = new ItemDetailsPage();
		ItemDetailsModel actual = page.parse();
		
		verified = verify(actual.getDisplayName("itemId"), actual.getItemId(), expected.getItemId());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("itemDescription"), actual.getItemDescription(), expected.getItemDescription());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("itemType"), actual.getItemType(), expected.getItemType());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("managedBy"), actual.getManagedBy(), expected.getManagedBy());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("revision"), actual.getRevision(), expected.getRevision());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("version"), actual.getVersion(), expected.getVersion());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("topLevelItem"), actual.getTopLevelItem(), expected.getTopLevelItem());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("costCommodity"), actual.getCostCommodity(), expected.getCostCommodity());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("classification"), actual.getClassification(), expected.getClassification());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("platform"), actual.getPlatform(), expected.getPlatform());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("productFamily"), actual.getProductFamily(), expected.getProductFamily());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("state"), actual.getState(), expected.getState());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("uom"), actual.getUom(), expected.getUom());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("inventory"), actual.getInventory(), expected.getInventory());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("itemBusiness"), actual.getItemBusiness(), expected.getItemBusiness());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("sourceSystem"), actual.getSourceSystem(), expected.getSourceSystem());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("firstLoadedOn"), actual.getFirstLoadedOn(), actual.getFirstLoadedOn());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("lastUpdatedOn"), actual.getLastUpdatedOn(), actual.getLastUpdatedOn());
		success = (verified) ? success : verified;
		
		closeItemDetails();
		return success;
	}
	
	public void openItemDetails(ItemDetailsModel expected)	{
		ItemDetailsPage page = new ItemDetailsPage();
		
		//String itemKey = DatabaseUtils.getItemKey(expected.getItemId(), expected.getVersion(), expected.getRevision());
		//By by = By.xpath(String.format("//a[contains(@href, 'itemKey=%s') and contains(text(), '%s')]", itemKey, expected.getItemId()));
		By by = By.partialLinkText(expected.getItemId());
		page.elementClick(by);
		
		page.sleep(2);
		page.browserSession.switchToNewWindow();
	}
	
	public void closeItemDetails()	{
		ItemDetailsPage page = new ItemDetailsPage();
		page.button_OK().click();
		page.sleep(2);
		page.browserSession.switchToPreviousOpenedBrowser();
	}
}
