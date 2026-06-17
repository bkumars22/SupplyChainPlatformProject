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

import com.scplatform.pcm.assignment.entity.ItemCategoryAssignment;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "ITEM_CATEGORY")
@SuppressWarnings("serial")
public class ItemCategory implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ITEM_CATEGORY_SEQ_GEN")
	@SequenceGenerator(name = "ITEM_CATEGORY_SEQ_GEN", sequenceName = "ITEM_CATEGORY_SEQ", allocationSize = 1)
	@Column(name = "ITEM_CATEGORY_KEY")
	protected Long categoryKey;

	@NaturalId(mutable = true)
	@Column(name = "ITEM_CATEGORY_IDENTIFIER")
	protected String categoryId;

	@NaturalId(mutable = true)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BUSINESS_ENTITY_KEY")
	protected BusinessEntity businessEntity;

	@Column(name = "ITEM_CATEGORY_NAME")
	protected String categoryName;

	@Column(name = "PRODUCT_FAMILY")
	protected String productFamily;

	@Column(name = "MANAGED_FLAG")
	protected String managedFlag;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_CATEGORY_KEY")
	protected ItemCategory parentCategory;

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
	protected Set<Item> items;

	@OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
	protected Set<ItemCategoryAssignment> assignments;
    
	
	public ItemCategory()
	{
		super();
	}
	
	public ItemCategory(Long categoryKey)
	{
		super();
		this.categoryKey = categoryKey;
	}
	
	public Long getCategoryKey()
	{
		return categoryKey;
	}
	public void setCategoryKey(Long categoryKey)
	{
		this.categoryKey = categoryKey;
	}
	public String getCategoryId()
	{
		return categoryId;
	}
	public void setCategoryId(String categoryId)
	{
		this.categoryId = categoryId;
	}
	public String getCategoryName()
	{
		return categoryName;
	}
	public void setCategoryName(String categoryName)
	{
		this.categoryName = categoryName;
	}
	public BusinessEntity getBusinessEntity()
	{
		return businessEntity;
	}
	public void setBusinessEntity(BusinessEntity businessEntity)
	{
		this.businessEntity = businessEntity;
	}
	public ItemCategory getParentCategory()
	{
		return parentCategory;
	}
	public void setParentCategory(ItemCategory parentCategory)
	{
		this.parentCategory = parentCategory;
	}
	public String getProductFamily()
	{
		return productFamily;
	}
	public void setProductFamily(String productFamily)
	{
		this.productFamily = productFamily;
	}
	public String getManagedFlag()
	{
		return managedFlag;
	}
	public void setManagedFlag(String managedFlag) {
		this.managedFlag = StringUtils.upperCase(managedFlag);
	}
	
	public String getAuditTitle() {
	    return SCPlatformMessages.INSTANCE.getAuditMessage("audit.itemCategoryTitle", new Object[]{this.categoryId,this.categoryName}, null);
	}

	public Set<Item> getItems() {
		return items;
	}

	public void setItems(Set<Item> items) {
		this.items = items;
	}

	public Set<ItemCategoryAssignment> getAssignments() {
		return assignments;
	}

	public void setAssignments(Set<ItemCategoryAssignment> assignments) {
		this.assignments = assignments;
	}
	
	public ObjectNode getCategoriesNaturalKeyAsJSON() {
        ObjectMapper om = new ObjectMapper();
        ObjectNode o = om.createObjectNode();
        o.put("categoryName",this.categoryName);
        return o;
    }

	@Override
	public int hashCode() {
		int result = new HashCodeBuilder(17, 37).append(this.getCategoryId())
				.append(this.getBusinessEntity())
				.toHashCode();
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ItemCategory))
			return false;
		ItemCategory castOther = (ItemCategory) other;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.getCategoryId(), castOther.getCategoryId());
		eb.append(this.getBusinessEntity(), castOther.getBusinessEntity());
		return eb.isEquals();
	}

	@Override
	public String toString() {
		return  categoryName;
	}
    
}
