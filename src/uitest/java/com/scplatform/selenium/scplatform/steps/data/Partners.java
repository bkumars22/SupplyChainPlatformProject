/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.selenium.common.Partner;
import com.test.selenium.common.PartnersList;
import com.test.selenium.scplatform.constants.Constants;
import com.test.selenium.scplatform.cucumber.Preprocessing;

public class Partners {

	public static void registerPartners()	{
		List<Partner> allPartners = getAllPartners();
		
		PartnersList list = new PartnersList();
		
		for (Partner partner : allPartners){
			list.setPartner(partner.getUniqueName(), partner);
			Preprocessing.addPreprocessingClass(partner.getUniqueName(), partner);
		}

		
	}
	
	public static Partner E2open()	{
		Partner partner = new Partner();
		partner.setUniqueName("E2open");
		partner.setDuns("002869795");
		partner.setName("E2open");
		return getOverridePartner(partner.getUniqueName(), partner);
	}
	
	public static Partner Enterprise()	{
		Partner partner = new Partner();
		
		partner.setUniqueName("Enterprise");
		partner.setDuns(getHubCompanyID());
		partner.setName(getHubCompanyID());
		partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
		partner.setDescription(getHubCompanyID() + " Description");
		partner.setSite("WW");
		partner.setAddress1("4100 East Third Avenue");
		partner.setAddress2("Suite 400");
		partner.setAddress3("Floor 4");
		partner.setCity("Foster City");
		partner.setCountry("USA");
		partner.setCounty("");
		partner.setState("CA");
		partner.setZip("94404");
		partner.setUdf1("Worldwide");			// Site Description
		partner.setUdf2("GLOBAL");				// Site Type
		partner.setUdf3("Ultimate Tablets");	// Alt name
		return getOverridePartner(partner.getUniqueName(), partner);
	}

	
	public static class Suppliers	{
		
