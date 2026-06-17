/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.avl.entity;

import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.Attribute;
import com.scplatform.pcm.common.entity.TrackDelta;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.type.YesNoConverter;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.*;

/**
 * 
 * Models the approved vendor list.
 * 
 */
@Entity
@Table(name = "ITEM_AVL")
@FilterDef(name = "supplierFilter", parameters = @ParamDef(name = "businessEntity", type = Long.class))
@Filter(name = "supplierFilter", condition = "(BUSINESS_ENTITY_KEY IN (:businessEntity))")
@SuppressWarnings("serial")
public class Avl implements TrackDelta, Serializable {
	// Fields
	@Id
	@SequenceGenerator(name = "ITEM_AVL_SEQ", sequenceName = "ITEM_AVL_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "ITEM_AVL_SEQ")
	@Column(name = "AVL_KEY")
	private Long avlKey;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BUSINESS_ENTITY_KEY", nullable = false)
	private BusinessEntity supplier;

	@Transient
	private String suppliedBy;

	@ManyToOne
	@JoinColumn(name = "BOM_KEY")
	private Bom bom;

	@Column(name = "PREFERRED_STATUS_CODE", length = 50)
	private String preferredStatusCode;

	@Column(name = "DESCRIPTION")
	private String description;

	@Convert(converter = YesNoConverter.class)
	@Column(name = "CURRENT_FLAG", nullable = false)
	private boolean currentFlag;

	@Convert(converter = YesNoConverter.class)
	@Column(name = "DELETE_FLAG")
	private Boolean deleteFlag = Boolean.FALSE;

	@Column(name = "INSERT_DT", nullable = false)
	private Date insertDate = new Date();

	@Column(name = "UPDATE_DT")
	private Date updateDate;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "PCM_ITEM_AVL_SITE",
		joinColumns = @JoinColumn(name = "AVL_KEY"),
		inverseJoinColumns = @JoinColumn(name = "SITE_KEY")
	)
	private Set<Site> supplierSites = new HashSet<Site>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ITEM_KEY")
	private Item item;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SUPPLIER_PART_KEY")
	private Item supplierItem;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "ITEM_AVL_ADD_ATTRIBUTE", joinColumns = @JoinColumn(name = "AVL_KEY", nullable = false))
	@OrderColumn(name = "ATTRIBUTE_KEY")
	private List<Attribute> attributes = new ArrayList<Attribute>();

	// Constructors
	/** default constructor */

	public Avl() {

	}
	
	/** constructor with id */
    public Avl(Long avlKey)
    {
        this.avlKey = avlKey;
    }

	public Long getAvlKey() {
		return this.avlKey;
	}

	public void setAvlKey(Long avlKey) {
		this.avlKey = avlKey;
	}

	/**
	 * 
	 * @param item
	 *            The mfgItem to set.
	 * 
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * 
	 * @return Returns the mfgItem.
	 * 
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * 
	 * @param item
	 *            The item to set.
	 * 
	 */
	public void setSupplierItem(Item item) {
		this.supplierItem = item;
	}

	/**
	 * 
	 * @return Returns the item.
	 * 
	 */
	public Item getSupplierItem() {
		return supplierItem;
	}

	/**
	 * 
	 * @param supplierSites
	 *            <Site> supplierSites
	 * 
	 * 
	 */
	public void setSupplierSites(Set<Site> supplierSites) {
		this.supplierSites = supplierSites;
	}

	/**
	 * 
	 * @return Returns the supplierSites.
	 * 
	 */
	public Set<Site> getSupplierSites() {
		return supplierSites;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * 
	 */
	public String getPreferredStatusCode() {
		return this.preferredStatusCode;
	}

	public void setPreferredStatusCode(String PerferredStatusCode) {
		this.preferredStatusCode = PerferredStatusCode;
	}

	/**
	 * 
	 */
	public String getSuppliedBy() {
		return suppliedBy;
	}

	public void setSuppliedBy(String suppliedBy) {
		this.suppliedBy = suppliedBy;
	}

	/**
	 * 
	 * @param supplier
	 *            The supplier to set.
	 * 
	 */
	public void setSupplier(BusinessEntity supplier) {
		this.supplier = supplier;
		setSuppliedBy(supplier.getBusinessEntityName());

	}

	/**
	 * 
	 * @return Returns the supplier.
	 * 
	 */
	public BusinessEntity getSupplier() {
		return supplier;
	}

	// Property accessors
	public Long getItemKey() {
		return (item != null) ? item.getItemKey() : null;
	}

	public Long getSupplierItemKey() {
		return (supplierItem != null) ? supplierItem.getItemKey() : null;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof Avl))
			return false;
		Avl castOther = (Avl) other;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.getSupplierItem(), castOther.getSupplierItem());
		eb.append(this.getItem(), castOther.getItem());
		return eb.isEquals();
	}

	public int hashCode() {
		int result = new HashCodeBuilder(17, 37).append(this.getItemKey())
				.append(this.getSupplierItemKey()).toHashCode();
		return result;
	}

	/**
	 * 
	 * @param bom
	 *            The bom to set.
	 * 
	 */
	public void setBom(Bom bom) {
		this.bom = bom;
	}

	/**
	 * 
	 * @return Returns the bom.
	 * 
	 */
	public Bom getBom() {
		return bom;
	}

	public String toString() {
		return "(" + getItem().getItemNumber() + ","
				+ getSupplierItem().getItemNumber() + ")";
	}
	
	public String getTitle() {
	    return getTitle(getItem());
        }
	
	public String getTitle(Item item) {
            List<Object> args = new ArrayList<Object>();
            args.add(item != null ? item.getItemNumber() : "");
            args.add(getSupplierItem().getItemNumber());
            args.add(getSupplier().getBusinessEntityName());
            if(getSupplierSites() != null && getSupplierSites().size() > 0) {
                List<String> sites = new ArrayList<String>();
                for(Site site : getSupplierSites()) {
                    sites.add(site.getSiteDescription());
                }
                args.add(sites.toString());
                return SCPlatformMessages.INSTANCE.getAuditMessage("audit.avlTitleWithSite", args.toArray(), null);
            }
            return SCPlatformMessages.INSTANCE.getAuditMessage("audit.avlTitle", args.toArray(), null);
        }

	public boolean getCurrentFlag() {
		return currentFlag;
	}

	public void setCurrentFlag(boolean currentFlag) {
		this.currentFlag = currentFlag;
	}

	public Boolean getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;

	}
	
	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public Object getAttribute(String name) {
		for (Attribute attribute : attributes) {
			if (attribute.getAttrName().equals(name)) {
				return (Object) attribute.getAttrValue();
			}
		}
		return null;
	}

	public boolean addAttribute(Attribute attr) {

		if (attributes.contains(attr)) {
			return false;
		}
		attributes.add(attr);
		return true;
	}

	public ObjectNode getAvlsNaturalKeyAsJSON() {
        ObjectMapper om = new ObjectMapper();
        ObjectNode o = om.createObjectNode();
        o.put("suppliedBy",this.suppliedBy);
        o.put("supplierItemKey",this.getSupplierItem().getItemKey());
        o.put("supplierItemNumber",this.getSupplierItem().getItemNumber());
        o.put("supplierItemDescription",this.getSupplierItem().getDescription());        
        return o;
    }
}