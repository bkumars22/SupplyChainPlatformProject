/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.allocationAudit.entity;


import java.sql.Timestamp;
import java.util.Date;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "FG_AUDIT_HISTORY")
@DiscriminatorColumn(name = "AUDIT_SOURCE", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class AllocationAuditHistory {

	public static final String ACTIONUPLOAD_CREATE = "UPLOAD_CREATE";
	public static final String ACTIONUPLOAD_UPDATE = "UPLOAD_UPDATE";
	public static final String ACTIONUPLOAD_DELETE = "UPLOAD_DELETE";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fgAuditKeySeqGen")
	@SequenceGenerator(name = "fgAuditKeySeqGen", sequenceName = "FG_AUDIT_KEY_SEQ", allocationSize = 1)
	private Long auditKey;

	@Column(name = "DATE_PERFORMED")
	protected Timestamp datePerformed;

	@Column(name = "USER_ID", nullable = false)
	protected String userId;

	@Column(name = "USER_ROLE")
	protected String userRole;

	@Column(name = "ACTION")
	protected String actionPerformed;

	@Column(name = "OPERATION_CODE")
	protected String operationCode;

	@ManyToOne
	@JoinColumn(name = "ITEM_NUMBER")
	protected Item item;

	@ManyToOne
	@JoinColumn(name = "SUPPLIER")
	protected BusinessEntity supplier;

	@ManyToOne
	@JoinColumn(name = "SITE")
	protected Site site;

	@Column(name = "BUCKET_START_DATE")
	protected Date bucketStartDate;

	@Column(name = "BUCKET_END_DATE")
	protected Date bucketEndDate;

	@Column(name = "COMMENTS")
	protected String comment;

	@Column(name = "AUDIT_SOURCE", insertable = false, updatable = false)
	private String source;

	@ManyToOne
	@JoinColumn(name = "FG_KEY", unique = true)
	protected FunctionalGroup functionalGroup;
	
	public AllocationAuditHistory() {
		
	}
	
	public AllocationAuditHistory(String userId, String userRole,
			String actionPerformed, String operationCode, FunctionalGroup functionalGroup, String comment, Timestamp timeStamp) {
		this.userId = userId;
		this.userRole = userRole;
		this.actionPerformed = actionPerformed;
		this.operationCode = operationCode;
		this.functionalGroup = functionalGroup;
		this.comment = comment;
		this.datePerformed = timeStamp;
	}
	
	public AllocationAuditHistory(String userId, String userRole,
			String actionPerformed, String operationCode, FunctionalGroup functionalGroup, Item item, String comment, Timestamp timeStamp) {
		this.userId = userId;
		this.userRole = userRole;
		this.actionPerformed = actionPerformed;
		this.operationCode = operationCode;
		this.functionalGroup = functionalGroup;
		this.item = item;
		this.comment = comment;
		this.datePerformed = timeStamp;
	}
	
	public Long getAuditKey() {
		return auditKey;
	}

	public void setAuditKey(Long auditKey) {
		this.auditKey = auditKey;
	}
	public Timestamp getDatePerformed() {
		return datePerformed;
	}
	public void setDatePerformed(Timestamp datePerformed) {
		this.datePerformed = datePerformed;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	public String getActionPerformed() {
		return actionPerformed;
	}
	public void setActionPerformed(String actionPerformed) {
		this.actionPerformed = actionPerformed;
	}
	public String getOperationCode() {
		return operationCode;
	}
	public void setOperationCode(String operationCode) {
		this.operationCode = operationCode;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public BusinessEntity getSupplier() {
		return supplier;
	}

	public void setSupplier(BusinessEntity supplier) {
		this.supplier = supplier;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}
	
	public Date getBucketStartDate() {
		return bucketStartDate;
	}

	public void setBucketStartDate(Date bucketStartDate) {
		this.bucketStartDate = bucketStartDate;
	}

	public Date getBucketEndDate() {
		return bucketEndDate;
	}

	public void setBucketEndDate(Date bucketEndDate) {
		this.bucketEndDate = bucketEndDate;
	}

	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	public FunctionalGroup getFunctionalGroup() {
		return functionalGroup;
	}

	public void setFunctionalGroup(FunctionalGroup functionalGroup) {
		this.functionalGroup = functionalGroup;
	}
	
}
