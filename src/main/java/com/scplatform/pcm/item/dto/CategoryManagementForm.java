/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.item.dto;

import java.text.ParseException;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;

import com.scplatform.pcm.searchframework.dto.MdmManagementForm;

@SuppressWarnings("serial")
public class CategoryManagementForm extends MdmManagementForm {
	protected String assignmentUserId;
	protected Date assignmentStartDate;
	protected Date assignmentEndDate;
	protected String managedFlag;

	public String getManagedFlag() {
		return managedFlag;
	}

	public void setManagedFlag(String managedFlag) {
		this.managedFlag = managedFlag;
	}

	public String getAssignmentUserId() {
		return assignmentUserId;
	}

	public void setAssignmentUserId(String assignmentUserId) {
		this.assignmentUserId = assignmentUserId;
	}

	public Date getAssignmentStartDateAsDate() {
		return assignmentStartDate;
	}

	public void setAssignmentStartDate(String date) {
		try {
			assignmentStartDate = sdf.parse(date);
		} catch (ParseException e) {
			validationErrors.addKey("assignmentStartDate", "errors.date", null, date);
		}
	}

	public Date getAssignmentEndDateAsDate() {
		return assignmentEndDate;
	}

	public void setAssignmentEndDate(String date) {
		try {
			assignmentEndDate = sdf.parse(date);
		} catch (ParseException e) {
			validationErrors.addKey("assignmentEndDate", "errors.date", null, date);
		}

	}

	public void reset(HttpServletRequest request) {
		super.reset(request);
		managedFlag = null;
		assignmentUserId = null;
	}

}
