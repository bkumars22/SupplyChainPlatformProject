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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.type.YesNoConverter;

import java.io.Serial;
import java.util.LinkedHashSet;
import java.util.Set;


@Entity
@Table(name = "PCM_COST_TYPE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PcmCostType implements java.io.Serializable, Comparable<Object> {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Primary key - unique cost type identifier
	 */
	@Id
	@Column(name = "COST_TYPE_KEY", nullable = false, precision = 19)
	private String costTypeKey;

	/**
	 * Display name for the cost type
	 */
	@Column(name = "COST_TYPE_NAME", nullable = false)
	private String costTypeName;

	/**
	 * Display order for cost type sorting/rendering
	 */
	@Column(name = "COST_TYPE_ORDER", precision = 4)
	private Long displayOrder;

	/**
	 * Flag indicating if this cost type should be included in rollup calculations
	 */
	@Convert(converter = YesNoConverter.class)
	@Column(name = "USE_IN_ROLLUP", nullable = false, length = 1)
	@Builder.Default
	private boolean useInRollup = false;

	/**
	 * Flag indicating if supplier information should be included in rollup
	 */
	@Convert(converter = YesNoConverter.class)
	@Column(name = "USE_SUPPLIER_IN_ROLLUP", nullable = false, length = 1)
	@Builder.Default
	private boolean useSupplierInRollup = false;

	/**
	 * Flag indicating if multiple providers are allowed for this cost type
	 */
	@Convert(converter = YesNoConverter.class)
	@Column(name = "ALLOW_MULTI_PROVIDER", nullable = false, length = 1)
	@Builder.Default
	private boolean allowMultiProvider = false;

	/**
	 * Flag indicating if this cost type should be used in item category cost calculations
	 */
	@Convert(converter = YesNoConverter.class)
	@Column(name = "USE_IN_ITEM_CATEGORY_COST", nullable = false, length = 1)
	@Builder.Default
	private boolean useInItemCategoryCost = false;

	/**
	 * One-to-many relationship with cost elements
	 * A cost type can have multiple cost elements
	 */
	@OneToMany(mappedBy = "pcmCostType", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderBy("displayOrder")
	@Builder.Default
	private Set<PcmCostElement> pcmCostElements = new LinkedHashSet<>(0);

    public int hashCode()
    {
        return (costTypeKey != null) ? costTypeKey.hashCode():0;
    }

    @Override
    public boolean equals(Object other)
    {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof PcmCostType))
            return false;
        PcmCostType castOther = (PcmCostType) other;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.costTypeKey, castOther.getCostTypeKey());
        return eb.isEquals();
    }

    @Override
    public int compareTo(Object o)
    {
        PcmCostType other = (PcmCostType)o;
        int ret = new CompareToBuilder()
                .append(this.displayOrder,other.displayOrder)
                .append(this.costTypeKey,other.costTypeKey)
                .toComparison();
        return ret;
    }

    @Override
    public String toString()
    {
        return costTypeKey;
    }

    public boolean isUseInItemCategoryCost() {
        return useInItemCategoryCost;
    }

    public void setUseInItemCategoryCost(boolean useInItemCategoryCost) {
        this.useInItemCategoryCost = useInItemCategoryCost;
    }
}
