/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
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
 * Models cost exception ODM email attachments.
 */
@Entity
@Table(name = "COST_EXCEPTION_ODM_EMAIL")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"exception"})
public class CostExceptionODMEmail implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COST_EXCEPTION_ODM_EMAIL_SEQ")
	@SequenceGenerator(name = "COST_EXCEPTION_ODM_EMAIL_SEQ", sequenceName = "COST_EXCEPTION_ODM_EMAIL_SEQ", allocationSize = 1, initialValue = 1)
	@Column(name = "ID")
	private Long id;

	@ManyToOne(optional = true)
	@JoinColumn(name = "EXCEPTION_KEY")
	private CostException exception;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "FILE_CONTENT")
	@Lob
	private byte[] fileContent;

	@Column(name = "UPLOADED_BY")
	private String uploadedBy;

	@Column(name = "UPLOADED_ON")
	private Timestamp uploadedOn;
}
