/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.main.manageUploadJobs.loadJob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class LoadJobDetailsPage extends SCPlatformPage {

	public By tableLocator()	{
		return By.id("loadEventTable_data");
	}
	
	public List<LoadJobDetailsModel> parseResults() {
    	List<LoadJobDetailsModel> tableData = new ArrayList<LoadJobDetailsModel>();
       	LoadJobDetailsModel data = new LoadJobDetailsModel();
  	
    	HashMap<String, ArrayList<String>> parsedTableData = new HashMap<String, ArrayList<String>>();
    	
    	parsedTableData = this.tableParse(tableLocator());
    	
     	if (parsedTableData == null)	{
    		JLog.error(this.getClass() + "parseResults() - No results returned for parsed table", TakeScreenshot.True);
    		return tableData;
    	}
    	if (!parsedTableData.containsKey(data.getDisplayName("type"))){
    		JLog.error(this.getClass() + "parseResults() - Unable to find column in parsed data: " + data.getDisplayName("type"), TakeScreenshot.True);
    		return tableData;
    	}
    	int tableSize = parsedTableData.get(data.getDisplayName("type")).size();
    	
    	for (int row = 1; row < tableSize; row++)	{
    		data = new LoadJobDetailsModel();
    		
    		try	{
    			data.setType(parsedTableData.get(data.getDisplayName("type")).get(row));
    			data.setMessage(parsedTableData.get(data.getDisplayName("message")).get(row));
    			data.setLocation(parsedTableData.get(data.getDisplayName("location")).get(row));
    			data.setDateLoaded(parsedTableData.get(data.getDisplayName("dateLoaded")).get(row));
    			
    		} catch (NullPointerException e){
    			JLog.error("Error getting data for row " + row, e, TakeScreenshot.True);
    		}
    		tableData.add(data);
    	}
    	
		return tableData;
	}
	
}
