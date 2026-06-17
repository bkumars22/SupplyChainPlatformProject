/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.common.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "MULTI_PURPOSE_USES")
@SequenceGenerator(name = "MULTI_PURPOSE_USES_SEQ", sequenceName = "MULTI_PURPOSE_USES_KEY_SEQ", allocationSize = 1)
public class MultiPurposeUses implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MULTI_PURPOSE_USES_SEQ")
	@Column(name = "ID", nullable = false, unique = true)
	private Long id;
	
	@Column(name = "OBJECT_TYPE", nullable = false)
	private String objectType;
	
	@Column(name = "string_param1")
	private String stringParam1;
	
	@Column(name = "string_param2")
	private String stringParam2;
	
	@Column(name = "string_param3")
	private String stringParam3;
	
	@Column(name = "number_param1")
	private Long longParam1;
	
	@Column(name = "number_param2")
	private Long longParam2;
	
	@Column(name = "number_param3")
	private Long longParam3;
	
	@Column(name = "date_param1")
	private Timestamp dateParam1;
	
	@Column(name = "date_param2")
	private Timestamp dateParam2;
	
	@Column(name = "date_param3")
	private Timestamp dateParam3;
	
	@Column(name = "CLOB_DATA")
	private String clobData;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public String getStringParam1() {
		return stringParam1;
	}
	public void setStringParam1(String stringParam1) {
		this.stringParam1 = stringParam1;
	}
	public String getStringParam2() {
		return stringParam2;
	}
	public void setStringParam2(String stringParam2) {
		this.stringParam2 = stringParam2;
	}
	public String getStringParam3() {
		return stringParam3;
	}
	public void setStringParam3(String stringParam3) {
		this.stringParam3 = stringParam3;
	}
	public Long getLongParam1() {
		return longParam1;
	}
	public void setLongParam1(Long longParam1) {
		this.longParam1 = longParam1;
	}
	public Long getLongParam2() {
		return longParam2;
	}
	public void setLongParam2(Long longParam2) {
		this.longParam2 = longParam2;
	}
	public Long getLongParam3() {
		return longParam3;
	}
	public void setLongParam3(Long longParam3) {
		this.longParam3 = longParam3;
	}
	public Timestamp getDateParam1() {
		return dateParam1;
	}
	public void setDateParam1(Timestamp dateParam1) {
		this.dateParam1 = dateParam1;
	}
	public Timestamp getDateParam2() {
		return dateParam2;
	}
	public void setDateParam2(Timestamp dateParam2) {
		this.dateParam2 = dateParam2;
	}
	public Timestamp getDateParam3() {
		return dateParam3;
	}
	public void setDateParam3(Timestamp dateParam3) {
		this.dateParam3 = dateParam3;
	}
	public String getClobData() {
		return clobData;
	}
	public void setClobData(String clobData) {
		this.clobData = clobData;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clobData == null) ? 0 : clobData.hashCode());
		result = prime * result + ((dateParam1 == null) ? 0 : dateParam1.hashCode());
		result = prime * result + ((dateParam2 == null) ? 0 : dateParam2.hashCode());
		result = prime * result + ((dateParam3 == null) ? 0 : dateParam3.hashCode());
		result = prime * result + ((longParam1 == null) ? 0 : longParam1.hashCode());
		result = prime * result + ((longParam2 == null) ? 0 : longParam2.hashCode());
		result = prime * result + ((longParam3 == null) ? 0 : longParam3.hashCode());
		result = prime * result + ((objectType == null) ? 0 : objectType.hashCode());
		result = prime * result + ((stringParam1 == null) ? 0 : stringParam1.hashCode());
		result = prime * result + ((stringParam2 == null) ? 0 : stringParam2.hashCode());
		result = prime * result + ((stringParam3 == null) ? 0 : stringParam3.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MultiPurposeUses other = (MultiPurposeUses) obj;
		if (clobData == null) {
			if (other.clobData != null)
				return false;
		} else if (!clobData.equals(other.clobData))
			return false;
		if (dateParam1 == null) {
			if (other.dateParam1 != null)
				return false;
		} else if (!dateParam1.equals(other.dateParam1))
			return false;
		if (dateParam2 == null) {
			if (other.dateParam2 != null)
				return false;
		} else if (!dateParam2.equals(other.dateParam2))
			return false;
		if (dateParam3 == null) {
			if (other.dateParam3 != null)
				return false;
		} else if (!dateParam3.equals(other.dateParam3))
			return false;
		if (longParam1 == null) {
			if (other.longParam1 != null)
				return false;
		} else if (!longParam1.equals(other.longParam1))
			return false;
		if (longParam2 == null) {
			if (other.longParam2 != null)
				return false;
		} else if (!longParam2.equals(other.longParam2))
			return false;
		if (longParam3 == null) {
			if (other.longParam3 != null)
				return false;
		} else if (!longParam3.equals(other.longParam3))
			return false;
		if (objectType == null) {
			if (other.objectType != null)
				return false;
		} else if (!objectType.equals(other.objectType))
			return false;
		if (stringParam1 == null) {
			if (other.stringParam1 != null)
				return false;
		} else if (!stringParam1.equals(other.stringParam1))
			return false;
		if (stringParam2 == null) {
			if (other.stringParam2 != null)
				return false;
		} else if (!stringParam2.equals(other.stringParam2))
			return false;
		if (stringParam3 == null) {
			if (other.stringParam3 != null)
				return false;
		} else if (!stringParam3.equals(other.stringParam3))
			return false;
		return true;
	}
	 
	

}
