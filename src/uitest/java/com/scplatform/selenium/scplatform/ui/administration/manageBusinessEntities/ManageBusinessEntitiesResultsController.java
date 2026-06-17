/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageBusinessEntities;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.messages.businessEntity.BusinessEntity;
import com.test.selenium.scplatform.messages.businessEntity.BusinessEntityFilter;
import com.test.selenium.scplatform.messages.businessEntity.BusinessEntityUtils;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsController;
import com.test.selenium.scplatform.ui.administration.manageBusinessEntities.details.BusinessDetailsController;

public class ManageBusinessEntitiesResultsController extends SCPlatformSearchResultsController {

	@Override
	public PageImpl getView() {
		return new ManageBusinessEntitiesResultsPage();
	}

	public void select(String businessName){
		ManageBusinessEntitiesResultsPage searchResults = new ManageBusinessEntitiesResultsPage();
		WebElement row = searchResults.findRow(businessName, searchResults.tableLocator());
		row.findElement(By.name("selectedBusinessKey")).click();
	}
	
	public boolean validate(List<BusinessEntity> expectedResults){
		boolean success = true;
		boolean verified = true;
    	
		List<String> businessEntityTypes = new ArrayList<String>();
		businessEntityTypes.add("Manufacturer");
		businessEntityTypes.add("Supplier");
		BusinessEntityUtils utils = new BusinessEntityUtils();
		List<String> uniquePartners = utils.getUniquePartners(expectedResults, businessEntityTypes);
		
		ManageBusinessEntitiesResultsPage page = new ManageBusinessEntitiesResultsPage();
		
    	for (String partner : uniquePartners){
    		JLog.section("Verify " + partner);
    		
    	   	search(partner);

    	   	BusinessEntityFilter<BusinessEntity> filter = new BusinessEntityFilter<BusinessEntity>();
    	   	List<BusinessEntity> businessEntityList = filter.byBusinessEntityName(partner).applyReturnList(expectedResults);
    	   	BusinessEntity businessEntity = businessEntityList.get(0);

 
    		List<ManageBusinessEntitiesResultsModel> actualModel = page.parseResults();
    		
    		if ( (actualModel == null) || (actualModel.isEmpty()) )	{
    			JLog.error("Unable to find BusinessEntity:  " + businessEntity.getBusinessEntityName(), TakeScreenshot.True);
    			continue;
    		}
    		ManageBusinessEntitiesResultsModel actual = actualModel.get(0);
    		
    		verified = verify(actual.getDisplayName("businessName"), actual.getBusinessName(), businessEntity.getBusinessEntityName());
    		success = (verified) ? success : verified;
    		
    		verified = verify(actual.getDisplayName("id"), actual.getId(), businessEntity.getBusinessEntity());
    		success = (verified) ? success : verified;
    		
    		verified = verify(actual.getDisplayName("type"), 
    				WordUtils.capitalize(actual.getType().toLowerCase()), 
    				WordUtils.capitalize(businessEntity.getBusinessEntityType().toLowerCase()));
    		success = (verified) ? success : verified;
    		
    		verified = verify(actual.getDisplayName("description"), actual.getDescription(), businessEntity.getDescription());
    		success = (verified) ? success : verified;
       		
    		verified = verify(actual.getDisplayName("primaryContact"), actual.getPrimaryContact(), businessEntity.getContactName());
    		success = (verified) ? success : verified;
     		
    		verified = verify(actual.getDisplayName("primaryContactEmail"), actual.getPrimaryContactEmail(), businessEntity.getContactUniqueId());
    		success = (verified) ? success : verified;
       
    		select(businessEntity.getBusinessEntityName());
    		BusinessDetailsController businessDetailsController = new BusinessDetailsController();
    		verified = businessDetailsController.validate(businessEntityList);
    		success = (verified) ? success : verified;
    		
    		JLog.blankLine();
    	}
    	return success;
	}
	
	protected void search(String businessName) {
		ManageBusinessEntitiesModel model = new ManageBusinessEntitiesModel();
		model.setBusinessName(businessName);
		
		ManageBusinessEntitiesController controller = new ManageBusinessEntitiesController();
		controller.setModel(model);
		controller.clickClear();
		controller.search();
	}
}
