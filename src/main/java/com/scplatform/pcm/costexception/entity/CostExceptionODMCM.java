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
 * Models cost exception ODM CM associations.
 * created on Nov 18, 2020
 * Copyright (c) 2000-2020, by E2open LLC.
 * All rights reserved.
 */
@Entity
@Table(name="COST_EXCEPTION_ODM_CM")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"exception"})
public class CostExceptionODMCM implements Serializable {
	@Id
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="COST_EXCEPTION_ODM_CM_SEQ")
	@SequenceGenerator(name="COST_EXCEPTION_ODM_CM_SEQ", sequenceName = "COST_EXCEPTION_ODM_CM_SEQ", allocationSize = 1, initialValue = 1)
	@Column(name="ID")
    private Long id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name="EXCEPTION_KEY")	
    private CostException exception;
	
	@Column(name="APPLICABLE_ODM_CM")
    private String applicableOdmCm;
}
