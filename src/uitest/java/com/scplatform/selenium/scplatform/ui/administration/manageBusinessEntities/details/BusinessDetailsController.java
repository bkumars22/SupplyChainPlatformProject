/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageBusinessEntities.details;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;

import com.test.selenium.common.JLog;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.messages.businessEntity.BusinessEntity;
import com.test.selenium.scplatform.messages.forecast.Forecast;
import com.test.selenium.scplatform.modelViewController.SCPlatformController;
import com.test.selenium.scplatform.ui.forecast.searchForecast.details.header.ForecastDetailsHeaderModel;

public class BusinessDetailsController extends SCPlatformController {

	@Override
	public PageImpl getView() {
		return new BusinessDetailsPage();
	}

	public boolean validate(List<BusinessEntity> businessEntityList)	{
		boolean success = true;
		boolean verified = true;
		
		BusinessEntity expected = businessEntityList.get(0);
		
		JLog.section("Verify Business Details - " + expected.getBusinessEntityName());

		BusinessDetailsPage page = new BusinessDetailsPage();
		BusinessDetailsModel actual = page.parse();
		
		verified = verify(actual.getDisplayName("id"), actual.getId(), expected.getBusinessEntity());
		success = (verified) ? success : verified;

		verified = verify(actual.getDisplayName("businessName"), actual.getBusinessName(), expected.getBusinessEntityName());
		success = (verified) ? success : verified;

		verified = verify(actual.getDisplayName("externalId"), actual.getExternalId(), expected.getBusinessEntityExternalId());
		success = (verified) ? success : verified;

		verified = verify(actual.getDisplayName("type"), 
				WordUtils.capitalize(actual.getType().toLowerCase()), 
				WordUtils.capitalize(expected.getBusinessEntityType().toLowerCase()));
		success = (verified) ? success : verified;

		verified = verify(actual.getDisplayName("description"), actual.getDescription(), expected.getDescription());
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("alternateNames"), actual.getAlternateNames(), getAlternateNames(businessEntityList));
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("currencies"), actual.getCurrencies(), getCurrencies(businessEntityList));
		success = (verified) ? success : verified;
		
		verified = verify(actual.getDisplayName("sites"), actual.getSites(), getSites(businessEntityList));
		success = (verified) ? success : verified;
		
		return success;
	}
	
	private List<String> getAlternateNames(List<BusinessEntity> businessEntityList)	{
		List<String> altNames = new ArrayList<String>();
		for (BusinessEntity data : businessEntityList)	{
			if (StringUtils.isNotBlank(data.getAlternates_AlternateName()))	{
				if (!altNames.contains(data.getAlternates_AlternateName()))	{
					altNames.add(data.getAlternates_AlternateName());
				}
			}
		}
		return altNames;
	}
	
	private List<String> getCurrencies(List<BusinessEntity> businessEntityList)	{
		List<String> currencies = new ArrayList<String>();
		for (BusinessEntity data : businessEntityList)	{
			if (StringUtils.isNotBlank(data.getCurrency_CurrencyCode()))	{
				if (!currencies.contains(data.getCurrency_CurrencyCode()))	{
					currencies.add(data.getCurrency_CurrencyCode());
				}
			}
		}
		return currencies;
	}
	
	private List<String> getSites(List<BusinessEntity> businessEntityList)	{
		List<String> sites = new ArrayList<String>();
		for (BusinessEntity data : businessEntityList)	{
			if (StringUtils.isNotBlank(data.getSite_Description()))	{
				if (!sites.contains(data.getSite_Description()))	{
					sites.add(data.getSite_Description());
				}
			}
		}
		return sites;
	}
	
}