		public static Partner Shenzhen_Guangdong()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("Shenzhen_Guangdong");
			partner.setDuns("Shenzhen");
			partner.setName("Shenzhen");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("Shenzhen Eastar Electronic Co. Ltd");
			partner.setSite("Guangdong");
			partner.setAddress1("Eastar Industrial Park");
			partner.setAddress2("Huanguan");
			partner.setAddress3("South Road,Guanlan Street");
			partner.setCity("Shenzhen");
			partner.setCountry("China");
			partner.setCounty("");
			partner.setState("Guangdong");
			partner.setZip("518110");
			partner.setUdf1("Guangdong Site");		// Site Description
			partner.setUdf2("SITE");				// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner Shenzhen_Hangrand()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("Shenzhen_Hangrand");
			partner.setDuns("Shenzhen");
			partner.setName("Shenzhen");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("Shenzhen Eastar Electronic Co. Ltd");
			partner.setSite("Hangrand");
			partner.setAddress1("Eastar Industrial Park");
			partner.setAddress2("Huanguan");
			partner.setAddress3("South Road,Guanlan Street");
			partner.setCity("Shenzhen");
			partner.setCountry("China");
			partner.setCounty("");
			partner.setState("Guangdong");
			partner.setZip("518110");
			partner.setUdf1("Hangrand Site");		// Site Description
			partner.setUdf2("SITE");				// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner Yeke_Chaoyang()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("Yeke_Chaoyang");
			partner.setDuns("Yeke");
			partner.setName("Yeke");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("Yeke Technology Co. Ltd");
			partner.setSite("Chaoyang");
			partner.setAddress1("2-2001, 2 North Star New Era");
			partner.setAddress2("Building, Jia 13");
			partner.setAddress3("Beiyuan Rd.");
			partner.setCity("Beijing");
			partner.setCountry("China");
			partner.setCounty("");
			partner.setState("Chaoyang");
			partner.setZip("100107");
			partner.setUdf1("Chaoyang Region");		// Site Description
			partner.setUdf2("REGION");				// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner Brilliant_Shahe()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("Brilliant_Shahe");
			partner.setDuns("Brilliant Technology");
			partner.setName("Brilliant Technology");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("Beijing Brilliant Technology Co. Ltd");
			partner.setSite("Shahe");
			partner.setAddress1("No. 301, 1st Unit");
			partner.setAddress2("Building 11");
			partner.setAddress3("Dou Gezhuang Xincun");
			partner.setCity("Beijing");
			partner.setCountry("China");
			partner.setCounty("");
			partner.setState("Shahe Town");
			partner.setZip("100000");
			partner.setUdf1("Shahe Town Site");		// Site Description
			partner.setUdf2("SITE");				// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner SanDisk_Milpitas()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("SanDisk_Milpitas");
			partner.setDuns("SanDisk");
			partner.setName("SanDisk");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("SanDisk Corporation");
			partner.setSite("Milpitas");
			partner.setAddress1("12F");
			partner.setAddress2("No. 360");
			partner.setAddress3("Bei-Tun Road");
			partner.setCity("Taichung");
			partner.setCountry("Taiwan");
			partner.setCounty("");
			partner.setState("R.O.C.");
			partner.setZip("406");
			partner.setUdf1("Milpitas Site");		// Site Description
			partner.setUdf2("SITE");				// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner SanDisk_Taichung()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("SanDisk_Taichung");
			partner.setDuns("SanDisk");
			partner.setName("SanDisk");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("SanDisk Corporation");
			partner.setSite("Taichung");
			partner.setAddress1("12F");
			partner.setAddress2("No. 360");
			partner.setAddress3("Bei-Tun Road");
			partner.setCity("Taichung");
			partner.setCountry("Taiwan");
			partner.setCounty("");
			partner.setState("R.O.C.");
			partner.setZip("406");
			partner.setUdf1("SanDisk Taiwan - Taichung");	// Site Description
			partner.setUdf2("SITE");						// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner Samsung_Ridgefield()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("Samsung_Ridgefield");
			partner.setDuns("Samsung");
			partner.setName("Samsung");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("SAMSUNG Electronics America");
			partner.setSite("Ridgefield Park");
			partner.setAddress1("85 Challenger Road");
			partner.setAddress2("");
			partner.setAddress3("");
			partner.setCity("Ridgefield Park");
			partner.setCountry("USA");
			partner.setCounty("");
			partner.setState("NJ");
			partner.setZip("07660");
			partner.setUdf1("Samsung Corporate Ridgefield Park Region");		// Site Description
			partner.setUdf2("REGION");										// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner STMicro_Chiba()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("STMicro_Chiba");
			partner.setDuns("ST Micro");
			partner.setName("ST Micro");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("ST Microelectronics");
			partner.setSite("Chiba");
			partner.setAddress1("NTT Logisco");
			partner.setAddress2("C Bldg. 4F");
			partner.setAddress3("717-88");
			partner.setCity("Chiba");
			partner.setCountry("Ichikawa-shi");
			partner.setCounty("");
			partner.setState("Futamata");
			partner.setZip("272-0001");
			partner.setUdf1("Chiba Site");		// Site Description
			partner.setUdf2("SITE");			// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner STMicro_Singapore()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("STMicro_Singapore");
			partner.setDuns("ST Micro");
			partner.setName("ST Micro");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("ST Microelectronics");
			partner.setSite("Singapore");
			partner.setAddress1("NTT Logisco");
			partner.setAddress2("C Bldg. 4F");
			partner.setAddress3("717-88");
			partner.setCity("Chiba");
			partner.setCountry("Ichikawa-shi");
			partner.setCounty("");
			partner.setState("Futamata");
			partner.setZip("272-0001");
			partner.setUdf1("Singapore Site");		// Site Description
			partner.setUdf2("SITE");				// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner MapleTouch_Guangzhou()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("MapleTouch_Guangzhou");
			partner.setDuns("MapleTouch");
			partner.setName("MapleTouch");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("MapleTouch System Limited");
			partner.setSite("Guangzhou");
			partner.setAddress1("5/F");
			partner.setAddress2("R&D Building");
			partner.setAddress3("3 Guangpu West Rd., Science City");
			partner.setCity("Guangzhou");
			partner.setCountry("China ");
			partner.setCounty("");
			partner.setState("Guangdong");
			partner.setZip("510000");
			partner.setUdf1("Guangzhou Region");		// Site Description
			partner.setUdf2("REGION");				// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner Chrontel_SanJose()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("Chrontel_SanJose");
			partner.setDuns("Chrontel");
			partner.setName("Chrontel");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("Chrontel");
			partner.setSite("San Jose");
			partner.setAddress1("2210 O'Toole Avenue");
			partner.setAddress2("Suite 100");
			partner.setAddress3("");
			partner.setCity("San Jose");
			partner.setCountry("USA");
			partner.setCounty("");
			partner.setState("CA");
			partner.setZip("95131-1326");
			partner.setUdf1("Chrontel San Jose Region");		// Site Description
			partner.setUdf2("REGION");						// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner TI_Dallas()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("TI_Dallas");
			partner.setDuns("TI");
			partner.setName("TI");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("Texas Instruments Inc.");
			partner.setSite("Dallas");
			partner.setAddress1("12500 TI Boulevard");
			partner.setAddress2("");
			partner.setAddress3("");
			partner.setCity("Dallas");
			partner.setCountry("usa");
			partner.setCounty("");
			partner.setState("TX");
			partner.setZip("75243");
			partner.setUdf1("Texas Instruments Dallas");	// Site Description
			partner.setUdf2("REGION");						// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner Shoulder_Shenzhen()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("Shoulder_Shenzhen");
			partner.setDuns("Shoulder");
			partner.setName("Shoulder");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("Shoulder Electronics Limited");
			partner.setSite("Shenzhen");
			partner.setAddress1("2/F Block C");
			partner.setAddress2("Minle Road, Minzhi");
			partner.setAddress3("Longhua Dist");
			partner.setCity("Shenzhen");
			partner.setCountry("China");
			partner.setCounty("");
			partner.setState("Guangdong");
			partner.setZip("518040");
			partner.setUdf1("Shenzhen Region");		// Site Description
			partner.setUdf2("REGION");				// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner Broadcom_Seattle()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("Broadcom_Seattle");
			partner.setDuns("Broadcom");
			partner.setName("Broadcom");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("Broadcom Corporation");
			partner.setSite("Seattle");
			partner.setAddress1("32001 32nd Avenue South");
			partner.setAddress2("Suite 410");
			partner.setAddress3("");
			partner.setCity("Seattle");
			partner.setCountry("USA");
			partner.setCounty("");
			partner.setState("WA");
			partner.setZip("98001");
			partner.setUdf1("Broadcom Seattle");	// Site Description
			partner.setUdf2("REGION");				// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner eCon_Ballwin()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("eCon_Ballwin");
			partner.setDuns("e-con");
			partner.setName("e-con");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("e-con Systems, Inc");
			partner.setSite("Ballwin");
			partner.setAddress1("1516 Strawberry Glen Ct");
			partner.setAddress2("");
			partner.setAddress3("");
			partner.setCity("Ballwin");
			partner.setCountry("USA");
			partner.setCounty("");
			partner.setState("MO");
			partner.setZip("63021");
			partner.setUdf1("Ballwin Region");		// Site Description
			partner.setUdf2("REGION");				// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner Broadcom_SanDiego()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("Broadcom_SanDiego");
			partner.setDuns("Broadcom");
			partner.setName("Broadcom");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("Broadcom");
			partner.setSite("San Diego");
			partner.setAddress1("10555 Sorrento Valley Rd");
			partner.setAddress2("");
			partner.setAddress3("");
			partner.setCity("San Diego");
			partner.setCountry("USA");
			partner.setCounty("");
			partner.setState("CA");
			partner.setZip("92121-1608");
			partner.setUdf1("Broadcom San Diego");	// Site Description
			partner.setUdf2("SITE");				// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		public static Partner Broadcom_Sunnyvale()	{
			Partner partner = new Partner();
			
