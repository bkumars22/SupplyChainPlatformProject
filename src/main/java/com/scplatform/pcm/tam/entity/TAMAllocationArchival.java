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
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.type.YesNoConverter;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.site.entity.Site;


@Entity
@Table(name = "TAM_ALLOCATION_ARCHIVAL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"supplierAllocationsArchival"})
@EqualsAndHashCode(of = {"id", "site", "functionalGroup"})
public class TAMAllocationArchival implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Primary key - unique TAM allocation archival identifier
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAM_ALLOCATION_ARCHIVAL_SEQ")
	@SequenceGenerator(sequenceName = "TAM_ALLOCATION_KEY_SEQ", name = "TAM_ALLOCATION_ARCHIVAL_SEQ", allocationSize = 1)
	@Column(name = "TAM_ALLOCATION_ID", nullable = false)
	private Long id;

	/**
	 * Associated site for this TAM allocation archival
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "SITE_KEY", nullable = false, unique = true)
	private Site site;

	/**
	 * Associated functional group for this TAM allocation archival
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "FUNCTIONAL_GROUP_ID", nullable = false, unique = true)
	private FunctionalGroup functionalGroup;

	/**
	 * Flag indicating if hedging is allowed for this allocation
	 */
	@Column(name = "ALLOW_HEDGING", length = 1, nullable = false)
	@Convert(converter = YesNoConverter.class)
	private Boolean allowHedging;

	/**
	 * Timestamp when this record was created
	 */
	@Column(name = "CREATED_ON")
	private Timestamp createdOn;

	/**
	 * User who created this record
	 */
	@Column(name = "CREATED_BY")
	private String createdBy;

	/**
	 * Timestamp when this record was last changed
	 */
	@Column(name = "LAST_CHANGED_ON")
	private Timestamp lastChangedOn;

	/**
	 * User who last changed this record
	 */
	@Column(name = "LAST_CHANGED_BY")
	private String lastChangedBy;

	/**
	 * Date for the next rollover of this allocation
	 */
	@Column(name = "NEXT_ROLLOVER_DATE")
	private Timestamp nextRolloverDate;

	/**
	 * Extract flag for data extraction process
	 */
	@Column(name = "EXTRACT_FLAG")
	private String extractFlag;

	/**
	 * Count of rollovers performed on this allocation
	 */
	@Column(name = "ROLL_OVER_COUNT")
	private Integer rollOverCount;

	/**
	 * Flag indicating if current data has been deleted
	 */
	@Column(name = "CURRENT_DATA_DELETED", length = 1)
	@Convert(converter = YesNoConverter.class)
	private Boolean isCurrentDataDeleted;

	/**
	 * Discipline-specific extract flag
	 */
	@Column(name = "DISCP_EXTRACT_FLAG")
	private String discpExtractFlag;

	/**
	 * Discipline-specific rollover extract flag (read-only, not updated)
	 */
	@Column(name = "DISCP_ROLLOVER_EXTRACT_FLAG", insertable = false, updatable = false)
	private String discpRolloverExtractFlag;

	/**
	 * Supplier allocations archival for this TAM allocation
	 * Maps to TAM_SUPPLIER_ALLOCATION_ARCHIVAL table with complex filtering
	 */
	@OneToMany(mappedBy = "tamAllocationArchival", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<FunctionalGroupSupplierAllocationArchival> supplierAllocationsArchival = new HashSet<>();

}