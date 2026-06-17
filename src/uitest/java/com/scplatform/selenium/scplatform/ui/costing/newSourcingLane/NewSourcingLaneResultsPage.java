/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.newSourcingLane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsPage;

public class NewSourcingLaneResultsPage extends SCPlatformSearchResultsPage {
	public By tableLocator()	{
		return By.id("searchResultsControlContainer");
	}
	
	public List<NewSourcingLaneResultsModel> parseResults() {
    	List<NewSourcingLaneResultsModel> tableData = new ArrayList<NewSourcingLaneResultsModel>();
       	NewSourcingLaneResultsModel data = new NewSourcingLaneResultsModel();
  	
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
    		data = new NewSourcingLaneResultsModel();
    		
    		try	{
        		data.setItemNumber(parsedTableData.get(data.getDisplayName("itemNumber")).get(row));
        		data.setItemDescription(parsedTableData.get(data.getDisplayName("itemDescription")).get(row));
        		data.setItemBusiness(parsedTableData.get(data.getDisplayName("itemBusiness")).get(row));
        		data.setRevision(parsedTableData.get(data.getDisplayName("revision")).get(row));
        		data.setCommodityName(parsedTableData.get(data.getDisplayName("commodityName")).get(row));
        		data.setSupplier(parsedTableData.get(data.getDisplayName("supplier")).get(row));
        		data.setResponsibility(parsedTableData.get(data.getDisplayName("responsibility")).get(row));

    		} catch (NullPointerException e){
    			JLog.error("Error getting data for row " + row, e, TakeScreenshot.True);
    		}

    		tableData.add(data);
    	}
    	
		return tableData;
	}
}
