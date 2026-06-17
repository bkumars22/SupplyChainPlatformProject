/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2024, by E2open Inc. All rights reserved.
 */
package com.scplatform.pcm.cost.entity;

import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.StatefulBase;
import com.scplatform.pcm.common.entity.TrackDelta;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.entity.YesNoConverter;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.scplatform.pcm.util.stateMachine.StateMachineReactor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.*;
import java.util.Collection;

/**
 * Spring Data JPA Entity for PCM Sourcing Lane
 * Maps to PCM_SOURCING_LANE table
 */
@NamedQueries({
    @NamedQuery(
        name = "dashboard:sourcingLane",
        query = "SELECT COUNT(*), sl.status FROM PcmSourcingLane sl " +
                "JOIN sl.item im " +
                "JOIN sl.toSite ts " +
                "LEFT OUTER JOIN sl.supplier sup " +
                "LEFT OUTER JOIN sl.fromSite fs " +
                "LEFT OUTER JOIN im.categories cat " +
                "WHERE sl.status IN (:status) " +
                "AND COALESCE(sl.updateDate, sl.insertDate) >= :cutoffDate " +
                "AND NOT EXISTS (SELECT cp.companyItemType FROM Users us " +
                "  LEFT JOIN us.userProfileMapping cp " +
                "  LEFT JOIN cp.itemCategory ic " +
                "  LEFT JOIN cp.costTypes ct " +
                "  WHERE cp.includeExcludeItem IS NOT NULL " +
                "  AND us.userKey = :userKey " +
                "  AND ic.categoryKey != -1 " +
                "  AND cp.companyItemType = im.dataSource " +
                "  AND ic.categoryKey = cat.categoryKey) " +
                "GROUP BY sl.status ORDER BY 2"
    ),
    @NamedQuery(
        name = "dashboard:sourcingLaneForOwner",
        query = "SELECT COUNT(*), sl.status FROM PcmSourcingLane sl " +
                "JOIN sl.item im " +
                "WHERE sl.status IN (:status) " +
                "AND COALESCE(sl.updateDate, sl.insertDate) >= :cutoffDate " +
                "AND EXISTS (SELECT 1 FROM im.assignments ia WHERE LOWER(ia.userId) = LOWER(:userId)) " +
                "GROUP BY sl.status ORDER BY 2"
    )
})
@Entity
@Table(name = "PCM_SOURCING_LANE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"costRecords", "pcmCostRecordsException"})
public class PcmSourcingLane  extends StatefulBase implements Serializable, TrackDelta {
	
	private static final long serialVersionUID = 1L;

	// ==================== PRIMARY KEY ====================
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PCM_SOURCING_LANE_SEQ")
	@SequenceGenerator(sequenceName = "PCM_SOURCING_LANE_SEQ", name = "PCM_SOURCING_LANE_SEQ", allocationSize = 1, initialValue = 1)
	@Column(name = "SOURCING_LANE_KEY")
	private Long sourcingLaneKey;

	// ==================== BASIC FIELDS ====================
	
	@Column(name = "SOURCING_LANE_NAME", nullable = false, length = 512)
	private String sourcingLaneName;

	@Column(name = "SOURCING_LANE_EXTERNAL_ID")
	private String sourcingLaneExternalId;

	@Column(name = "DESCRIPTION", length = 1024)
	private String description;

	@Column(name = "CURRENCY_CODE", length = 3)
	private String currencyCode;

	@Column(name = "PRODUCT_STATE", length = 32)
	private String productState;

	@Column(name = "COST_PROV_BUSINESS_ENTITY", length = 255)
	private String costProviderBusinessEntity;


	@Column(name = "CURRENT_FLAG", length = 1, nullable = false)
	@Convert(converter = YesNoConverter.class)
	private boolean currentFlag = true;

	@Column(name = "DELETE_FLAG", length = 1)
	@Convert(converter = YesNoConverter.class)
	private Boolean deleteFlag = false;

	@Column(name = "SYSTEM_DERIVED", length = 1, nullable = false)
	@Convert(converter = YesNoConverter.class)
	private boolean systemDerived = false;

	@Column(name = "ENDDATE_REQUIRED", length = 1, nullable = false)
	@Convert(converter = YesNoConverter.class)
	private boolean endDateRequired = false;

	@Column(name = "COLLABORATION")
	private Boolean collaboration;

	// ==================== DATE FIELDS ====================
	
	@Column(name = "INSERT_DT", nullable = false, updatable = false)
	
	private Date insertDate = new Date();

	@Column(name = "UPDATE_DT")
	
	private Date updateDate;

	// ==================== NUMERIC FIELDS ====================
	
	@Column(name = "DATE_OFFSET")
	private Long dateOffset;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITEM_KEY", nullable = false)
	private Item item;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BOM_KEY")
	private Bom bom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FROM_SITE_KEY")
	private Site fromSite;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TO_SITE_KEY")
	private Site toSite;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUPPLIER_KEY")
	private BusinessEntity supplier;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER_KEY")
	private Users owner;

	@OneToMany(mappedBy = "sourcingLane", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<PcmCostRecord> costRecords = new HashSet<>();

	@OneToMany(mappedBy = "sourcingLane", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<PcmCostRecordException> pcmCostRecordsException = new HashSet<>();
	
	// ==================== CONSTRUCTORS ====================
	
	public PcmSourcingLane(Long sourcingLaneKey, String sourcingLaneName, String status, Date insertDate, Boolean currentFlag) {
		this.sourcingLaneKey = sourcingLaneKey;
		this.sourcingLaneName = sourcingLaneName;
		this.status = status;
		this.insertDate = insertDate;
		this.currentFlag = currentFlag;
	}

	// ==================== BUSINESS LOGIC METHODS ====================
	
	public boolean addCostRecord(PcmCostRecord pcr)
	{
		pcr.setSourcingLane(this);
		return costRecords.add(pcr);		
	}

	public boolean removeCostRecord(PcmCostRecord pcr)
	{
		if (costRecords.remove(pcr))
		{
			pcr.setSourcingLane(null);
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean addCostRecord(PcmCostRecordException pcr)
	{
		pcr.setSourcingLane(this);
		return pcmCostRecordsException.add(pcr);		
	}

	public boolean removeCostRecord(PcmCostRecordException pcr)
	{
		if (pcmCostRecordsException.remove(pcr))
		{
			pcr.setSourcingLane(null);
			return true;
		}
		else
		{
			return false;
		}
	}

	public Set<PcmCostType> getCostTypesInLane()
	{
		Set<PcmCostType> results = new HashSet<PcmCostType>();
		for (PcmCostRecord pcr: costRecords)
		{
			results.add(pcr.getCostType());
		}
		return results;
	}
	
	public boolean getLaneContainsCostType(String costTypeKey)
	{
		for (PcmCostRecord pcr: costRecords)
		{
			if (pcr.getCostType().getCostTypeKey().equals(costTypeKey))
			{
				return true;
			}
		}
		return false;
	}
	
	public Collection getChildren()
	{
		return costRecords;
	}

	public StateMachineReactor getParent() {
		return null;
	}

	@Override
	public boolean getCurrentFlag() {
		return currentFlag;
	}

	@Override
	public void setCurrentFlag(boolean currentFlag) {
		this.currentFlag = currentFlag;
	}

	/**
	 * Returns the generated ID for the lane
	 * which is a combination of the other location data
	 * @return - dynamically generated ID, this will change as the lane does
	 */
	public String getTitle()
	{
		StringBuilder name = new StringBuilder();
		if (getItem() != null)
		{
			name.append(getItem().getItemNumber());
		}
		if (getBom() != null)
		{
			name.append("-").append(getBom().getBomName());
		} 
		String shortTitle = getShortTitle();
		if (shortTitle.length() > 0)
		{
			name.append("-").append(shortTitle);
		}
		return name.toString();
	}
	
    /**
     * Gets the audit message using the sourcing lane attributes
     * @return
     */
    public String getAuditTitle() {
        List<Object> args = new ArrayList<Object>();
        args.add(getItem() != null ? getItem().getItemNumber() : "");
        args.add(getItem().getBusinessEntity() != null ? getItem().getBusinessEntity() : "");
        args.add(getSupplier() != null ? getSupplier().getBusinessEntityName() : "");
        args.add(getFromSite() != null ? getFromSite().getSiteDescription() : "");
        args.add(getToSite() != null ? getToSite().getSiteDescription() : "");
        args.add(getCurrencyCode() != null ? getCurrencyCode() : "");
        return SCPlatformMessages.INSTANCE.getAuditMessage("audit.sourcingLane", args.toArray(), null);
    }

	/**
	 * Does not contain the item or BOM
	 * @return
	 */
	public String getShortTitle()
	{
		StringBuilder name = new StringBuilder();
		if (getSupplier() != null)
		{
			if (name.length() > 0)
			{
				name.append("-");	
			}
			name.append(getSupplier().getBusinessEntityName());
		}
		if (getFromSite() != null)
		{
			if (name.length() > 0)
			{
				name.append("-");	
			}
			name.append(getFromSite().getSiteDescription());
		}
		if (getToSite() != null)
		{
			if (name.length() > 0)
			{
				name.append("-");	
			}
			name.append(getToSite().getSiteDescription());
		}
		if (getCurrencyCode() != null)
		{
			if (name.length() > 0)
			{
				name.append("-");	
			}
			name.append(getCurrencyCode());
		}
		if (getBom() != null)
		{
			name.append(" (NM)");
		}
		return name.toString();
	}
}
