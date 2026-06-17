/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.main.manageUploadJobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsPage;

public class ManageUploadJobsResultsPage extends SCPlatformSearchResultsPage {

	public By tableLocator()	{
		return By.id("loadJobSearchResultTable_data");
	}
	
	public List<ManageUploadJobsResultsModel> parseResults() {
    	List<ManageUploadJobsResultsModel> tableData = new ArrayList<ManageUploadJobsResultsModel>();
       	ManageUploadJobsResultsModel data = new ManageUploadJobsResultsModel();
  	
    	HashMap<String, ArrayList<String>> parsedTableData = new HashMap<String, ArrayList<String>>();
    	
    	if (!this.getResultsFoundCount().equals("0"))	{
    		parsedTableData = this.tableParse(tableLocator());
    	}
    	
     	if (parsedTableData == null)	{
    		JLog.error(this.getClass() + "parseResults() - No results returned for parsed table", TakeScreenshot.True);
    		return tableData;
    	}
    	if (!parsedTableData.containsKey(data.getDisplayName("dateLoaded"))){
    		JLog.error(this.getClass() + "parseResults() - Unable to find column in parsed data: " + data.getDisplayName("dateLoaded"), TakeScreenshot.True);
    		return tableData;
    	}
    	int tableSize = parsedTableData.get(data.getDisplayName("dateLoaded")).size();
    	
    	for (int row = 1; row < tableSize; row++)	{
    		data = new ManageUploadJobsResultsModel();
    		
    		try	{
    			data.setDateLoaded(parsedTableData.get(data.getDisplayName("dateLoaded")).get(row));
    			data.setStatus(parsedTableData.get(data.getDisplayName("status")).get(row));
    			data.setState(parsedTableData.get(data.getDisplayName("state")).get(row));
    			data.setLoadedBy(parsedTableData.get(data.getDisplayName("loadedBy")).get(row));
    			data.setUploadType(parsedTableData.get(data.getDisplayName("uploadType")).get(row));
    			data.setJobID(parsedTableData.get(data.getDisplayName("jobID")).get(row));
    			data.setFileLoaded(parsedTableData.get(data.getDisplayName("fileLoaded")).get(row));
    		} catch (NullPointerException e){
    			JLog.error("Error getting data for row " + row, e, TakeScreenshot.True);
    		}
    		tableData.add(data);
    	}
    	
		return tableData;
	}
	
}
