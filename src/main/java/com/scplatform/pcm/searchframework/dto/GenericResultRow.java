/*
 * Created on Apr 20, 2005
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * @author bblasko
 *
 */
public class GenericResultRow 
{
    private String key;
    private ArrayList<Object> values;
    
    public GenericResultRow()
    {
        values = new ArrayList<Object>();
    }

    public GenericResultRow(Object value)
    {
    	if (value instanceof Object[])
    	{
    		this.values = new ArrayList<Object>(Arrays.asList((Object[])value));
    	}
    	else
    	{
    		this.values = new ArrayList<Object>();
    		values.add(value);
    	}
    }
            
    /**
     * @param key The key to set.
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * @return Returns the key.
     */
    public String getKey()
    {
        return key;
    }

    public void addValue(Object value)
    {
        values.add(value);
    }

    public Object getObject(int idx)
    {
        try
        {
            return values.get(idx);            
        }
        catch(Exception e)
        {
            return "No value at " + idx;
        }

    }
    public String getValue(int idx, int length)
    {
        String v = getValue(idx);
        if (v.length() > length)
        {
            return v.substring(0,length)+"...";
        }
        return v;
    }
    
    
    public String getValue(int idx)
    {
        Object value = getObject(idx);
        return (value != null) ? value.toString():"";            
    }
    
    public List<Object> getValues()
    {
        return values;
    }
}
