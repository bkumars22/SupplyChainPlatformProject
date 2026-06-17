/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.bom.entity;

import com.scplatform.pcm.common.entity.Attribute;
import com.scplatform.pcm.item.entity.Item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.type.YesNoConverter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import com.scplatform.pcm.aml.entity.Aml;

/**
 * 
 * The BomLine class represents one part within a BOM. The line will contain
 * 
 * information associated with how that item is used within the BOM.
 * 
 * Each BomLine references one Item within the model along with a Map of
 * 
 * valid approved vendor entries.
 * 
 * 
 * 
 * The BomLine also supports the Attribute class and therefore you can attach
 * 
 * user defined attribute values to each. The attribute instances are managed by
 * the
 * 
 * BomLine.
 * 
 */
@Entity
@Table(name = "BOM_LINE_ITEM")
@Getter
@Setter
@SuppressWarnings("serial")
public class BomLine extends BaseBomEntity implements Serializable, Comparable<BomLine> {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOM_LINE_ITEM_SEQ_GEN")
	@SequenceGenerator(name = "BOM_LINE_ITEM_SEQ_GEN", sequenceName = "BOM_LINE_ITEM_SEQ", allocationSize = 1)
	@Column(name = "BOM_LINE_ITEM_KEY", nullable = false, unique = true)
	protected Long bomLineKey;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BOM_KEY", nullable = false)
	protected Bom bom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUB_BOM_KEY", nullable = true)
	protected Bom subBom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITEM_KEY", nullable = false)
	protected Item item;

	@Transient
	protected String bomLineId;

	@Transient
	protected String revision;

	@Convert(converter = YesNoConverter.class)
	@Column(name = "ROLLUP_FLAG", nullable = false)
	protected Boolean isRollupExtractEnable = true;

	@Column(name = "IS_SERIALIZATION_REQ", nullable = true)
	protected Boolean serialNumberRequired;

	@Column(name = "BOM_TYPE_CODE", length = 36, nullable = true)
	protected String typeCode;

	@Column(name = "BOM_TYPE_CODE_OTHER", nullable = true)
	protected String typeCodeOther;

	@Column(name = "NOTES", nullable = true)
	protected String note;

	@Column(name = "ITEM_QUANTITY", nullable = true)
	protected Float itemQty;

	@Column(name = "LEAD_TIME", nullable = true)
	protected Float leadTime;

	@Column(name = "PRODUCT_QTY_TYPE_CODE", length = 36, nullable = true)
	protected String productQtyTypeCode;

	@Column(name = "PRODUCT_QTY_TYPE_CODE_OTHER", nullable = true)
	protected String productQtyTypeCodeOther;

	@Column(name = "DESCRIPTION", nullable = true)
	protected String description;

	@Column(name = "SEQUENCE_IDENTIFIER", nullable = true)
	protected String sequenceNumber;

	@Column(name = "MANAGED_FLAG", nullable = true)
	protected String managedFlag;

	@Column(name = "GROUP_ID", nullable = true)
	protected String groupId;

	@Column(name = "BOM_LINE_ALT_TYPE", nullable = true)
	protected String bomLineAltType;

	@Column(name = "PRIORITY", nullable = true)
	protected Integer priority;

	@Column(name = "REASON_CODE", nullable = true)
	protected String reasonCode;

	@ElementCollection
	@CollectionTable(name = "BOM_LINE_ITEM_ADD_ATTRIBUTE", joinColumns = @JoinColumn(name = "BOM_LINE_ITEM_KEY"))
	protected Collection<Attribute> attributes = new ArrayList<Attribute>();

	@Transient
	protected BigDecimal attritionRate;

	public static final String PRIMARY = "PRIMARY";

	public BomLine() {
	}

	public BomLine(Long key) {
		this.bomLineKey = key;
	}

	public boolean addAttribute(Attribute attr) {
		if (attributes.contains(attr)) {
			return false;
		}
		attributes.add(attr);
		return true;
	}

