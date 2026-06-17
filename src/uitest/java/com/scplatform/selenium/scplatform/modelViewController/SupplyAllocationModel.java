/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import com.test.selenium.common.modelViewController.model.Model;

   /**
	     * @author Supply Chain Platform
	     *
	     */
	    public class SupplyAllocationModel extends Model {
	    	 private String groupName;
	    	 private String searchGroupName;
	    	 private String parentGroupName;

	    	
	    	    public String getGroupName() {
	    	        return groupName;
	    	    }

	    	    public void setGroupName(String groupName) {
	    	        this.groupName = groupName;
	    	    }
	    	    
	    	    public String getSearchGroupName(){
	    	    	return searchGroupName;
	    	    }
	    	    
	    	    public void setSearchGroupName(String searchGroupName){
	    	    	this.searchGroupName = searchGroupName;
	    	    }
	    	    
	    	    public String getParentGroupName(){
	    	    	return parentGroupName;
	    	    }
	    	    
	    	    public void setParentGroupName(String parentGroupName){
	    	    	this.parentGroupName = parentGroupName;
	    	    }
	    }
