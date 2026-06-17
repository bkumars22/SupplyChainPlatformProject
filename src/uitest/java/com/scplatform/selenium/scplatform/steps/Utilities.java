/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps;

import java.util.ArrayList;
import java.util.List;

import com.test.selenium.common.JLog;
import com.test.selenium.common.Partner;
import com.test.selenium.scplatform.cucumber.CukeHelper;

public class Utilities {

	public static List<Partner> getPartners(String stringValue) {
		List<Partner> partners = new ArrayList<Partner>();
		
		String[] keyList = stringValue.split(";");
		for (String key : keyList)	{
			Partner data = (Partner) CukeHelper.findSavedClass(key.trim());
			if (data == null){
				JLog.error("Utilities.getPartners() - unable to find partner key: " + key);
			} else	{
				partners.add(data);
			}
		}
		return partners;
	}
}
