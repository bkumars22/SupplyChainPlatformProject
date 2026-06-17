/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.service;

public class SearchParameterDate extends SearchParameterText
{
	public SearchParameterDate(String name, String labelKey)
	{
		super(name, labelKey);
	}
	
	public String getType()
	{
		return "DATE";
	}	
}
