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

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * 
 * Models the link between a bom and a bom group.  This is a many to many
 * relationship in the db
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name="BOM_BOM_GROUP",uniqueConstraints=@UniqueConstraint(columnNames= {"BOM_GROUP_KEY","BOM_KEY"}))
@IdClass(BomGroupLinkId.class)
public class BomGroupLink implements java.io.Serializable
{
	// Fields
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="BOM_GROUP_KEY",nullable=false)
	private BomGroup bomGroup;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="BOM_KEY",nullable=false)
	private Bom bom;

	// Constructors
	/** default constructor */
	public BomGroupLink()
	{
	}

	public BomGroupLink(Bom bom, BomGroup group)
	{
		this.bom = bom;
		this.bomGroup = group;
	}

	// Property accessors
	/**
	 * 
	 */
	public BomGroup getBomGroup()
	{
		return this.bomGroup;
	}

	public void setBomGroup(BomGroup BomGroup)
	{
		this.bomGroup = BomGroup;
	}

	/**
	 * 
	 */
	public Bom getBom()
	{
		return this.bom;
	}

	public void setBom(Bom Bom)
	{
		this.bom = Bom;
	}

	public boolean equals(Object other)
	{
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BomGroupLink))
			return false;
		BomGroupLink castOther = (BomGroupLink) other;
		return (this.getBomGroup() == castOther.getBomGroup())
				|| (this.getBomGroup() == null ? false : (castOther
						.getBomGroup() == null ? false : this.getBomGroup()
						.equals(castOther.getBomGroup())))
				&& (this.getBom() == castOther.getBom())
				|| (this.getBom() == null ? false
						: (castOther.getBom() == null ? false : this.getBom()
								.equals(castOther.getBom())));
	}

	public int hashCode()
	{
		int result = 17;
		result = 37 * result + this.getBomGroup().hashCode();
		result = 37 * result + this.getBom().hashCode();
		return result;
	}
}