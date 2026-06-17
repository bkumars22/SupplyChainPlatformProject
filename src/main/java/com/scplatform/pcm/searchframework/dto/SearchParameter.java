/*
 * Copyright (c) 2007 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2007, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.searchframework.dto;

import com.scplatform.pcm.searchframework.service.SearchParameterInitializer;

import java.util.Map;

/**
 * Interface defining a search parameter.   This is used to drive the search UI where each
 * parameter is represented by some field for the user to enter a value to refine a search
 * 
 * The interface also provides a way to bind the parameter to a particular Criteria Expression.
 *
 */
public interface SearchParameter
{
	public enum MatchType {EXACT,IEXACT,LIKE,ILIKE,GE,GT,LE,LT,IN};
	
	public abstract String getType();
	public abstract SearchParameter setName(String name);
	public abstract SearchParameter setOptionId(String optionId);
	public abstract String getName();
	public abstract String getOptionId();
	public abstract SearchParameter setLabelKey(String labelKey);
	public abstract String getLabelKey();
	public abstract SearchParameter setValue(Object value);
	public abstract Object getValue();
	public abstract Object getValueForSQL();
	public abstract boolean hasValue();
	public abstract SearchParameter setFinderName(String finderName);
	public abstract String getFinderName();
	public abstract SearchParameter setPopupFinderName(String popupFinderName);
	public abstract String getPopupFinderName();
	public abstract void setSearchExpression(SearchExpression criteria);
	public abstract SearchExpression getSearchExpression();
	public abstract void setDefaultExpressions(DefaultExpressions defaultExprs);
	public abstract DefaultExpressions getDefaultExpressions();
	public abstract boolean isValueArray();
	public abstract MatchType getMatchType();
	public abstract void setMatchType(MatchType type);
	public abstract boolean initialize(Map context);
	public abstract void setInitializer(SearchParameterInitializer initializer);
	public abstract void setDataType(String type);
	public abstract String getDataType();
	public abstract void setDataFormat(String type);
        public abstract String getDataFormat();
	public abstract void setRequired(boolean required);
	public abstract Map<String,String> getProperties();
	public abstract void setProperty(String propName, String propValue);
}