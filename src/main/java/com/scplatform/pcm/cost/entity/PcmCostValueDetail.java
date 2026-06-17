/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.cost.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * PcmCostValueDetail - represents detailed cost values
 */
@Entity
@Table(name = "PCM_COST_VALUE_DETAIL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class PcmCostValueDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PCM_COST_VALUE_DETAIL_SEQ")
	@SequenceGenerator(sequenceName = "PCM_COST_VALUE_DETAIL_SEQ", name = "PCM_COST_VALUE_DETAIL_SEQ", allocationSize = 1, initialValue = 1)
	@Column(name = "COST_VALUE_DETAIL_KEY")
	private Long costValueDetailKey;

	@ManyToOne(optional = false)
	@JoinColumn(name = "COST_RECORD_VALUE_KEY", nullable = false)
	private PcmCostRecordValue costRecordValue;

	@Column(name = "COST_VALUE_NAME")
	private String costValueName;

	@Column(name = "COST_VALUE_VALUE", precision = 19, scale = 6)
	private BigDecimal costValueValue;

	@Column(name = "COST_VALUE_BLEND", precision = 10, scale = 2)
	private BigDecimal costValueBlend;

	/**
	 * Creates a deep copy of this cost value detail
	 *
	 * @return a copy of this detail
	 */
	public PcmCostValueDetail copy() {
		PcmCostValueDetail copy = new PcmCostValueDetail();
		copy.costValueBlend = this.costValueBlend;
		copy.costValueName = this.costValueName;
		copy.costValueValue = this.costValueValue;
		return copy;
	}

	/**
	 * Compares this cost value detail with another
	 *
	 * @param o the object to compare to
	 * @return comparison result
	 */
	public int compareTo(Object o) {
		PcmCostValueDetail other = (PcmCostValueDetail) o;
		return new CompareToBuilder().append(this.getCostRecordValue(), other.getCostRecordValue())
				.append(this.getCostValueName(), other.getCostValueName()).toComparison();
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof PcmCostValueDetail))
			return false;
		// We can use compare to
		return (compareTo(other) == 0);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.getCostRecordValue()).toHashCode();
	}
}
