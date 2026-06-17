/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.item.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Parent;

import java.util.Date;

@Embeddable
@SuppressWarnings("serial")
public class ItemAlternate implements java.io.Serializable
{
	@Parent
	private Item item;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALTERNATE_ITEM_KEY", nullable = false)
	private Item alternateItem;

	@Column(name = "PREFERRED_STATUS_CODE", length = 50)
	private String preferredStatusCode;

	@Temporal(TemporalType.DATE)
	@Column(name = "PREFERRED_STATUS_START")
	private Date preferredStartDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "PREFERRED_STATUS_END")
	private Date preferredEndDate;	
	
	public ItemAlternate()
	{
		
	}

	public ItemAlternate(Item item, Item alternateItem)
	{
		this.item = item;
		this.alternateItem = alternateItem;
	}
	
	public void setItem(Item item)
	{
		this.item = item;
	}

	public Item getItem()
	{
		return item;
	}

	public void setAlternateItem(Item alternateItem)
	{
		this.alternateItem = alternateItem;
	}

	public Item getAlternateItem()
	{
		return alternateItem;
	}

	public void setPreferredStatusCode(String preferredStatusCode)
	{
		this.preferredStatusCode = preferredStatusCode;
	}

	public String getPreferredStatusCode()
	{
		return preferredStatusCode;
	}

	public void setPreferredStartDate(Date preferredStartDate)
	{
		this.preferredStartDate = preferredStartDate;
	}

	public Date getPreferredStartDate()
	{
		return preferredStartDate;
	}

	public void setPreferredEndDate(Date preferredEndDate)
	{
		this.preferredEndDate = preferredEndDate;
	}

	public Date getPreferredEndDate()
	{
		return preferredEndDate;
	}
	
	public ObjectNode getAlternatesNaturalKeyAsJSON() {
        ObjectMapper om = new ObjectMapper();
        ObjectNode o = om.createObjectNode();
        o.put("alternateItem",this.getAlternateItem().getItemNumber());
        return o;
    }
	
	public boolean equals(Object other)
	{
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ItemAlternate))
			return false;
		ItemAlternate castOther = (ItemAlternate) other;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(getItem(), castOther.getItem());
		eb.append(getAlternateItem(), castOther.getAlternateItem());
		return eb.isEquals();
	}
	
	public int hashCode()
	{
		int result = new HashCodeBuilder(17, 37).append(this.getItem())
				.append(this.getAlternateItem()).toHashCode();
		return result;
	}
	
}
