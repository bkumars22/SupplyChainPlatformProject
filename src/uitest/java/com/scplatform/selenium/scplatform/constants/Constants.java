/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.constants;

import com.test.selenium.common.users.User;
import com.test.selenium.scplatform.login.LoginSCPlatform;
import com.test.selenium.scplatform.steps.LoginUI;
import com.test.selenium.scplatform.utilities.Database;


public class Constants {

	protected Constants()	{
		// prevent initialization
	}


	
	/**
	 * <br>Value set in Constants.properties with key: "HubCompanyID"
	 * <br>Value is e2.ssp.hub.company.id key from System file (/scplatform/config/system.properties)
	 */
	public static String HubCompanyID = ConstantsReader.getText("HubCompanyID", "unknown");
	
	/**
	 * Gets the e2.ssp.hub.company.id from system.properties
	 * <br>Value set in Constants.properties with key: "StackID"
	 */
	public static String StackID = ConstantsReader.getText("StackID", "unknown");
	

	
	
	/**
	 * DateFormatInbound = "yyyy-MM-dd'T'HH:mm:ss"
	 * <br>Value set in Constants.properties with key: "DateFormatInbound"
	 */
	public static String DateFormatInbound = ConstantsReader.getText("DateFormatInbound");
		
	/**
	 * DateFormatOutbound = "yyyy-MM-dd'T'HH:mm:ss"
	 * <br>Value set in Constants.properties with key: "DateFormatOutbound"
	 */
	public static String DateFormatOutbound = ConstantsReader.getText("DateFormatOutbound");
	
	/**
	 * Gets the ssp.date.format.external from model.properties.  Default is "yyyy-MM-dd'T'HH:mm:ssZ"
	 * <br>Value set in Constants.properties with key: "DateFormatExternal"
	 */
	public static String DateFormatExternal = ConstantsReader.getText("DateFormatExternal", "yyyy-MM-dd'T'HH:mm:ssZ");
	
	
	/**
	 * UI Date Format.  Gets the data format from the database PCM_USER_PREFS table.<br> 
	 * If not there, then from Constants.properties.<br>
	 * If not there, then "MM-dd-yyyy"<br>
	 * <br>Value set in Constants.properties with key: "DateFormatUI"
	 */
	public static String DateFormatUI(){
		return getUserPreference("DATE_FORMAT", ConstantsReader.getText("DateFormatUI", "MM-dd-yyyy"));
	}
	
	/**
	 * UI Date Time Format.  
	 * Gets the data from Constants.properties.<br>
	 * If not there, then "yyyy-MM-dd HH:mm:ss"<br>
	 * <br>Value set in Constants.properties with key: "DateTimeFormatUI"
	 */
	public static String DateTimeFormatUI(){
		return ConstantsReader.getText("DateTimeFormatUI", "yyyy-MM-dd HH:mm:ss.SSS");
	}
	
	/**
	 * Date TimeZone.  Gets the data format from the database PCM_USER_PREFS table.<br> 
	 * If not there, then from Constants.properties.<br>
	 * If not there, then "UTC"<br>
	 * <br>Value set in Constants.properties with key: "timezone"
	 */
	public static String DateTimeZone()	{ 
		return getUserPreference("TIMEZONE", ConstantsReader.getText("timezone", "UTC"));
	}
	
	
	/**
	 * UI Date Format.  Default is "MM/dd/yyyy"
	 * <br>Value set in Constants.properties with key: "DateFormatExcelUpload"
	 */
	public static String DateFormatExcelUpload = ConstantsReader.getText("DateFormatExcelUpload", "MM/dd/yyyy");
	
	
	
	
	/**
	 * Gets the ssp.date.format.internal from model.properties.  Default is "yyyy-MM-dd'T'HH:mm:ssZ"
	 * <br>Value set in Constants.properties with key: "DateFormatInternal"
	 */
	public static String DateFormatInternal = ConstantsReader.getText("DateFormatInternal", "yyyy-MM-dd'T'HH:mm:ssZ");
	

	/**
	 * Format for float values on the UI.  Default is "###0.00"
	 * <br>Value set in Constants.properties with key: "FloatFormatUI"
	 */
	public static String FloatFormatUI = ConstantsReader.getText("FloatFormatUI", "###0.00");
	

	/**
	 * [2014-11-11 13:13:23 PST] WARNING: select PREF_VALUE from PCM_USER_PREFS where PREF_NAME='DATE_FORMAT' and USER_KEY=(select USER_KEY from PCM_USER where USER_ID='shivaniotc')
	 * <br>[2014-11-11 13:13:23 PST] WARNING: Error running sql: java.sql.SQLException: Exhausted Resultset
	 */
	private static boolean userPreferencesError = true;
	
	protected static String getUserPreference (String prefName, String defaultValue){
		if (userPreferencesError){
			return defaultValue;
		}
		
		String value = null;;
		
		User user = LoginSCPlatform.getCurrentLoggedInUser();
		if (user == null){
			user = LoginUI.getCurrentLogggedInUser();
		}
		if (user == null){
//			JLog.warning (String.format("Constants.getUserPreference(%s, %s): current user is null, unexpected!", prefName, defaultValue));
			return defaultValue;
		}
		String loginID = user.getLoginID();
		
		String sql = "select PREF_VALUE from PCM_USER_PREFS where PREF_NAME='" + prefName + 
				"' and USER_KEY=(select USER_KEY from PCM_USER where USER_ID='" + loginID + "')";
		
		value = Database.getResult(sql, "PREF_VALUE");
		if (value == null){
			value = defaultValue;
		}
		
		return value;
	}
}

