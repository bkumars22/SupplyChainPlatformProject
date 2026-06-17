/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */package com.scplatform.pcm.aml.entity;


import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.TrackDelta;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.type.YesNoConverter;

import java.util.Date;



/**
 * 
 * Models the approved manufacturer list.
 * 
 */
@Entity
@Table(name="ITEM_AML")
public class Aml implements TrackDelta
{
	// Fields
	@EmbeddedId
	private AmlId amlId;
	
	@ManyToOne
	@JoinColumn(name="MANUFACTURER_KEY",nullable=false)
	private BusinessEntity mfg;
	
	@Transient
	private String mfgBy;
	
	@ManyToOne
	@JoinColumn(name="SITE_KEY",nullable=true)
	private Site site;
	
	@ManyToOne
	@JoinColumn(name="BOM_KEY",nullable=true)
	private Bom bom;
	
	@Column(name="PART_STATUS_CODE",length=50,nullable=true)
	private String partStatusCode;
	
	@Transient
	private String partStatusCodeOther;
	
	@Column(name="PREFERRED_STATUS",length=50,nullable=true)
	private String preferredStatusCode;
	
	@Column(name="DESCRIPTION",nullable=true)
	private String description;
	
	@Column(name="CURRENT_FLAG",nullable=false,length=1)
	@Convert(converter = YesNoConverter.class)
	private boolean currentFlag;
	
	@Column(name="DELETE_FLAG",nullable=true,length=1)
	@Convert(converter = YesNoConverter.class)
	private Boolean deleteFlag = Boolean.FALSE;
	
	@Column(name="INSERT_DT",nullable=false,columnDefinition="TIMESTAMP")
	private Date insertDate = new Date();
	
	@Column(name="UPDATE_DT",nullable=true,columnDefinition="TIMESTAMP")
	private Date updateDate;

	// Constructors
	/** default constructor */
	public Aml()
	{
		this.amlId = new AmlId();
	}

	/** constructor with id */
	public Aml(AmlId amlId)
	{
		this.amlId = amlId;
	}

	// Property accessors
	/**
	 * 
	 */
	public AmlId getAmlId()
	{
		return this.amlId;
	}

	public void setAmlId(AmlId amlId)
	{
		this.amlId = amlId;
	}

	/**
	 * 
	 */
	public Item getMfgItem()
	{
		return amlId.getMfgItem();
	}

	public void setMfgItem(Item mfgItem)
	{
		this.amlId.setMfgItem(mfgItem);
	}

	/**
	 * 
	 */
	public Item getItem()
	{
		return amlId.getItem();
	}

	public void setItem(Item item)
	{
		this.amlId.setItem(item);
	}

	/**
	 * 
	 */
	public String getPartStatusCode()
	{
		return this.partStatusCode;
	}

	public void setPartStatusCode(String PartStatusCode)
	{
		this.partStatusCode = PartStatusCode;
	}

	/**
	 * 
	 */
	public String getPartStatusCodeOther()
	{
		return this.partStatusCodeOther;
	}

	public void setPartStatusCodeOther(String PartStatusCodeOther)
	{
		this.partStatusCodeOther = PartStatusCodeOther;
	}

	/**
	 * 
	 */
	public String getPreferredStatusCode()
	{
		return this.preferredStatusCode;
	}

	public void setPreferredStatusCode(String PerferredStatusCode)
	{
		this.preferredStatusCode = PerferredStatusCode;
	}

	public String getMfgBy()
	{
		return mfgBy;
	}

	public void setMfgBy(String mfgBy)
	{
		this.mfgBy = mfgBy;
	}

	/**
	 * 
	 * @param mfg
	 *            The mfg to set.
	 * 
	 */
	public void setMfg(BusinessEntity mfg)
	{
		this.mfg = mfg;
		setMfgBy(mfg.getBusinessEntityName());
	}

	/**
	 * 
	 * @return Returns the mfg.
	 * 
	 */
	public BusinessEntity getMfg()
	{
		return mfg;
	}

	/**
	 * 
	 * @param site
	 *            The site to set.
	 * 
	 */
	public void setSite(Site site)
	{
		this.site = site;
	}

	/**
	 * 
	 * @return Returns the site.
	 * 
	 */
	public Site getSite()
	{
		return site;
	}

	public boolean equals(Object other)
	{
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof Aml))
			return false;
		Aml castOther = (Aml) other;
		return this.getAmlId().equals(castOther.getAmlId());
	}

	public int hashCode()
	{
		return this.getAmlId().hashCode();
	}

	/**
	 * 
	 * @param bom
	 *            The bom to set.
	 * 
	 */
	public void setBom(Bom bom)
	{
		this.bom = bom;
	}

	/**
	 * 
	 * @return Returns the bom.
	 * 
	 */
	public Bom getBom()
	{
		return bom;
	}

	/**
	 * 
	 * @param description
	 *            The description to set.
	 * 
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * 
	 * @return Returns the description.
	 * 
	 */
	public String getDescription()
	{
		return description;
	}

	public String toString()
	{
		return amlId.toString() + " " + mfgBy + " " + description;
	}

	public boolean getCurrentFlag()
	{
		return currentFlag;
	}

	public void setCurrentFlag(boolean currentFlag)
	{
		this.currentFlag = currentFlag;
	}

	public Boolean getDeleteFlag()
	{
		return deleteFlag;
	}

	public void setDeleteFlag(Boolean deleteFlag)
	{
		this.deleteFlag = deleteFlag;
	}


	public Date getInsertDate()
	{
		return insertDate;
	}

	public void setInsertDate(Date insertDate)
	{
		this.insertDate = insertDate;		
	}

	public Date getUpdateDate()
	{
		return updateDate;
	}

	public void setUpdateDate(Date updateDate)
	{
		this.updateDate = updateDate;
		
	}
	
	public ObjectNode getAmlsNaturalKeyAsJSON() {
        ObjectMapper om = new ObjectMapper();
        ObjectNode o = om.createObjectNode();
        o.put("mfgBy",this.mfgBy);
        o.put("mfgItemKey",this.getMfgItem().getItemKey());
        o.put("mfgItemNumber",this.getMfgItem().getItemNumber());
        o.put("mfgItemDescription",this.getMfgItem().getDescription());       
        return o;
    }
}