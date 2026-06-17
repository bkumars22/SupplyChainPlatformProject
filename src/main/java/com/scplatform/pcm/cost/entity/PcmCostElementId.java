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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * PcmCostElementId - composite embedded ID for cost elements
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class PcmCostElementId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "COST_ELEMENT_KEY")
	private String costElementKey;

	@Column(name = "COST_TYPE_KEY")
	private String costTypeKey;

	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof PcmCostElementId))
			return false;
		PcmCostElementId castOther = (PcmCostElementId) other;
		return ((this.getCostElementKey() == castOther.getCostElementKey()) || (this.getCostElementKey() != null
				&& castOther.getCostElementKey() != null && this.getCostElementKey().equals(
				castOther.getCostElementKey())))
				&& ((this.getCostTypeKey() == castOther.getCostTypeKey()) || (this.getCostTypeKey() != null
						&& castOther.getCostTypeKey() != null && this.getCostTypeKey().equals(
						castOther.getCostTypeKey())));
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + (getCostElementKey() == null ? 0 : this.getCostElementKey().hashCode());
		result = 37 * result + (getCostTypeKey() == null ? 0 : this.getCostTypeKey().hashCode());
		return result;
	}
}
