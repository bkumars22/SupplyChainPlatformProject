/**
 * 
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.entity;

import com.scplatform.pcm.cost.entity.PcmCostRecordException;
import com.scplatform.pcm.cost.entity.PcmCostType;
import com.scplatform.pcm.util.stateMachine.StateMachineReactor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.type.YesNoConverter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Models a cost exception record for pricing management.
 * 
 * created on Nov 18, 2020
 * Copyright (c) 2000-2020, by E2open LLC.
 * All rights reserved.
 */
@Entity
@Table(name="COST_EXCEPTION")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"costExceptionOdmCm", "costExceptionLOB", "costExceptionInfo", "costExceptionPricing", "costExceptionODMEmail", "pcmCostException", "exceptionApproval"})
public class CostException implements Serializable, StateMachineReactor {
	
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="COST_EXCEPTION_SEQ")
	@SequenceGenerator(name="COST_EXCEPTION_SEQ", sequenceName = "COST_EXCEPTION_SEQ", allocationSize = 1, initialValue = 1)
	@Column(name="EXCEPTION_KEY")
	private Long exceptionKey;
	
	@Column(name="EXCEPTION_ID")	
    private String exceptionId;
	
	@Column(name="EXCEPTION_NAME")
    private String exceptionName;
	
	@Column(name="EXCEPTION_REQUESTOR")
    private String exceptionRequestor;
	
	@Column(name="EXCEPTION_OWNER")
    private String exceptionOwner;
	
	@Column(name="EXCEPTION_APPROVER")
    private String exceptionApprover;
	
	@ManyToOne(optional = false)
	@JoinColumn(name="COST_TYPE_KEY")	
    private PcmCostType costType;
	
	@Column(name="REQUEST_TYPE")
    private String requestType;
	
	@Column(name="COMMODITY")
    private String commodity;
	
	@Convert(converter = YesNoConverter.class)
	@Column(name="SUBTIER", length=1)
    private Boolean subtier;
	
	@Column(name="PLATFORM_NAME")
    private String platformName;
	
	@Column(name="ODM_ACKNOWLEDGEMENT")
    private String odmAcknowledgement;
	
	@Column(name="PREVIOUS_STATE")
    private String previousState;
	
	@Column(name="STATE")
    private String state;
	
	@Column(name="CREATED_ON")
    private Timestamp createdOn;
	
	@Column(name="CREATED_BY")
    private String createdBy;
	
	@Column(name="APPROVED_ON")
    private Timestamp approvedOn;
	
	@Column(name="APPROVED_BY")
    private String approvedBy;
	
	@Column(name="REJECTED_ON")
    private Timestamp rejectedOn;
	
	@Column(name="REJECTED_BY")
    private String rejectedBy;
	
	@Column(name="CLOSED_ON")
    private Timestamp closedOn;
	
	@Column(name="CLOSED_BY")
    private String closedBy;

	@Column(name="REQUESTED_ON")
    private Timestamp requestedOn;
	
	@Column(name="LAST_CHANGED_ON")
    private Timestamp lastChangedOn;
	
	@Column(name="LAST_CHANGED_BY")
    private String lastChangedBy;
	
	@Column(name="RE_REQUESTED_ON", nullable = true)
	private Timestamp reRequestedOn;
	
	@Column(name="RE_REQUESTED_BY", nullable = true)
	private String reRequestedBy;
    
	@Column(name = "UPLOAD_TYPE")
	private String uploadType;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "exception")
	private Set<CostExceptionODMCM> costExceptionOdmCm;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "exception")
	private Set<CostExceptionLOB> costExceptionLOB;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "exception" )
	private Set<CostExceptionInfo> costExceptionInfo;
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "exception", fetch = FetchType.LAZY, optional = true)
	private CostExceptionPricing costExceptionPricing;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "exception")
	private Set<CostExceptionODMEmail> costExceptionODMEmail;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "costException")
	private Set<PcmCostRecordException> pcmCostException;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "exception")
	@OrderBy("actionDate ASC")
	private Set<CostExceptionApprover> exceptionApproval;
	
	@Transient
	private Boolean partiallyApprovedState = Boolean.FALSE;

	public Long getId() {
		return exceptionKey;
	}

	public void setId(Long exceptionKey) {
		this.exceptionKey = exceptionKey;
	}

	public void setCostExceptionInfo(Set<CostExceptionInfo> costExceptionInfo) {
		if (this.costExceptionInfo == null) {
			this.costExceptionInfo = new HashSet<>();
		}
		this.costExceptionInfo.clear();
		if (costExceptionInfo != null) {
			this.costExceptionInfo.addAll(costExceptionInfo);
		}
	}

	public void addCostExceptionInfo(Set<CostExceptionInfo> costExceptionInfos) {
		if (this.costExceptionInfo == null) {
			this.costExceptionInfo = new HashSet<>();
		}
		if (costExceptionInfos != null) {
			this.costExceptionInfo.addAll(costExceptionInfos);
		}
	}

	public Collection<StateMachineReactor> getChildren() {
		return List.of();
	}

	public StateMachineReactor getParent() {
		return null;
	}

	public Date getStatusChangeDate() {
		return lastChangedOn;
	}

	public void setStatusChangeDate(Date statusChangeDate) {
		this.lastChangedOn = statusChangeDate == null ? null : new Timestamp(statusChangeDate.getTime());
	}

	public String getStatusLastChangeBy() {
		return lastChangedBy;
	}

	public void setStatusLastChangeBy(String statusLastChangeBy) {
		this.lastChangedBy = statusLastChangeBy;
	}
} 
