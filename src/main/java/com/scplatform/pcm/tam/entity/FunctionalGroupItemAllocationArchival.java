/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.tam.entity;

import java.io.Serial;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.scplatform.pcm.item.entity.Item;

/**
 * Functional Group Item Allocation Archival Entity - represents archived TAM item allocations
 * Maps to TAM_ITEM_ALLOCATION_ARCHIVAL table
 */
@Entity
@Table(name = "TAM_ITEM_ALLOCATION_ARCHIVAL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"functionalGroupSupplierAllocationArchival"})
@EqualsAndHashCode(of = {"id", "item", "allocation"})
public class FunctionalGroupItemAllocationArchival implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Primary key - unique TAM item allocation archival identifier
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAM_ITEM_ALLOCATION_ARCHIVAL_SEQ")
	@SequenceGenerator(name = "TAM_ITEM_ALLOCATION_ARCHIVAL_SEQ", sequenceName = "TAM_ITEM_ALLOCATION_KEY_SEQ", allocationSize = 1)
	@Column(name = "TAM_ITEM_ALLOCATION_ID", nullable = false)
	private Long id;

	/**
	 * Associated item for this archival allocation
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "ITEM_KEY", nullable = false, unique = true)
	private Item item;

	/**
	 * Allocation percentage or amount for this item
	 */
	@Column(name = "ALLOCATION")
	private Double allocation;

	/**
	 * Associated functional group supplier allocation archival
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "TAM_SUPPLIER_ALLOCATION_ID", nullable = false)
	private FunctionalGroupSupplierAllocationArchival functionalGroupSupplierAllocationArchival;

}
