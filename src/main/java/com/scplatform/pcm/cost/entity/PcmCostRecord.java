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

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.NaturalId;

import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.StatefulBase;
import com.scplatform.pcm.common.entity.TrackDelta;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.cost.service.PcmCostRecordService;
import com.scplatform.pcm.util.stateMachine.StateMachineReactor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Models a cost record Note: this clasTransients has a natural ordering that is
 * inconsistent with equals.
 */
@NamedQueries({
    @NamedQuery(
        name = "dashboard:costRecord",
        query = "SELECT COUNT(*), cr.status FROM PcmSourcingLane sl " +
                "JOIN sl.costRecords cr " +
                "WHERE cr.status IN (:status) " +
                "AND COALESCE(cr.updateDt, cr.insertDt) >= :cutoffDate " +
                "GROUP BY cr.status ORDER BY 2"
    ),
    @NamedQuery(
        name = "dashboard:costRecordForOwner",
        query = "SELECT COUNT(*), cr.status FROM PcmSourcingLane sl " +
                "JOIN sl.costRecords cr JOIN sl.item im " +
                "WHERE cr.status IN (:status) " +
                "AND COALESCE(cr.updateDt, cr.insertDt) >= :cutoffDate " +
                "AND EXISTS (SELECT 1 FROM im.assignments ia WHERE LOWER(ia.userId) = LOWER(:userId)) " +
                "GROUP BY cr.status ORDER BY 2"
    )
})
@Entity
@Table(name = "PCM_COST_RECORD")
@SuppressWarnings("serial")
@NoArgsConstructor
@Getter
@Setter
public class PcmCostRecord extends StatefulBase implements Serializable, Comparable<Object>, TrackDelta {
    public static final String DEFAULT_UOM = "EA";
    private final static Logger LOG = LogManager.getLogger(PcmCostRecord.class);
    private static PcmCostRecordService pcmCostRecordService;

