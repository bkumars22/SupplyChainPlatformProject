/**
 * @Forecast.java@
 *
 * Created on Thu Oct 23 06:51:48 PDT 2014
 *
 *      Copyright (c) 2014 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
 
 /**************************************************
 * THIS IS GENERATED CODE.DO NOT MODIFY THIS CODE. * 
 * YOUR CHANGES WILL BE MOST DEFINITELY BE LOST.   *
 ***************************************************/
 
package com.test.selenium.scplatform.messages.forecast;
 
import java.util.List;

import javax.annotation.processing.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.joda.time.DateTime;

import com.scplatform.qa.iris.model.FieldDefinition;
import com.scplatform.qa.iris.model.MessageLine;
import com.scplatform.qa.iris.model.annotations.FlexCDMData;
import com.scplatform.qa.iris.model.annotations.Metadata;
import com.scplatform.qa.iris.model.annotations.RestrictToStrings;
import com.scplatform.qa.iris.model.exceptions.FieldNotFoundException;
import com.scplatform.qa.iris.model.exceptions.InvalidValueException;
import com.scplatform.qa.iris.model.proxy.MessageLineProxy;
import com.scplatform.qa.iris.predicates.FieldPredicate;
import com.scplatform.qa.iris.predicates.FieldPredicateFactory;
import com.scplatform.qa.iris.predicates.proxy.MessageLinePredicateBuilderProxy;
import com.test.selenium.scplatform.messages.forecast.subClasses.PointInTime;
 
@Generated("IrisCodeGenerator")
public interface Forecast extends MessageLine {

