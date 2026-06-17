/**
 * 
 */
package com.scplatform.pcm.costexception.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Models cost exception line of business mappings.
 * created on Nov 18, 2020
 * Copyright (c) 2000-2020, by E2open LLC.
 * All rights reserved.
 */
@Entity
@Table(name="COST_EXCEPTION_LOB")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"exception"})
public class CostExceptionLOB implements Serializable {
	@Id
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="COST_EXCEPTION_LOB_SEQ")
	@SequenceGenerator(name="COST_EXCEPTION_LOB_SEQ", sequenceName = "COST_EXCEPTION_LOB_SEQ", allocationSize = 1, initialValue = 1)
	@Column(name="ID")
    private Long id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name="EXCEPTION_KEY")	
    private CostException exception;
	
	@Column(name="LINE_OF_BUSINESS")
    private String lineOfBusiness;
}
