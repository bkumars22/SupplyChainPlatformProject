/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.scplatform.pcm.launchpad.dto.About;

/**
 * 
 * @author averma
 *
 */
public class GetAboutInfo {

	private final static Logger logger = LogManager.getLogger(GetAboutInfo.class);
	
	
	public About about() throws MalformedURLException {
		BufferedReader in = null;
		About about = new About();
		try {
			String version = "";
			String build = "";
			String port = System.getProperty("tomcat.http.port");
			StringBuilder builder = new StringBuilder();
			builder.append("http://")
			.append("localhost")
			.append(":")
			.append(port)
			.append("/common/about_e2modern.jsp");		
			URL url = new URL(builder.toString());
			in = new BufferedReader(new InputStreamReader(url.openStream()));

			String line;
			while ((line = in.readLine()) != null) {
				if (line.contains("Version:")) {
					version = line.substring(line.indexOf("Version:"));
				}
				if (line.contains("Build:")) {
					build = line.substring(line.indexOf("Build:"));
				}
			}
			about.setVersion(version);
			about.setBuild(build);
			about.setLabel("Multi Tier Cost Manager");
			System.out.println(version);
			System.out.println(build);
			logger.error("Version-" + version);
			logger.error("Build-" + build);
			
		} catch (Exception e) {
			logger.error("Unable to get About Info", e);
		} 
		finally {
			try {
				if (in != null) {
					in.close();
				}
				
			} catch (IOException e) {
				logger.error("Unable to close stream", e);
			}
		}
		return about;
		
	}
	
}
