/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseUtils {

	public static void setEnterpriseCompanyName (String businessIdentifier)	{
		StringBuilder sql = new StringBuilder();
		sql.append("update BUSINESS_ENTITY set ");
		sql.append("BUSINESS_ENTITY_IDENTIFIER=");
		sql.append("'" + businessIdentifier + "', ");
		sql.append("BUSINESS_ENTITY_NAME=");
		sql.append("'" + businessIdentifier + "', ");
		sql.append("BUSINESS_ENTITY_DESC=");
		sql.append("'" + businessIdentifier + "', ");
		sql.append("DATA_SOURCE=");
		sql.append("'" + businessIdentifier + "' ");		
		sql.append("where BUSINESS_ENTITY_TYPE_KEY=1");
		
		Database.executeUpdateQuery(sql.toString());
	}
	
	/**
	 * Deletes data from the MTCM database
	 * 
	 * <UL>
	 * <LI> {@link #deleteMTCMData()}
	 * <LI> {@link #deleteAllocation()}
	 * <LI> {@link #deleteCostRecords()}
	 * <LI> {@link #deleteSourcingLane()}
	 * </UL>
	 */
	public static void deleteMTCMData ()	{
		deleteForecast();
		deleteAllocation();
		deleteCostRecords();
		deleteSourcingLane();
		 
	}
	
	/**
	 * Deletes all Forecast data from database tables:
	 * 
	 * <UL>
	 * <LI> PCM_FORECAST
	 * <LI> PCM_FORECAST_VALUE
	 * </UL>
	 */
	public static void deleteForecast()	{
		Database.executeUpdateQuery("DELETE FROM PCM_FORECAST");
		Database.executeUpdateQuery("DELETE FROM PCM_FORECAST_VALUE");
	}
	
	/**
	 * Deleats the Supplier Allocation table - PCM_SUPPLIER_ALLOCATION
	 */
	public static void deleteAllocation()	{
		Database.executeUpdateQuery("DELETE FROM PCM_SUPPLIER_ALLOCATION");
	}
	
	/**
	 * Deletes all Cost Record data from database tables:
	 * 
	 * <UL>
	 * <LI> PCM_COST_RECORD_VALUE
	 * <LI> PCM_COST_RECORD_RANGE
	 * <LI> PCM_COST_RECORD
	 * </UL>
	 */
	public static void deleteCostRecords()	{
		Database.executeUpdateQuery("DELETE FROM PCM_COST_RECORD_VALUE");
		Database.executeUpdateQuery("DELETE FROM PCM_COST_RECORD_RANGE");
		Database.executeUpdateQuery("DELETE FROM PCM_COST_RECORD");
	}
	
	/**
	 * Deletes a sub-set of the Cost Record data based on the user ID that made the change.
	 * 
	 * @param whereLastChangeUser	User ID of last user to make change to the records.
	 */
	public static void deleteCostRecords (String whereLastChangeUser){
		Database.executeUpdateQuery("DELETE FROM PCM_COST_RECORD_VALUE WHERE COST_RECORD_RANGE_KEY in " +
				"(select COST_RECORD_RANGE_KEY from PCM_COST_RECORD_RANGE where COST_RECORD_KEY in " +
				"(select COST_RECORD_KEY from PCM_COST_RECORD where STATUS_LAST_CHANGE_BY='" + whereLastChangeUser + "'))");
		
		Database.executeUpdateQuery("DELETE FROM PCM_COST_RECORD_RANGE where COST_RECORD_KEY in (select COST_RECORD_KEY from PCM_COST_RECORD where STATUS_LAST_CHANGE_BY='" + whereLastChangeUser + "')");
		
		Database.executeUpdateQuery("DELETE FROM PCM_COST_RECORD where STATUS_LAST_CHANGE_BY='" + whereLastChangeUser + "'");
	}
	
	/**
	 * Deletes all Sourcing Lane data from database tables:
	 * 
	 * <UL>
	 * <LI> PCM_SOURCING_LANE
	 * </UL>
	 */
	public static void deleteSourcingLane()	{
		Database.executeUpdateQuery("DELETE FROM PCM_SOURCING_LANE");

	}
	
	public static void createBatchUser ()	{
		String sql = "delete from pcm_user where user_id='BATCH'";
		Database.executeUpdateQuery(sql.toString());
		
		sql = "delete from pcm_user where user_id='batch'";
		Database.executeUpdateQuery(sql.toString());
		
		sql = "select BUSINESS_ENTITY_KEY from BUSINESS_ENTITY where BUSINESS_ENTITY_TYPE_KEY=1";
		String BUSINESS_ENTITY_KEY = Database.getResult(sql, "BUSINESS_ENTITY_KEY");
		
		sql = "INSERT INTO PCM_USER (USER_KEY, USER_ID, USER_NAME, BUSINESS_ENTITY_KEY, EMAIL_ADDRESS, ROLE_KEY, ENABLED_FLAG) VALUES (999, 'BATCH', 'BATCH', " + 
				BUSINESS_ENTITY_KEY + ", 'batch.user@local.test', 1, 'Y')";
		
		Database.executeUpdateQuery(sql.toString());
	}
	
	/**
	 * @return	Retrieves all unique cost element keys from the PCM_COST_ELEMENT table
	 */
	public static List<String> getCostElements (){
		String sql = "select unique COST_ELEMENT_KEY from PCM_COST_ELEMENT order by COST_ELEMENT_KEY";
		HashMap<String, List<String>> results = Database.getResults (sql, "COST_ELEMENT_KEY");
		return results.get("COST_ELEMENT_KEY");
	}
	
	/**
	 * @param siteName	The site name based on the site description
	 * @return	the SITE_DESCRIPTION for the given siteName
	 */
	public static String getSiteDescription (String siteName){
		String sql = "select SITE_DESCRIPTION from SITE where SITE_IDENTIFIER='" + siteName + "'";
		return Database.getResult(sql, "SITE_DESCRIPTION");
	}
	
	/**
	 * @param siteDescription	The site description
	 * @return	the SITE_IDENTIFIER for the given siteDescription
	 */
	public static String getSiteName (String siteDescription){
		String sql = "select SITE_IDENTIFIER from SITE where SITE_DESCRIPTION='" + siteDescription + "'";
		return Database.getResult(sql, "SITE_IDENTIFIER");
	}
	
	/**
	 * @param costType	The Cost Type value (typically from {@link CostTypeEnum}
	 * @return	the COST_TYPE_NAME for the given costType
	 */
	public static String getCostTypeName (String costType){
		String sql = "select COST_TYPE_NAME from PCM_COST_TYPE where COST_TYPE_KEY='" + costType + "'";
		return Database.getResult(sql, "COST_TYPE_NAME");
	}
	
	/**
	 * @param itemIdentifier	The Item Identifier
	 * @param itemVersion		The Item Version
	 * @param itemRevision		The Item Revision
	 * @return					The ITEM_KEY from the ITEM_MASTER database table
	 */
	public static String getItemKey(String itemIdentifier, String itemVersion, String itemRevision)	{
		String sql = String.format("select ITEM_KEY from ITEM_MASTER where ITEM_IDENTIFIER='%s' and VERSION=%s and REVISION='%s'", 
				itemIdentifier,
				itemVersion,
				itemRevision);
		
		return Database.getResult(sql, "ITEM_KEY");
	}
	
	public static void deleteItems()	{
		deleteItems(defaultListOfItems());
	}
	
	public static void deleteItems(List<String> itemsToDelete)	{
		String sqlItems = convertListToSQL(itemsToDelete);
		
		Database.executeUpdateQuery(String.format(
				"delete from ITEM_ADD_ATTRIBUTE where ITEM_KEY in (select ITEM_KEY from ITEM_MASTER where ITEM_IDENTIFIER in %s)", 
				sqlItems));
		
		Database.executeUpdateQuery(String.format(
				"delete from ITEM_AVL where ITEM_KEY in (select ITEM_KEY from ITEM_MASTER where ITEM_IDENTIFIER in %s)", 
				sqlItems));
		
		Database.executeUpdateQuery(String.format(
				"delete from ITEM_CATEGORY where ITEM_CATEGORY_KEY in (select ITEM_CATEGORY_KEY from ITEM_ITEM_CATEGORY where ITEM_KEY in (select ITEM_KEY from ITEM_MASTER where ITEM_IDENTIFIER in %s))", 
				sqlItems));
		
		Database.executeUpdateQuery(String.format(
				"delete from ITEM_ITEM_CATEGORY where ITEM_KEY in (select ITEM_KEY from ITEM_MASTER where ITEM_IDENTIFIER in %s)", 
				sqlItems));
		
		Database.executeUpdateQuery(String.format(
				"delete from ITEM_PLATFORM where ITEM_PLATFORM_KEY in (select ITEM_PLATFORM_KEY from ITEM_ITEM_PLATFORM where ITEM_KEY in (select ITEM_KEY from ITEM_MASTER where ITEM_IDENTIFIER in %s))", 
				sqlItems));
		
		Database.executeUpdateQuery(String.format(
				"delete from ITEM_ITEM_PLATFORM where ITEM_KEY in (select ITEM_KEY from ITEM_MASTER where ITEM_IDENTIFIER in %s)", 
				sqlItems));
		
		Database.executeUpdateQuery(String.format(
				"delete from ITEM_MASTER where ITEM_IDENTIFIER in %s", 
				sqlItems));
	}
	
	protected static String convertListToSQL(List<String> list)	{
		StringBuilder converted = new StringBuilder();
		
		for (String data : list){
			converted.append("'");
			converted.append(data);
			converted.append("',");
		}
		converted.deleteCharAt(converted.length()-1);
		return "(" + converted.toString() + ")";
	}
	
	protected static List<String> defaultListOfItems()	{
		List<String> items = new ArrayList<String>();
		
		items.add("GYROSCOPE");
		items.add("GYROSCOPE-CHIBA");
		items.add("GYROSCOPE-SINGAPORE");
		items.add("SCREEN-CONTROLLER");
		items.add("SCREEN-CONTROLLER-GUANGZHOU");
		items.add("LVDS-TRANSMITTER");
		items.add("LVDS-TRANSMITTER-SAN JOSE");
		items.add("DISPLAY-17");
		items.add("DISPLAY-17-GUANGDONG");
		items.add("DISPLAY-17-HANGRAND");
		items.add("DISPLAY-17-CHAOYANG");
		items.add("DISPLAY-17-SHAHE");
		items.add("TOUCH-SCREEN-17");
		items.add("TOUCH-SCREEN-17-GUANGDONG");
		items.add("TOUCH-SCREEN-17-HANGRAND");
		items.add("TOUCH-SCREEN-17-CHAOYANG");
		items.add("TOUCH-SCREEN-17-SHAHE");
		items.add("SANDISK-16GB");
		items.add("SANDISK-16GB-MILPITAS");
		items.add("SANDISK-16GB-TAICHUNG");
		items.add("DDR-4GB");
		items.add("DDR-4GB-RIDGEFIELD PARK");
		items.add("CORE-PROCESS");
		items.add("CORE-PROCESS-SINGAPORE");
		items.add("APP-PROCESS");
		items.add("APP-PROCESS-DALLAS");
		items.add("RF-TRANSEIVER");
		items.add("RF-TRANSEIVER-SHENZHEN");
		items.add("BLUETOOTH/WLAN");
		items.add("BLUETOOTH/WLAN-DALLAS");
		items.add("GPS-RECEIVER");
		items.add("GPS-RECEIVER-SEATTLE");
		items.add("FRONT-CAMERA");
		items.add("FRONT-CAMERA-BALLWIN");
		items.add("REAR-CAMERA");
		items.add("REAR-CAMERA-BALLWIN");
		items.add("POWER-MGMT-IC");
		items.add("POWER-MGMT-IC-SAN DIEGO");
		items.add("POWER-MGMT-IC-SUNNYVALE");
		items.add("BATTERY");
		items.add("BATTERY-HANGRAND");
		items.add("HEADSET");
		items.add("HEADSET-GUANGDONG");
		items.add("USB-CHARGER");
		items.add("USB-CHARGER-SAN DIEGO");
		items.add("USB-CHARGER-SUNNYVALE");
		items.add("USB-ADAPTER");
		items.add("USB-ADAPTER-SAN DIEGO");
		items.add("USB-ADAPTER-SUNNYVALE");
		items.add("POWER-ADAPTER");
		
		return items;
	}
}
