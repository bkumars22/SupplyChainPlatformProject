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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;

/**
 * Functional Group Supplier Allocation Archival Entity - represents archived TAM supplier allocations
 * Maps to TAM_SUPPLIER_ALLOCATION_ARCHIVAL table
 */
@Entity
@Table(name = "TAM_SUPPLIER_ALLOCATION_ARCHIVAL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"itemAllocationsArchival", "tamAllocationArchival"})
@EqualsAndHashCode(of = {"id", "businessEntity", "startDate", "endDate", "allocation"})
public class FunctionalGroupSupplierAllocationArchival implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Primary key - unique TAM supplier allocation archival identifier
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAM_SUPPLIER_ALLOCATION_ARCHIVAL_SEQ")
	@SequenceGenerator(name = "TAM_SUPPLIER_ALLOCATION_ARCHIVAL_SEQ", sequenceName = "TAM_SUPPLIER_ALLOCATION_KEY_SEQ", allocationSize = 1)
	@Column(name = "TAM_SUPPLIER_ALLOCATION_ID", nullable = false)
	private Long id;

	/**
	 * Associated business entity (supplier) for this archival allocation
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "BUSINESS_ENTITY_KEY", nullable = false, unique = true)
	private BusinessEntity businessEntity;

	/**
	 * Start date for this allocation period
	 */
	@Column(name = "START_DATE")
	private Date startDate;

	/**
	 * End date for this allocation period
	 */
	@Column(name = "END_DATE")
	private Date endDate;

	/**
	 * Allocation percentage or amount
	 */
	@Column(name = "ALLOCATION")
	private Double allocation;

	/**
	 * Item allocations archival for this supplier allocation
	 * Maps to TAM_ITEM_ALLOCATION_ARCHIVAL table
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "functionalGroupSupplierAllocationArchival", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<FunctionalGroupItemAllocationArchival> itemAllocationsArchival = new HashSet<>();

	/**
	 * Associated TAM allocation archival
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "TAM_ALLOCATION_ID", nullable = false)
	private TAMAllocationArchival tamAllocationArchival;

}
