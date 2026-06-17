/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.masterDataManagement.itemAssignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsPage;

public class ItemAssignmentResultsPage extends SCPlatformSearchResultsPage {

	public By tableLocator()	{
		return By.id("itemMgmtSearchResultTable_data");
	}
	
	public List<ItemAssignmentResultsModel> parseResults() {
    	List<ItemAssignmentResultsModel> tableData = new ArrayList<ItemAssignmentResultsModel>();
       	ItemAssignmentResultsModel data = new ItemAssignmentResultsModel();
  	
    	HashMap<String, ArrayList<String>> parsedTableData = new HashMap<String, ArrayList<String>>();
    	
    	if (!this.getResultsFoundCount().equals("0"))	{
    		parsedTableData = this.tableParse(tableLocator());
    	}
    	
     	if (parsedTableData == null)	{
    		JLog.error(this.getClass() + "parseResults() - No results returned for parsed table", TakeScreenshot.True);
    		return tableData;
    	}
    	if (!parsedTableData.containsKey(data.getDisplayName("itemNumber"))){
    		JLog.error(this.getClass() + "parseResults() - Unable to find column in parsed data: " + data.getDisplayName("itemNumber"), TakeScreenshot.True);
    		return tableData;
    	}
    	int tableSize = parsedTableData.get(data.getDisplayName("itemNumber")).size();
    	
    	for (int row = 1; row < tableSize; row++)	{
    		data = new ItemAssignmentResultsModel();
    		
    		try	{
        		data.setItemNumber(parsedTableData.get(data.getDisplayName("itemNumber")).get(row));
        		data.setItemType(parsedTableData.get(data.getDisplayName("itemType")).get(row));
        		data.setItemDescription(parsedTableData.get(data.getDisplayName("itemDescription")).get(row));
        		data.setCostCommodity(parsedTableData.get(data.getDisplayName("costCommodity")).get(row));
        		data.setBusinessName(parsedTableData.get(data.getDisplayName("businessName")).get(row));
        		data.setResponsibility(parsedTableData.get(data.getDisplayName("responsibility")).get(row));
        		data.setDaysSinceAdded(parsedTableData.get(data.getDisplayName("daysSinceAdded")).get(row));

    		} catch (NullPointerException e){
    			JLog.error("Error getting data for row " + row, e, TakeScreenshot.True);
    		}

    		
    		tableData.add(data);
    	}
    	
		return tableData;
	}
}
