/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.service;

import com.scplatform.pcm.searchframework.dto.SearchParameter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class SearchParameterSelect extends SearchParameterText
{
	protected Map<String,String> selectValues = new LinkedHashMap<String,String>();
	protected boolean multiSelect = false;
	protected boolean multiValue = false;
	public SearchParameterSelect(String name, String labelKey)
	{
		super(name, labelKey);
	}
	
	
	public SearchParameter setMultiSelect(boolean multiSelect)
	{
		this.multiSelect = multiSelect;
		return this;
	}
	
	public boolean getMultiSelect()
	{
		return multiSelect;
	}

	public SearchParameter setMultiValue(boolean multiValue)
	{
		this.multiValue = multiValue;
		return this;
	}

	public boolean getMultiValue()
	{
		return multiValue;
	}
	
	public String getType()
	{
		return (multiSelect) ?"MULTISELECT":"SINGLESELECT";
	}
	
	public Map getSelectValues()
	{
		return selectValues;
	}

	public Set getSelectValueEntries()
	{
		return selectValues.entrySet();
	}
	
	public SearchParameter addSelectValue(String label, String value)
	{
		selectValues.put(value, label);
		return this;
	}
	
	public boolean isValueArray()
	{
		return multiSelect;
	} 
	
}
