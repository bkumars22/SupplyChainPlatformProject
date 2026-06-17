/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.forecast.entity;

import com.scplatform.pcm.common.dto.ChangeTracker;
import com.scplatform.pcm.common.entity.StatefulBase;
import com.scplatform.pcm.common.entity.TrackDelta;
import com.scplatform.pcm.forecast.enums.ForecastModel;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;

import com.scplatform.pcm.util.datetime.ISO8601;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.scplatform.pcm.util.stateMachine.StateMachineReactor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.SortNatural;
import org.hibernate.type.YesNoConverter;

import java.math.BigDecimal;
import java.util.*;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name="PCM_FORECAST")
@SequenceGenerator(name="PCM_FORECAST_SEQ", sequenceName = "PCM_FORECAST_SEQ",allocationSize = 1)
public class PcmForecast extends StatefulBase
        implements TrackDelta
{
    public static final String ROLLOVER_PERIOD_AUDIT = "EXTENDED FORECAST TERM";

    @Id
    @GeneratedValue(generator="PCM_FORECAST_SEQ")
    @Column(name="FORECAST_KEY")
    private Long forecastKey;

    @Column(name="FORECAST_EXTERNAL_ID", nullable=true)
    private String forecastExternalId;

    @Column(name="DESCRIPTION")
    private String description;

    @ManyToOne(fetch=LAZY, optional = false)
    @JoinColumn(name="ITEM_KEY")
    private Item item;

    @ManyToOne(fetch=LAZY, optional = true)
    @JoinColumn(name="SITE_KEY")
    private Site site;

    @Column(name="FORECAST_TYPE", nullable=false)
    private String forecastType;

    @Column(name="PERIOD_TYPE")
    private String periodType;

    @Column(name="CALENDAR_NAME", nullable=false)
    private String calendarName;

    @Column(name="PERIODIC_ADJUST_TYPE")
    private String periodicAdjustmentType;

    @Column(name="PERIODIC_ADJUST_AMOUNT")
    private BigDecimal periodicAdjustmentAmount;

    @Column(name="CONFIDENCE_FACTOR")
    private BigDecimal confidenceFactor;

    @Column(name="REMAINING_ROLLOVERS", nullable=true)
    private Integer remainingRollovers;

    @Column(name="INSERT_DT", nullable=false)
    private Date insertDate = new Date();

    @Column(name="UPDATE_DT")
    private Date updateDate;

    @Column(name="LAST_CHANGE_BY",nullable=true)
    private String lastChangeBy;

    @Column(name="DELETE_FLAG")
    @Convert(converter = YesNoConverter.class)
    private Boolean deleteFlag  = Boolean.FALSE;

    @Column(name="CURRENT_FLAG")
    @Convert(converter = YesNoConverter.class)
    private boolean currentFlag = true;

    @Column(name="EFFECTIVE_FROM_DT")
    private Date effectiveFromDt;

    @Column(name="EFFECTIVE_TO_DT")
    private Date effectiveToDt;

    @Column(name="LAST_ROLLOVER_START_DATE")
    private Date lastRolloverStartDate;

    @Column(name="FORECAST_MODEL", nullable=false)
    @Enumerated(EnumType.STRING)
    private ForecastModel forecastModel;

    @Column(name="FORECAST_ORIGIN_EXTERNAL_ID", nullable=true)
    private String forecastOriginExternalId;

    @Column(name = "LAST_LOADED_BY_USER")
    private String lastLoadedByUser;

    @OneToMany(cascade = ALL, mappedBy = "forecast", orphanRemoval = true)
    @SortNatural
    private SortedSet<PcmForecastValue> forecastValues = new TreeSet<PcmForecastValue>();


    // Flex Attributes
    @Column(name="STRING_ATTRIBUTE1")
    private String stringAttribute1;

    @Column(name="STRING_ATTRIBUTE2")
    private String stringAttribute2;

    @Column(name="STRING_ATTRIBUTE3")
    private String stringAttribute3;

    @Column(name="STRING_ATTRIBUTE4")
    private String stringAttribute4;

    @Column(name="STRING_ATTRIBUTE5")
    private String stringAttribute5;

    @Column(name="STRING_ATTRIBUTE6")
    private String stringAttribute6;

    @Column(name="STRING_ATTRIBUTE7")
    private String stringAttribute7;

    @Column(name="STRING_ATTRIBUTE8")
    private String stringAttribute8;

    @Column(name="STRING_ATTRIBUTE9")
    private String stringAttribute9;

    @Column(name="STRING_ATTRIBUTE10")
    private String stringAttribute10;

    @Column(name="NUMBER_ATTRIBUTE1")
    private Integer numberAttribute1;

    @Column(name="NUMBER_ATTRIBUTE2")
    private Integer numberAttribute2;

    @Column(name="NUMBER_ATTRIBUTE3")
    private Integer numberAttribute3;

    @Column(name="NUMBER_ATTRIBUTE4")
    private Integer numberAttribute4;

    @Column(name="NUMBER_ATTRIBUTE5")
    private Integer numberAttribute5;

    @Column(name="NUMBER_ATTRIBUTE6")
    private Integer numberAttribute6;

    @Column(name="NUMBER_ATTRIBUTE7")
    private Integer numberAttribute7;

    @Column(name="NUMBER_ATTRIBUTE8")
    private Integer numberAttribute8;

    @Column(name="NUMBER_ATTRIBUTE9")
    private Integer numberAttribute9;

    @Column(name="NUMBER_ATTRIBUTE10")
    private Integer numberAttribute10;

    @Column(name="FLOAT_ATTRIBUTE1")
    private BigDecimal floatAttribute1;

    @Column(name="FLOAT_ATTRIBUTE2")
    private BigDecimal floatAttribute2;

    @Column(name="FLOAT_ATTRIBUTE3")
    private BigDecimal floatAttribute3;

    @Column(name="FLOAT_ATTRIBUTE4")
    private BigDecimal floatAttribute4;

    @Column(name="FLOAT_ATTRIBUTE5")
    private BigDecimal floatAttribute5;

    @Column(name="FLOAT_ATTRIBUTE6")
    private BigDecimal floatAttribute6;

    @Column(name="FLOAT_ATTRIBUTE7")
    private BigDecimal floatAttribute7;

    @Column(name="FLOAT_ATTRIBUTE8")
    private BigDecimal floatAttribute8;

    @Column(name="FLOAT_ATTRIBUTE9")
    private BigDecimal floatAttribute9;

    @Column(name="FLOAT_ATTRIBUTE10")
    private BigDecimal floatAttribute10;

    @Column(name="DATE_ATTRIBUTE1")
    private Date dateAttribute1;

    @Column(name="DATE_ATTRIBUTE2")
    private Date dateAttribute2;

    @Column(name="DATE_ATTRIBUTE3")
    private Date dateAttribute3;

    @Column(name="DATE_ATTRIBUTE4")
    private Date dateAttribute4;

    @Column(name="DATE_ATTRIBUTE5")
    private Date dateAttribute5;

    @Column(name="DATE_ATTRIBUTE6")
    private Date dateAttribute6;

    @Column(name="DATE_ATTRIBUTE7")
    private Date dateAttribute7;

    @Column(name="DATE_ATTRIBUTE8")
    private Date dateAttribute8;

    @Column(name="DATE_ATTRIBUTE9")
    private Date dateAttribute9;

    @Column(name="DATE_ATTRIBUTE10")
    private Date dateAttribute10;


    @Transient
    transient ChangeTracker<PcmForecast> forecastChangeTracker = new ChangeTracker<PcmForecast>();

    public PcmForecast()
    {
        super();
        this.forecastChangeTracker.setObservedObject(this);
    }

    public PcmForecast(PcmForecast forecast, ForecastModel forecastModel)
    {
        super();
        this.forecastExternalId = java.util.UUID.randomUUID().toString();
        this.description = forecast.description;
        this.item = forecast.item;
        this.site = forecast.site;
        this.forecastType = forecast.forecastType;
        this.periodType = forecast.periodType;
        this.calendarName = forecast.calendarName;
        this.periodicAdjustmentType = forecast.periodicAdjustmentType;
        this.periodicAdjustmentAmount = forecast.periodicAdjustmentAmount;
        this.confidenceFactor = forecast.confidenceFactor;
        this.remainingRollovers = forecast.remainingRollovers;
        this.insertDate = new Date();
        this.effectiveFromDt = forecast.effectiveFromDt;
        this.effectiveToDt = forecast.effectiveToDt;
        this.lastChangeBy=forecast.lastChangeBy;
        this.forecastModel = forecastModel;
    }

    public PcmForecast(String forecastType, Item item)
    {
        super();
        this.forecastType = forecastType;
        this.item = item;
        this.effectiveFromDt = new Date();
    }

    public Long getForecastKey()
    {
        return forecastKey;
    }

    public String getForecastExternalId()
    {
        return forecastExternalId;
    }

    public void setForecastExternalId(String forecastExternalId)
    {
        this.forecastExternalId = forecastExternalId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Item getItem()
    {
        return item;
    }

    public void setItem(Item item)
    {
        this.item = item;
    }

    public Site getSite()
    {
        return site;
    }

    public void setSite(Site site)
    {
        this.site = site;
    }

    public String getForecastType()
    {
        return forecastType;
    }

    public void setForecastType(String forecastType)
    {
        this.forecastType = forecastType;
    }

    public String getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType(String periodType)
    {
        this.periodType = periodType;
    }

    public String getCalendarName()
    {
        return calendarName;
    }

    public void setCalendarName(String calendarName)
    {
        this.forecastChangeTracker.firePropertyChangeEvent("calendarName", this.calendarName, calendarName);
        this.calendarName = calendarName;
    }

    public String getPeriodicAdjustmentType()
    {
        return periodicAdjustmentType;
    }

    public void setPeriodicAdjustmentType(String periodicAdjustmentType)
    {
        this.forecastChangeTracker.firePropertyChangeEvent("periodicAdjustmentType", this.periodicAdjustmentType, periodicAdjustmentType);
        this.periodicAdjustmentType = periodicAdjustmentType;
    }

    public BigDecimal getPeriodicAdjustmentAmount()
    {
        return periodicAdjustmentAmount;
    }

    public void setPeriodicAdjustmentAmount(BigDecimal periodicAdjustmentAmount)
    {
        this.forecastChangeTracker.firePropertyChangeEventUsingCompare("periodicAdjustmentAmount", this.periodicAdjustmentAmount, periodicAdjustmentAmount);
        this.periodicAdjustmentAmount = periodicAdjustmentAmount;
    }

    public BigDecimal getConfidenceFactor()
    {
        return confidenceFactor;
    }

    public void setConfidenceFactor(BigDecimal confidenceFactor)
    {
        this.forecastChangeTracker.firePropertyChangeEventUsingCompare("confidenceFactor", this.confidenceFactor, confidenceFactor);
        this.confidenceFactor = confidenceFactor;
    }

    public Integer getRemainingRollovers()
    {
        return remainingRollovers;
    }

    public void setRemainingRollovers(Integer remainingRollovers)
    {
        this.forecastChangeTracker.firePropertyChangeEvent("remainingRollovers", this.remainingRollovers, remainingRollovers);
        this.remainingRollovers = remainingRollovers;
    }

    public void subtractRemainingRollovers(Integer value)
    {
        if(this.remainingRollovers != null && value != null) {
            this.remainingRollovers-=value;
        }
    }

    @Override
    public Date getInsertDate()
    {
        return this.insertDate;
    }

    @Override
    public void setInsertDate(Date insertDt) {
        this.insertDate = insertDt;
    }


    @Override
    public Date getUpdateDate()
    {
        return this.updateDate;
    }

    @Override
    public void setUpdateDate(Date updateDate)
    {
        this.updateDate = updateDate;
    }

    public void setLastChangeBy(String lastChangeBy) {
        this.lastChangeBy = lastChangeBy;
    }


    public String getLastChangeBy() {
        return this.lastChangeBy;
    }


    public Date getEffectiveFromDt()
    {
        return this.effectiveFromDt;
    }

    public void setEffectiveFromDt(Date effectiveFromDt)
    {
        this.forecastChangeTracker.firePropertyChangeEvent("effectiveFromDt", this.effectiveFromDt, effectiveFromDt);
        this.effectiveFromDt = effectiveFromDt;
    }

    public Date getEffectiveToDt()
    {
        return this.effectiveToDt;
    }

    public void setEffectiveToDt(Date effectiveToDt)
    {
        this.forecastChangeTracker.firePropertyChangeEvent("effectiveToDt", this.effectiveToDt, effectiveToDt);
        this.effectiveToDt = effectiveToDt;
    }

    @Override
    public Boolean getDeleteFlag()
    {
        return this.deleteFlag;
    }

    @Override
    public void setDeleteFlag(Boolean deleteFlag)
    {
        this.deleteFlag = deleteFlag;
    }

    @Override
    public boolean getCurrentFlag()
    {
        return this.currentFlag;
    }

    @Override
    public void setCurrentFlag(boolean currentFlag)
    {
        this.currentFlag = currentFlag;
    }

    public Date getLastRolloverStartDate()
    {
        return this.lastRolloverStartDate;
    }



    // TODO consider a helper setter that takes a String

    public String getForecastOriginExternalId() {
        return forecastOriginExternalId;
    }

    public void setForecastOriginExternalId(String forecastOriginExternalId) {
        this.forecastOriginExternalId = forecastOriginExternalId;
    }

    public String getLastLoadedByUser() {
        return lastLoadedByUser;
    }

    public void setLastLoadedByUser(String lastLoadedByUser) {
        this.lastLoadedByUser = lastLoadedByUser;
    }

    @Override
    public Collection<StateMachineReactor> getChildren()
    {
        return null;
    }

    @Override
    public StateMachineReactor getParent()
    {
        return null;
    }

    public void setForecastValues(SortedSet<PcmForecastValue> forecastValues)
    {
        this.forecastValues = forecastValues;
    }

    public SortedSet<PcmForecastValue> getForecastValues()
    {
        return forecastValues;
    }

    /**
     * @return the changeTracker
     */
    public ChangeTracker<PcmForecast> getForecastChangeTracker() {
        return this.forecastChangeTracker;
    }

    public Set<String> getForecastMeasureKeys()
    {
        Set<String> keys = new HashSet<String>();
        for (PcmForecastValue value: forecastValues)
        {
            keys.add(value.getForecastMeasureKey());
        }
        return keys;
    }

    /**
     * Find a {@link PcmForecastValue} for the given measure key whose effective
     * period starts at the supplied date. Returns {@code null} when no matching
     * value exists.
     */
    public PcmForecastValue getForecastValue(String measureKey, Date periodStart)
    {
        if (measureKey == null || periodStart == null) {
            return null;
        }
        for (PcmForecastValue value : forecastValues) {
            if (measureKey.equals(value.getForecastMeasureKey())
                    && periodStart.equals(value.getEffectiveFromDt())) {
                return value;
            }
        }
        return null;
    }

    /**
     * Add a new {@link PcmForecastValue} to this forecast.
     */
    public boolean addForecastValue(PcmForecastValue pfv)
    {
        if (pfv == null) {
            return false;
        }
        return this.forecastValues.add(pfv);
    }

    /**
     * Group {@link PcmForecastValue}s by their effective-from date and then by
     * measure key. Used by {@code forecastSearchPage.jsp} as
     * {@code ${fc.forecastValuesByPeriod[period.startDate]['ActualForecast'].calculatedForecastValue}}.
     *
     * @return map keyed by period start date whose values are maps keyed by
     *         {@link PcmForecastValue#getForecastMeasureKey() measure key}.
     */
    @Transient
    public Map<Date, Map<String, PcmForecastValue>> getForecastValuesByPeriod()
    {
        Map<Date, Map<String, PcmForecastValue>> byPeriod = new HashMap<Date, Map<String, PcmForecastValue>>();
        if (forecastValues == null) {
            return byPeriod;
        }
        for (PcmForecastValue pfv : forecastValues) {
            Date periodStart = pfv.getEffectiveFromDt();
            if (periodStart == null) {
                continue;
            }
            Map<String, PcmForecastValue> byMeasure = byPeriod.get(periodStart);
            if (byMeasure == null) {
                byMeasure = new HashMap<String, PcmForecastValue>();
                byPeriod.put(periodStart, byMeasure);
            }
            byMeasure.put(pfv.getForecastMeasureKey(), pfv);
        }
        return byPeriod;
    }

    @Override
    public boolean equals(Object other)
    {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof PcmForecast))
            return false;
        PcmForecast castOther = (PcmForecast) other;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.getForecastType(),castOther.getForecastType());
        eb.append(this.getStatus(),castOther.getStatus());
        eb.append(this.getItem(),castOther.getItem());
        eb.append(this.getSite(),castOther.getSite());
        eb.append(this.getForecastModel(),castOther.getForecastModel());
        return eb.isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(this.getForecastType())
                .append(this.getForecastModel())
                .append(this.getStatus())
                .toHashCode();
    }

    public ForecastModel getForecastModel() {
        return forecastModel;
    }

    public void setLastRolloverStartDate(Date lastRolloverStartDate)
    {
        this.forecastChangeTracker.firePropertyChangeEvent("lastRolloverStartDate", this.lastRolloverStartDate, lastRolloverStartDate);
        this.lastRolloverStartDate = lastRolloverStartDate;
    }


    public void setForecastModel(ForecastModel forecastModel) {
        this.forecastChangeTracker.firePropertyChangeEvent("forecastModel", this.forecastModel, forecastModel);
        this.forecastModel = forecastModel;
    }
    /**
     * Generates a short title based on the business key
     * @return
     */
    public String getTitle()
    {
        List<Object> args = new ArrayList<Object>();
        args.add(this.getForecastType());
        args.add(item!=null ? item.getItemNumber() : "");
        args.add(site!=null ? site.getSiteDescription() : "");
        args.add(this.getState());
        args.add(this.getForecastModel().getAlias());
        return SCPlatformMessages.INSTANCE.getAuditMessage("audit.forecastTitle", args.toArray(), null);
    }

    @Override
    public String toString()
    {
        return getTitle();
    }


    public ObjectNode getCurrentStateAsJSON() {
        ObjectMapper om = new ObjectMapper();
        ObjectNode o = om.createObjectNode();
        o.put("status",status);
        o.put("state",getState());
        o.put("forecastExternalId", forecastExternalId);
        o.put("forecastOriginExternalId", forecastOriginExternalId);
        o.put("description",description);
        o.put("lastChangeBy",lastChangeBy);
        o.put("forecastType",forecastType);
        o.put("periodType",periodType);
        o.put("calendarName",calendarName);
        o.put("periodicAdjustmentType",periodicAdjustmentType);
        o.put("forecastModel",forecastModel == null ? null : forecastModel.toString());
        o.put("periodicAdjustmentAmount",periodicAdjustmentAmount);
        o.put("confidenceFactor",confidenceFactor);
        if (remainingRollovers != null) {
            o.put("remainingRollovers",remainingRollovers);
        }
        o.put("effectiveFromDt",effectiveFromDt == null ? null : ISO8601.format(effectiveFromDt));
        o.put("effectiveToDt",effectiveToDt == null ? null : ISO8601.format(effectiveToDt));
        o.put("lastRolloverStartDate",lastRolloverStartDate == null ? null : ISO8601.format(lastRolloverStartDate));
        o.put("deleteFlag",deleteFlag);
        o.put("currentFlag",currentFlag);
        ArrayNode fValues = o.putArray("forecastValues");
        for (PcmForecastValue fv : getForecastValues()) {
            fValues.add(fv.getCurrentStateAsJSON());
        }
        return o;
    }

    public String getStringAttribute1() {
        return stringAttribute1;
    }

    public void setStringAttribute1(String stringAttribute1) {
        this.stringAttribute1 = stringAttribute1;
    }

    public String getStringAttribute2() {
        return stringAttribute2;
    }

    public void setStringAttribute2(String stringAttribute2) {
        this.stringAttribute2 = stringAttribute2;
    }

    public String getStringAttribute3() {
        return stringAttribute3;
    }

    public void setStringAttribute3(String stringAttribute3) {
        this.stringAttribute3 = stringAttribute3;
    }

    public String getStringAttribute4() {
        return stringAttribute4;
    }

    public void setStringAttribute4(String stringAttribute4) {
        this.stringAttribute4 = stringAttribute4;
    }

    public String getStringAttribute5() {
        return stringAttribute5;
    }

    public void setStringAttribute5(String stringAttribute5) {
        this.stringAttribute5 = stringAttribute5;
    }

    public String getStringAttribute6() {
        return stringAttribute6;
    }

    public void setStringAttribute6(String stringAttribute6) {
        this.stringAttribute6 = stringAttribute6;
    }

    public String getStringAttribute7() {
        return stringAttribute7;
    }

    public void setStringAttribute7(String stringAttribute7) {
        this.stringAttribute7 = stringAttribute7;
    }

    public String getStringAttribute8() {
        return stringAttribute8;
    }

    public void setStringAttribute8(String stringAttribute8) {
        this.stringAttribute8 = stringAttribute8;
    }

    public String getStringAttribute9() {
        return stringAttribute9;
    }

    public void setStringAttribute9(String stringAttribute9) {
        this.stringAttribute9 = stringAttribute9;
    }

    public String getStringAttribute10() {
        return stringAttribute10;
    }

    public void setStringAttribute10(String stringAttribute10) {
        this.stringAttribute10 = stringAttribute10;
    }

    public Integer getNumberAttribute1() {
        return numberAttribute1;
    }

    public void setNumberAttribute1(Integer numberAttribute1) {
        this.numberAttribute1 = numberAttribute1;
    }

    public Integer getNumberAttribute2() {
        return numberAttribute2;
    }

    public void setNumberAttribute2(Integer numberAttribute2) {
        this.numberAttribute2 = numberAttribute2;
    }

    public Integer getNumberAttribute3() {
        return numberAttribute3;
    }

    public void setNumberAttribute3(Integer numberAttribute3) {
        this.numberAttribute3 = numberAttribute3;
    }

    public Integer getNumberAttribute4() {
        return numberAttribute4;
    }

    public void setNumberAttribute4(Integer numberAttribute4) {
        this.numberAttribute4 = numberAttribute4;
    }

    public Integer getNumberAttribute5() {
        return numberAttribute5;
    }

    public void setNumberAttribute5(Integer numberAttribute5) {
        this.numberAttribute5 = numberAttribute5;
    }

    public Integer getNumberAttribute6() {
        return numberAttribute6;
    }

    public void setNumberAttribute6(Integer numberAttribute6) {
        this.numberAttribute6 = numberAttribute6;
    }

    public Integer getNumberAttribute7() {
        return numberAttribute7;
    }

    public void setNumberAttribute7(Integer numberAttribute7) {
        this.numberAttribute7 = numberAttribute7;
    }

    public Integer getNumberAttribute8() {
        return numberAttribute8;
    }

    public void setNumberAttribute8(Integer numberAttribute8) {
        this.numberAttribute8 = numberAttribute8;
    }

    public Integer getNumberAttribute9() {
        return numberAttribute9;
    }

    public void setNumberAttribute9(Integer numberAttribute9) {
        this.numberAttribute9 = numberAttribute9;
    }

    public Integer getNumberAttribute10() {
        return numberAttribute10;
    }

    public void setNumberAttribute10(Integer numberAttribute10) {
        this.numberAttribute10 = numberAttribute10;
    }

    public BigDecimal getFloatAttribute1() {
        return floatAttribute1;
    }

    public void setFloatAttribute1(BigDecimal floatAttribute1) {
        this.floatAttribute1 = floatAttribute1;
    }

    public BigDecimal getFloatAttribute2() {
        return floatAttribute2;
    }

    public void setFloatAttribute2(BigDecimal floatAttribute2) {
        this.floatAttribute2 = floatAttribute2;
    }

    public BigDecimal getFloatAttribute3() {
        return floatAttribute3;
    }

    public void setFloatAttribute3(BigDecimal floatAttribute3) {
        this.floatAttribute3 = floatAttribute3;
    }

    public BigDecimal getFloatAttribute4() {
        return floatAttribute4;
    }

    public void setFloatAttribute4(BigDecimal floatAttribute4) {
        this.floatAttribute4 = floatAttribute4;
    }

    public BigDecimal getFloatAttribute5() {
        return floatAttribute5;
    }

    public void setFloatAttribute5(BigDecimal floatAttribute5) {
        this.floatAttribute5 = floatAttribute5;
    }

    public BigDecimal getFloatAttribute6() {
        return floatAttribute6;
    }

    public void setFloatAttribute6(BigDecimal floatAttribute6) {
        this.floatAttribute6 = floatAttribute6;
    }

    public BigDecimal getFloatAttribute7() {
        return floatAttribute7;
    }

    public void setFloatAttribute7(BigDecimal floatAttribute7) {
        this.floatAttribute7 = floatAttribute7;
    }

    public BigDecimal getFloatAttribute8() {
        return floatAttribute8;
    }

    public void setFloatAttribute8(BigDecimal floatAttribute8) {
        this.floatAttribute8 = floatAttribute8;
    }

    public BigDecimal getFloatAttribute9() {
        return floatAttribute9;
    }

    public void setFloatAttribute9(BigDecimal floatAttribute9) {
        this.floatAttribute9 = floatAttribute9;
    }

    public BigDecimal getFloatAttribute10() {
        return floatAttribute10;
    }

    public void setFloatAttribute10(BigDecimal floatAttribute10) {
        this.floatAttribute10 = floatAttribute10;
    }

    public Date getDateAttribute1() {
        return dateAttribute1;
    }

    public void setDateAttribute1(Date dateAttribute1) {
        this.dateAttribute1 = dateAttribute1;
    }

    public Date getDateAttribute2() {
        return dateAttribute2;
    }

    public void setDateAttribute2(Date dateAttribute2) {
        this.dateAttribute2 = dateAttribute2;
    }

    public Date getDateAttribute3() {
        return dateAttribute3;
    }

    public void setDateAttribute3(Date dateAttribute3) {
        this.dateAttribute3 = dateAttribute3;
    }

    public Date getDateAttribute4() {
        return dateAttribute4;
    }

    public void setDateAttribute4(Date dateAttribute4) {
        this.dateAttribute4 = dateAttribute4;
    }

    public Date getDateAttribute5() {
        return dateAttribute5;
    }

    public void setDateAttribute5(Date dateAttribute5) {
        this.dateAttribute5 = dateAttribute5;
    }

    public Date getDateAttribute6() {
        return dateAttribute6;
    }

    public void setDateAttribute6(Date dateAttribute6) {
        this.dateAttribute6 = dateAttribute6;
    }

    public Date getDateAttribute7() {
        return dateAttribute7;
    }

    public void setDateAttribute7(Date dateAttribute7) {
        this.dateAttribute7 = dateAttribute7;
    }

    public Date getDateAttribute8() {
        return dateAttribute8;
    }

    public void setDateAttribute8(Date dateAttribute8) {
        this.dateAttribute8 = dateAttribute8;
    }

    public Date getDateAttribute9() {
        return dateAttribute9;
    }

    public void setDateAttribute9(Date dateAttribute9) {
        this.dateAttribute9 = dateAttribute9;
    }

    public Date getDateAttribute10() {
        return dateAttribute10;
    }

    public void setDateAttribute10(Date dateAttribute10) {
        this.dateAttribute10 = dateAttribute10;
    }



}
