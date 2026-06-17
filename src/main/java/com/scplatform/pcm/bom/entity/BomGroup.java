/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.bom.entity;

import java.util.HashSet;
import java.util.Set;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * A BomGroup models a logical group of BOM instances. A BOM can belong to
 * multiple
 * 
 * BOM groups and BomGroup instances can contain multiple BOM instances.
 * 
 * For instance a BOM may belong to the DESIGN and ASSEMBLE group.
 * 
 * 
 * 
 * A BomGroup is linked to a BOM via the BomGroupLink class. A link can be
 * established
 * 
 * by invoking the addBom method.
 * 
 */
@Entity
@Table(name = "BOM_GROUP")
@Getter
@Setter
@SuppressWarnings("serial")
public class BomGroup extends BaseBomEntity implements java.io.Serializable
{
	// Fields
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOM_GROUP_SEQ_GEN")
	@SequenceGenerator(name = "BOM_GROUP_SEQ_GEN", sequenceName = "BOM_GROUP_SEQ", allocationSize = 1)
	@Column(name = "BOM_GROUP_KEY", nullable = false, unique = true)
	private Long bomGroupKey;

	@Column(name = "OWNER_NAME", nullable = false)
	private String ownerName;

	@Column(name = "GROUP_NAME", nullable = false)
	private String groupName;

	@Column(name = "DATA_SOURCE", nullable = false)
	private String dataSource;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BUSINESS_ENTITY_KEY", nullable = true)
	private BusinessEntity businessEntity;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "bomGroup")
	private Set<BomGroupLink> bomGroupLinks = new HashSet<>();

	// Constructors
	/** default constructor */
	public BomGroup()
	{
	}

	/** constructor with id */
	public BomGroup(Long BomGroupKey)
	{
		this.bomGroupKey = BomGroupKey;
	}

	public void addBom(Bom bom)
	{
		bomGroupLinks.add(new BomGroupLink(bom, this));
	}

	public void removeLink(BomGroupLink link)
	{
		if (bomGroupLinks.remove(link))
		{
			link.getBom().getBomGroupLinks().remove(link);
		}
	}
}