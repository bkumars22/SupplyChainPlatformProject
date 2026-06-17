/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import com.test.selenium.common.FileHelper;
import com.test.selenium.common.JLog;
import com.test.selenium.scplatform.resources.Config;
import com.test.selenium.scplatform.resources.Messages;
import com.test.selenium.scplatform.resources.Model;


public class ConstantsReader {

	public static String getText (String keyName)	{
		return getText(keyName, null);
	}
	
	public static String getText (String keyName, String defaultValue){
		String value = null;
		
		// determine if the factory is in the overridable properties
		Properties overrideable = loadOverridableProperties ();
		if (overrideable != null)	{
			if (overrideable.containsKey(keyName)){
				value = overrideable.getProperty(keyName, defaultValue);
			}
		}
		
		// if not a override, check the default factories.properties
		if (value == null){
			value = getBaseText(keyName);
		}
		
		if (value == null){
			value = defaultValue;
		} else if (value.contains("|"))	{
			value = processValue (value);
		}
		return value;
	}
	
	
	// =========================================
	// Read the properties files
	// =========================================
	private static ResourceBundle bundle = null;

	private static String getBaseText(String key) {
		try {
			if (bundle == null)	{
				bundle = ResourceBundle
						.getBundle("com.test.selenium.scplatform.constants.Constants");
			}
			if (bundle != null)	{
				if (bundle.containsKey(key))	{
					return bundle.getString(key); 
				}
				
			}
			
			if (property == null)	{
				File propertyFile = FileHelper.getResourceFile(ConstantsReader.class, "Constants.properties");
				
				property = new Properties();
				try {
					property.load(new FileInputStream(propertyFile));
				} catch (FileNotFoundException e) {
					property = null;
					JLog.warning(JLog.getStackTraceAsString(e));
				} catch (IOException e) {
					property = null;
					JLog.warning(JLog.getStackTraceAsString(e));
				}
				
				return property.getProperty(key);
			}

		} catch (Exception e) {
			// key doesn't exist
		}
		return null;
	}
	
	private static Properties property = null;
	private static Properties overridableProperties = null;
	private static String overrideProperties_file = null;
	
	public static void registerOveridePropertiesFile (String fileName){
		overrideProperties_file = fileName;
	}
	
	private static Properties loadOverridableProperties ()	{
		if (overridableProperties != null){
			return overridableProperties;
		}

		if (overrideProperties_file == null)	{
			return null;
		}
		
		File file = new File (overrideProperties_file);
		if (!file.exists()){
			return null;
		}
		
		overridableProperties = new Properties();
		try {
			overridableProperties.load(new FileInputStream(overrideProperties_file));
		} catch (FileNotFoundException e) {
			overridableProperties = null;
			JLog.warning(JLog.getStackTraceAsString(e));
		} catch (IOException e) {
			overridableProperties = null;
			JLog.warning(JLog.getStackTraceAsString(e));
		}
		
		return overridableProperties;
	}
	
	private static String processValue (String originalValue)	{
		String value = null;
		String[] parts = originalValue.split("\\|");
		
		String source = parts[0];
		String key = parts[1];
		String defaultValue = null;
		if (parts.length > 2){
			defaultValue = parts[2];
		}
		
		if (source.equals("Config"))	{
			value = Config.getText(key, defaultValue);
		} else if (source.equals("Messages"))	{
			value = Messages.getText(key, defaultValue);
		} else if (source.equals("Model"))	{
			value = Model.getText(key, defaultValue);
		} else if (source.equals("System"))	{
			value = com.test.selenium.scplatform.resources.System.getText(key, defaultValue);
		} else	{
			JLog.error("ConstantsReader#processValue(" + originalValue + ") : Unknown Source Type (" + source + ")!");
		}
		
		return value;
	}
	
}