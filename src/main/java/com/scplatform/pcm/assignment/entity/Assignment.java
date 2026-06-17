/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
 package com.scplatform.pcm.assignment.entity;


import org.hibernate.type.YesNoConverter;
import jakarta.persistence.Convert;

import com.scplatform.pcm.common.entity.AuditRevisionBase;
import com.scplatform.pcm.common.entity.TrackChange;
import com.scplatform.pcm.site.entity.Site;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.scplatform.pcm.responsibility.entity.PcmResponsibility;
import com.scplatform.pcm.responsibility.entity.RegionalResponsibility;



@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="ASSIGNMENT_TYPE", discriminatorType=DiscriminatorType.STRING)
@Table(name="PCM_ASSIGNMENT")
public abstract class Assignment extends AuditRevisionBase
	implements TrackChange, Serializable
{
	private static final long serialVersionUID = -943076902470859512L;
	
	public static final String OWNER = "OWNER";
	public static final String BUYER = "BUYER";
	public static final String APPROVER = "APPROVER";
	
	@Id
	@SequenceGenerator(name="PCM_ASSIGNMENT_SEQ", sequenceName = "PCM_ASSIGNMENT_SEQ",allocationSize = 1)
	@GeneratedValue(generator = "PCM_ASSIGNMENT_SEQ")
	@Column(name="ASSIGNMENT_KEY")
	protected Long assignmentKey;
	
	@Column(name="USER_ID", length=60)
	protected String userId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="RESPONSIBILITY", nullable=false)
	protected PcmResponsibility responsibility;

	@Column(name="ASSIGNMENT_CODE", length=32)
	protected String assignmentCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SITE_KEY")
	protected Site site;
	
	@Column(name="EFFECTIVE_FROM_DT")
	protected Date effectiveFromDate;
	
	@Column(name="EFFECTIVE_TO_DT")
	protected Date effectiveToDate;

	@Convert(converter = YesNoConverter.class)
	@Column(name="CURRENT_FLAG", length=1)
	protected boolean currentFlag;
	
	@Column(name="REGION")
	protected String region;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="assignment")
	protected Set<RegionalResponsibility> regionalResponsibility;
	
	public Assignment()
	{
		super();
	}
	
	public Long getAssignmentKey()
	{
		return assignmentKey;
	}

	public void setAssignmentKey(Long assignmentKey)
	{
		this.assignmentKey = assignmentKey;
	}

	public PcmResponsibility getResponsibility()
	{
		return responsibility;
	}

	public void setResponsibility(PcmResponsibility responsibility)
	{
		this.responsibility = responsibility;
	}
	
	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getAssignmentCode()
	{
		return assignmentCode;
	}

	public void setAssignmentCode(String assignmentCode)
	{
		this.assignmentCode = assignmentCode;
	}

	public Site getSite()
	{
		return site;
	}

	public void setSite(Site site)
	{
		this.site = site;
	}

	public Date getEffectiveFromDate()
	{
		return effectiveFromDate;
	}

	public void setEffectiveFromDate(Date effectiveFromDate)
	{
		this.effectiveFromDate = effectiveFromDate;
	}

	public Date getEffectiveToDate()
	{
		return effectiveToDate;
	}

	public void setEffectiveToDate(Date effectiveToDate)
	{
		this.effectiveToDate = effectiveToDate;
	}
	
	public boolean getCurrentFlag()
	{
		return currentFlag;
	}
	
	public void setCurrentFlag(boolean currentFlag)
	{
		this.currentFlag = currentFlag;
	}

	public Set<RegionalResponsibility> getRegionalResponsibility() {
		return regionalResponsibility;
	}

	public void setRegionalResponsibility(
			Set<RegionalResponsibility> regionalResponsibility) {
		this.regionalResponsibility = regionalResponsibility;
	}
	
	public void addRegionalResponsibility(RegionalResponsibility regionalResponsibility){
		if(this.regionalResponsibility==null){
			this.regionalResponsibility = new HashSet<RegionalResponsibility>();
		}
		this.regionalResponsibility.add(regionalResponsibility);
	}
	
	public void removeRegionalResponsibility(RegionalResponsibility regionalResponsibility){
		if(this.regionalResponsibility!=null){
			this.regionalResponsibility.remove(regionalResponsibility);
		}
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
	
}
