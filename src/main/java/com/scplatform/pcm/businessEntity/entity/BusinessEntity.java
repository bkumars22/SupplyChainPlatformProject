/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.businessEntity.entity;

import com.scplatform.pcm.bom.entity.BaseBomEntity;
import com.scplatform.pcm.common.entity.Attribute;
import com.scplatform.pcm.contact.entity.Contact;
import com.scplatform.pcm.currency.entity.Currency;
import com.scplatform.pcm.site.entity.Site;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * BusinessEntity - represents a business entity (company).
 * A business entity can be an operator, enterprise, manufacturer, or supplier.
 * Maps to BUSINESS_ENTITY and BUSINESS_ENTITY_ALT tables
 */
@Entity
@Table(name = "BUSINESS_ENTITY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"attributes", "alternates", "currencies", "sites", "contact"})
@EqualsAndHashCode(of = {"businessEntityIdentifier", "businessEntityTypeKey"}, callSuper = false)
@FilterDef(
	name = "businessFilter",
	parameters = @ParamDef(name = "businessEntity", type = Long.class))
@Filter(
	name = "businessFilter",
	condition = "(BUSINESS_ENTITY_TYPE_KEY = 1 OR BUSINESS_ENTITY_KEY IN (:businessEntity))")
public class BusinessEntity extends BaseBomEntity implements Serializable, Comparable<BusinessEntity> {

	@Serial
	private static final long serialVersionUID = 1L;

	/** Business entity type constants */
	public static final long UNKNOWN_TYPE = -1;	
	public static final long OPERATOR_TYPE = 0;	
	public static final long ENTERPRISE_TYPE = 1;
	public static final long MFG_TYPE = 2;
	public static final long SUPPLIER_TYPE = 3;

	/**
	 * Primary key - unique business entity identifier
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "businessEntitySeqGen")
	@SequenceGenerator(name = "businessEntitySeqGen", sequenceName = "BUSINESS_ENTITY_SEQ", allocationSize = 1)
	@Column(name = "BUSINESS_ENTITY_KEY", nullable = false, unique = true)
	private Long businessEntityKey;

	/**
	 * Associated contact information
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "CONTACT_KEY", unique = true)
	protected Contact contact;

	/**
	 * Unique business entity identifier
	 */
	@Column(name = "BUSINESS_ENTITY_IDENTIFIER", nullable = false)
	protected String businessEntityIdentifier;

	/**
	 * Business entity description
	 */
	@Column(name = "BUSINESS_ENTITY_DESC")
	protected String businessEntityDesc;

	/**
	 * Business entity name
	 */
	@Column(name = "BUSINESS_ENTITY_NAME")
	protected String businessEntityName;

	/**
	 * Business entity type key (references BUSINESS_ENTITY_TYPE table)
	 */
	@Column(name = "BUSINESS_ENTITY_TYPE_KEY", nullable = false)
	protected long businessEntityTypeKey = 1;

	@Column(name = "DATA_SOURCE", nullable = false)
	protected String dataSource = "MCM";

	/**
	 * Business entity type name (transient - fetched separately or computed from typeKey)
	 * Avoid using @Formula to prevent Hibernate alias issues
	 */
	@Transient
	protected String businessEntityTypeName;

	/**
	 * External ID for integration purposes
	 */
	@Column(name = "BUSINESS_ENTITY_EXTERNAL_ID", length = 255)
	protected String externalId;

	/**
	 * Additional attributes for this business entity
	 * Maps to BUSINESS_ENTITY_ADD_ATTRIBUTE table
	 */
	@ElementCollection
	@CollectionTable(name = "BUSINESS_ENTITY_ADD_ATTRIBUTE", joinColumns = @JoinColumn(name = "BUSINESS_ENTITY_KEY"))
	protected List<Attribute> attributes = new ArrayList<>();

	/**
	 * Alternate business entities
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "businessEntity", orphanRemoval = true)
	protected Set<BusinessEntityAlternate> alternates = new HashSet<>();

	/**
	 * Associated currencies for this business entity
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
		name = "BUSINESS_ENTITY_CURRENCY",
		joinColumns = @JoinColumn(name = "BUSINESS_ENTITY_KEY"),
		inverseJoinColumns = @JoinColumn(name = "CURRENCY_CODE"))
	protected Set<Currency> currencies = new HashSet<>();

	/**
	 * Associated sites for this business entity
	 */
	@OneToMany(mappedBy = "businessEntity")
	protected Set<Site> sites = new HashSet<>();

	/**
	 * Constructor with business entity key
	 * 
	 * @param key the business entity key
	 */
	public BusinessEntity(Long key) {
		super();
		this.businessEntityKey = key;
	}

	/**
	 * Add an attribute to this business entity
	 * 
	 * @param attr the attribute to add
	 * @return true if added, false if already exists
	 */
	public boolean addAttribute(Attribute attr) {
		if (attributes.contains(attr)) {
			return false;
		}
		return attributes.add(attr);
	}

	/**
	 * Get business entity type name from type key
	 * 
	 * @return the type name
	 */
	public String getBusinessEntityTypeName() {
		return getNameFromTypeKey(businessEntityTypeKey);
	}

	/**
	 * Add an alternate business entity
	 * 
	 * @param beAlt the alternate to add
	 * @return true if added, false if already exists
	 */
	public boolean addAlternate(BusinessEntityAlternate beAlt) {
		if (alternates.contains(beAlt)) {
			return false;
		}
		beAlt.setBusinessEntity(this);
		return alternates.add(beAlt);
	}

	/**
	 * Add a currency to this business entity
	 * 
	 * @param currency the currency to add
	 * @return true if added
	 */
	public boolean addCurrency(Currency currency) {
		return this.currencies.add(currency);
	}

	/**
	 * Remove a currency from this business entity
	 * 
	 * @param currency the currency to remove
	 * @return true if removed
	 */
	public boolean removeCurrency(Currency currency) {
		return this.currencies.remove(currency);
	}

	/**
	 * Custom hashCode based on businessEntityIdentifier
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(13, 37).append(this.getBusinessEntityIdentifier()).toHashCode();
	}

	/**
	 * Custom equals based on businessEntityIdentifier and businessEntityTypeKey
	 */
	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BusinessEntity))
			return false;
		BusinessEntity castOther = (BusinessEntity) other;

		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.getBusinessEntityIdentifier(), castOther.getBusinessEntityIdentifier());
		eb.append(this.getBusinessEntityTypeKey(), castOther.getBusinessEntityTypeKey());
		return eb.isEquals();
	}

	/**
	 * Compare business entities by identifier and name
	 */
	@Override
	public int compareTo(BusinessEntity o) {
		BusinessEntity other = o;
		return new CompareToBuilder()
			.append(this.getBusinessEntityIdentifier(), other.getBusinessEntityIdentifier())
			.append(this.getBusinessEntityName(), other.getBusinessEntityName())
			.toComparison();
	}

	/**
	 * String representation of business entity
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getBusinessEntityIdentifier());
		sb.append("[Name=").append(this.getBusinessEntityName());
		sb.append(", Type=").append(this.getBusinessEntityTypeName());
		sb.append(", Desc=").append(this.getBusinessEntityDesc());
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Get business entity type key from type name
	 * 
	 * @param businessType the type name
	 * @return the type key
	 */
	public static long getTypeFromName(String businessType) {
		if ("MANUFACTURER".equalsIgnoreCase(businessType)) {
			return MFG_TYPE;
		} else if ("SUPPLIER".equalsIgnoreCase(businessType)) {
			return SUPPLIER_TYPE;
		} else if ("OPERATOR".equalsIgnoreCase(businessType)) {
			return OPERATOR_TYPE;
		} else if ("ENTERPRISE".equalsIgnoreCase(businessType)) {
			return ENTERPRISE_TYPE;
		} else {
			return UNKNOWN_TYPE;
		}
	}

	/**
	 * Get business entity type name from type key
	 * 
	 * @param typeKey the type key
	 * @return the type name
	 */
	public static String getNameFromTypeKey(long typeKey) {
		if (typeKey == MFG_TYPE) {
			return "MANUFACTURER";
		} else if (typeKey == SUPPLIER_TYPE) {
			return "SUPPLIER";
		} else if (typeKey == OPERATOR_TYPE) {
			return "OPERATOR";
		} else if (typeKey == ENTERPRISE_TYPE) {
			return "ENTERPRISE";
		} else {
			return null;
		}
	}

	/**
	 * Get current state as JSON
	 * 
	 * @return ObjectNode with current state
	 */
	public ObjectNode getCurrentStateAsJSON() {
		ObjectMapper om = new ObjectMapper();
		ObjectNode o = om.createObjectNode();
		o.put("identifier", this.businessEntityIdentifier);
		o.put("description", this.businessEntityDesc);
		o.put("type", getBusinessEntityTypeName());
		o.put("externalId", this.externalId);
		return o;
	}

	/**
	 * Get natural key as JSON
	 * 
	 * @return ObjectNode with natural key components
	 */
	public ObjectNode getNaturalKeyAsJSON() {
		ObjectMapper om = new ObjectMapper();
		ObjectNode o = om.createObjectNode();
		o.put("identifier", this.businessEntityIdentifier);
		o.put("type", getBusinessEntityTypeName());
		return o;
	}

	/**
	 * Comparator for sorting business entities by name
	 */
	public static class NameSorter implements Comparator<BusinessEntity> {
		@Override
		public int compare(BusinessEntity o1, BusinessEntity o2) {
			return new CompareToBuilder()
				.append(o1.getBusinessEntityName(), o2.getBusinessEntityName())
				.toComparison();
		}
	}

	/**
	 * Get an attribute value by name
	 * 
	 * @param name the attribute name
	 * @return the attribute value or null if not found
	 */
	public Object getAttribute(String name) {
		for (Attribute attribute : attributes) {
			if (attribute.getAttrName().equals(name)) {
				return (Object) attribute.getAttrValue();
			}
		}
		return null;
	}
}