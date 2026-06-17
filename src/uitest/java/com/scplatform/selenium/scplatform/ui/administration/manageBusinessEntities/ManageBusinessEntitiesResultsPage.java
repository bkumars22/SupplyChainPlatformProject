/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageBusinessEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsPage;

public class ManageBusinessEntitiesResultsPage extends SCPlatformSearchResultsPage {

	public By tableLocator()	{
		return By.id("searchResults");
	}
	
	public List<ManageBusinessEntitiesResultsModel> parseResults() {
    	List<ManageBusinessEntitiesResultsModel> tableData = new ArrayList<ManageBusinessEntitiesResultsModel>();
    	ManageBusinessEntitiesResultsModel data = new ManageBusinessEntitiesResultsModel();

    	HashMap<String, ArrayList<String>> parsedTableData = new HashMap<String, ArrayList<String>>();
    	
    	if (!this.getResultsFoundCount().equals("0"))	{
    		parsedTableData = this.tableParse(tableLocator());
    	}
    	
    	if (parsedTableData == null)	{
    		JLog.error(this.getClass() + "parseResults() - No results returned for parsed table", TakeScreenshot.True);
    		return tableData;
    	}
    	if (!parsedTableData.containsKey(data.getDisplayName("businessName"))){
    		JLog.error(this.getClass() + "parseResults() - Unable to find column in parsed data: " + data.getDisplayName("businessName"), TakeScreenshot.True);
    		return tableData;
    	}
    	int tableSize = parsedTableData.get(data.getDisplayName("businessName")).size();
    	
    	for (int row = 1; row < tableSize; row++)	{
    		data = new ManageBusinessEntitiesResultsModel();
    		
    		try	{
        		data.setBusinessName(parsedTableData.get(data.getDisplayName("businessName")).get(row));
        		data.setId(parsedTableData.get(data.getDisplayName("id")).get(row));
        		data.setType(parsedTableData.get(data.getDisplayName("type")).get(row));
        		data.setDescription(parsedTableData.get(data.getDisplayName("description")).get(row));
        		data.setPrimaryContact(parsedTableData.get(data.getDisplayName("primaryContact")).get(row));
        		data.setPrimaryContactEmail(parsedTableData.get(data.getDisplayName("primaryContactEmail")).get(row));
    		} catch (NullPointerException e){
    			JLog.error("Error getting data for row " + row, e, TakeScreenshot.True);
    		}

    		
    		tableData.add(data);
    	}
    	
		return tableData;
	}

	
}

