/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.assignment.dto;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.scplatform.pcm.site.entity.Site;
import com.scplatform.common.web.taglib.UiMessages;
import com.scplatform.pcm.responsibility.entity.PcmResponsibility;
import com.scplatform.pcm.searchframework.dto.MdmManagementForm;

@SuppressWarnings("serial")
public class ItemManagementForm extends MdmManagementForm
{

	protected String assignmentUserId;
	protected Date assignmentStartDate;
	protected Date assignmentEndDate;
	protected String managedFlag;
	protected String assignmentResponsibility;
	protected List<PcmResponsibility> responsibilities = null;
	protected List<Site> siteList;
	protected String[] region;
	protected UiMessages lineMessages = new UiMessages();
	protected Boolean unassignPopup = false;
	protected List<String> alreadyAssignedUser = null;
	protected String alreadyAssignedResponsibility = null;
	protected List<String> item = null;
	protected List<String> selectedPageKeysToReAssign;
	
	
	public String getManagedFlag()
	{
		return managedFlag;
	}	
	
	public void setManagedFlag(String managedFlag)
	{
		this.managedFlag = managedFlag;
	}
	
	public String getAssignmentUserId()
	{
		return assignmentUserId;
	}

	public void setAssignmentUserId(String assignmentUserId)
	{
		this.assignmentUserId = assignmentUserId;
	}

	public Date getAssignmentStartDateAsDate()
	{
		return assignmentStartDate;
	}

	public void setAssignmentStartDate(String date)
	{
		try
		{
			assignmentStartDate = sdf.parse(date);
		}
		catch (ParseException e)
		{
			validationErrors.addKey("assignmentStartDate", "errors.date", null, date);
		}
	}

	public Date getAssignmentEndDateAsDate()
	{
		return assignmentEndDate;
	}

	public void setAssignmentEndDate(String date)
	{
		try
		{
			date = StringUtils.trimToNull(date);
			if (date != null)
			{
				assignmentEndDate = sdf.parse(date);
			}
			else
			{
				assignmentEndDate = null;
			}
		}
		catch (ParseException e)
		{
			validationErrors.addKey("assignmentEndDate", "errors.date", null, date);
		}

	}

	public String getAssignmentResponsibility()
	{
		return assignmentResponsibility;
	}

	public void setAssignmentResponsibility(String assignmentResponsibility)
	{
		this.assignmentResponsibility = assignmentResponsibility;
	}
	
	public void setResponsibilities(List<PcmResponsibility> responsibilities){
		this.responsibilities = new ArrayList<>(responsibilities);		
	}
		
	public List<PcmResponsibility> getResponsibilities()
	{
		return responsibilities;
	}
	
	@Override
	public void reset(HttpServletRequest request)
	{
		super.reset(request);
		managedFlag = null;
		assignmentUserId = null;
		assignmentResponsibility = null;
		siteList = null;
		region = null;
		unassignPopup = false;
	}
	
	public void clearLineMessages() {
		lineMessages.clear();
    }

	public UiMessages getLineMessages() {
        return lineMessages;
    }

	public List<String> getLineMessages(String key) {
		return new ArrayList<>(lineMessages.getMessages(key));
    }

	public void addLineMessage(String type, String key, String message) {
		lineMessages.add(type + "|" + key, message);
    }

	public String[] getRegion() {
		return region;
	}

	public void setRegion(String[] region) {
		this.region = region;
	}

	public List<Site> getSiteList() {
		return siteList;
	}

	public void setSiteList(List<Site> siteList) {
		this.siteList = siteList;
	}

	public Boolean getUnassignPopup() {
		return unassignPopup;
	}

	public void setUnassignPopup(Boolean unassignPopup) {
		this.unassignPopup = unassignPopup;
	}

	
	public List<String> getAlreadyAssignedUser() {
		return alreadyAssignedUser;
	}

	public void setAlreadyAssignedUser(List<String> alreadyAssignedUser) {
		this.alreadyAssignedUser = alreadyAssignedUser;
	}

	public String getAlreadyAssignedResponsibility() {
		return alreadyAssignedResponsibility;
	}

	public void setAlreadyAssignedResponsibility(String alreadyAssignedResponsibility) {
		this.alreadyAssignedResponsibility = alreadyAssignedResponsibility;
	}

	public List<String> getItem() {
		return item;
	}

	public void setItem(List<String> item) {
		this.item = item;
	}

	public List<String> getSelectedPageKeysToReAssign() {
		return selectedPageKeysToReAssign;
	}

	public void setSelectedPageKeysToReAssign(List<String> selectedPageKeysToReAssign) {
		this.selectedPageKeysToReAssign = selectedPageKeysToReAssign;
	}

	
	
	
	
	
}