	public Set<Aml> getAmls() {
		return bom.getAmlsForItem(this.getItem());
	}

	public void setManagedFlag(String managedFlag) {
		this.managedFlag = StringUtils.upperCase(managedFlag);
	}

	@Override
	public Boolean getIsRollupExtractEnable() {
		return isRollupExtractEnable;
	}

	@Override
	public void setIsRollupExtractEnable(Boolean isRollupExtractEnable) {
		this.isRollupExtractEnable = isRollupExtractEnable;
	}

	@Transient
	public boolean isNonPrimaryAlternate() {
		if (StringUtils.trimToNull(groupId) != null && !PRIMARY.equals(bomLineAltType)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BomLine))
			return false;
		BomLine castOther = (BomLine) other;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.getBom(), castOther.getBom());
		eb.append(this.getItem(), castOther.getItem());
		eb.append(this.getEffectiveFrom(), castOther.getEffectiveFrom());
		eb.append(this.getEffectiveTo(), castOther.getEffectiveTo());
		eb.append(this.getGroupId(), castOther.getGroupId());
		eb.append(this.getBomLineAltType(), castOther.getBomLineAltType());
		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hc = new HashCodeBuilder(17, 37);
		hc.append(this.getBom());
		hc.append(this.getItem());
		hc.append(this.getEffectiveFrom());
		hc.append(this.getEffectiveTo());
		hc.append(this.getGroupId());
		hc.append(this.getBomLineAltType());
		return hc.toHashCode();
	}

	/**
	 * Copies all the attributes to the target except for the key
	 * 
	 * @param target
	 */
	public void copyDetailsTo(BomLine target) {
		target.item = item;
		target.bomLineId = bomLineId;
		target.revision = revision;
		target.serialNumberRequired = serialNumberRequired;
		target.typeCode = typeCode;
		target.typeCodeOther = typeCodeOther;
		target.note = note;
		target.itemQty = itemQty;
		target.productQtyTypeCode = productQtyTypeCode;
		target.productQtyTypeCodeOther = productQtyTypeCodeOther;
		target.description = description;
		target.sequenceNumber = sequenceNumber;
		target.managedFlag = managedFlag;
		target.setEffectiveFrom(getEffectiveFrom());
		target.setEffectiveTo(getEffectiveTo());
		target.setGroupId(groupId);
		target.setBomLineAltType(bomLineAltType);
		target.setPriority(priority);
		target.setReasonCode(reasonCode);
		copyFlexAttributes(target);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Quantity=").append(itemQty);
		sb.append(", BOM=(").append(bom).append(")");
		sb.append(", Item=(").append(item).append(") ");
		if (subBom != null) {
			sb.append(" SubBOM=(").append(subBom).append(")");
		}
		return sb.toString();
	}

	@Override
	public int compareTo(BomLine o2) {
		if (this == o2)
			return 0;
		CompareToBuilder ctb = new CompareToBuilder();
		int thisAltSeq = getAltTypeSeq(this.getBomLineAltType());
		int o2AltSeq = getAltTypeSeq(o2.getBomLineAltType());

		// Ignoring the bom for comparison in compare as it makes no sense to compare
		// bomlines across boms
		ctb.append(this.getGroupId(), o2.getGroupId()).append(thisAltSeq, o2AltSeq)
				.append(this.getBomLineAltType(), o2.getBomLineAltType()).append(this.getPriority(), o2.getPriority())
				.append(this.getEffectiveFrom(), o2.getEffectiveFrom())
				.append(this.getEffectiveTo(), o2.getEffectiveTo()).append(this.getItem(), o2.getItem());
		return ctb.toComparison();
	}

	private int getAltTypeSeq(String altType) {
		int seq = 0;
		if (altType != null) {
			if (PRIMARY.equals(altType)) {
				seq = -1;
			}
		}
		return seq;
	}
}
