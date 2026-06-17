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
import java.sql.Timestamp;

/**
 * Models cost exception information and state changes.
 * 
 * created on Nov 18, 2020
 * Copyright (c) 2000-2020, by E2open LLC.
 * All rights reserved.
 */
@Entity
@Table(name="COST_EXCEPTION_INFO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"exception"})
public class CostExceptionInfo implements Serializable {
	@Id
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="COST_EXCEPTION_INFO_SEQ")
	@SequenceGenerator(name="COST_EXCEPTION_INFO_SEQ", sequenceName = "COST_EXCEPTION_INFO_SEQ", allocationSize = 1, initialValue = 1)
	@Column(name="ID")
    private Long id;
	
	@ManyToOne(optional = true)
	@JoinColumn(name="EXCEPTION_KEY")	
    private CostException exception;
	
	@Column(name="STATE")
    private String state;
	
	@Column(name="STATE_CHANGE_BY")
    private String stateChangeBy;
	
	@Column(name="STATE_CHANGE_ON")
    private Timestamp stateChangeOn;
	
	@Column(name="EXCEPTION_COMMENT")
    private String comment;
}
