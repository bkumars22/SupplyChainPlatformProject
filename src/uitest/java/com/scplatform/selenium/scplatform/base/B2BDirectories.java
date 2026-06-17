/**
 * @B2BDirectories.java@
 *
 * Created on Jul 11, 2011
 *
 *      Copyright (c) 2010 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.testng.AssertJUnit;

import com.test.selenium.common.CalendarUtils;
import com.test.selenium.common.DBUtility;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.RandomUtils;
import com.test.selenium.common.RealTime;

/**
 * Retrieves the directory for B2B Client for the given partner
 * @author 	David Genrich
 * @since	SSP 7.3
 */
public class B2BDirectories {

	/**
	 * Upload/Download Configurations
	 * 
	 * @author dgenrich
	 *
	 */
	public enum CONFIGURATION {
		/**
		 * Skip the upload or download test
		 */
		NONE, 
		
		/**
		 * Use UI to do uploads/downloads
		 */
		UI, 
		
		/**
		 * Use B2BC to do uploads/downloads
		 */
		B2BC, 
		
		/**
		 * Use Directory Adaptor to do uploads.  Unable to do downloads using Directory Adaptor.
		 */
		DIRECTORY_ADAPTOR
	};
	
	private static CONFIGURATION uploadConfiguration = null;
	private static CONFIGURATION downloadConfiguration = null;
	
	
	
	/**
	 * @return the uploadConfiguration
	 */
	public static CONFIGURATION getUploadConfiguration() {
		if (uploadConfiguration == null)	{
			if (supportB2BC)	{
				if (forceB2BLoading)	{
					B2BDirectories.uploadConfiguration = CONFIGURATION.B2BC;
				} else	{
					B2BDirectories.uploadConfiguration = CONFIGURATION.UI;
				}
			} else	{
				B2BDirectories.uploadConfiguration = CONFIGURATION.UI;
			}
		}
		return uploadConfiguration;
	}


	/**
	 * @param uploadConfiguration the uploadConfiguration to set
	 */
	public static void setUploadConfiguration(CONFIGURATION uploadConfiguration) {
		B2BDirectories.uploadConfiguration = uploadConfiguration;
	}


	/**
	 * @return the downloadConfiguration
	 */
	public static CONFIGURATION getDownloadConfiguration() {
		if (downloadConfiguration == null)	{
			if (supportB2BC)	{
				if (forceB2BDownloading)	{
					B2BDirectories.downloadConfiguration = CONFIGURATION.B2BC;
				} else	{
					B2BDirectories.downloadConfiguration = CONFIGURATION.UI;
				}
			} else	{
				B2BDirectories.downloadConfiguration = CONFIGURATION.UI;
			}
		}
		return downloadConfiguration;
	}


	/**
	 * @param downloadConfiguration the downloadConfiguration to set
	 */
	public static void setDownloadConfiguration(CONFIGURATION downloadConfiguration) {
		B2BDirectories.downloadConfiguration = downloadConfiguration;
	}

	/**
	 * set this to true if B2BC is supported.  Used in {@link #uploadB2BC()}.
	 */
	private static boolean supportB2BC = true;
	
	/**
	 * set this to true to force B2B paths.  
	 * Used in {@link #uploadB2BC()}.  Set with #link {@link #setForceB2BLoading(boolean)}
	 */
	private static boolean  forceB2BLoading = false;

	
	/*
	 * Set this to true to false downloads (where possible) to use B2BC
	 */
	private static boolean forceB2BDownloading = false;
	
	/**
	 * B2BC is supported by default.  However, there might be times in the code that B2BC needs to be disabled
	 * (such as when B2BC Client install fails).  This method is to support changing the value of the class variable "supportB2BC"
	 * 
	 * @param supportClient	Set true if B2BC is supported.  
	 */
	public static void setSupportB2BC (boolean supportClient)	{
		supportB2BC = supportClient;
	}
	
	
	/**
	 * @return		Value set in {@link #supportB2BC}
	 */
	public static boolean isB2BCSupported ()	{
		return supportB2BC;
	}
	
	/*
	 * By Default, many downloads do random UI or B2BC downloads.  Setting this to TRUE will cause those tests
	 * with a random download method to always download via B2BC.
	 * 
	 * Default is FALSE.
	 */
	public static void setForceB2BDownload( boolean downloadOnlyB2BC)	{
		forceB2BDownloading = downloadOnlyB2BC;
	}
	
	/**
	 * B2BC is supported by default.  However, there might be times in the code that you want all upload/downlaods to be done
	 * using B2B (often during development of tests).  
	 * 
	 * @param supportClient	Set true if you want {@link #uploadB2BC()} to always return true
	 */
	public static void setForceB2BLoading (boolean foreceB2B)	{
		forceB2BLoading = foreceB2B;
	}
	
	/**
	 * @return	Returns a random boolean to determine if you should upload via B2BC or E2.
	 * If B2BC is not supported, or to only upload via E2, set class variable
	 * supportB2BC to false.
	 */
	public static boolean uploadB2BC ()	{
		return uploadB2BC("Tenant.b2bc.rootdir");
	}
	
	/**
	 * @return	Returns a random boolean to determine if you should upload via B2BC or E2.
	 * If B2BC is not supported, or to only upload via E2, set class variable
	 * supportB2BC to false.
	 */
	public static boolean uploadB2BC (String b2bKeyToCheck)	{
		// If b2bc is not setup, return false! Otherwise, it could actually try to use b2bc and then fail.
		RealTime runtime = RealTime.getInstance();
		String value = runtime.get().getProperty(b2bKeyToCheck);
		if (null == value){
			return false;
		}
		
		if (!supportB2BC)	{
			return false;
		} else if (forceB2BLoading){
			return true;
		}
		
		boolean uploadB2BC = RandomUtils.randomBoolean();
		return uploadB2BC;
	}
	
