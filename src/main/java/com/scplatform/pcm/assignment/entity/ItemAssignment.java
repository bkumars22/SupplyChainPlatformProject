/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
 package com.scplatform.pcm.assignment.entity;


import com.scplatform.pcm.item.entity.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Comparator;

@Entity
@DiscriminatorValue("I")
public class ItemAssignment extends Assignment
{
	private static final long serialVersionUID = 1146618258900813415L;
	
	@ManyToOne
	@JoinColumn(name="OBJECT_KEY")
	Item item;	
	
	public ItemAssignment()
	{
		super();
	}
	
	public Item getItem()
	{
		return item;
	}

	public void setItem(Item item)
	{
		this.item = item;
	}
	
	public ObjectNode getAssignmentsNaturalKeyAsJSON() {
        ObjectMapper om = new ObjectMapper();
        ObjectNode o = om.createObjectNode();
        o.put("region",this.region);
        o.put("userId" , this.userId);
		ObjectNode responsibilityAsJSON = this.responsibility.getResponsibilityNaturalKeyAsJSON();
		o.put("responsibility", responsibilityAsJSON);
        return o;
    }

	@Override
    public int hashCode()
	{
		int result = new HashCodeBuilder(17, 37).append(this.getItem().getItemKey())
				.append(this.getResponsibility()).toHashCode();
		return result;
	}

	@Override
    public boolean equals(Object other)
	{
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ItemAssignment))
			return false;
		ItemAssignment castOther = (ItemAssignment) other;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.getItem(), castOther.getItem());
		eb.append(this.getResponsibility(), castOther.getResponsibility());
		eb.append(this.getUserId(), castOther.getUserId());
		eb.append(this.getSite(), castOther.getSite());
		return eb.isEquals();
	}
	
	public static class ItemAssignmentResponsibilityComparator implements Serializable, Comparator<ItemAssignment> {
		private static final long serialVersionUID = 2573497508971342712L;

		@Override
        public int compare(ItemAssignment o1, ItemAssignment o2) {
			int ret = 0;
			ret = new CompareToBuilder()
			       .append(o1.getResponsibility().getDisplayOrder(), o2.getResponsibility().getDisplayOrder())
			       .append(o1.getUserId(), o2.getUserId())
			       .toComparison();
			return ret;
		}
	}
}
