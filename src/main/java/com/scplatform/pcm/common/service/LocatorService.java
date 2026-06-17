/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */

package com.scplatform.pcm.common.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.scplatform.pcm.config.util.PcmConfigUtil;

import lombok.AllArgsConstructor;

/**
 * Provides services to locate a resource based on a list of areas to search.
 * @author bblasko
 *
 */
@Service
@AllArgsConstructor
public class LocatorService
{
	private final PcmConfigUtil pcmConfigUtil;

	protected final static Logger logger = LogManager.getLogger(LocatorService.class);

	// Locate by URL, then
	public URL locateResource(String resource, boolean searchClassPath)
	{
		return locateResource(resource,Arrays.asList(""),searchClassPath);
	}
	
	/**
	 * Locate a resource using a search dirs property. If the resource is not found null is returned
	 * 
	 * @param resource
	 * @param searchDirsProperty
	 * @return The resource URL
	 */
	public URL locateResource(String resource,String searchDirsProperty,boolean searchClassPath) {
	    if (StringUtils.isBlank(resource) || StringUtils.isBlank(searchDirsProperty)) {
	        return null;
	    }
	    
	    List<String> configDirs = pcmConfigUtil.getList(searchDirsProperty);
	    if (configDirs == null || configDirs.isEmpty()) {
	        return null;
	    }
	    
	    return locateResource(resource, configDirs, searchClassPath);
	}
	
	public URL locateResource(String resource, List<String> searchPaths, boolean searchClassPath)
	{
		URL url = null;
		if (searchPaths != null )
		{
			for (String path: searchPaths)
			{				
				if (logger.isDebugEnabled())
				{
					logger.debug("Looking for for:'" + resource + "' path:'" + path + "'");
				}
				url = isFile(resource,path);
				
				if (url == null)
				{
					url = isURL(resource,path);	
				}
				if (url != null)
				{
					break;
				}
			}
		}
		// Try as just a resource on the class path, ignoring any searchPaths
		if (url == null && searchClassPath)
		{
			url = isResource(resource);
		}
		if (logger.isDebugEnabled())
		{
			if (url != null)
			{
				logger.debug("Located resource:'" + resource + "' path:'" + url);
			}
			else
			{
				logger.debug("Failed to locate resource:" + resource + "' using paths:'" + searchPaths);
			}
		}
		return url;
	}
	
	protected URL isURL(String resource, String baseURL)
	{
		URL url = null;
		InputStream is = null;
		try
		{
			if (StringUtils.isNotEmpty(baseURL))
			{
				if (baseURL.endsWith("/") == false)
				{
					baseURL += "/";
				}
				URL base = new URL(baseURL);
				url = new URL(base, resource);
			}
			else
			{
				url = new URL(resource);
			}
			is = url.openStream();
			if (logger.isDebugEnabled())
			{
				logger.debug("   Located as URL: " + url);
			}			
		}
		catch (Exception e)
		{
			// Not a real URL, return nothing			
			if (logger.isDebugEnabled())
			{
				logger.debug("   isURL failed for:'" + resource + "' path:'" + baseURL + "' exception:" + e);
			}
			// Incase the open failed
			url = null;
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
		return url;
	}

	protected URL isFile(String resource, String baseDir)
	{
		URL url = null;
		try
		{
			File file = null;
			String filePath = resource;
			
			if (StringUtils.isNotEmpty(baseDir))
			{ 
				filePath = FilenameUtils.concat(baseDir, resource);
			}
			
			// Try as absolute or relative file path first
			file = new File(filePath);
			if (file.exists())
			{
				url = file.toURI().toURL();
				if (logger.isDebugEnabled())
				{
					logger.debug("  Located as File: " + file.getAbsolutePath());
				}
				return url;
			}
			
			// If file not found, try from classpath as fallback
			String classPathResource = StringUtils.isNotEmpty(baseDir) 
				? FilenameUtils.separatorsToUnix(filePath) 
				: resource;
			
			if (logger.isDebugEnabled())
			{
				logger.debug("  File not found at: " + file.getAbsolutePath() + ", trying classpath: " + classPathResource);
			}
			
			URL classPathUrl = Thread.currentThread().getContextClassLoader().getResource(classPathResource);
			if (classPathUrl != null) {
				if (logger.isDebugEnabled())
				{
					logger.debug("  Located as Classpath Resource: " + classPathUrl);
				}
				return classPathUrl;
			}
		}
		catch (Exception e)
		{
			// Not a file
			if (logger.isDebugEnabled())
			{
				logger.debug("isFile failed for:'" + resource + "' path:'" + baseDir + "' exception:" + e);
			}			
		}
		return null;
	}
	
	protected URL isResource(String resource)
	{
		URL url = null;
		InputStream is = null;
		try
		{
			url = Thread.currentThread().getContextClassLoader().getResource(resource);
			if (url == null)
			{
				throw new Exception("Resource not found");
			}
			is = url.openStream();
			if (logger.isDebugEnabled())
			{
				logger.debug("  located as Classpath Resource: " + url);
			}
			
		}
		catch (Exception e)
		{
			// Not a resource
			if (logger.isDebugEnabled())
			{
				logger.debug("isResource failed for: '" + resource + "' exception:" + e);
			}		
			// Incase the open failed
			url = null;
			
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
		
		return url;
	}
}
