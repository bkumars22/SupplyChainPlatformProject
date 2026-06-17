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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scplatform.pcm.cost.service.ComputedCostRecordValueService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PcmCostRecordValue - represents individual cost values for a pricing range
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
@Entity
@Table(name = "PCM_COST_RECORD_VALUE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"serial", "rawtypes"})
public class PcmCostRecordValue implements Serializable, Comparable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PCM_COST_RECORD_VALUE_SEQ")
	@SequenceGenerator(sequenceName = "PCM_COST_RECORD_VALUE_SEQ", name = "PCM_COST_RECORD_VALUE_SEQ", allocationSize = 1, initialValue = 1)
	@Column(name = "COST_RECORD_VALUE_KEY")
	private Long costRecordValueKey;

	@ManyToOne(optional = false)
	@JoinColumn(name = "COST_RECORD_RANGE_KEY", nullable = false)
	private PcmCostRecordRange costRecordRange;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "COST_ELEMENT_KEY", nullable = false),
		@JoinColumn(name = "COST_TYPE_KEY", nullable = false)
	})
	private PcmCostElement costElement;

	@Column(name = "COST_VALUE", precision = 19, scale = 6)
	private BigDecimal costValue;

	@Column(name = "COST_UOM")
	private String costUom;

	@Column(name = "COST_VALUE_TYPE")
	private String costValueType;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "costRecordValue")
	private Map<String, PcmCostValueDetail> costValueDetails = new HashMap<>();

	/**
	 * Adds a cost value to this record value. If a value already exists, adds to it.
	 * 
	 * @param costValue the value to add
	 */
	public void addCostValue(BigDecimal costValue) {
		if (this.costValue == null) {
			this.costValue = costValue;
		} else {
			this.costValue = this.costValue.add(costValue);
		}
	}

	/**
	 * Gets a computed value for this cost record value. See { ComputedCostRecordValueEvaluator} for details.
	 *
	 * @return the computed cost value
	 */
	public BigDecimal getComputedCostValue() {
		return ComputedCostRecordValueService.getInstance().computeCostValue(this);
	}

	/**
	 * Sets or updates a cost value detail
	 *
	 * @param valueDetail the detail to set
	 * @return the old detail if replaced, null otherwise
	 */
	public PcmCostValueDetail setCostValueDetail(PcmCostValueDetail valueDetail) {
		PcmCostValueDetail old = costValueDetails.remove(valueDetail.getCostValueName());
		if (old != null) {
			old.setCostRecordValue(null);
		} else {
			valueDetail.setCostRecordValue(this);
			costValueDetails.put(valueDetail.getCostValueName(), valueDetail);
		}
		return old;
	}

	/**
	 * Sets or updates a cost value detail by name, value, and blend
	 *
	 * @param name the detail name
	 * @param value the cost value
	 * @param blend the blend percentage
	 * @return the created or updated detail
	 */
	public PcmCostValueDetail setCostValueDetail(String name, BigDecimal value, BigDecimal blend) {
		PcmCostValueDetail vd = costValueDetails.get(name);
		if (vd == null) {
			vd = new PcmCostValueDetail();
			vd.setCostRecordValue(this);
			costValueDetails.put(name, vd);
		}
		vd.setCostValueName(name);
		vd.setCostValueValue(value);
		vd.setCostValueBlend(blend);
		return vd;
	}

	/**
	 * Gets the total of all cost value details
	 *
	 * @return the sum of all detail values
	 */
	public BigDecimal getValueTotal() {
		BigDecimal result = BigDecimal.ZERO.setScale(6, BigDecimal.ROUND_HALF_UP);
		for (PcmCostValueDetail pcvd : costValueDetails.values()) {
			if (pcvd.getCostRecordValue() != null) {
				result = result.add(pcvd.getCostValueValue());
			}
		}
		return result;
	}

	/**
	 * Gets the total of all blend percentages
	 *
	 * @return the sum of all blend values
	 */
	public BigDecimal getValueBlendTotal() {
		BigDecimal result = BigDecimal.ZERO.setScale(1, BigDecimal.ROUND_HALF_UP);
		for (PcmCostValueDetail pcvd : costValueDetails.values()) {
			if (pcvd.getCostValueBlend() != null) {
				result = result.add(pcvd.getCostValueBlend());
			}
		}
		return result;
	}

	/**
	 * Using the detail records, perform the appropriate calculation to get the value.
	 * If there are no details or there is no calculation associated with the cost detail type (ie. SIMPLE, or TIERED)
	 * a null value is returned
	 *
	 * @return the calculated value or null
	 */
	public BigDecimal getCalculatedValue() {
		BigDecimal result = null;
		if ("B".equalsIgnoreCase(getCostValueType())) {
			result = BigDecimal.ZERO.setScale(6, BigDecimal.ROUND_HALF_UP);
			for (PcmCostValueDetail pcvd : costValueDetails.values()) {
				BigDecimal blendValue = pcvd.getCostValueBlend();
				BigDecimal valueValue = pcvd.getCostValueValue();
				BigDecimal percent = blendValue.movePointLeft(2);
				result = result.add(valueValue.multiply(percent).setScale(6, BigDecimal.ROUND_HALF_UP));
			}
		}
		return result;
	}

	/**
	 * Creates a deep copy of this cost record value
	 *
	 * @return a copy of this value with copied details
	 */
	public PcmCostRecordValue copy() {
		PcmCostRecordValue copy = new PcmCostRecordValue();
		copy.costElement = this.costElement;
		copy.costRecordRange = this.costRecordRange;
		copy.costUom = this.costUom;
		copy.costValue = this.costValue;
		copy.costValueType = this.costValueType;
		if (this.costValueDetails != null) {
			for (PcmCostValueDetail vd : this.costValueDetails.values()) {
				PcmCostValueDetail newVd = vd.copy();
				newVd.setCostRecordValue(copy);
				copy.setCostValueDetail(newVd);
			}
		}
		return copy;
	}

	@Override
	public int compareTo(Object o) {
		PcmCostRecordValue other = (PcmCostRecordValue) o;
		return new CompareToBuilder().append(this.costElement, other.costElement)
				.append(this.costValue, other.costValue).toComparison();
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof PcmCostRecordValue))
			return false;
		PcmCostRecordValue castOther = (PcmCostRecordValue) other;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.getCostRecordRange(), castOther.getCostRecordRange());
		eb.append(this.getCostElement(), castOther.getCostElement());
		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.getCostElement()).toHashCode();
	}
}
