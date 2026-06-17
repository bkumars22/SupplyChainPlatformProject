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


import com.scplatform.pcm.item.entity.ItemCategory;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("IC")
public class ItemCategoryAssignment extends Assignment
{
	@ManyToOne
	@JoinColumn(name="OBJECT_KEY")
	ItemCategory category;	
	
	public ItemCategoryAssignment()
	{
		super();
	}
	
	public ItemCategory getItemCategory()
	{
		return category;
	}

	public void setItemCategory(ItemCategory category)
	{
		this.category = category;
	}


}
