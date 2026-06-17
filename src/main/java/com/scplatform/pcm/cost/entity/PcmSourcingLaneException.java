/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.cost.entity;



import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.StatefulBase;
import com.scplatform.pcm.common.entity.TrackDelta;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.scplatform.pcm.util.stateMachine.StateMachineReactor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.hibernate.type.YesNoConverter;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "PCM_SOURCING_LANE_EXCEPTION")
@Getter
@Setter
@NoArgsConstructor
@Filters({ @Filter(name = "businessFilter", condition = "(EXISTS (SELECT 1 FROM IV_SOURCING_BUSINESS_ENTITY SBE "
		+ "	WHERE SBE.SOURCING_LANE_KEY = SOURCING_LANE_KEY " + "	AND (SBE.SUPPLIER_KEY IN (:businessEntity) OR "
		+ "	SBE.BUSINESS_ENTITY_KEY IN (:businessEntity) OR " + "	SBE.COST_PROVIDER_KEY IN (:businessEntity))))"),
		@Filter(name = "categoryFilter", condition = "(EXISTS (SELECT 1 FROM ITEM_ITEM_CATEGORY IIC "
				+ "	WHERE IIC.ITEM_KEY = ITEM_KEY AND IIC.ITEM_CATEGORY_KEY IN(SELECT DISTINCT PAC.TARGET_ENTITY_KEY "
				+ "	FROM PCM_ACCESS_CONTROL PAC WHERE PAC.ACL='Read' AND PAC.ENTITY_TYPE='CATEGORY' "
				+ "   AND (PAC.USER_KEY = :user OR PAC.ROLE_KEY = :role))))"),
		@Filter(name = "platformFilter", condition = "((EXISTS (SELECT 1 FROM ITEM_ITEM_PLATFORM IIP "
				+ "	WHERE IIP.ITEM_KEY = ITEM_KEY AND IIP.ITEM_PLATFORM_KEY IN(SELECT DISTINCT PAC.TARGET_ENTITY_KEY "
				+ "	FROM PCM_ACCESS_CONTROL PAC WHERE PAC.ACL='Read' AND PAC.ENTITY_TYPE='PLATFORM' "
				+ "   AND (PAC.USER_KEY = :user OR PAC.ROLE_KEY = :role)))) "
				+ "   OR (NOT EXISTS (SELECT 1 FROM ITEM_ITEM_PLATFORM IIP2 WHERE IIP2.ITEM_KEY = ITEM_KEY) "
				+ "   AND EXISTS (SELECT 1 FROM PCM_ACCESS_CONTROL PAC WHERE PAC.ACL='Read' AND PAC.ENTITY_TYPE='PLATFORM' AND PAC.TARGET_ENTITY_KEY='-1' "
				+ "   AND (PAC.USER_KEY = :user OR PAC.ROLE_KEY = :role))))"),
		@Filter(name = "siteFilter", condition = "(TO_SITE_KEY IS NULL OR TO_SITE_KEY IN (SELECT DISTINCT PAC.TARGET_ENTITY_KEY "
				+ "	FROM PCM_ACCESS_CONTROL PAC WHERE PAC.ACL='Read' AND PAC.ENTITY_TYPE='SITE' "
				+ "   AND (PAC.USER_KEY = :user OR PAC.ROLE_KEY = :role)))") })
@SuppressWarnings("serial")
public class PcmSourcingLaneException extends StatefulBase implements TrackDelta,Serializable {
	private static final Logger logger = LogManager.getLogger(PcmSourcingLaneException.class);

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PCM_SOURCING_LANE_EXCEPTION_SEQ")
	@SequenceGenerator(sequenceName = "PCM_SOURCING_LANE_EXCEPTION_SEQ", name = "PCM_SOURCING_LANE_EXCEPTION_SEQ", allocationSize = 1, initialValue = 1)
	@Column(name = "SOURCING_LANE_KEY")
	private Long sourcingLaneKey;

	@ManyToOne(optional = true)
	@JoinColumn(name = "OWNER_KEY")
	@Fetch(value = FetchMode.SELECT)
	private Users owner;

	@NaturalId(mutable = true)
	@ManyToOne(optional = false)
	@JoinColumn(name = "ITEM_KEY", nullable = false)
	@Fetch(value = FetchMode.SELECT)
	private Item item;

	@NaturalId(mutable = true)
	@ManyToOne(optional = true)
	@JoinColumn(name = "BOM_KEY", nullable = true)
	@Fetch(value = FetchMode.SELECT)
	private Bom bom;