    @Id
    @Column(name = "COST_RECORD_KEY", scale = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pcm_cost_record_seq")
    @SequenceGenerator(name = "pcm_cost_record_seq", sequenceName = "PCM_COST_RECORD_SEQ", allocationSize = 1)
    private Long costRecordKey;

    @NaturalId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOURCING_LANE_KEY", nullable = false)
    private PcmSourcingLane sourcingLane;
    @Transient
    private PcmSourcingLaneException sourcingLaneException;

    @NaturalId
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COST_TYPE_KEY", nullable = false)
    private PcmCostType costType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COST_PROVIDER_KEY", nullable = true)
    private BusinessEntity costProvider;

    @Column(name = "DESCRIPTION", length = 1024)
    private String description;

    @Column(name = "REASON_CODE", length = 128)
    private String reasonCode;

    @Column(name = "COST_RECORD_EXTERNAL_ID")
    private String costRecordExternalId;

    @Column(name = "INSERT_DT", nullable = false, columnDefinition = "TIMESTAMP")
    private Date insertDt = new Date();

    @Column(name = "UPDATE_DT", columnDefinition = "TIMESTAMP")
    private Date updateDt;

    @NaturalId
    @Column(name = "EFFECTIVE_FROM_DT", length = 7, nullable = false)
    
    private Date effectiveFromDt;

    @Column(name = "EFFECTIVE_TO_DT", length = 7, nullable = true)
    
    private Date effectiveToDt;

    @Column(name = "DELETE_FLAG", length = 1)
    @Convert(converter = org.hibernate.type.YesNoConverter.class)
    private Boolean deleteFlag = Boolean.FALSE;

    @Column(name = "CURRENT_FLAG", length = 1, nullable = false)
    @Convert(converter = org.hibernate.type.YesNoConverter.class)
    private boolean currentFlag = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRICING_SCENARIO_KEY", nullable = true)
    private PcmPricingScenario pricingScenario;

    @OneToMany(mappedBy = "costRecord", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private SortedSet<PcmCostRecordRange> costRecordRanges = new TreeSet<PcmCostRecordRange>();

    @Column(name = "SYSTEM_ACTION", length = 1024)
    private String systemAction;

    @Transient
    List<String> mpnFilterCriteria = new ArrayList<>();

    @Transient
    List<String> readonlyAttributes = new ArrayList<>();

    @Transient
    private boolean useMPNCompare;

    @Transient
    private String CONFIGURED_XLOB_FG_NAME;

    @Column(name = "PROJECT_NAME", length = 255)
    private String projectName;

    @Column(name = "LAST_LOADED_BY_USER", length = 64)
    private String lastLoadedByUser;

    @Column(name = "CREATED_BY", length = 64)
    private String createdBy;

    @Column(name = "LAST_UPDATED_BY", length = 64)
    private String lastUpdatedBy;

    // Flex Attributes
    @Column(name = "STRING_ATTRIBUTE1", length = 1024)
    private String stringAttribute1;

    @Column(name = "STRING_ATTRIBUTE2", length = 1024)
    private String stringAttribute2;

    @Column(name = "STRING_ATTRIBUTE3", length = 1024)
    private String stringAttribute3;

    @Column(name = "STRING_ATTRIBUTE4", length = 1024)
    private String stringAttribute4;

    @Column(name = "STRING_ATTRIBUTE5", length = 1024)
    private String stringAttribute5;

    @Column(name = "STRING_ATTRIBUTE6", length = 1024)
    private String stringAttribute6;

    @Column(name = "STRING_ATTRIBUTE7", length = 1024)
    private String stringAttribute7;

    @Column(name = "STRING_ATTRIBUTE8", length = 1024)
    private String stringAttribute8;

    @Column(name = "STRING_ATTRIBUTE9", length = 1024)
    private String stringAttribute9;

    @Column(name = "STRING_ATTRIBUTE10", length = 1024)
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

    @PostLoad
    private void initFromConfig() {
        PcmConfigUtil pcmConfigUtil = SpringContextHolder.getBean(PcmConfigUtil.class);
        this.mpnFilterCriteria = pcmConfigUtil.getList("pcm.costRecord.mpnValidation.required.types");
        this.readonlyAttributes = pcmConfigUtil.getList("scplatform.flexAttributes.costrecord.readonly");
    }

    public PcmCostRecord(Long costRecordKey, PcmSourcingLane pcmSourcingLane, PcmCostType pcmCostType, String status,
                         Date insertDt, boolean currentFlag) {
        super();
        this.costRecordKey = costRecordKey;
        this.sourcingLane = pcmSourcingLane;
        this.costType = pcmCostType;
        this.status = status;
        this.insertDt = insertDt;
        this.currentFlag = currentFlag;
    }

    // Getters and setters are automatically generated by Lombok @Data

    /*
     * (non-Javadoc)
     *
     * @see com.scplatform.repository.pcm.domain.TrackDelta#getInsertDt()
     */
    @Override
    public Date getInsertDate() {
        return this.insertDt;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.scplatform.repository.pcm.domain.TrackDelta#setInsertDt(java.util.Date)
     */
    @Override
    public void setInsertDate(Date insertDt) {
        this.insertDt = insertDt;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.scplatform.repository.pcm.domain.TrackDelta#getUpdateDt()
     */
    @Override
    public Date getUpdateDate() {
        return this.updateDt;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.scplatform.repository.pcm.domain.TrackDelta#setUpdateDt(java.util.Date)
     */
    @Override
    public void setUpdateDate(Date updateDt) {
        this.updateDt = updateDt;
    }
    /*
     * (non-Javadoc)
     *
     * @see com.scplatform.repository.pcm.domain.TrackDelta#getDeleteFlag()
     */
    @Override
    public Boolean getDeleteFlag() {
        return this.deleteFlag;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.scplatform.repository.pcm.domain.TrackDelta#setDeleteFlag(java.lang.Boolean)
     */
    @Override
    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.scplatform.repository.pcm.domain.TrackDelta#getCurrentFlag()
     */
    @Override
    public boolean getCurrentFlag() {
        return this.currentFlag;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.scplatform.repository.pcm.domain.TrackDelta#setCurrentFlag(boolean)
     */
    @Override
    public void setCurrentFlag(boolean currentFlag) {
        this.currentFlag = currentFlag;
    }

    // ...existing code... Getters and setters for all entity fields are generated by Lombok @Data



    /**
     * Gets the cost record values for the active range or an empty map if a ranges
     * are empty
     *
     * @return
     */
    public Map<String, PcmCostRecordValue> getCostRecordValues() {
        return this.getActiveCostRecordValues();
    }

    /**
     * Sets the cost record values on the active range. An IllegalStateException is
     * thrown if the active range is null
     *
     * @param pcmCostRecordValues
     */
    public void setCostRecordValues(Map<String, PcmCostRecordValue> pcmCostRecordValues) {
        PcmCostRecordRange ar = assertAndGetActiveCostRecordRange();
        ar.setCostRecordValues(pcmCostRecordValues);
    }

    public String getSystemAction() {
        return systemAction;
    }

    // ...existing code... Additional getters and setters for systemAction, projectName, lastLoadedByUser, createdBy, lastUpdatedBy generated by Lombok @Data

    /**
     * Add a CostRecordValue to the active range.An IllegalStateException is thrown
     * if the active range is null
     *
     * @param element
     * @param value
     * @param uom
     * @return
     */
    public PcmCostRecordValue addCostRecordValue(PcmCostElement element, BigDecimal value, String uom) {
        PcmCostRecordRange ar = assertAndGetActiveCostRecordRange();
        PcmCostRecordValue result = ar.addCostRecordValue(element, value, uom);
        return result;
    }

    /**
     * Get a cost record value associated with a PcmCostrecordElement for the active
     * range. Returns null if a cost record value is not found associated with the
     * element or if ranges are not specified or null
     *
     * @param element
     * @return
     */
    public PcmCostRecordValue getCostRecordValue(PcmCostElement element) {
        Map<String, PcmCostRecordValue> crvs = this.getActiveCostRecordValues();
        if (crvs == null) {
            return null;
        }
        return crvs.get(element.getId().getCostElementKey());
    }

    /**
     * Get a cost record value associated with a element key. Returns null if a cost
     * record value is not found associated with the element
     *
     * @param elementKey
     * @return
     */
    public PcmCostRecordValue getCostRecordValue(String elementKey) {
        Map<String, PcmCostRecordValue> crvs = this.getActiveCostRecordValues();
        if (crvs == null) {
            return null;
        }
        return crvs.get(elementKey);
    }

    /**
     * Get the active cost record range. Returns the costs record range marked
     * active or if none are marked active then the lowest range.
     *
     * @return the active cost record range or null if the ranges are not set
     */
    public PcmCostRecordRange getActiveCostRecordRange() {
        PcmCostRecordRange retval = null;
        // Check if ranges are set
        if (this.costRecordRanges == null || this.costRecordRanges.size() == 0) {
            return null;
        }
        // Iterate through the ranges to find the active range
        for (PcmCostRecordRange r : this.costRecordRanges) {
            Boolean active = r.getActive();
            if (active != null && Boolean.TRUE.equals(active)) {
                retval = r;
                break;
            }
        }
        if (retval == null) { // No active range
            retval = this.costRecordRanges.first();
        }
        return retval;
    }

    /**
     * Asserts that an active range exists and returns it. If an active range does
     * not exist an IllegalStateException is thrown
     *
     * @return the active range
     */
    protected PcmCostRecordRange assertAndGetActiveCostRecordRange() {
        PcmCostRecordRange ar = this.getActiveCostRecordRange();
        if (ar == null) {
            throw new IllegalStateException("Cannot call function since active range is null");
        }
        return ar;
    }

    /**
     * Get the cost record values associated with the active range or null if the
     * active range is null
     *
     * @return
     */
    public Map<String, PcmCostRecordValue> getActiveCostRecordValues() {
        PcmCostRecordRange activeRange = this.getActiveCostRecordRange();
        if (activeRange == null) {
            return null; // TODO should we return null here ???
        }
        return activeRange.getCostRecordValues();
    }

    // ...existing code... Getter/setter for costRecordRanges generated by Lombok @Data

    /**
     * Get a previously persisted PcmCostRecordRange data using its key. This method
     * is meant to be invoked internally.
     *
     * @param key
     * @return
     */
    public PcmCostRecordRange getCostRecordRangeByKey(Long key) {
        if (key == null) {
            return null;
        }
        PcmCostRecordRange result = null;
        for (PcmCostRecordRange crrng : this.costRecordRanges) {
            Long rangeKey = crrng.getCostRecordRangeKey();
            if (key.equals(rangeKey)) {
                result = crrng;
                break;
            }
        }
        return result;
    }

    /**
     * Gets a cost record Range based on range values if it exists for this cost
     * record.
     *
     * @param fromRange
     * @param toRange
     * @return
     */
    public PcmCostRecordRange getCostRecordRange(BigDecimal fromRange, BigDecimal toRange) {
        PcmCostRecordRange result = null;
        for (PcmCostRecordRange crrng : this.costRecordRanges) {
            boolean frm;
            BigDecimal crFrmRange = crrng.getFromRange();
            if (crFrmRange == null) {
                frm = fromRange == null;
            } else {
                if (fromRange == null) {
                    continue;
                }
                frm = crFrmRange.compareTo(fromRange) == 0;
            }

            boolean to;
            BigDecimal crToRange = crrng.getToRange();
            if (crToRange == null) {
                to = toRange == null;
            } else {
                if (toRange == null) {
                    continue;
                }
                to = crToRange.compareTo(toRange) == 0;
            }

            if (frm && to) {
                result = crrng;
                break;
            }
        }
        return result;
    }

    /**
     * Add a cost record Range to the collection
     *
     * @param fromRange
     * @param toRange
     * @param isActive
     * @return
     */
    public PcmCostRecordRange addCostRecordRange(BigDecimal fromRange, BigDecimal toRange, Boolean isActive) {
        PcmCostRecordRange result = getCostRecordRange(fromRange, toRange);
        if (result == null) {
            result = new PcmCostRecordRange(fromRange);
            result.setToRange(toRange);
            result.setCostRecord(this);
            this.costRecordRanges.add(result);
        }
        result.setActive(isActive);
        return result;
    }
    public void addCostRecordRange(PcmCostRecordRange range) {
        this.costRecordRanges.add(range);
        range.setCostRecord(this);
    }

    public PcmCostRecordRange addDefaultCostRecordRange() {
        PcmCostRecordRange range = getCostRecordRange(new BigDecimal(0), null);
        if (range != null) {
            return range;
        }
        if (this.costRecordRanges != null && this.costRecordRanges.size() != 0) {
            throw new IllegalStateException(
                    "Cannot call this method since costRecordRanges exist for this cost record.");
        }
        range = new PcmCostRecordRange(new BigDecimal(0));
        range.setActive(Boolean.TRUE);
        range.setCostRecord(this);
        this.costRecordRanges.add(range);
        return range;
    }

    @Override
    public Collection getChildren() {
        return null;
    }

    @Override
    public StateMachineReactor getParent() {
        return getSourcingLane();
    }



    /**
     * This is less strict than equals in that it does not compare the lane the cost
     * record is in. As a result this can be used to sort records across lanes
     */
    @Override
    public int compareTo(Object o) {
        PcmCostRecord other = (PcmCostRecord) o;
        return new CompareToBuilder().append(this.costType, other.getCostType()).append(this.status, other.getStatus())
                .append(this.costProvider, other.getCostProvider())
                .append(this.effectiveFromDt, other.getEffectiveFromDt())
                .append(this.effectiveToDt, other.getEffectiveToDt()).append(this.insertDt, other.getInsertDate())
                .toComparison();
    }

    /**
     * This is more strict than compare in that it always makes sure the cost record
     * is in the same lane.
     */
    @Override
    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof PcmCostRecord))
            return false;
        PcmCostRecord castOther = (PcmCostRecord) other;
        EqualsBuilder eb = new EqualsBuilder();
        try {
            eb.append(this.getSourcingLane(), castOther.getSourcingLane());
            eb.append(this.costRecordExternalId, castOther.getCostRecordExternalId());
            eb.append(this.costType, castOther.getCostType());
            eb.append(this.status, castOther.getStatus());
            eb.append(this.costProvider, castOther.getCostProvider());
            eb.append(this.effectiveFromDt, castOther.getEffectiveFromDt());
            eb.append(this.effectiveToDt, castOther.getEffectiveToDt());
            if (useMPNCompare) {
                if (mpnFilterCriteria != null && this.costType != null
                        && mpnFilterCriteria.contains(this.costType.getCostTypeKey())) {
                    if (this.stringAttribute2 != null || castOther.getStringAttribute2() != null) {
                        eb.append(this.stringAttribute2, castOther.getStringAttribute2());
                    }
                }
            }
            //handling xwap cr
            if(this.getCostType().getCostTypeKey().equals("XWAP") && castOther.getCostType().getCostTypeKey().equals("XWAP")) {
                if (this.numberAttribute1 != null || castOther.getNumberAttribute1() != null) {
                    eb.append(this.numberAttribute1, castOther.getNumberAttribute1());
                }
            }
        } catch (Throwable t) {
            LOG.warn("isEqual failed, using KEY", t);
            eb.append(this.getCostRecordKey(), castOther.getCostRecordKey());
        }
        return eb.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.getCostType()).append(this.getStatus()).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        sb.append(this.costType);
        PcmCostRecordRange activeRange = getActiveCostRecordRange();
        BigDecimal total = BigDecimal.ZERO;
        if (activeRange != null) {
            total = activeRange.getComputedTotalNotOfCostElementTypeFixed();
        }
        sb.append("[total:").append(total);
        sb.append(", status:").append(this.status);
        sb.append(", from:");
        if (effectiveFromDt != null) {
            sb.append(df.format(this.effectiveFromDt));
        }
        sb.append(", to:");
        if (effectiveToDt != null) {
            sb.append(df.format(this.effectiveToDt));
        }
        sb.append("]");
        return sb.toString();
    }

    // Flex attribute methods moved to PcmCostRecordService:
    // - getAttribute(String)
    // - setAttribute(String, Object)
    // - setReadonlyAttribute(String, Object)
    // - addAttribute(Attribute)



    @Transient
    public boolean isUseMPNCompare() {
        return useMPNCompare;
    }

    public void setUseMPNCompare(boolean useMPNCompare) {
        this.useMPNCompare = useMPNCompare;
    }

    public PcmCostRecordException getCopiedExceptionObject() {
        PcmCostRecordException costRecordException = new PcmCostRecordException();
        return costRecordException;
    }

    public PcmCostRecordException getExtraAttrCopiedExceptionObject(PcmCostRecordException costRecordException) {
        costRecordException.setStringAttribute1(this.stringAttribute1);
        costRecordException.setStringAttribute2(this.stringAttribute2);
        costRecordException.setStringAttribute3(this.stringAttribute3);
        costRecordException.setStringAttribute4(this.stringAttribute4);
        costRecordException.setStringAttribute5(this.stringAttribute5);
        costRecordException.setStringAttribute6(this.stringAttribute6);
        costRecordException.setStringAttribute7(this.stringAttribute7);
        costRecordException.setStringAttribute8(this.stringAttribute8);
        costRecordException.setStringAttribute9(this.stringAttribute9);
        costRecordException.setStringAttribute10(this.stringAttribute10);
        costRecordException.setFloatAttribute1(this.floatAttribute1);
        costRecordException.setFloatAttribute2(this.floatAttribute2);
        costRecordException.setFloatAttribute3(this.floatAttribute3);
        costRecordException.setFloatAttribute4(this.floatAttribute4);
        costRecordException.setFloatAttribute5(this.floatAttribute5);
        costRecordException.setFloatAttribute6(this.floatAttribute6);
        costRecordException.setFloatAttribute7(this.floatAttribute7);
        costRecordException.setFloatAttribute8(this.floatAttribute8);
        costRecordException.setFloatAttribute9(this.floatAttribute9);
        costRecordException.setFloatAttribute10(this.floatAttribute10);
        costRecordException.setNumberAttribute1(this.numberAttribute1);
        costRecordException.setNumberAttribute2(this.numberAttribute2);
        costRecordException.setNumberAttribute3(this.numberAttribute3);
        costRecordException.setNumberAttribute4(this.numberAttribute4);
        costRecordException.setNumberAttribute5(this.numberAttribute5);
        costRecordException.setNumberAttribute6(this.numberAttribute6);
        costRecordException.setNumberAttribute7(this.numberAttribute7);
        costRecordException.setNumberAttribute8(this.numberAttribute8);
        costRecordException.setNumberAttribute9(this.numberAttribute9);
        costRecordException.setNumberAttribute10(this.numberAttribute10);
        costRecordException.setDateAttribute1(this.dateAttribute1);
        costRecordException.setDateAttribute2(this.dateAttribute2);
        costRecordException.setDateAttribute3(this.dateAttribute3);
        costRecordException.setDateAttribute4(this.dateAttribute4);
        costRecordException.setDateAttribute5(this.dateAttribute5);
        costRecordException.setDateAttribute6(this.dateAttribute6);
        costRecordException.setDateAttribute7(this.dateAttribute7);
        costRecordException.setDateAttribute8(this.dateAttribute8);
        costRecordException.setDateAttribute9(this.dateAttribute9);
        costRecordException.setDateAttribute10(this.dateAttribute10);
        return costRecordException;
    }

    public PcmCostRecord copy() {
        PcmCostRecord copy = new PcmCostRecord();
        copy.sourcingLane = this.sourcingLane;
        copy.costType = this.costType;
        copy.status = this.status;
        copy.costProvider = this.costProvider;
        copy.description = this.description;
        copy.reasonCode = reasonCode;
        copy.pricingScenario = this.pricingScenario;
        copyAttributes(copy);
        for (PcmCostRecordRange range : costRecordRanges) {
            PcmCostRecordRange newRange = range.copy();
            copy.addCostRecordRange(newRange);
        }
        return copy;
    }

    // reason code is removed in this copy method
    public PcmCostRecord copyOnUIAction() {
        PcmCostRecord copy = new PcmCostRecord();
        copy.sourcingLane = this.sourcingLane;
        copy.costType = this.costType;
        copy.status = this.status;
        copy.costProvider = this.costProvider;
        copy.description = this.description;
        copy.pricingScenario = this.pricingScenario;
        copyAttributes(copy);
        for (PcmCostRecordRange range : costRecordRanges) {
            PcmCostRecordRange newRange = range.copy();
            copy.addCostRecordRange(newRange);
        }
        return copy;
    }

    private void copyAttributes(PcmCostRecord copy) {
        copy.stringAttribute1 = this.stringAttribute1;
        copy.stringAttribute2 = this.stringAttribute2;
        copy.stringAttribute3 = this.stringAttribute3;
        copy.stringAttribute4 = this.stringAttribute4;
        // copy.stringAttribute5 = this.stringAttribute5;
        copy.stringAttribute6 = this.stringAttribute6;

        if (!readonlyAttributes.contains("stringAttribute7")) {
            copy.stringAttribute7 = this.stringAttribute7;
        }
        copy.stringAttribute8 = this.stringAttribute8;
        copy.stringAttribute9 = this.stringAttribute9;
        copy.stringAttribute10 = this.stringAttribute10;

        copy.numberAttribute1 = this.numberAttribute1;
        copy.numberAttribute2 = this.numberAttribute2;
        copy.numberAttribute3 = this.numberAttribute3;
        copy.numberAttribute4 = this.numberAttribute4;
        copy.numberAttribute5 = this.numberAttribute5;
        copy.numberAttribute6 = this.numberAttribute6;
        copy.numberAttribute7 = this.numberAttribute7;
        copy.numberAttribute8 = this.numberAttribute8;
        copy.numberAttribute9 = this.numberAttribute9;
        copy.numberAttribute10 = this.numberAttribute10;

        copy.floatAttribute1 = this.floatAttribute1;
        copy.floatAttribute2 = this.floatAttribute2;
        copy.floatAttribute3 = this.floatAttribute3;
        copy.floatAttribute4 = this.floatAttribute4;
        copy.floatAttribute5 = this.floatAttribute5;
        copy.floatAttribute6 = this.floatAttribute6;
        copy.floatAttribute7 = this.floatAttribute7;
        copy.floatAttribute8 = this.floatAttribute8;
        copy.floatAttribute9 = this.floatAttribute9;
        copy.floatAttribute10 = this.floatAttribute10;

        copy.dateAttribute1 = this.dateAttribute1;
        copy.dateAttribute2 = this.dateAttribute2;
        copy.dateAttribute3 = this.dateAttribute3;
        copy.dateAttribute4 = this.dateAttribute4;
        copy.dateAttribute5 = this.dateAttribute5;
        copy.dateAttribute6 = this.dateAttribute6;
        copy.dateAttribute7 = this.dateAttribute7;
        copy.dateAttribute8 = this.dateAttribute8;
        copy.dateAttribute9 = this.dateAttribute9;
        copy.dateAttribute10 = this.dateAttribute10;
    }


}