	private static java.sql.Connection dbConnection = null;
	public static void connectDatabase()	{
		try {
			dbConnection = DBUtility.openTestRunnerConnection();
		} catch (SQLException e) {
			JLog.fail(e);
		}
	}
	
	public static void disconnectDatabase()	{
	    DBUtility.closeSafely(dbConnection);
	}
	/**
	 * @return	Returns a random boolean to determine if you should upload via B2BC or E2.
	 * If B2BC is not supported, or to only upload via E2, set class variable
	 * supportB2BC to false.
	 */
	public static boolean downloadB2BC (String scheduleName)	{
		if (!supportB2BC)	{
			return false;
		} else if (forceB2BLoading){
			return true;
		} else if (forceB2BDownloading){
			return true;
		}
		
		boolean useB2BC = true;
		boolean doInsert = true;
		
		Prop prop = Prop.getInstance();
		String stack = prop.get().getProperty("stack.name", "unknown");

		String sqlQuery = "select LAST_METHOD from DOWNLOADS where STACK='" + stack + "' and SCHEDULE_NAME='" + scheduleName + "'";
		ResultSet rs = null;

		try {
			if (dbConnection == null){
				connectDatabase();
			}
			
			rs = DBUtility.executeQuery(dbConnection, sqlQuery);
			rs.next();

			String result = rs.getString("LAST_METHOD");
			if (result != null)	{
				doInsert = false;
				
				if (result.equalsIgnoreCase("B2BC"))	{
					useB2BC = false;
				}
			} 

		} catch (SQLException e) {
			JLog.write("SQL: " + sqlQuery);
			JLog.warning("B2BDirectories.downloadB2BC(): Problem Executing SQL: " + e.toString());
		} finally {
		    DBUtility.closeSafely(rs);
		}
		
		
		
		
		try	{
			String currentMethod = (useB2BC) ? "B2BC" : "UI";
			String lastModifiedDate = CalendarUtils.dateToString(new Date(), "dd-MMM-yyyy kk:mm:ss");
			
			StringBuilder sqlBuilder = new StringBuilder();
			
			if (doInsert){
				sqlBuilder.append("INSERT INTO DOWNLOADS ");
				sqlBuilder.append("(STACK, SCHEDULE_NAME, LAST_METHOD, LAST_MODIFIED) ");
				sqlBuilder.append("VALUES (");
				sqlBuilder.append("'" + stack + "', ");
				sqlBuilder.append("'" + scheduleName + "', ");
				sqlBuilder.append("'" + currentMethod + "', ");
				sqlBuilder.append("to_date('" + lastModifiedDate + "', 'dd-mon-yyyy HH24:mi:ss')");
				sqlBuilder.append(")");
			} else	{
				sqlBuilder.append("UPDATE DOWNLOADS SET LAST_MODIFIED=to_date('" + lastModifiedDate + "', 'dd-mon-yyyy HH24:mi:ss'), ");
				sqlBuilder.append("LAST_METHOD='" + currentMethod + "' ");
				sqlBuilder.append("where STACK='" + stack + "' and ");
				sqlBuilder.append("SCHEDULE_NAME='" + scheduleName + "'");
			}
			sqlQuery = sqlBuilder.toString();
			DBUtility.executeQuery(dbConnection, sqlQuery);
			
		} catch (SQLException e) {
			JLog.write("SQL: " + sqlQuery);
			JLog.warning("B2BDirectories.downloadB2BC(): Problem Updating Status: " + e.toString());
		} 

		return useB2BC;
	}
	
	
	
	/**
	 * @param partnerKey	the partner key (example: Tenant or this.Supplier1)
	 * @return				The B2BC Installation directory for the given partner key
	 */
	public static String b2bc_install_directory (String partnerKey)	{
		return getRuntimeValue (partnerKey + ".b2bc.rootdir");
	}
	
	/**
	 * @param partnerKey	the partner key (example: Tenant or this.Supplier1)
	 * @return				The B2BC inbox directory for the given partner key
	 */
	public static String b2bc_inbox_directory (String partnerKey)	{
		return getRuntimeValue (partnerKey + ".b2bc.inbox");
	}
	
	/**
	 * @param partnerKey	the partner key (example: Tenant or this.Supplier1)
	 * @return				The B2BC outbox directory for the given partner key
	 */
	public static String b2bc_outbox_directory (String partnerKey)	{
		return getRuntimeValue (partnerKey + ".b2bc.outbox");
	}

	/**
	 * @param partnerKey	the partner key (example: Tenant or this.Supplier1)
	 * @return				The B2BC archive directory for the given partner key
	 */
	public static String b2bc_archive_directory (String partnerKey)	{
		return getRuntimeValue (partnerKey + ".b2bc.archive");
	}
	
	/**
	 * @param partnerKey	the partner key (example: Tenant or this.Supplier1)
	 * @return				The B2BC download directory for the given partner key
	 */
	public static String b2bc_download_directory (String partnerKey)	{
		return getRuntimeValue (partnerKey + ".b2bc.download");
	}
	
	private static String getRuntimeValue (String key)	{
		String value;
		RealTime runtime = RealTime.getInstance();
		value = runtime.get().getProperty(key);
		AssertJUnit.assertNotNull("Runtime key not found - value is null: " + key, value);
		return value;
	}
	
}