	@NaturalId(mutable = true)
	@ManyToOne(optional = true)
	@JoinColumn(name = "FROM_SITE_KEY", nullable = true)
	@Fetch(value = FetchMode.SELECT)
	private Site fromSite;

	@NaturalId(mutable = true)
	@ManyToOne(optional = true)
	@JoinColumn(name = "TO_SITE_KEY", nullable = true)
	@Fetch(value = FetchMode.SELECT)
	private Site toSite;

	@NaturalId(mutable = true)
	@ManyToOne(optional = true)
	@JoinColumn(name = "SUPPLIER_KEY", nullable = true)
	@Fetch(value = FetchMode.SELECT)
	private BusinessEntity supplier;

	@NaturalId(mutable = true)
	@Column(name = "SOURCING_LANE_NAME", length = 512, nullable = false)
	private String sourcingLaneName;

	@Column(name = "SOURCING_LANE_EXTERNAL_ID")
	private String sourcingLaneExternalId;

	@Column(name = "DATE_OFFSET", precision = 10, scale = 0)
	private Long dateOffset;

	@Column(name = "PRODUCT_STATE", length = 32)
	private String productState;

	@Column(name = "ENDDATE_REQUIRED", length = 1, nullable = false)
    @Convert(converter = YesNoConverter.class)
	private boolean endDateRequired = Boolean.FALSE;

	@Column(name = "CURRENCY_CODE", length = 3)
	private String currencyCode;

	@Column(name = "SYSTEM_DERIVED", length = 1, nullable = false)
    @Convert(converter = YesNoConverter.class)
	private boolean systemDerived = Boolean.FALSE;

	@Column(name = "DESCRIPTION", length = 1024)
	private String description;

	@Column(name = "INSERT_DT", nullable = false)
	private Date insertDt = new Date();

	@Column(name = "UPDATE_DT")
	private Date updateDt;

	@Column(name = "DELETE_FLAG", length = 1)
    @Convert(converter = YesNoConverter.class)
	private Boolean deleteFlag = Boolean.FALSE;

	@Column(name = "CURRENT_FLAG", length = 1, nullable = false)
    @Convert(converter = YesNoConverter.class)
	private boolean currentFlag = Boolean.TRUE;

	@Column(name = "COLLABORATION", length = 1)
    @Convert(converter = YesNoConverter.class)
	private Boolean collaboration = Boolean.FALSE;

	@Column(name = "COST_PROV_BUSINESS_ENTITY", length = 255)
	private String costProviderBusinessEntity;

	@OneToMany(mappedBy = "sourcingLaneException")
	private Set<PcmCostRecordException> pcmCostRecordsException;

	@Column(name = "IS_AVL_EXISTS", length = 1, nullable = false)
    @Convert(converter = YesNoConverter.class)
	private Boolean isAVLExists = Boolean.FALSE;

	public PcmSourcingLaneException(Long sourcingLaneKey, Item item, String sourcingLaneName, String status,
			Date insertDt, boolean currentFlag) {
		super();
		this.sourcingLaneKey = sourcingLaneKey;
		this.item = item;
		this.sourcingLaneName = sourcingLaneName;
		this.status = status;
		this.insertDt = insertDt;
		this.currentFlag = currentFlag;
	}

	public boolean addCostRecord(PcmCostRecordException pcr) {
		pcr.setSourcingLaneException(this);
		return pcmCostRecordsException.add(pcr);
	}

	public boolean removeCostRecord(PcmCostRecordException pcr) {
        if (pcmCostRecordsException.remove(pcr)) {
            pcr.setSourcingLaneException(null);
            return true;
        } else {
            return false;
        }
    }

	@Override
	public Collection<StateMachineReactor> getChildren() {
		return List.of();
	}

	@Override
	public StateMachineReactor getParent() {
		return null;
	}

	/**
	 * Returns the generated ID for the lane which is a combination of the othe
	 * location data
	 * 
	 * @return - dynamically generated ID, this will change as the lane does
	 */
	public String getTitle() {
		StringBuilder name = new StringBuilder();
		if (getItem() != null) {
			name.append(getItem().getItemNumber());
		}
		if (getBom() != null) {
			name.append("-").append(getBom().getBomName());
		}
		String shortTitle = getShortTitle();
		if (shortTitle.length() > 0) {
			name.append("-").append(shortTitle);
		}
		return name.toString();
	}

