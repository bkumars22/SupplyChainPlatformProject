/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.rebate.entity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.StatefulBase;
import com.scplatform.pcm.common.entity.TrackDelta;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.util.common.SCPlatformConstant;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.scplatform.pcm.util.stateMachine.StateMachineReactor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import static com.scplatform.pcm.util.common.SCPlatformConstant.DEFAULT_DATE_FORMAT;

/**
 * Rebate program domain entity.
 */
@NamedQueries({
    @NamedQuery(
        name = "dashboard:rebateProgram",
        query = "SELECT COUNT(*), rp.status FROM PcmRebateProgram rp " +
                "WHERE rp.status IN (:status) " +
                "AND COALESCE(rp.updateDate, rp.insertDate) >= :cutoffDate " +
                "GROUP BY rp.status ORDER BY 2"
    ),
    @NamedQuery(
        name = "dashboard:rebateProgramForOwner",
        query = "SELECT COUNT(*), rp.status FROM PcmRebateProgram rp " +
                "WHERE rp.status IN (:status) " +
                "AND COALESCE(rp.updateDate, rp.insertDate) >= :cutoffDate " +
                "AND (LOWER(rp.programOwner) = LOWER(:userId) OR LOWER(rp.financialProgramOwner) = LOWER(:userId)) " +
                "GROUP BY rp.status ORDER BY 2"
    )
})
@Entity
@Table(name = "PCM_REBATE_PROGRAM")
@SequenceGenerator(name = "pcmRebateProgramSeqGen", sequenceName = "PCM_REBATE_PROGRAM_SEQ", allocationSize = 1)
public class PcmRebateProgram extends StatefulBase implements TrackDelta {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pcmRebateProgramSeqGen")
	@Column(name = "REBATE_PROGRAM_KEY")
	private Long rebateProgramKey;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "REBATE_BUSINESS_ENTITY_KEY")
	private BusinessEntity businessEntity;

	@Column(name = "REBATE_NAME")
	private String rebateName;

	@Column(name = "REBATE_TYPE")
	private String rebateType;

	@Column(name = "REBATE_EXTERNAL_ID")
	private String rebateExternalId;

	@Column(name = "CONFIDENCE_FACTOR")
	private BigDecimal confidenceFactor;

	@Column(name = "PROGRAM_OWNER")
	private String programOwner;

	@Column(name = "FINANCIAL_PROGRAM_OWNER")
	private String financialProgramOwner;

	@Column(name = "PAYMENT_TYPE")
	private String paymentType;

	@Column(name = "PROGRAM_TYPE")
	private String programType;

	@Column(name = "INSERT_DT")
	
	private Date insertDate = new Date();

	@Column(name = "UPDATE_DT")
	
	private Date updateDate;

	@Column(name = "DELETE_FLAG")
	private Boolean deleteFlag = Boolean.FALSE;

	@Column(name = "CURRENT_FLAG")
	private boolean currentFlag = true;

	@Column(name = "EFFECTIVE_FROM_DT")
	
	private Date effectiveFromDt;

	@Column(name = "EFFECTIVE_TO_DT")
	
	private Date effectiveToDt;

	@OneToMany(mappedBy = "rebateProgram", fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<PcmRebateItem> rebateItems = new HashSet<>();

	@OneToMany(mappedBy = "rebateProgram", fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<PcmRebateRule> rebateRules = new HashSet<>();

	public PcmRebateProgram() {
	}

	public PcmRebateProgram(Long rebateProgramKey, BusinessEntity businessEntity,
			String rebateName, String rebateType, String status, Date insertDate, boolean currentFlag) {
		this.rebateProgramKey = rebateProgramKey;
		this.businessEntity = businessEntity;
		this.rebateName = rebateName;
		this.rebateType = rebateType;
		this.status = status;
		this.insertDate = insertDate;
		this.currentFlag = currentFlag;
	}

	public PcmRebateProgram(Long rebateProgramKey, BusinessEntity businessEntity,
			String rebateName, String rebateType, String rebateExternalId,
			BigDecimal confidenceFactor, String programOwner, String financialProgramOwner,
			String paymentType, String programType, String status, Date statusChangeDate,
			String statusLastChangeBy, Date insertDate, Date updateDate, Date effectiveFromDt,
			Date effectiveToDt, Boolean deleteFlag, boolean currentFlag, Long auditRev,
			Date lastRevChangeDate) {
		this.rebateProgramKey = rebateProgramKey;
		this.businessEntity = businessEntity;
		this.rebateName = rebateName;
		this.rebateType = rebateType;
		this.rebateExternalId = rebateExternalId;
		this.confidenceFactor = confidenceFactor;
		this.programOwner = programOwner;
		this.financialProgramOwner = financialProgramOwner;
		this.paymentType = paymentType;
		this.programType = programType;
		this.status = status;
		this.statusChangeDate = statusChangeDate;
		this.statusLastChangeBy = statusLastChangeBy;
		this.insertDate = insertDate;
		this.updateDate = updateDate;
		this.effectiveFromDt = effectiveFromDt;
		this.effectiveToDt = effectiveToDt;
		this.deleteFlag = deleteFlag;
		this.currentFlag = currentFlag;
		this.auditRev = auditRev;
		this.lastRevChangeDate = lastRevChangeDate;
	}

	public Long getRebateProgramKey() {
		return this.rebateProgramKey;
	}

	public void setRebateProgramKey(Long rebateProgramKey) {
		this.rebateProgramKey = rebateProgramKey;
	}

	public BusinessEntity getBusinessEntity() {
		return this.businessEntity;
	}

	public void setBusinessEntity(BusinessEntity businessEntity) {
		this.businessEntity = businessEntity;
	}

	public String getRebateName() {
		return this.rebateName;
	}

	public void setRebateName(String rebateName) {
		this.rebateName = rebateName;
	}

	public String getRebateType() {
		return this.rebateType;
	}

	public void setRebateType(String rebateType) {
		this.rebateType = rebateType;
	}

	public String getRebateExternalId() {
		return this.rebateExternalId;
	}

	public void setRebateExternalId(String rebateExternalId) {
		this.rebateExternalId = rebateExternalId;
	}

	public BigDecimal getConfidenceFactor() {
		return this.confidenceFactor;
	}

	public void setConfidenceFactor(BigDecimal confidenceFactor) {
		this.confidenceFactor = confidenceFactor;
	}

	public String getProgramOwner() {
		return this.programOwner;
	}

	public void setProgramOwner(String programOwner) {
		this.programOwner = programOwner;
	}

	public String getFinancialProgramOwner() {
		return this.financialProgramOwner;
	}

	public void setFinancialProgramOwner(String financialProgramOwner) {
		this.financialProgramOwner = financialProgramOwner;
	}

	public String getPaymentType() {
		return this.paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getProgramType() {
		return this.programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	@Override
	public Date getInsertDate() {
		return this.insertDate;
	}

	@Override
	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	@Override
	public Date getUpdateDate() {
		return this.updateDate;
	}

	@Override
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getEffectiveFromDt() {
		return this.effectiveFromDt;
	}

	public void setEffectiveFromDt(Date effectiveFromDt) {
		this.effectiveFromDt = effectiveFromDt;
	}

	public Date getEffectiveToDt() {
		return this.effectiveToDt;
	}

	public void setEffectiveToDt(Date effectiveToDt) {
		this.effectiveToDt = effectiveToDt;
	}

	@Override
	public Boolean getDeleteFlag() {
		return this.deleteFlag;
	}

	@Override
	public void setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public boolean getCurrentFlag() {
		return this.currentFlag;
	}

	public void setCurrentFlag(boolean currentFlag) {
		this.currentFlag = currentFlag;
	}

	public Set<Item> getRebateItemSet() {
		Set<Item> itemSet = new HashSet<>();
		for (PcmRebateItem pri : rebateItems) {
			itemSet.add(pri.getItem());
		}
		return itemSet;
	}

	public Set<PcmRebateItem> getRebateItems() {
		return rebateItems;
	}

	public void setRebateItems(Set<PcmRebateItem> rebateItems) {
		this.rebateItems = rebateItems;
	}

	public boolean addRebateItem(Item item, Date fromDate, Date toDate, BigDecimal amount) {
		PcmRebateItem pri = new PcmRebateItem();
		pri.setEffectiveFromDt(fromDate);
		pri.setEffectiveToDt(toDate);
		pri.setItem(item);
		pri.setRebateAmount(amount);
		return addRebateItem(pri);
	}

	public PcmRebateItem findRebateItem(Item item, Date fromDate, Date toDate) {
		PcmRebateItem match = new PcmRebateItem();
		match.setEffectiveFromDt(fromDate);
		match.setEffectiveToDt(toDate);
		match.setItem(item);
		match.setRebateProgram(this);
		return findRebateItem(match);
	}

	public PcmRebateItem findRebateItem(long key) {
		for (PcmRebateItem pri : rebateItems) {
			if (pri.getRebateItemKey() == key) {
				return pri;
			}
		}
		return null;
	}

	public PcmRebateItem findRebateItem(PcmRebateItem match) {
		for (PcmRebateItem pri : rebateItems) {
			if (pri.equals(match)) {
				return pri;
			}
		}
		return null;
	}

	public boolean addRebateItem(PcmRebateItem rebateItem) {
		rebateItem.setRebateProgram(this);
		return this.rebateItems.add(rebateItem);
	}

	public void removeRebateItem(PcmRebateItem i) {
		if (rebateItems.remove(i)) {
			i.setRebateProgram(null);
		}
	}

	public Set<PcmRebateRule> getRebateRules() {
		return rebateRules;
	}

	public void setRebateRules(Set<PcmRebateRule> rebateRules) {
		this.rebateRules = rebateRules;
	}

	public boolean addRebateRule(PcmRebateRule prr) {
		prr.setRebateProgram(this);
		return rebateRules.add(prr);
	}

	public void removeRebateRule(PcmRebateRule prr) {
		if (rebateRules.remove(prr)) {
			prr.setRebateProgram(null);
		}
	}

	public PcmRebateRule getRuleById(String id) {
		for (PcmRebateRule rule : rebateRules) {
			if (id.equals(rule.getRebateRuleId())) {
				return rule;
			}
		}
		return null;
	}

	public PcmRebateRule getRuleByKey(Long key) {
		for (PcmRebateRule rule : rebateRules) {
			if (key.equals(rule.getRebateRuleKey())) {
				return rule;
			}
		}
		return null;
	}

	@Override
	public Collection<StateMachineReactor> getChildren() {
		return null;
	}

	@Override
	public StateMachineReactor getParent() {
		return null;
	}

	public String getTitle() {
		String df = SCPlatformMessages.INSTANCE.getAuditMessage("audit.dateFormat", null, null);
		if (df == null) {
			df = SCPlatformConstant.DEFAULT_DATE_FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(df);
		List<Object> args = new ArrayList<>();
		args.add(rebateName);
		args.add(sdf.format(effectiveFromDt));
		if (this.effectiveToDt != null) {
			args.add(sdf.format(effectiveToDt));
		} else {
			args.add("EVERGREEN");
		}
		return SCPlatformMessages.INSTANCE.getAuditMessage("audit.rebate", args.toArray(), null);
	}
}
