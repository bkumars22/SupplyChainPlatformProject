/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.masterDataManagement.bomManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsPage;

public class BomManagementResultsPage extends SCPlatformSearchResultsPage {
	
	public WebElement replaceButton() {
		return getElement(By.partialLinkText("Replace"));
	}
	
	public WebElement compareButton() {
		return getElement(By.partialLinkText("Compare"));
	}
	
	public WebElement closeButton() {
		return getElement(By.partialLinkText("Close"));
	}
	
	public WebElement reopenButton() {
		return getElement(By.partialLinkText("Reopen"));
	}
	
	
	public By tableLocator()	{
		return By.id("bomMgmtSearchResultTable_data");
	}
	
	public List<BomManagementResultsModel> parseResults() {
    	List<BomManagementResultsModel> tableData = new ArrayList<BomManagementResultsModel>();
       	BomManagementResultsModel data = new BomManagementResultsModel();
  	
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
    		data = new BomManagementResultsModel();
    		
    		try	{
        		data.setItemNumber(parsedTableData.get(data.getDisplayName("itemNumber")).get(row));
        		data.setItemDescription(parsedTableData.get(data.getDisplayName("itemDescription")).get(row));
        		data.setBusinessName(parsedTableData.get(data.getDisplayName("businessName")).get(row));
        		data.setStatus(parsedTableData.get(data.getDisplayName("status")).get(row));
        		data.setVersion(parsedTableData.get(data.getDisplayName("version")).get(row));
        		data.setRevision(parsedTableData.get(data.getDisplayName("revision")).get(row));
        		data.setBomDescription(parsedTableData.get(data.getDisplayName("bomDescription")).get(row));
        		data.setTopLevelItem(parsedTableData.get(data.getDisplayName("topLevelItem")).get(row));
        		data.setRepairs(parsedTableData.get(data.getDisplayName("repairs")).get(row));
        		data.setResponsibility(parsedTableData.get(data.getDisplayName("responsibility")).get(row));

    		} catch (NullPointerException e){
    			JLog.error("Error getting data for row " + row, e, TakeScreenshot.True);
    		}

    		tableData.add(data);
    	}
    	
		return tableData;
	}
}
