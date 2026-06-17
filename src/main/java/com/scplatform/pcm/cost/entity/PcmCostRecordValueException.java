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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NaturalId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * PcmCostRecordValueException - represents individual cost value exceptions for a pricing range
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
@Entity
@Table(name = "PCM_COST_RECORD_VALUE_EXCEPTION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"serial", "rawtypes"})
public class PcmCostRecordValueException implements Serializable, Comparable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PCM_COST_RECORD_VALUE_EXCEPTION_SEQ")
	@SequenceGenerator(sequenceName = "PCM_COST_RECORD_VALUE_EXCEPTION_SEQ", name = "PCM_COST_RECORD_VALUE_EXCEPTION_SEQ", allocationSize = 1, initialValue = 1)
	@Column(name = "COST_RECORD_VALUE_KEY")
	private Long costRecordValueKey;

	@NaturalId(mutable = true)
	@ManyToOne(optional = false)
	@JoinColumn(name = "COST_RECORD_RANGE_KEY", nullable = false)
	@Fetch(value = FetchMode.SELECT)
	private PcmCostRecordRangeException costRecordRange;

	@NaturalId(mutable = true)
	@ManyToOne(optional = false)
	@JoinColumns({
		@JoinColumn(name = "COST_ELEMENT_KEY"),
		@JoinColumn(name = "COST_TYPE_KEY")
	})
	@Fetch(value = FetchMode.SELECT)
	private PcmCostElement costElement;

	@Column(name = "COST_VALUE")
	private BigDecimal costValue;

	@Column(name = "COST_UOM")
	private String costUom;

	@Column(name = "COST_VALUE_TYPE")
	private String costValueType;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "costRecordValue")
	@MapKeyColumn(name = "COST_VALUE_NAME")
	private Map<String, PcmCostValueDetailException> costValueDetails = new HashMap<>();

	/**
	 * Adds a cost value to this record exception value. If a value already exists, adds to it.
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
	 * Sets or updates a cost value detail exception
	 *
	 * @param valueDetail the detail exception to set
	 * @return the old detail exception if replaced, null otherwise
	 */
	public PcmCostValueDetailException setCostValueDetail(PcmCostValueDetailException valueDetail) {
		PcmCostValueDetailException old = costValueDetails.remove(valueDetail.getCostValueName());
		if (old != null) {
			old.setCostRecordValue(null);
		} else {
			valueDetail.setCostRecordValue(this);
			costValueDetails.put(valueDetail.getCostValueName(), valueDetail);
		}
		return old;
	}

	/**
	 * Sets or updates a cost value detail exception by name, value, and blend
	 *
	 * @param name the detail name
	 * @param value the cost value
	 * @param blend the blend percentage
	 * @return the created or updated detail exception
	 */
	public PcmCostValueDetailException setCostValueDetail(String name, BigDecimal value, BigDecimal blend) {
		PcmCostValueDetailException vd = costValueDetails.get(name);
		if (vd == null) {
			vd = new PcmCostValueDetailException();
			vd.setCostRecordValue(this);
			costValueDetails.put(name, vd);
		}
		vd.setCostValueName(name);
		vd.setCostValueValue(value);
		vd.setCostValueBlend(blend);
		return vd;
	}

	/**
	 * Creates a deep copy of this cost record value exception
	 *
	 * @return a copy of this exception value with copied details
	 */
	public PcmCostRecordValueException copy() {
		PcmCostRecordValueException copy = new PcmCostRecordValueException();
		copy.costElement = this.costElement;
		copy.costRecordRange = this.costRecordRange;
		copy.costUom = this.costUom;
		copy.costValue = this.costValue;
		copy.costValueType = this.costValueType;
		if (this.costValueDetails != null) {
			for (PcmCostValueDetailException vd : this.costValueDetails.values()) {
				PcmCostValueDetailException newVd = vd.copy();
				newVd.setCostRecordValue(copy);
				copy.setCostValueDetail(newVd);
			}
		}
		return copy;
	}

	@Override
	public int compareTo(Object o) {
		PcmCostRecordValueException other = (PcmCostRecordValueException) o;
		return new CompareToBuilder().append(this.costElement, other.costElement)
				.append(this.costValue, other.costValue).toComparison();
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof PcmCostRecordValueException))
			return false;
		PcmCostRecordValueException castOther = (PcmCostRecordValueException) other;
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
