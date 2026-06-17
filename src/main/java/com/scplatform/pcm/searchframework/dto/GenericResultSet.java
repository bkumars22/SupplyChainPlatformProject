/*
 * Created on Apr 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
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
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GenericResultSet
{

    private ArrayList<GenericResultRow> rows = new ArrayList<GenericResultRow>();
    private ArrayList<String> names;
    public GenericResultSet()
    {
    	
    }
    
    /**
     * Construct a result were the list contains the names of each column
     * @param names
     */
    public GenericResultSet(List<String> names)
    {
    	names = new ArrayList<String>(names);
    }

    public GenericResultSet(String[] names)
    {
    	this.names = new ArrayList<String>(Arrays.asList(names));
    }
    
    public List<String> getColumnNames()
    {
    	return names;
    }
    
    public int getColumnIndex(String name)
    {
    	return names.indexOf(name);
    }
    
    public void clear()
    {
        rows.clear();
        names.clear();
    }
        
    public void add(GenericResultRow row)
    {
        rows.add(row);
    }
    
    public List<GenericResultRow> getValues()
    {
        return rows;
    }
    
}
