/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.data;

import java.util.ArrayList;
import java.util.List;

import com.scplatform.qa.e2Messages.utilities.PartnerInfo;
import com.test.selenium.scplatform.messages.commodityCode.CommodityCodeModel;

public class GeneralData {

	public static List<CommodityCodeModel> getCommodityCodes(PartnerInfo enterpriseCompany)	{
		List<CommodityCodeModel> commodityCodes = new ArrayList<CommodityCodeModel>();
		CommodityCodeModel commodity = new CommodityCodeModel();
		
		
		commodityCodes.add(buildCommodity(enterpriseCompany, "TC", "Tablet Computer"));
		commodityCodes.add(buildCommodity(enterpriseCompany, "DIS", "Display"));
		commodityCodes.add(buildCommodity(enterpriseCompany, "MEM", "Memory"));
		commodityCodes.add(buildCommodity(enterpriseCompany, "PRO", "Processor"));
		commodityCodes.add(buildCommodity(enterpriseCompany, "COM", "Communications"));
		commodityCodes.add(buildCommodity(enterpriseCompany, "CAM", "Camera"));
		commodityCodes.add(buildCommodity(enterpriseCompany, "PWR", "Power Management"));
		commodityCodes.add(buildCommodity(enterpriseCompany, "PKG", "Packaging"));
		return commodityCodes;
	}
	private static CommodityCodeModel buildCommodity(PartnerInfo enterpriseCompany, String CommodityCode, String description)	{
		CommodityCodeModel commodity = new CommodityCodeModel();
		
		commodity.setCommodityCode(CommodityCode);
		commodity.setCommodityCodeDescription(description);
		commodity.setManagedBy(enterpriseCompany.getName());
		commodity.setOperationCode("C");
		return commodity;
	}
}
