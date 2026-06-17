
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.businessEntity.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import com.scplatform.pcm.common.entity.AttributeGroup;

@SuppressWarnings("serial")
@Entity
@Table(name = "BUSINESS_ENTITY_ADD_ATTRIBUTE")
@Getter
@Setter
public class BusinessEntityAttribute implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attributeSeqGen")
	@SequenceGenerator(name = "attributeSeqGen", sequenceName = "ATTRIBUTE_SEQ", allocationSize = 1)
	@Column(name = "ATTRIBUTE_KEY")
	private Long businessEntityAttributeId;

	@ManyToOne
	@JoinColumn(name = "BUSINESS_ENTITY_KEY")
	private BusinessEntity businessEntity;

	@ManyToOne
	@JoinColumn(name = "ATTRIBUTE_GROUP_KEY", nullable = false)
	private AttributeGroup attributeGroup;

	@Column(name = "ATTRIBUTE_NAME", nullable = false, length = 50)
	private String attributeName;

	@Column(name = "ATTRIBUTE_TYPE", nullable = false, length = 50)
	private String attributeType;

	@Column(name = "ATTRIBUTE_VALUE")
	private String attributeValue;

	@Column(name = "DESCRIPTION")
	private String description;

	@Override
	public int hashCode() {
		return Objects.hash(attributeGroup, attributeName, attributeType, businessEntity);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BusinessEntityAttribute other = (BusinessEntityAttribute) obj;
		return Objects.equals(attributeGroup, other.attributeGroup)
				&& Objects.equals(attributeName, other.attributeName)
				&& Objects.equals(attributeType, other.attributeType)
				&& Objects.equals(businessEntity, other.businessEntity);
	}

}
