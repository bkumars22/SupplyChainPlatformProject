/**
 * 
 */
package com.scplatform.pcm.costexception.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Models cost exception pricing records.
 * created on Dec 01, 2020
 * Copyright (c) 2000-2020, by E2open LLC.
 * All rights reserved.
 */
@Entity
@Table(name="COST_EXCEPTION_PRICING")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"exception"})
public class CostExceptionPricing implements Serializable {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="COST_EXCEPTION_PRICING_SEQ")
	@SequenceGenerator(name="COST_EXCEPTION_PRICING_SEQ", sequenceName = "COST_EXCEPTION_PRICING_SEQ", allocationSize = 1, initialValue = 1)
	@Column(name="ID")
    private Long id;
	
	@OneToOne(optional = true)
	@JoinColumn(name="EXCEPTION_KEY")	
    private CostException exception;
	
	@Column(name="FILE_NAME")
    private String fileName;
	
	@Column(name="COST_RECORD_COUNT")
    private Long costRecordCount;
	
	@Column(name="UPLOADED_BY")
    private String uploadedBy;
	
	@Column(name="UPLOADED_ON")
    private Timestamp uploadedOn;
}
