/*
 * @AdvancedAdminModel.java@
 * Created on Aug 3, 2017
 *
 * Copyright (c) 2017 E2open, Inc.
 * All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 */
/**
 * 
 */
package com.test.selenium.scplatform.modelViewController;

import com.test.selenium.common.modelViewController.model.Model;

/**
 * @author AParameswaran
 *
 */
public class FunctionalGroupModel extends Model {
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
