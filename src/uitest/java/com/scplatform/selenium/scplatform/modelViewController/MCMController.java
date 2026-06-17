/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.scplatform.qa.e2Messages.utilities.NullValue;
import com.test.selenium.common.JLog;
import com.test.selenium.common.StringUtilities;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.controller.BrowserCRUDControllerImpl;
import com.test.selenium.common.modelViewController.model.Model;

public abstract class SCPlatformController extends BrowserCRUDControllerImpl {
	private static int row = 0;
	
	public static int getRow()	{
		return row;
	}
	
	@Override
    public void create() throws Exception {
        super.assertModelsExist();
        for (Model model : models) {
            populateValues(model);
            save(model);
            handlePostErrors();
        }
    }
	
	@Override
	public void gotoAddPage(Model model) {
        gotoHomePage(model);
        SCPlatformPage page = new SCPlatformPage();
        clickAndCheckForPOSTError(page.addButton());
    }

	@Override
	public void gotoHomePage(Model model) {
	}

	@Override
	public void save(Model model) {
		SCPlatformPage page = new SCPlatformPage();
        clickAndCheckForPOSTError(page.saveButton());
	}

	@Override
	protected void gotoEditPage(Model model) {
		SCPlatformPage page = new SCPlatformPage();
        clickAndCheckForPOSTError(page.editButton());
    }

	@Override
	public String getErrorMessage() {
		SCPlatformPage page = new SCPlatformPage();
        return page.getErrorMessage();
	}
	
	
	protected boolean verify(String headerName, String actualData, String expectedData)	{
		boolean success = true;
		
		if (actualData == null) actualData = "";
		if (expectedData == null) expectedData = "";
		
		if (actualData.equals(expectedData)){
			JLog.write(String.format("Verify '%s' is '%s'", headerName, expectedData));
		} else	{
			success = false;
			JLog.error(String.format("Verify '%s' is '%s'.  Actual is '%s'", headerName, expectedData, actualData), TakeScreenshot.True);
		}
		
		return success;
	}

	protected boolean verify(String headerName, List<String> actualData, List<String> expectedData)	{
		boolean success = true;
		
		if (actualData==null)	{
			actualData = new ArrayList<String>();
		} else if (expectedData==null)	{
			expectedData = new ArrayList<String>();
		}
		
		Collections.sort(actualData);
		Collections.sort(expectedData);
		
		if ( (expectedData.size() > 5) || (actualData.size() > 5) )	{
			return verifyLargeArraySet(headerName, actualData, expectedData);
		}
		
		HashSet<List> actualHashSet = new HashSet<List>(Arrays.asList(actualData));
		HashSet<List> expectedHashSet = new HashSet<List>(Arrays.asList(expectedData));

		
		if (actualHashSet.equals(expectedHashSet)){
			JLog.write(String.format("Verify '%s' is '%s'", headerName, expectedData));
		} else	{
			success = false;
			JLog.error(String.format("Verify '%s' is '%s'.  Actual is '%s'", headerName, expectedData, actualData), TakeScreenshot.True);
		}
		
		return success;
	}
	
	private boolean verifyLargeArraySet(String headerName, List<String> actualData, List<String> expectedData) {
		JLog.blankLine();
		boolean success = true;
		Map<String, String> missingFields = new HashMap<String, String>();
		
		for (int i = 0; i < actualData.size(); i++)	{
			if (!expectedData.contains(actualData.get(i)))	{
				success = false;
				missingFields.put("Actual " + i, actualData.get(i));
			} 
		}
		String actualMissing = findMissing("Actual", missingFields);
		
		for (int i = 0; i < expectedData.size(); i++)	{
			if (!actualData.contains(expectedData.get(i)))	{
				success = false;
				missingFields.put("Expected " + i, expectedData.get(i));
			}
		}
		String expectedMissing = findMissing("Expected", missingFields);
		
		JLog.write(String.format("Verification of '%s' list is '%s'", headerName, (success) ? "Succesfull" : "Errors"));
		JLog.write("Actual Data: " + join(actualData));
		if (actualMissing != null)	{
			JLog.error("Data in Expected but not Actual: " + actualMissing, TakeScreenshot.True);
		}
		if (expectedMissing != null)	{
			JLog.error("Data in Actual but not Expected: " + expectedMissing, TakeScreenshot.True);
		}
		
		JLog.blankLine();
		return success;
	}

	private String findMissing(String missingFrom, Map<String, String> missingFields) {
		List<String> missing = new ArrayList<String>();
		
		for (String key : missingFields.keySet()){
			if (key.startsWith(missingFrom))	{
				missing.add(missingFields.get(key));
			}
		}
		
		if (missing.isEmpty()){
			return null;
		}
		return join(missing);
	}

	private String join(List<String> list){
		return "[" + StringUtilities.join(list, ", ") + "]";
	}
	
	protected boolean verify(String headerName, float actualData, float expectedData)	{
		return verify(headerName, actualData, expectedData, null);
	}
	
	protected boolean verify(String headerName, float actualData, float expectedData, String additionalErrorMsg)	{
		boolean success = true;
		String expected = "";
		String actual = "";
		if (expectedData != NullValue.FLOAT)	{
			expected = StringUtilities.formatNumber(expectedData, "###0.0000");
		}
		
		if (actualData != NullValue.FLOAT)	{
			actual = StringUtilities.formatNumber(actualData, "###0.0000");
		}
		
		if (actual.equals(expected)){
			JLog.write(String.format("Verify '%s' is '%f'", headerName, expectedData));
		} else	{
			success = false;
			JLog.error(String.format("Verify '%s' is '%f'.  Actual is '%f'", headerName, expectedData, actualData), TakeScreenshot.True);
			if (StringUtils.isNotBlank(additionalErrorMsg))	{
				JLog.warning(additionalErrorMsg);
			}
		}
		
		return success;
	}
	
	protected boolean verify(String headerName, DateTime actualData, DateTime expectedData)	{
		boolean success = true;
		
		if ( (actualData == null) && (expectedData == null) ){
			JLog.write(String.format("Verify '%s' is '%s'", headerName, expectedData));
		} else if ( (actualData == null) ||  (expectedData == null) )	{
			success = false;
			JLog.error(String.format("Verify '%s' is '%s'.  Actual is '%s'", headerName, expectedData, actualData), TakeScreenshot.True);
		} else if (actualData.equals(expectedData)){
			JLog.write(String.format("Verify '%s' is '%s'", headerName, expectedData));
		} else	{
			success = false;
			JLog.error(String.format("Verify '%s' is '%s'.  Actual is '%s'", headerName, expectedData, actualData), TakeScreenshot.True);
		}
		
		return success;
	}
	

}
