/*
 * Copyright (c) 2007 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2007, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.searchframework.service;

import com.scplatform.pcm.searchframework.dto.DefaultExpressions;
import com.scplatform.pcm.searchframework.dto.SearchExpression;
import com.scplatform.pcm.searchframework.dto.SearchParameter;

import java.util.LinkedHashMap;
import java.util.Map;


public class SearchParameterText implements SearchParameter
{
	protected String name;
	protected String optionId;
	protected String labelKey;
	protected Object value;
	protected String finderName;
	protected String popupFinderName;
	protected MatchType matchType;
	protected SearchExpression searchExpression;
	protected DefaultExpressions defaultExpressions;
	protected SearchParameterInitializer initializer;
	protected String dataType;
	protected String dataFormat;
	protected boolean required;
	protected Map<String,String> properties = new LinkedHashMap<String,String>();
	public SearchParameterText(String name, String labelKey)
	{
		this.matchType = MatchType.EXACT;
		this.name = name;
		this.labelKey = labelKey;
		this.value = null;
		this.setFinderName(null);
	}
	
	/* (non-Javadoc)
	 * @see com.scplatform.pcm.web.action.search.SearchFilter#getType()
	 */
	public String getType()
	{
		return "TEXT";
	}
	
	/* (non-Javadoc)
	 * @see com.scplatform.pcm.web.action.search.SearchFilter#setName(java.lang.String)
	 */
	public SearchParameter setName(String name)
	{
		this.name = name;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.scplatform.pcm.web.action.search.SearchFilter#setOptionId(java.lang.String)
	 */
	public SearchParameter setOptionId(String optionId)
	{
		this.optionId = optionId;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.scplatform.pcm.web.action.search.SearchFilter#getName()
	 */
	public String getName()
	{
		return name;
	}
	
	/* (non-Javadoc)
	 * @see com.scplatform.pcm.web.action.search.SearchFilter#getOptionId()
	 */
	public String getOptionId()
	{
		return optionId;
	}
	
	/* (non-Javadoc)
	 * @see com.scplatform.pcm.web.action.search.SearchFilter#setLabelKey(java.lang.String)
	 */
	public SearchParameter setLabelKey(String labelKey)
	{
		this.labelKey = labelKey;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.scplatform.pcm.web.action.search.SearchFilter#getLabelKey()
	 */
	public String getLabelKey()
	{
		return (labelKey == null) ? name : labelKey;
	}
	
	/* (non-Javadoc)
	 * @see com.scplatform.pcm.web.action.search.SearchFilter#setValue(java.lang.Object)
	 */
	public SearchParameter setValue(Object value)
	{
		this.value = value;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.scplatform.pcm.web.action.search.SearchFilter#getValue()
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Default is to simply return the raw value
	 */			
	public Object getValueForSQL()
	{
		return getValue();
	}
	
	/* (non-Javadoc)
	 * @see com.scplatform.pcm.web.action.search.SearchFilter#setFinderName(java.lang.String)
	 */
	public SearchParameter setFinderName(String finderName)
	{
		this.finderName = finderName;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.scplatform.pcm.web.action.search.SearchFilter#getFinderName()
	 */
	public String getFinderName()
	{
		return finderName;
	}
	
	/* (non-Javadoc)
	 * @see com.scplatform.pcm.web.action.search.SearchFilter#setPopupFinderName(java.lang.String)
	 */
	public SearchParameter setPopupFinderName(String popupFinderName)
	{
		this.popupFinderName = popupFinderName;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see com.scplatform.pcm.web.action.search.SearchFilter#getPopupFinderName()
	 */
	public String getPopupFinderName()
	{
		return popupFinderName;
	}

	public void setSearchExpression(SearchExpression criteria)
	{
		this.searchExpression = criteria;
	}

	public SearchExpression getSearchExpression()
	{
		return searchExpression;
	}

	public DefaultExpressions getDefaultExpressions() {
		return defaultExpressions;
	}

	public void setDefaultExpressions(DefaultExpressions defaultExpressions) {
		this.defaultExpressions = defaultExpressions;
	}

	public boolean hasValue()
	{
		return !isValueEmpty(value);
	}
	
	protected boolean isValueEmpty(Object dataValue)
    {
        if (dataValue == null)
        {
            return true;
        }
        if (dataValue instanceof Object[])
        {
            if (((Object[]) dataValue).length == 0)
            {
                return true;
            }
            else if (((Object[]) dataValue).length == 1)
            {
                if (((Object[]) dataValue)[0] instanceof String)
                {
                    return ((String)((Object[]) dataValue)[0]).length() == 0;
                }
            }            
        } 
        else if (dataValue instanceof String)
        {
            return ((String) dataValue).length() == 0;
        }
        return false;
    }

	public boolean isValueArray()
	{
		return false;
	}

	public MatchType getMatchType()
	{
		return matchType;
	}

	public void setMatchType(MatchType type)
	{
		this.matchType = type;		
	}

	public void setInitializer(SearchParameterInitializer initializer)
	{
		this.initializer = initializer; 
		
	}

	public boolean initialize(Map context)
	{
		if (initializer != null)
		{
			return initializer.initializeParameter(this,context);
		}
		return false;
	}

	public void setDataType(String type)
	{
		this.dataType = type;
		
	}

	public String getDataType()
	{
		return dataType;
	}
	
	public void setDataFormat(String format)
        {
                this.dataFormat = format;
        }

        public String getDataFormat()
        {
                return dataFormat;
        }

	public void setRequired(boolean required)
	{
		this.required = required;
	}
	
	public boolean getRequired()
	{
		return required;
	}

	public Map<String, String> getProperties()
	{
		return properties;
	}

	public void setProperty(String propName, String propValue)
	{
		properties.put(propName, propValue);		
	}
}
