/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.searchSourcingLane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsPage;

public class SearchSourcingLaneResultsPage extends SCPlatformSearchResultsPage {

	public By tableLocator()	{
		return By.id("slSearchResultTable_data");
	}
	
	public List<SearchSourcingLaneResultsModel> parseResults() {
    	List<SearchSourcingLaneResultsModel> tableData = new ArrayList<SearchSourcingLaneResultsModel>();
    	SearchSourcingLaneResultsModel data = new SearchSourcingLaneResultsModel();

    	HashMap<String, ArrayList<String>> parsedTableData = new HashMap<String, ArrayList<String>>();
    	
    	if (!this.getResultsFoundCount().equals("0"))	{
    		parsedTableData = this.tableParse(tableLocator());
    	}
    	
    	if (parsedTableData == null)	{
    		JLog.error(this.getClass() + "parseResults() - No results returned for parsed table", TakeScreenshot.True);
    		return tableData;
    	}
    	if (!parsedTableData.containsKey(data.getDisplayName("item"))){
    		JLog.error(this.getClass() + "parseResults() - Unable to find column in parsed data: " + data.getDisplayName("item"), TakeScreenshot.True);
    		return tableData;
    	}
    	int tableSize = parsedTableData.get(data.getDisplayName("item")).size();
    	
    	for (int row = 0; row < tableSize; row++)	{
    		data = new SearchSourcingLaneResultsModel();
    		
    		try	{
        		data.setStatus(parsedTableData.get(data.getDisplayName("status")).get(row));
        		data.setCommodityName(parsedTableData.get(data.getDisplayName("commodityName")).get(row));
        		data.setItem(parsedTableData.get(data.getDisplayName("item")).get(row));
        		data.setItemDescription(parsedTableData.get(data.getDisplayName("itemDescription")).get(row));
        		data.setItemBusiness(parsedTableData.get(data.getDisplayName("itemBusiness")).get(row));
        		data.setSupplier(parsedTableData.get(data.getDisplayName("supplier")).get(row));
        		data.setSourceSite(parsedTableData.get(data.getDisplayName("sourceSite")).get(row));
        		data.setDestinationSite(parsedTableData.get(data.getDisplayName("destinationSite")).get(row));
        		data.setCurrency(parsedTableData.get(data.getDisplayName("currency")).get(row));
        		data.setProductState(parsedTableData.get(data.getDisplayName("productState")).get(row));
        		data.setResponsibility(parsedTableData.get(data.getDisplayName("responsibility")).get(row));
        		data.setNonManagedCostAdjustment(parsedTableData.get(data.getDisplayName("nonManagedCostAdjustment")).get(row));
    		} catch (NullPointerException e){
    			JLog.error("Error getting data for row " + row, e, TakeScreenshot.True);
    		}

    		
    		tableData.add(data);
    	}
    	
		return tableData;
	}

	
}
