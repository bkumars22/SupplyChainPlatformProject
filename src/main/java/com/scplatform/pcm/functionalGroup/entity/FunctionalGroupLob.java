/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.functionalGroup.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;


@SuppressWarnings("serial")
@Entity
@Table(name = "FG_LOB_VALUES")
public class FunctionalGroupLob implements java.io.Serializable   {

	@Id
	@SequenceGenerator(name = "FG_LOB_GENERATOR", sequenceName = "FG_LOB_SEQUENCE",allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FG_LOB_GENERATOR")
	@Column(name = "ID")
	private long id;

	
	@Column(name = "\"VALUE\"")
	private String lobValue;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "FUNCTIONAL_GROUP_ID")
	private FunctionalGroup functionalGroup;

    public FunctionalGroupLob() {
    }

    public FunctionalGroupLob(FunctionalGroup functionalGroup) {
        this.functionalGroup = functionalGroup;
    }

    public FunctionalGroupLob( FunctionalGroup functionalGroup, String lobValue) {
        this.lobValue = lobValue;
        this.functionalGroup = functionalGroup;
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	

	public String getLobValue() {
		return lobValue;
	}

	public void setLobValue(String lobValue) {
		this.lobValue = lobValue;
	}

	public FunctionalGroup getFunctionalGroup() {
		return functionalGroup;
	}

	public void setFunctionalGroup(FunctionalGroup functionalGroup) {
		this.functionalGroup = functionalGroup;
	}
	

}
