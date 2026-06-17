/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.masterDataManagement.commodityManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsPage;

public class CommodityManagementResultsPage extends SCPlatformSearchResultsPage {
	public WebElement applyToAllResults()	{
		return getElement(By.name("applyToAll"));
	}

	public WebElement assignTo()	{
		return getElement(By.name("assignmentUserId"));
	}

	public WebElement assignManagedBy()	{
		return getElement(By.name("managedFlag"));
	}
	
	
	public WebElement assignButton()	{
		return getElement(By.partialLinkText("Assign"));
	}
	
	public WebElement unassignButton()	{
		return getElement(By.partialLinkText("Unassign"));
	}
	
	public WebElement setButton()	{
		return getElement(By.partialLinkText("Set"));
	}
	
	public By tableLocator()	{
		return By.id("catMgmtSearchResultTable_data");
	}
	
	public List<CommodityManagementResultsModel> parseResults() {
    	List<CommodityManagementResultsModel> tableData = new ArrayList<CommodityManagementResultsModel>();
       	CommodityManagementResultsModel data = new CommodityManagementResultsModel();
         	
    	HashMap<String, ArrayList<String>> parsedTableData = new HashMap<String, ArrayList<String>>();
    	
    	if (!this.getResultsFoundCount().equals("0"))	{
    		parsedTableData = this.tableParse(tableLocator());
    	}
    	
    	if (parsedTableData == null)	{
    		JLog.error(this.getClass() + "parseResults() - No results returned for parsed table", TakeScreenshot.True);
    		return tableData;
    	}
    	if (!parsedTableData.containsKey(data.getDisplayName("commodityName"))){
    		JLog.error(this.getClass() + "parseResults() - Unable to find column in parsed data: " + data.getDisplayName("commodityName"), TakeScreenshot.True);
    		return tableData;
    	}
    	int tableSize = parsedTableData.get(data.getDisplayName("commodityName")).size();
    	
    	for (int row = 0; row < tableSize; row++)	{
    		data = new CommodityManagementResultsModel();
    		
    		try	{
        		data.setCommodityName(parsedTableData.get(data.getDisplayName("commodityName")).get(row));
        		data.setManagedBy(parsedTableData.get(data.getDisplayName("managedBy")).get(row));
        		data.setAssignedTo(parsedTableData.get(data.getDisplayName("assignedTo")).get(row));
        		data.setResponsibility(parsedTableData.get(data.getDisplayName("responsibility")).get(row));
    		} catch (NullPointerException e){
    			JLog.error("Error getting data for row " + row, e, TakeScreenshot.True);
    		}
    		
    		tableData.add(data);
    	}
    	
		return tableData;
	}
    

	
}