	/**
	 * Gets the audit message using the sourcing lane attributes
	 * 
	 * @return
	 */
	public String getAuditTitle() {
		List<Object> args = new ArrayList<Object>();
		args.add(getItem() != null ? getItem().getItemNumber() : "");
		args.add(getSupplier() != null ? getSupplier().getBusinessEntityName() : "");
		args.add(getFromSite() != null ? getFromSite().getSiteDescription() : "");
		args.add(getToSite() != null ? getToSite().getSiteDescription() : "");
		args.add(getCurrencyCode() != null ? getCurrencyCode() : "");
		return SCPlatformMessages.INSTANCE.getAuditMessage("audit.sourcingLane", args.toArray(), null);
	}

	/**
	 * Does not contain the item or BOM
	 * 
	 * @return
	 */
	public String getShortTitle() {
		StringBuilder name = new StringBuilder();
		if (getSupplier() != null) {
			if (name.length() > 0) {
				name.append("-");
			}
			name.append(getSupplier().getBusinessEntityName());
		}
		if (getFromSite() != null) {
			if (name.length() > 0) {
				name.append("-");
			}
			name.append(getFromSite().getSiteDescription());
		}
		if (getToSite() != null) {
			if (name.length() > 0) {
				name.append("-");
			}
			name.append(getToSite().getSiteDescription());
		}
		if (getCurrencyCode() != null) {
			if (name.length() > 0) {
				name.append("-");
			}
			name.append(getCurrencyCode());
		}
		if (getBom() != null) {
			name.append(" (NM)");
		}
		return name.toString();
	}

	public String getCostProviderBusinessEntity() {
		return costProviderBusinessEntity;
	}

	public void setCostProviderBusinessEntity(String costProviderBusinessEntity) {
		this.costProviderBusinessEntity = costProviderBusinessEntity;
	}

	@Override
	public String toString() {
		return getTitle();
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof PcmSourcingLaneException))
			return false;
		PcmSourcingLaneException castOther = (PcmSourcingLaneException) other;
		EqualsBuilder eb = new EqualsBuilder();
		try {
			eb.append(this.getItem(), castOther.getItem());
			eb.append(this.getBom(), castOther.getBom());
			eb.append(this.getSupplier(), castOther.getSupplier());
			eb.append(this.getFromSite(), castOther.getFromSite());
			eb.append(this.getToSite(), castOther.getToSite());
		} catch (Throwable t) {
			logger.warn("isEqual failed, using KEY", t);
			eb.append(this.getSourcingLaneKey(), castOther.getSourcingLaneKey());
		}
		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.getSupplier()).append(this.getStatus()).toHashCode();
	}

	public PcmSourcingLane getPCMSourcingLane() {
		PcmSourcingLane sourcingLane = new PcmSourcingLane();
		sourcingLane.setOwner(owner);
		sourcingLane.setItem(item);
		sourcingLane.setBom(bom);
		sourcingLane.setFromSite(fromSite);
		sourcingLane.setToSite(toSite);
		sourcingLane.setSupplier(supplier);
		sourcingLane.setSourcingLaneName(sourcingLaneName);
		sourcingLane.setSourcingLaneExternalId(sourcingLaneExternalId);
		sourcingLane.setDateOffset(dateOffset);
		sourcingLane.setProductState(productState);
		sourcingLane.setEndDateRequired(endDateRequired);
		sourcingLane.setCurrencyCode(currencyCode);
		sourcingLane.setSystemDerived(systemDerived);
		sourcingLane.setDescription(description);
		sourcingLane.setInsertDate(insertDt);
		sourcingLane.setUpdateDate(updateDt);
		sourcingLane.setDeleteFlag(deleteFlag);
		sourcingLane.setCurrentFlag(currentFlag);
		sourcingLane.setCollaboration(collaboration);
		sourcingLane.setCostProviderBusinessEntity(costProviderBusinessEntity);
		sourcingLane.setStatus(status);
		sourcingLane.setStatusChangeDate(statusChangeDate);
		sourcingLane.setStatusLastChangeBy(statusLastChangeBy);
		sourcingLane.setAuditRev(auditRev);
		sourcingLane.setLastRevChangeDate(lastRevChangeDate);
		return sourcingLane;
	}

    @Override
    public Date getInsertDate() {
        return this.insertDt;
    }

    @Override
    public void setInsertDate(Date insertDt) {
        this.insertDt = insertDt;
    }

    @Override
    public Date getUpdateDate() {
        return this.updateDt;
    }

    @Override
    public void setUpdateDate(Date updateDt) {
        this.updateDt = updateDt;
    }

    @Override
    public boolean getCurrentFlag() {
        return this.currentFlag;
    }
}
