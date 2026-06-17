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

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.StatefulBase;
import com.scplatform.pcm.costexception.entity.CostException;
import com.scplatform.pcm.common.entity.TrackDelta;
import com.scplatform.pcm.util.stateMachine.StateMachineReactor;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.*;
import org.hibernate.type.YesNoConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Models a cost record Note: this class has a natural ordering that is
 * inconsistent with equals.
 */
@Entity
@Table(name = "PCM_COST_RECORD_EXCEPTION")
@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("serial")
@Filters( {
	 @Filter(name = "businessFilter", condition = "(COST_PROVIDER_KEY IN (:businessEntity) OR" 
		+ " EXISTS ( SELECT 1 FROM IV_SOURCING_BUSINESS_ENTITY_EXCEPTION SBE"
		+ " WHERE SBE.COST_RECORD_KEY = COST_RECORD_KEY"
		+ " AND (SBE.SUPPLIER_KEY IN (:businessEntity) OR"
		+ "	SBE.BUSINESS_ENTITY_KEY IN (:businessEntity))))"),
	 @Filter(name="categoryFilter", condition="(EXISTS (SELECT 1 FROM ITEM_ITEM_CATEGORY IIC JOIN PCM_SOURCING_LANE SL"
      + "ON IIC.ITEM_KEY = SL.ITEM_KEY"
      + "WHERE SL.SOURCING_LANE_KEY = SOURCING_LANE_KEY AND IIC.ITEM_CATEGORY_KEY"
      + "IN(SELECT DISTINCT PAC.TARGET_ENTITY_KEY"
      + "FROM PCM_ACCESS_CONTROL PAC WHERE PAC.ACL='Read' AND PAC.ENTITY_TYPE='CATEGORY'"
      + "AND (PAC.USER_KEY = :user OR PAC.ROLE_KEY = :role))))"),
	 @Filter(name="platformFilter", condition="((EXISTS (SELECT 1 FROM ITEM_ITEM_PLATFORM IIP JOIN PCM_SOURCING_LANE SL"
      + "ON IIP.ITEM_KEY = SL.ITEM_KEY"
      + "WHERE SL.SOURCING_LANE_KEY = SOURCING_LANE_KEY AND IIP.ITEM_PLATFORM_KEY"
      + "IN(SELECT DISTINCT PAC.TARGET_ENTITY_KEY"
      + "FROM PCM_ACCESS_CONTROL PAC WHERE PAC.ACL='Read' AND PAC.ENTITY_TYPE='PLATFORM'"
      + "AND (PAC.USER_KEY = :user OR PAC.ROLE_KEY = :role)))) OR" 
      + "(NOT EXISTS (SELECT 1 FROM ITEM_ITEM_PLATFORM IIP2 "
      + "JOIN PCM_SOURCING_LANE SL2 ON IIP2.ITEM_KEY = SL2.ITEM_KEY"
  	   + "WHERE SL2.SOURCING_LANE_KEY = SOURCING_LANE_KEY)"
  	   + "AND EXISTS (SELECT 1 FROM PCM_ACCESS_CONTROL PAC WHERE PAC.ACL='Read' AND PAC.ENTITY_TYPE='PLATFORM' AND PAC.TARGET_ENTITY_KEY='-1'"
      + "AND (PAC.USER_KEY = :user OR PAC.ROLE_KEY = :role))))"),	
	 @Filter(name="siteFilter", condition="(EXISTS (SELECT 1 FROM PCM_SOURCING_LANE SL"
      + "WHERE SL.SOURCING_LANE_KEY = SOURCING_LANE_KEY AND" 
      + "(SL.TO_SITE_KEY IS NULL OR SL.TO_SITE_KEY"  
      + "IN (SELECT DISTINCT PAC.TARGET_ENTITY_KEY" 
      +  "FROM PCM_ACCESS_CONTROL PAC WHERE PAC.ACL='Read' AND PAC.ENTITY_TYPE='SITE'"
      +  "AND (PAC.USER_KEY = :user OR PAC.ROLE_KEY = :role)))))"), 
	 @Filter(name="costTypeFilter", condition="(COST_TYPE_KEY IN(SELECT DISTINCT PAC.TARGET_ENTITY_KEY" 
		+ "FROM PCM_ACCESS_CONTROL PAC WHERE PAC.ACL='Read' AND PAC.ENTITY_TYPE='COSTTYPE'"
		+ "AND (PAC.USER_KEY = :user OR PAC.ROLE_KEY = :role)))"),
	 @Filter(name="costTypeExcludeFilter", condition="COST_TYPE_KEY NOT IN (:costTypes)")	
} )
public class PcmCostRecordException extends StatefulBase implements Serializable,
        Comparable<Object>, TrackDelta {
    public static final String DEFAULT_UOM = "EA";
    private final static Logger LOG = LogManager.getLogger(PcmCostRecordException.class);

    @Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PCM_COST_RECORD_EXCEPTION_SEQ")
	@SequenceGenerator(sequenceName = "PCM_COST_RECORD_EXCEPTION_SEQ", name = "PCM_COST_RECORD_EXCEPTION_SEQ", allocationSize = 1, initialValue = 1)
    @Column(name="COST_RECORD_KEY")
    private Long costRecordKey;

    @ManyToOne(optional = true)
	@JoinColumn(name="EXCEPTION_KEY")
    private CostException costException;

    @NaturalId (mutable = true)
    @ManyToOne(optional = true)
	@JoinColumn(name = "SOURCING_LANE_KEY")
    @Fetch(value = FetchMode.SELECT)
    private PcmSourcingLane sourcingLane;

    @NaturalId (mutable = true)
    @ManyToOne(optional = true)
	@JoinColumn(name = "SOURCING_LANE_EXCEPTION_KEY")
    @Fetch(value = FetchMode.SELECT)
    private PcmSourcingLaneException sourcingLaneException;

	@NaturalId (mutable = true)
    @ManyToOne(optional = false)
	@JoinColumn(name = "COST_TYPE_KEY", nullable = false)
    @Fetch(value = FetchMode.SELECT)
    private PcmCostType costType;

    @NaturalId (mutable = true)
    @Column(name = "EFFECTIVE_FROM_DT", nullable = false)
    private Date effectiveFromDt;

    @ManyToOne(optional = true)
	@JoinColumn(name = "COST_PROVIDER_KEY", nullable = true)
    private BusinessEntity costProvider;

    @Column(name = "DESCRIPTION", nullable = true)
    private String description;

    @Column(name = "REASON_CODE", nullable = true)
    private String reasonCode;

    @Column(name = "COST_RECORD_EXTERNAL_ID")
    private String costRecordExternalId;

    @Column(name = "INSERT_DT", nullable = false)
    private Date insertDt = new Date();

    @Column(name = "UPDATE_DT")
    private Date updateDt;

    @Column(name = "EFFECTIVE_TO_DT", nullable = true)
    private Date effectiveToDt;

    @Column(name = "DELETE_FLAG", length = 1)
    @Convert(converter = YesNoConverter.class)
    private Boolean deleteFlag = Boolean.FALSE;

    @Column(name = "CURRENT_FLAG", length = 1, nullable = false)
    private boolean currentFlag = true;

    @ManyToOne(optional = true)
	@JoinColumn(name = "PRICING_SCENARIO_KEY", nullable = true)
    @Fetch(value = FetchMode.SELECT)
    private PcmPricingScenario pricingScenario;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "costRecord")
    @SortNatural
    private SortedSet<PcmCostRecordRangeException> costRecordRanges = new TreeSet<PcmCostRecordRangeException>();

    @Column(name = "SYSTEM_ACTION")
    private String systemAction;

    @Column(name = "PROJECT_NAME")
    private String projectName;

    // Flex Attributes
    @Column(name = "STRING_ATTRIBUTE1")
    private String stringAttribute1;

    @Column(name = "STRING_ATTRIBUTE2")
    private String stringAttribute2;

    @Column(name = "STRING_ATTRIBUTE3")
    private String stringAttribute3;

    @Column(name = "STRING_ATTRIBUTE4")
    private String stringAttribute4;

    @Column(name = "STRING_ATTRIBUTE5")
    private String stringAttribute5;

    @Column(name = "STRING_ATTRIBUTE6")
    private String stringAttribute6;

    @Column(name = "STRING_ATTRIBUTE7")
    private String stringAttribute7;

    @Column(name = "STRING_ATTRIBUTE8")
    private String stringAttribute8;

    @Column(name = "STRING_ATTRIBUTE9")
    private String stringAttribute9;

    @Column(name = "STRING_ATTRIBUTE10")
    private String stringAttribute10;

    @Column(name = "NUMBER_ATTRIBUTE1")
    private Integer numberAttribute1;

    @Column(name = "NUMBER_ATTRIBUTE2")
    private Integer numberAttribute2;

    @Column(name = "NUMBER_ATTRIBUTE3")
    private Integer numberAttribute3;

    @Column(name = "NUMBER_ATTRIBUTE4")
    private Integer numberAttribute4;

    @Column(name = "NUMBER_ATTRIBUTE5")
    private Integer numberAttribute5;

    @Column(name = "NUMBER_ATTRIBUTE6")
    private Integer numberAttribute6;

    @Column(name = "NUMBER_ATTRIBUTE7")
    private Integer numberAttribute7;

    @Column(name = "NUMBER_ATTRIBUTE8")
    private Integer numberAttribute8;

    @Column(name = "NUMBER_ATTRIBUTE9")
    private Integer numberAttribute9;

    @Column(name = "NUMBER_ATTRIBUTE10")
    private Integer numberAttribute10;

    @Column(name = "FLOAT_ATTRIBUTE1")
    private BigDecimal floatAttribute1;

    @Column(name = "FLOAT_ATTRIBUTE2")
    private BigDecimal floatAttribute2;

    @Column(name = "FLOAT_ATTRIBUTE3")
    private BigDecimal floatAttribute3;

    @Column(name = "FLOAT_ATTRIBUTE4")
    private BigDecimal floatAttribute4;

    @Column(name = "FLOAT_ATTRIBUTE5")
    private BigDecimal floatAttribute5;

    @Column(name = "FLOAT_ATTRIBUTE6")
    private BigDecimal floatAttribute6;

    @Column(name = "FLOAT_ATTRIBUTE7")
    private BigDecimal floatAttribute7;

    @Column(name = "FLOAT_ATTRIBUTE8")
    private BigDecimal floatAttribute8;

    @Column(name = "FLOAT_ATTRIBUTE9")
    private BigDecimal floatAttribute9;

    @Column(name = "FLOAT_ATTRIBUTE10")
    private BigDecimal floatAttribute10;

    @Column(name = "DATE_ATTRIBUTE1")
    private Date dateAttribute1;

    @Column(name = "DATE_ATTRIBUTE2")
    private Date dateAttribute2;

    @Column(name = "DATE_ATTRIBUTE3")
    private Date dateAttribute3;

    @Column(name = "DATE_ATTRIBUTE4")
    private Date dateAttribute4;

    @Column(name = "DATE_ATTRIBUTE5")
    private Date dateAttribute5;

    @Column(name = "DATE_ATTRIBUTE6")
    private Date dateAttribute6;

    @Column(name = "DATE_ATTRIBUTE7")
    private Date dateAttribute7;

    @Column(name = "DATE_ATTRIBUTE8")
    private Date dateAttribute8;

    @Column(name = "DATE_ATTRIBUTE9")
    private Date dateAttribute9;

    @Column(name = "DATE_ATTRIBUTE10")
    private Date dateAttribute10;

    @Override
    public Date getInsertDate() {
        return null;
    }

    @Override
    public void setInsertDate(Date insertDt) {

    }

    @Override
    public Date getUpdateDate() {
        return null;
    }

    @Override
    public void setUpdateDate(Date updateDt) {

    }

    @Override
    public boolean getCurrentFlag() {
        return false;
    }

    @Override
    public Collection<StateMachineReactor> getChildren() {
        return List.of();
    }

    @Override
    public StateMachineReactor getParent() {
        return null;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
