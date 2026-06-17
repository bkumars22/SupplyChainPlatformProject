/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.site.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import com.scplatform.pcm.bom.entity.BaseBomEntity;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.contact.entity.Contact;

/**
 * Models a site. A site is some specific location for a business entity. Most
 * commonly used to specify that a BOM is for a particular site
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "SITE")
@Getter
@Setter
public class Site extends BaseBomEntity implements java.io.Serializable {
	public static final String GLOBAL_TYPE = "GLOBAL";
	public static final String REGION_TYPE = "REGION";
	public static final String SITE_TYPE = "SITE";
	public static final String CCN_TYPE = "CCN";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "siteSeqGen")
	@SequenceGenerator(name = "siteSeqGen", sequenceName = "SITE_SEQ", allocationSize = 1)
	@Column(name = "SITE_KEY", nullable = false, unique = true)
	private Long siteKey;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BUSINESS_ENTITY_KEY")
	private BusinessEntity businessEntity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_SITE_KEY")
	private Site parentSite;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONTACT_KEY")
	private Contact contact;

	@Column(name = "SITE_IDENTIFIER", nullable = false)
	private String siteName;

	@Column(name = "SITE_DESCRIPTION")
	private String siteDescription;

	@Column(name = "SITE_TYPE")
	private String siteType;

	@Column(name = "DEFAULT_CURRENCY_CODE")
	private String defaultCurrencyCode;

	@Column(name = "DATA_SOURCE", nullable = false)
	private String dataSource = "MCM";

	@OneToMany(mappedBy = "parentSite", cascade = CascadeType.REMOVE)
	private Set<Site> childSites;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "SITE_DETAIL_ID", unique = true)
	private SiteDetails siteDetail;

	@OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<CCNSite> ccnSites = new HashSet<CCNSite>();

	@OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<SiteFacility> siteFacilities = new HashSet<SiteFacility>();

	// Constructors
	/** default constructor */
	public Site() {
		super();
	}

	/** constructor with id */
	public Site(long SiteKey) {
		super();
		this.siteKey = SiteKey;
	}

	public long getLevel() {
		return (parentSite != null) ? parentSite.getLevel() + 1 : 0;
	}

	/**
	 * Override setter to prevent self-reference
	 * @param parentSite the parent site to set
	 * @throws Exception if trying to set site as parent of itself
	 */
	public void setParentSite(Site parentSite) throws Exception {
		if (this == parentSite) {
			throw new Exception("Site cannot be parent of self");
		}
		this.parentSite = parentSite;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Site=").append(siteName);
		sb.append("[Desc=").append(siteDescription);
		sb.append(" ,Type=").append(siteType);
		sb.append("]");
		return sb.toString();
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof Site))
			return false;
		Site castOther = (Site) other;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.getSiteName(), castOther.getSiteName());
		eb.append(this.getSiteType(), castOther.getSiteType());
		eb.append(this.getBusinessEntity(), castOther.getBusinessEntity());
		eb.append(this.getParentSite(), castOther.getParentSite());
		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.getSiteType()).toHashCode();
	}

	/**
	 * Get teh site's natural key as a JSON
	 * 
	 * @return
	 */
	public ObjectNode getNaturalKeyAsJSON() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		node.put("siteName", this.siteName);
		if (this.businessEntity == null) {
			node.put("business", NullNode.instance);
		} else {
			node.put("business", this.businessEntity.getNaturalKeyAsJSON());
		}
		return node;
	}
}