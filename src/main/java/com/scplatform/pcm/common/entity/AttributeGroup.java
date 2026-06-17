/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */

package com.scplatform.pcm.common.entity;

import java.io.Serial;
import java.io.Serializable;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Models an attribute group entity
 * Represents a grouping of attributes for different entity types
 */
@Entity
@Table(name = "ATTRIBUTE_GROUP")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"attributeGroupName", "objectType"})
public class AttributeGroup implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * Primary key - unique attribute group identifier
	 */
	@Id
	@SequenceGenerator(name = "ATTRIBUTE_GROUP_SEQ", sequenceName = "ATTRIBUTE_GROUP_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ATTRIBUTE_GROUP_SEQ")
	@Column(name = "ATTRIBUTE_GROUP_KEY")
	private Long attributeGroupKey;

	/**
	 * Attribute group name - natural key component
	 * Part of a composite natural key with objectType
	 */
	@NaturalId(mutable = true)
	@Column(name = "ATTRIBUTE_GROUP_NAME", nullable = false)
	private String attributeGroupName;

	/**
	 * Object type this attribute group applies to - natural key component
	 * Part of a composite natural key with attributeGroupName
	 */
	@NaturalId(mutable = true)
	@Enumerated(EnumType.STRING)
	@Column(name = "OBJECT_TYPE", nullable = false)
	private AttributeEntityType objectType;

	/**
	 * Constructor with primary key
	 * 
	 * @param attributeGroupKey the primary key
	 */
	public AttributeGroup(Long attributeGroupKey) {
		this.attributeGroupKey = attributeGroupKey;
	}

	/**
	 * Get the attribute group natural key as JSON
	 * 
	 * @return ObjectNode containing the natural key components
	 */
	public ObjectNode getAttributeGroupNaturalKeyAsJSON() {
		ObjectMapper om = new ObjectMapper();
		ObjectNode o = om.createObjectNode();
		o.put("attributeGroupName", this.attributeGroupName);
		o.put("objectType", this.objectType.toString());
		return o;
	}

}