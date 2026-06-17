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
package com.test.selenium.scplatform.modelViewController.CostRecords;

import com.test.selenium.common.modelViewController.model.Model;

/**
 * @author AParameswaran
 *
 */
public class CostRecordsModel extends Model {
	
	private String value;
	
	    public CostRecordsModel(String value) { this.value = value; }
	    public String getValue() { return value; }

	    private String errorMessage;

	    public void ErrorMessage(String errorMessage) {
	        this.errorMessage = errorMessage;
	    }

	    public String getErrorMessage() {
	        return errorMessage;
	    }
	 
	
}