			partner.setUniqueName("Broadcom_Sunnyvale");
			partner.setDuns("Broadcom");
			partner.setName("Broadcom");
			partner.setUdf4(partner.getDuns());	// BusinessEntityExternalId
			partner.setDescription("Broadcom");
			partner.setSite("Sunnyvale");
			partner.setAddress1("10555 Sorrento Valley Rd");
			partner.setAddress2("");
			partner.setAddress3("");
			partner.setCity("San Diego");
			partner.setCountry("USA");
			partner.setCounty("");
			partner.setState("CA");
			partner.setZip("92121-1608");
			partner.setUdf1("Broadcom Sunnyvale");	// Site Description
			partner.setUdf2("SITE");				// Site Type
			return getOverridePartner(partner.getUniqueName(), partner);
		}
		
		
		public static List<Partner> getAll()	{
			List<Partner> allSuppliers = new ArrayList<Partner>();
			
			allSuppliers.add(Shenzhen_Guangdong());
			allSuppliers.add(Shenzhen_Hangrand());
			allSuppliers.add(Yeke_Chaoyang());
			allSuppliers.add(Brilliant_Shahe());
			allSuppliers.add(SanDisk_Milpitas());
			allSuppliers.add(SanDisk_Taichung());
			allSuppliers.add(Samsung_Ridgefield());
			allSuppliers.add(STMicro_Chiba());
			allSuppliers.add(STMicro_Singapore());
			allSuppliers.add(MapleTouch_Guangzhou());
			allSuppliers.add(Chrontel_SanJose());
			allSuppliers.add(TI_Dallas());
			allSuppliers.add(Shoulder_Shenzhen());
			allSuppliers.add(Broadcom_Seattle());
			allSuppliers.add(eCon_Ballwin());
			allSuppliers.add(Broadcom_SanDiego());
			allSuppliers.add(Broadcom_Sunnyvale());
			
			return allSuppliers;
		}

	}

	
	public static class Manufacturers	{
		
		public static List<Partner> getAll()	{
			List<Partner> allManufacturers = new ArrayList<Partner>();
						
			return allManufacturers;
		}
		
	}
	
	public static List<Partner> getAllPartners()	{
		List<Partner> allPartners = new ArrayList<Partner>();
		
		allPartners.add(Enterprise());
//		allPartners.add(Suppliers.Shenzhen_Guangdong());
//		allPartners.add(Suppliers.Shenzhen_Hangrand());
		allPartners.addAll(Suppliers.getAll());
//		allPartners.addAll(Manufacturers.getAll());
		return allPartners;
	}
	
	private static String hubCompanyID = null;
	
	private static String getHubCompanyID()	{
		if (hubCompanyID == null)	{
			hubCompanyID = Constants.HubCompanyID;
		}
		return hubCompanyID;
	}
	
	
	private static Map<String, Partner> overridePartners;
	
	/**
	 * Allows the Partner data to be overwritten with new data.  The existing data for
	 * a partner can be called, then modified and passed to this method. When the
	 * partner is accessed, the override data, if any, will be used.
	 * 
	 * <pre>
	 *  	Partner enterprise = Partners.Enterprise();
	 *   	enterprise.setDescription("Lab126 Dev1");
	 *   	enterprise.setDuns("867A7154-C1FE-11E0-B9F8-000C293F2B37");
	 *   	Partners.setOverridePartner(enterprise.getUniqueName(), enterprise);
	 * </pre>
	 * @param partnerKey
	 * @param partner
	 */
	public static void setOverridePartner(String partnerKey, Partner partner){
		if (overridePartners == null){
			overridePartners = new HashMap<String, Partner>();
		}
		
		overridePartners.put(partnerKey, partner);
	}
	

	protected static Partner getOverridePartner(String partnerKey, Partner defaultPartner){
		Partner partner = defaultPartner;
		if (overridePartners == null){
			overridePartners = new HashMap<String, Partner>();
		}
		if (overridePartners.containsKey(partnerKey))	{
			partner = overridePartners.get(partnerKey);
		}
		
		return partner;
	}
}
