/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */package com.scplatform.pcm.aml.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scplatform.pcm.item.entity.Item;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


/**
 * 
 * Implements the composite key for AVLs
 * 
 */
@SuppressWarnings("serial")
@Embeddable
public class AmlId implements java.io.Serializable
{
	// Fields
	@ManyToOne
	@JoinColumn(name="MANUFACTURER_ITEM_KEY",nullable=true)
	private Item mfgItem;

	@ManyToOne
	@JoinColumn(name="ITEM_KEY",nullable=true)
	private Item item;

	// Constructors
	/** default constructor */
	public AmlId()
	{
	}

	public AmlId(Item item,Item mfgItem)
	{
		this.mfgItem = mfgItem;
		this.item = item;
	}

	// Property accessors
	/**
	 * 
	 */
	public Long getItemKey()
	{
		return (item != null) ? item.getItemKey() : null;
	}

	/**
	 * 
	 */
	public Long getMfgItemKey()
	{
		return (mfgItem != null) ? mfgItem.getItemKey() : null;
	}

	public boolean equals(Object other)
	{
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof AmlId))
			return false;
		AmlId castOther = (AmlId) other;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.getItem(), castOther.getItem());
		eb.append(this.getMfgItem(), castOther.getMfgItem());
		return eb.isEquals();
	}

	public int hashCode()
	{
		int result = new HashCodeBuilder(17, 37).append(this.getItemKey())
				.append(this.getMfgItemKey()).toHashCode();
		return result;
	}

	/**
	 * 
	 * @param mfgItem
	 *            The mfgItem to set.
	 * 
	 */
	public void setMfgItem(Item mfgItem)
	{
		this.mfgItem = mfgItem;
	}

	/**
	 * 
	 * @return Returns the mfgItem.
	 * 
	 */
	public Item getMfgItem()
	{
		return mfgItem;
	}

	/**
	 * 
	 * @param item
	 *            The item to set.
	 * 
	 */
	public void setItem(Item item)
	{
		this.item = item;
	}

	/**
	 * 
	 * @return Returns the item.
	 * 
	 */
	public Item getItem()
	{
		return item;
	}

	public String toString()
	{
		return "(" + getItemKey() + "," + getMfgItemKey() + ")";
	}
}