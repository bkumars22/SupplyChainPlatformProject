/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.rebate.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.util.message.SCPlatformMessages;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import static com.scplatform.pcm.util.common.SCPlatformConstant.*;


@Entity
@Table(name = "PCM_REBATE_ITEM")
@SequenceGenerator(name = "pcmRebateItemSeqGen", sequenceName = "PCM_REBATE_ITEM_SEQ", allocationSize = 1)
public class PcmRebateItem implements Serializable, Comparable<PcmRebateItem> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pcmRebateItemSeqGen")
	@Column(name = "REBATE_ITEM_KEY")
	private Long rebateItemKey;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REBATE_PROGRAM_KEY")
	private PcmRebateProgram rebateProgram;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITEM_KEY")
	private Item item;

	@Column(name = "EFFECTIVE_FROM_DT")
	
	private Date effectiveFromDt;

	@Column(name = "EFFECTIVE_TO_DT")
	
	private Date effectiveToDt;

	@Column(name = "REBATE_AMOUNT")
	private BigDecimal rebateAmount;

	public PcmRebateItem() {
	}

	public PcmRebateItem(Long rebateItemKey, Item item,
			PcmRebateProgram rebateProgram, BigDecimal rebateAmount) {
		this.rebateItemKey = rebateItemKey;
		this.item = item;
		this.rebateProgram = rebateProgram;
		this.rebateAmount = rebateAmount;
	}

	public PcmRebateItem(PcmRebateItem copy) {
		if (copy.rebateAmount != null) {
			this.rebateAmount = new BigDecimal(copy.rebateAmount.toString());
		}
		if (copy.effectiveFromDt != null) {
			this.effectiveFromDt = new Date(copy.effectiveFromDt.getTime());
		}
		if (copy.effectiveToDt != null) {
			this.effectiveToDt = new Date(copy.effectiveToDt.getTime());
		}
		this.item = copy.item;
		this.rebateProgram = copy.rebateProgram;
	}

	public Long getRebateItemKey() {
		return rebateItemKey;
	}

	public void setRebateItemKey(Long rebateItemKey) {
		this.rebateItemKey = rebateItemKey;
	}

	public PcmRebateProgram getRebateProgram() {
		return rebateProgram;
	}

	public void setRebateProgram(PcmRebateProgram rebateProgram) {
		this.rebateProgram = rebateProgram;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Date getEffectiveFromDt() {
		return effectiveFromDt;
	}

	public void setEffectiveFromDt(Date effectiveFromDt) {
		this.effectiveFromDt = effectiveFromDt;
	}

	public Date getEffectiveToDt() {
		return effectiveToDt;
	}

	public void setEffectiveToDt(Date effectiveToDt) {
		this.effectiveToDt = effectiveToDt;
	}

	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}

	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PcmRebateItem castOther)) {
			return false;
		}
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.getRebateProgram(), castOther.getRebateProgram());
		eb.append(this.getItem(), castOther.getItem());
		eb.append(this.getEffectiveFromDt(), castOther.getEffectiveFromDt());
		eb.append(this.getEffectiveToDt(), castOther.getEffectiveToDt());
		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(this.getRebateProgram())
			.append(this.getItem())
			.toHashCode();
	}

	@Override
	public int compareTo(PcmRebateItem other) {
		if (this.equals(other)) {
			return 0;
		}
		CompareToBuilder cb = new CompareToBuilder();
		cb.append((this.item != null) ? item.getItemNumber() : null,
			(other.item != null) ? other.item.getItemNumber() : null);
		cb.append(this.getEffectiveFromDt(), other.getEffectiveFromDt());
		cb.append(this.getEffectiveToDt(), other.getEffectiveToDt());
		return cb.toComparison();
	}

	public String getAuditTitle() {
		String df = SCPlatformMessages.INSTANCE.getAuditMessage("audit.dateFormat", null, null);
		if (df == null) {
			df = DEFAULT_DATE_FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(df);
		List<Object> args = new ArrayList<>();
		args.add(sdf.format(effectiveFromDt));
		if (this.effectiveToDt != null) {
			args.add(sdf.format(effectiveToDt));
		} else {
			args.add("EVERGREEN");
		}
		args.add(rebateAmount);
		return SCPlatformMessages.INSTANCE.getAuditMessage("audit.rebateItem", args.toArray(), null);
	}
}