    /**
     * UOM for the display of forecast buckets: WEEKLY or MONTHLY.
     */
    @NotNull
    @Size(max=64)
    @RestrictToStrings({"Day","Week","Month","Period"})
    @Metadata(index=15, displayName="BucketUnitOfMeasure")
    @FlexCDMData({"*","*.down"})
    public String getBucketUnitOfMeasure();
    /**
     * UOM for the display of forecast buckets: WEEKLY or MONTHLY.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"Day","Week","Month","Period"})</li>
     * <li>@Metadata(index=15, displayName="BucketUnitOfMeasure")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBucketUnitOfMeasure(String value);


    /**
     * The item's business.
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=7, displayName="BusinessEntity")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntity();
    /**
     * The item's business.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=7, displayName="BusinessEntity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntity(String value);


    /**
     * The business type.
     */
    @NotNull
    @Size(max=64)
    @RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})
    @Metadata(index=8, displayName="BusinessEntityType")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntityType();
    /**
     * The business type.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})</li>
     * <li>@Metadata(index=8, displayName="BusinessEntityType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntityType(String value);


    /**
     * Fiscal or Gregorian calendar to use for the display
     */
    @NotNull
    @Size(max=64)
    @RestrictToStrings({"Fiscal","Standard","Manufacturing"})
    @Metadata(index=16, displayName="CalendarName")
    @FlexCDMData({"*","*.down"})
    public String getCalendarName();
    /**
     * Fiscal or Gregorian calendar to use for the display
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"Fiscal","Standard","Manufacturing"})</li>
     * <li>@Metadata(index=16, displayName="CalendarName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCalendarName(String value);


    /**
     * A ranking entered by the user to indicate confidence in the forecast.
     */
    @Size(max=126)
    @Metadata(index=19, displayName="ConfidenceFactor")
    @FlexCDMData({"*","*.down"})
    public float getConfidenceFactor();
    /**
     * A ranking entered by the user to indicate confidence in the forecast.
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=19, displayName="ConfidenceFactor")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setConfidenceFactor(float value);


    /**
     * The forecast description
     */
    @Size(max=1024)
    @Metadata(index=1, displayName="Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * The forecast description
     * Annotations:
     * <ul>
     * <li>@Size(max=1024)</li>
     * <li>@Metadata(index=1, displayName="Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDescription(String value);


    /**
     * End date for display of buckets. Note that all forecasts are calculated for 12 months / 52 weeks.
     */
    @Metadata(index=10, displayName="EndDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEndDate();
    /**
     * End date for display of buckets. Note that all forecasts are calculated for 12 months / 52 weeks.
     * Annotations:
     * <ul>
     * <li>@Metadata(index=10, displayName="EndDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEndDate(DateTime value);


    /**
     * External ID of forecard
     */
    @Size(max=255)
    @Metadata(index=0, displayName="ForecastExternalId")
    @FlexCDMData({"*","*.down"})
    public String getForecastExternalId();
    /**
     * External ID of forecard
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="ForecastExternalId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setForecastExternalId(String value);


    /**
     * The cost forecast type, either a current or adjustable forecast.
     */
    @NotNull
    @Size(max=64)
    @RestrictToStrings({"CURRENT","ADJUSTABLE"})
    @Metadata(index=13, displayName="ForecastModel")
    @FlexCDMData({"*","*.down"})
    public String getForecastModel();
    /**
     * The cost forecast type, either a current or adjustable forecast.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"CURRENT","ADJUSTABLE"})</li>
     * <li>@Metadata(index=13, displayName="ForecastModel")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setForecastModel(String value);


    /**
     * Type of forecast
     */
    @NotNull
    @Size(max=64)
    @RestrictToStrings({"Cost","Inventory"})
    @Metadata(index=12, displayName="ForecastType")
    @FlexCDMData({"*","*.down"})
    public String getForecastType();
    /**
     * Type of forecast
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"Cost","Inventory"})</li>
     * <li>@Metadata(index=12, displayName="ForecastType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setForecastType(String value);


    /**
     * The item being forecasted.
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=2, displayName="ItemIdentifier")
    @FlexCDMData({"*","*.down"})
    public String getItemIdentifier();
    /**
     * The item being forecasted.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=2, displayName="ItemIdentifier")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemIdentifier(String value);


    /**
     * The revision of the item.
     */
    @Size(max=255)
    @Metadata(index=4, displayName="ItemRevision")
    @FlexCDMData({"*","*.down"})
    public String getItemRevision();
    /**
     * The revision of the item.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=4, displayName="ItemRevision")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemRevision(String value);


    /**
     * The type of the item
     */
    @Size(max=64)
    @RestrictToStrings({"Item","CFG","Supplier Item","Mfg Item","Phantom Item"})
    @Metadata(index=6, displayName="ItemType")
    @FlexCDMData({"*","*.down"})
    public String getItemType();
    /**
     * The type of the item
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"Item","CFG","Supplier Item","Mfg Item","Phantom Item"})</li>
     * <li>@Metadata(index=6, displayName="ItemType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemType(String value);


    /**
     * The supplier's external reference number or other part identifier.
     */
    @Size(max=255)
    @Metadata(index=3, displayName="ItemUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getItemUniqueId();
    /**
     * The supplier's external reference number or other part identifier.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=3, displayName="ItemUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemUniqueId(String value);


    /**
     * The version of the item.
     */
    @Size(max=255)
    @Metadata(index=5, displayName="ItemVersion")
    @FlexCDMData({"*","*.down"})
    public String getItemVersion();
    /**
     * The version of the item.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=5, displayName="ItemVersion")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemVersion(String value);


    /**
     * Userid that performed the last state change of this record or null if no state. If the forecast is auto- approved by the system, this attribute also stores the string "SYSTEM"
     */
    @Size(max=255)
    @Metadata(index=14, displayName="LastChangeBy")
    @FlexCDMData({"*","*.down"})
    public String getLastChangeBy();
    /**
     * Userid that performed the last state change of this record or null if no state. If the forecast is auto- approved by the system, this attribute also stores the string "SYSTEM"
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=14, displayName="LastChangeBy")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLastChangeBy(String value);


    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
<br>
     * A=Add
<br>
     * C=Changed
<br>
     * D=Delete
<br>
     * U=Unchanged<br>
     */
    @Size(max=64)
    @RestrictToStrings({"A","C","D","U"})
    @Metadata(index=21, displayName="OperationCode")
    @FlexCDMData({"*","*.down"})
    public String getOperationCode();
    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
<br>
     * A=Add
<br>
     * C=Changed
<br>
     * D=Delete
<br>
     * U=Unchanged<br>
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"A","C","D","U"})</li>
     * <li>@Metadata(index=21, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    @Size(max=255)
    @Metadata(index=23, displayName="PRODUCTIONResponsibility")
    @FlexCDMData({"*","*.down"})
    public String getPRODUCTIONResponsibility();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=23, displayName="PRODUCTIONResponsibility")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPRODUCTIONResponsibility(String value);


    /**
     * Unit of Measure for the periodic adjustment. Periodic adjustment can be a percentage or a fixed amount.
     */
    @Size(max=64)
    @RestrictToStrings({"Fixed","Percent"})
    @Metadata(index=18, displayName="PeriodicAdjustmentType")
    @FlexCDMData({"*","*.down"})
    public String getPeriodicAdjustmentType();
    /**
     * Unit of Measure for the periodic adjustment. Periodic adjustment can be a percentage or a fixed amount.
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"Fixed","Percent"})</li>
     * <li>@Metadata(index=18, displayName="PeriodicAdjustmentType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPeriodicAdjustmentType(String value);


    /**
     * For buckets in the forecast period that do not have an actual Item Landed Cost, this is a periodic adjustment to be applied to the previous bucket?s cost forecast. A negative number means a price increase. E.g., assume we have actual cost data of $10 for Item A for the month June, but not July or August and that the periodic adjustment is 0.5%. The forecast for June is $10, for July is $9.95 and for August is $9.90025.
     */
    @Size(max=126)
    @Metadata(index=17, displayName="PeriodicAdjustmentValue")
    @FlexCDMData({"*","*.down"})
    public float getPeriodicAdjustmentValue();
    /**
     * For buckets in the forecast period that do not have an actual Item Landed Cost, this is a periodic adjustment to be applied to the previous bucket?s cost forecast. A negative number means a price increase. E.g., assume we have actual cost data of $10 for Item A for the month June, but not July or August and that the periodic adjustment is 0.5%. The forecast for June is $10, for July is $9.95 and for August is $9.90025.
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=17, displayName="PeriodicAdjustmentValue")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPeriodicAdjustmentValue(float value);


    /**
     * Cost Record Range is a Sub Structure.<br>
     * A collection of forecast bucket records is contained within each forecast record. Each bucket models a particular point in time (PIT) value within the forecast.
     */
    @Metadata(index=22, displayName="PointInTime")
    @FlexCDMData({"*","*.down"})
    public List<PointInTime> getPointInTime();
    /**
     * Cost Record Range is a Sub Structure.<br>
     * A collection of forecast bucket records is contained within each forecast record. Each bucket models a particular point in time (PIT) value within the forecast.
     * Annotations:
     * <ul>
     * <li>@Metadata(index=22, displayName="PointInTime")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPointInTime(List<PointInTime> value);


    /**
     * Default=0
     */
    @Metadata(index=20, displayName="RemainingRolloverPeriods")
    @FlexCDMData({"*","*.down"})
    public int getRemainingRolloverPeriods();
    /**
     * Default=0
     * Annotations:
     * <ul>
     * <li>@Metadata(index=20, displayName="RemainingRolloverPeriods")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setRemainingRolloverPeriods(int value);


    @Size(max=255)
    @Metadata(index=24, displayName="SERVICEResponsibility")
    @FlexCDMData({"*","*.down"})
    public String getSERVICEResponsibility();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=24, displayName="SERVICEResponsibility")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSERVICEResponsibility(String value);


    /**
     * site for when a forecast is site specific
     */
    @Size(max=255)
    @Metadata(index=11, displayName="Site")
    @FlexCDMData({"*","*.down"})
    public String getSite();
    /**
     * site for when a forecast is site specific
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=11, displayName="Site")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite(String value);


    /**
     * The start of the forecast period. Default is today
     */
    @Metadata(index=9, displayName="StartDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getStartDate();
    /**
     * The start of the forecast period. Default is today
     * Annotations:
     * <ul>
     * <li>@Metadata(index=9, displayName="StartDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setStartDate(DateTime value);


    /**
     * Used for the status verification on Forecat Search page
     */
    @Size(max=64)
    @RestrictToStrings({"New","Closed","Pending","Approved"})
    @Metadata(index=25, displayName="Verification_Status")
    @FlexCDMData({"*","*.down"})
    public String getVerification_Status();
    /**
     * Used for the status verification on Forecat Search page
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"New","Closed","Pending","Approved"})</li>
     * <li>@Metadata(index=25, displayName="Verification_Status")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVerification_Status(String value);

    
    /**
     * Fields Defined by this interface.
     * <p>
     * <strong>NOTE</strong><br/>
     * This inner interface allows easy reference to the FieldDefinition. However, please note that the field definition
     * is not complete and <strong>holds only the internal field name</strong>. Please use <code>getMessageStructure()</code> to
     * get the full structure for the fields
     * </p>
     */
    public interface Fields {
        /**
         * UOM for the display of forecast buckets: WEEKLY or MONTHLY.
         */
        public static final FieldDefinition BUCKETUNITOFMEASURE = new FieldDefinition("BucketUnitOfMeasure");
        /**
         * The item's business.
         */
        public static final FieldDefinition BUSINESSENTITY = new FieldDefinition("BusinessEntity");
        /**
         * The business type.
         */
        public static final FieldDefinition BUSINESSENTITYTYPE = new FieldDefinition("BusinessEntityType");
        /**
         * Fiscal or Gregorian calendar to use for the display
         */
        public static final FieldDefinition CALENDARNAME = new FieldDefinition("CalendarName");
        /**
         * A ranking entered by the user to indicate confidence in the forecast.
         */
        public static final FieldDefinition CONFIDENCEFACTOR = new FieldDefinition("ConfidenceFactor");
        /**
         * The forecast description
         */
        public static final FieldDefinition DESCRIPTION = new FieldDefinition("Description");
        /**
         * End date for display of buckets. Note that all forecasts are calculated for 12 months / 52 weeks.
         */
        public static final FieldDefinition ENDDATE = new FieldDefinition("EndDate");
        /**
         * External ID of forecard
         */
        public static final FieldDefinition FORECASTEXTERNALID = new FieldDefinition("ForecastExternalId");
        /**
         * The cost forecast type, either a current or adjustable forecast.
         */
        public static final FieldDefinition FORECASTMODEL = new FieldDefinition("ForecastModel");
        /**
         * Type of forecast
         */
        public static final FieldDefinition FORECASTTYPE = new FieldDefinition("ForecastType");
        /**
         * The item being forecasted.
         */
        public static final FieldDefinition ITEMIDENTIFIER = new FieldDefinition("ItemIdentifier");
        /**
         * The revision of the item.
         */
        public static final FieldDefinition ITEMREVISION = new FieldDefinition("ItemRevision");
        /**
         * The type of the item
         */
        public static final FieldDefinition ITEMTYPE = new FieldDefinition("ItemType");
        /**
         * The supplier's external reference number or other part identifier.
         */
        public static final FieldDefinition ITEMUNIQUEID = new FieldDefinition("ItemUniqueId");
        /**
         * The version of the item.
         */
        public static final FieldDefinition ITEMVERSION = new FieldDefinition("ItemVersion");
        /**
         * Userid that performed the last state change of this record or null if no state. If the forecast is auto- approved by the system, this attribute also stores the string "SYSTEM"
         */
        public static final FieldDefinition LASTCHANGEBY = new FieldDefinition("LastChangeBy");
        /**
         * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
<br>
         * A=Add
<br>
         * C=Changed
<br>
         * D=Delete
<br>
         * U=Unchanged<br>
         */
        public static final FieldDefinition OPERATIONCODE = new FieldDefinition("OperationCode");
        public static final FieldDefinition PRODUCTIONRESPONSIBILITY = new FieldDefinition("PRODUCTIONResponsibility");
        /**
         * Unit of Measure for the periodic adjustment. Periodic adjustment can be a percentage or a fixed amount.
         */
        public static final FieldDefinition PERIODICADJUSTMENTTYPE = new FieldDefinition("PeriodicAdjustmentType");
        /**
         * For buckets in the forecast period that do not have an actual Item Landed Cost, this is a periodic adjustment to be applied to the previous bucket?s cost forecast. A negative number means a price increase. E.g., assume we have actual cost data of $10 for Item A for the month June, but not July or August and that the periodic adjustment is 0.5%. The forecast for June is $10, for July is $9.95 and for August is $9.90025.
         */
        public static final FieldDefinition PERIODICADJUSTMENTVALUE = new FieldDefinition("PeriodicAdjustmentValue");
        /**
         * Cost Record Range is a Sub Structure.<br>
         * A collection of forecast bucket records is contained within each forecast record. Each bucket models a particular point in time (PIT) value within the forecast.
         */
        public static final FieldDefinition POINTINTIME = new FieldDefinition("PointInTime");
        /**
         * Default=0
         */
        public static final FieldDefinition REMAININGROLLOVERPERIODS = new FieldDefinition("RemainingRolloverPeriods");
        public static final FieldDefinition SERVICERESPONSIBILITY = new FieldDefinition("SERVICEResponsibility");
        /**
         * site for when a forecast is site specific
         */
        public static final FieldDefinition SITE = new FieldDefinition("Site");
        /**
         * The start of the forecast period. Default is today
         */
        public static final FieldDefinition STARTDATE = new FieldDefinition("StartDate");
        /**
         * Used for the status verification on Forecat Search page
         */
        public static final FieldDefinition VERIFICATION_STATUS = new FieldDefinition("Verification_Status");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link Forecast.PredicateBuilder} 
     */
    public interface ForecastFieldPredicateFactory<T extends Forecast>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereBucketUnitOfMeasure();
        public FieldPredicate<T,String> whereBusinessEntity();
        public FieldPredicate<T,String> whereBusinessEntityType();
        public FieldPredicate<T,String> whereCalendarName();
        public FieldPredicate<T,Float> whereConfidenceFactor();
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,DateTime> whereEndDate();
        public FieldPredicate<T,String> whereForecastExternalId();
        public FieldPredicate<T,String> whereForecastModel();
        public FieldPredicate<T,String> whereForecastType();
        public FieldPredicate<T,String> whereItemIdentifier();
        public FieldPredicate<T,String> whereItemRevision();
        public FieldPredicate<T,String> whereItemType();
        public FieldPredicate<T,String> whereItemUniqueId();
        public FieldPredicate<T,String> whereItemVersion();
        public FieldPredicate<T,String> whereLastChangeBy();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> wherePRODUCTIONResponsibility();
        public FieldPredicate<T,String> wherePeriodicAdjustmentType();
        public FieldPredicate<T,Float> wherePeriodicAdjustmentValue();
        public FieldPredicate<T,String> wherePointInTime();
        public FieldPredicate<T,Integer> whereRemainingRolloverPeriods();
        public FieldPredicate<T,String> whereSERVICEResponsibility();
        public FieldPredicate<T,String> whereSite();
        public FieldPredicate<T,DateTime> whereStartDate();
        public FieldPredicate<T,String> whereVerification_Status();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link Forecast}
         */
        public static Forecast newInstance() {
            return MessageLineProxy.newInstance(Forecast.class);
        }
        
        /**
         * Clone a instance of {@link Forecast}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static Forecast clone(Forecast dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(Forecast.class, dataToClone);
        }
        
        /**
         * Use this method to create a field based predicate on {@link Forecast}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;Forecast&gt; pred = Forecast
                                     .Factory
                                             .newFieldPredicate()
                                             .where&lt;FieldName&gt;()
                                             .satisfies(new SomeClassThatExtendsCritera() {
                                                                        
                                                            public boolean evaluate() {
                                                             &lt;criteria code use methods defined in the Criteria class&gt;
                                                            }
                                                     })
                                                   .build();
         * </code>
         * </pre>
         */
        @SuppressWarnings("unchecked")
        public static ForecastFieldPredicateFactory<Forecast> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(Forecast.class, ForecastFieldPredicateFactory.class);
        }
    }
 }