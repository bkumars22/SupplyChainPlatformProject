/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.rebate.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "PCM_REBATE_RULE")
@SequenceGenerator(name = "pcmRebateRuleSeqGen", sequenceName = "PCM_REBATE_RULE_SEQ", allocationSize = 1)
public class PcmRebateRule implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pcmRebateRuleSeqGen")
	@Column(name = "REBATE_RULE_KEY")
	private Long rebateRuleKey;

	@Column(name = "REBATE_RULE_ID")
	private String rebateRuleId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REBATE_PROGRAM_KEY")
	private PcmRebateProgram rebateProgram;

	public Long getRebateRuleKey() {
		return rebateRuleKey;
	}

	public void setRebateRuleKey(Long rebateRuleKey) {
		this.rebateRuleKey = rebateRuleKey;
	}

	public String getRebateRuleId() {
		return rebateRuleId;
	}

	public void setRebateRuleId(String rebateRuleId) {
		this.rebateRuleId = rebateRuleId;
	}

	public PcmRebateProgram getRebateProgram() {
		return rebateProgram;
	}

	public void setRebateProgram(PcmRebateProgram rebateProgram) {
		this.rebateProgram = rebateProgram;
	}
}
