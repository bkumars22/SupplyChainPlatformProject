/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.forecast.searchForecast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.constants.Constants;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsPage;

/**
 * Navigation: Forecast -> Search Forecast
 * 
 * @see	#parseResults()
 */
public class SearchForecastResultsPage extends SCPlatformSearchResultsPage {

	public WebElement button_Next()	{
		return getElement(By.partialLinkText("Next"));
	}
	
	public By tableLocator()	{
		return By.id("forecastSearchResultTable_data");
	}
	
	/**
	 * @return	Parses the results listing and returns a list of {@link SearchForecastResultsModel} data
	 */
	public List<SearchForecastResultsModel> parseResults(
			String previousForecastlabel, 
			String currentForecastLabel, 
			String nextForecastLabel) {
		
    	List<SearchForecastResultsModel> tableData = new ArrayList<SearchForecastResultsModel>();
    	SearchForecastResultsModel data = new SearchForecastResultsModel();
 	
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
    	DateTimeFormatter formatter = DateTimeFormat.forPattern(Constants.DateTimeFormatUI());
    	

    	
    	for (int row = 1; row < tableSize; row++)	{
    		data = new SearchForecastResultsModel();
    		
    		try	{
        		data.setItemNumber(parsedTableData.get(data.getDisplayName("itemNumber")).get(row));
        		data.setItemDescription(parsedTableData.get(data.getDisplayName("itemDescription")).get(row));
        		data.setRegion(parsedTableData.get(data.getDisplayName("region")).get(row));
        		data.setMemberOfGroup(parsedTableData.get(data.getDisplayName("memberOfGroup")).get(row));
        		data.setCommodityName(parsedTableData.get(data.getDisplayName("commodityName")).get(row));
        		data.setPlatform(parsedTableData.get(data.getDisplayName("platform")).get(row));
        		data.setClassification(parsedTableData.get(data.getDisplayName("classification")).get(row));
        		data.setProductFamily(parsedTableData.get(data.getDisplayName("productFamily")).get(row));
        		data.setForecastModel(parsedTableData.get(data.getDisplayName("forecastModel")).get(row));
        		data.setStatus(parsedTableData.get(data.getDisplayName("status")).get(row));
        		data.setExtendForecastTerm(parsedTableData.get(data.getDisplayName("extendForecastTerm")).get(row));
        		data.setLastChangedOn(DateTime.parse(parsedTableData.get(data.getDisplayName("lastChangedOn")).get(row), formatter));
        		data.setLastChangeBy(parsedTableData.get(data.getDisplayName("lastChangeBy")).get(row));
        		data.setResponsibility(parsedTableData.get(data.getDisplayName("responsibility")).get(row));
        		
        		if ( (previousForecastlabel != null) && (StringUtils.isNotBlank(parsedTableData.get(previousForecastlabel).get(row))) )	
        			data.setPitBuckets(previousForecastlabel, Float.parseFloat(parsedTableData.get(previousForecastlabel).get(row)));
        		
        		if ( (currentForecastLabel != null) && (StringUtils.isNotBlank(parsedTableData.get(currentForecastLabel).get(row))) )	
        			data.setPitBuckets(currentForecastLabel, Float.parseFloat(parsedTableData.get(currentForecastLabel).get(row)));
        		
        		if ( (nextForecastLabel != null) && (StringUtils.isNotBlank(parsedTableData.get(nextForecastLabel).get(row))) )	
        			data.setPitBuckets(nextForecastLabel, Float.parseFloat(parsedTableData.get(nextForecastLabel).get(row)));
        		
    		} catch (NullPointerException e){
    			JLog.error("Error getting data for row " + row, e, TakeScreenshot.True);
    		}
    		tableData.add(data);
    	}
    	
		return tableData;
	}
	
}
