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

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Defines a query
 * @author bblasko
 *
 */
public class SearchDefinition
{
	public static enum Order {ASC,DESC,NOTSET};	
	
	protected String name;
	protected Map<String, SearchExpression> expressions = new LinkedHashMap<String,SearchExpression>();
	protected Map<String,Order> orderBySettings = new LinkedHashMap<String,Order>();
	protected Map<String,String> orderByColumnMappings = new HashMap<String,String>();
	protected long[] keyColumns;
	protected long startDisplayColumn = 0;
	protected String source;
	protected String groupBy;
	protected String sourceTransform;	
	
	protected String extractSource;
	protected String extractRowWriterClass;
	protected String extractRowWriterProp;	
	protected String extractRowWriterTransform;
	protected String extractTemplateConfig;
	protected String extractTemplate;
	protected String extractType;
	protected String commodityProfileFilter;
	protected String sourceQueryType;
	protected String extractQueryType;

	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	public void setSource(String source)
	{
		this.source = source;
	}
	
	public String getSource()
	{
		return source;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public String getSourceTransform()
	{
		return sourceTransform;
	}

	public void setSourceTransform(String sourceTransform)
	{
		this.sourceTransform = sourceTransform;
	}

	
	public Set<String> getOrderByFields()
	{
		return orderBySettings.keySet();
	}

	public Map<String,Order> getOrderByMap()
	{
		return orderBySettings;
	}
	
	public Map<String,String> getOrderColumnMap()
	{
		return orderByColumnMappings;
	}
	
	public void addOrderBy(String fieldName, String columnName)
	{
		orderBySettings.put(fieldName, Order.NOTSET);
		if (columnName != null)
		{
			orderByColumnMappings.put(columnName,fieldName);
		}
	}
	
	public void setOrderBy(String fieldName, String order)
	{
		if (Order.ASC.name().equalsIgnoreCase(order))
		{
			setOrderBy(fieldName, Order.ASC);
		}
		else if (Order.DESC.name().equalsIgnoreCase(order))
		{
			setOrderBy(fieldName, Order.DESC);
		}
		else
		{
			setOrderBy(fieldName, Order.NOTSET);
		}
	}
	public void setOrderBy(String fieldName, Order initialOrder)
	{
		orderBySettings.put(fieldName, initialOrder);
	}
	
	public Order getOrderBy(String fieldName)
	{
		return orderBySettings.get(fieldName);
	}
	
	public SearchExpression addExpression(String name, String expressionValue)
	{
		SearchExpression se = new SearchExpression(expressionValue);
		expressions.put(name, se);
		return se;
	}
	
	public Map<String,SearchExpression>getExpressions()
	{
		return expressions;
	}
 
	public long[] getKeyColumns()
	{
		return keyColumns;
	}
	
	public void setKeyColumns(long[] keys)
	{
		keyColumns = keys;
	}
	
	public void setKeys(String value)
	{
		value = StringUtils.trimToNull(value);
		if (value != null)
		{
			String[] keys = value.split(",");			
			keyColumns = new long[keys.length];
			for (int idx=0; idx < keys.length; idx++)
			{
				keyColumns[idx] = Long.valueOf(keys[idx]);
			}			
		}		
	}

	public long getStartDisplayColumn()
	{
		return startDisplayColumn;
	}

	public void setStartDisplayColumn(long startDisplayColumn)
	{
		this.startDisplayColumn = startDisplayColumn;
	}

	public String getExtractSource()
	{
		return extractSource;
	}
	
	public void setExtractSource(String extractSource)
	{
		this.extractSource = extractSource;
		
	}

	public void setExtractWriterClass(String className)
	{
		this.extractRowWriterClass = className;		
	}
	
	public String getExtractWriterClass()
	{
		return this.extractRowWriterClass;
	}
	
	public String getExtractWriterProp()
	{
		return extractRowWriterProp;
	}

	public void setExtractWriterProp(String prop)
	{
		this.extractRowWriterProp = prop;
	}

	public String getExtractWriterTransform()
	{
		return extractRowWriterTransform;
	}

	public void setExtractWriterTransform(String transformDefintion)
	{
		this.extractRowWriterTransform = transformDefintion;
	}

        public String getExtractTemplateConfig() {
                return extractTemplateConfig;
        }
    
        public void setExtractTemplateConfig(String extractTemplateConfig) {
                this.extractTemplateConfig = extractTemplateConfig;
        }

        public String getExtractTemplate() {
                return extractTemplate;
        }
    
        public void setExtractTemplate(String extractTemplate) {
                this.extractTemplate = extractTemplate;
        }

        /**
         * @return the extractType
         */
        public String getExtractType() {
            return extractType;
        }

        /**
         * @param extractType the extractType to set
         */
        public void setExtractType(String extractType) {
            this.extractType = extractType;
        }

		public String getCommodityProfileFilter() {
			return commodityProfileFilter;
		}

		public void setCommodityProfileFilter(String commodityProfileFilter) {
			this.commodityProfileFilter = commodityProfileFilter;
		}

		public String getSourceQueryType() {
			return sourceQueryType;
		}

		public void setSourceQueryType(String sourceQueryType) {
			this.sourceQueryType = sourceQueryType;
		}

		public String getExtractQueryType() {
			return extractQueryType;
		}

		public void setExtractQueryType(String extractQueryType) {
			this.extractQueryType = extractQueryType;
		}
}

