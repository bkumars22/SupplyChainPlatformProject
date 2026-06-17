/**
 * @PointInTime.java@
 *
 * Created on Thu Oct 23 06:52:46 PDT 2014
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
 
package com.test.selenium.scplatform.messages.forecast.subClasses;
 
import javax.annotation.processing.Generated;
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
import com.test.selenium.scplatform.messages.calendar.Calendar;
 
@Generated("IrisCodeGenerator")
public interface PointInTime extends MessageLine {

    /**
     * Calendar is a Sub Structure. Not part of the spec, but adding here for easier validation later
     */
    @Metadata(index=8, displayName="Calendar")
    @FlexCDMData({"*","*.down"})
    public Calendar getCalendar();
    /**
     * Calendar is a Sub Structure. Not part of the spec, but adding here for easier validation later
     * Annotations:
     * <ul>
     * <li>@Metadata(index=8, displayName="Calendar")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCalendar(Calendar value);


    /**
     * End date for the point in time.
     */
    @Metadata(index=1, displayName="EndDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEndDate();
    /**
     * End date for the point in time.
     * Annotations:
     * <ul>
     * <li>@Metadata(index=1, displayName="EndDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEndDate(DateTime value);


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
    @Metadata(index=7, displayName="OperationCode")
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
     * <li>@Metadata(index=7, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    /**
     * Name defining the point in time period. If the startDate is not supplied. The startDate takes precedence.
     */
    @Size(max=255)
    @Metadata(index=2, displayName="Period")
    @FlexCDMData({"*","*.down"})
    public String getPeriod();
    /**
     * Name defining the point in time period. If the startDate is not supplied. The startDate takes precedence.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=2, displayName="Period")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPeriod(String value);


    /**
     * Unit of Measure for the periodic adjustment. Periodic adjustment can be a percentage or a fixed amount.
     */
    @Size(max=64)
    @RestrictToStrings({"Fixed","Percent"})
    @Metadata(index=6, displayName="PeriodicAdjustmentType")
    @FlexCDMData({"*","*.down"})
    public String getPeriodicAdjustmentType();
    /**
     * Unit of Measure for the periodic adjustment. Periodic adjustment can be a percentage or a fixed amount.
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"Fixed","Percent"})</li>
     * <li>@Metadata(index=6, displayName="PeriodicAdjustmentType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPeriodicAdjustmentType(String value);


    /**
     * This is a periodic adjustment to be applied to the point in time value. A negative number means in increase to the value.
     */
    @Size(max=126)
    @Metadata(index=5, displayName="PeriodicAdjustmentValue")
    @FlexCDMData({"*","*.down"})
    public float getPeriodicAdjustmentValue();
    /**
     * This is a periodic adjustment to be applied to the point in time value. A negative number means in increase to the value.
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=5, displayName="PeriodicAdjustmentValue")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPeriodicAdjustmentValue(float value);


    /**
     * The point in time type
     */
    @Size(max=64)
    @Metadata(index=3, displayName="PitTypeCode")
    @FlexCDMData({"*","*.down"})
    public String getPitTypeCode();
    /**
     * The point in time type
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@Metadata(index=3, displayName="PitTypeCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPitTypeCode(String value);


    /**
     * The point in time value
     */
    @Size(max=126)
    @Metadata(index=4, displayName="PitValue")
    @FlexCDMData({"*","*.down"})
    public float getPitValue();
    /**
     * The point in time value
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=4, displayName="PitValue")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPitValue(float value);


    /**
     * The start of the point in time period.
     */
    @Metadata(index=0, displayName="StartDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getStartDate();
    /**
     * The start of the point in time period.
     * Annotations:
     * <ul>
     * <li>@Metadata(index=0, displayName="StartDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setStartDate(DateTime value);

    
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
         * Calendar is a Sub Structure. Not part of the spec, but adding here for easier validation later
         */
        public static final FieldDefinition CALENDAR = new FieldDefinition("Calendar");
        /**
         * End date for the point in time.
         */
        public static final FieldDefinition ENDDATE = new FieldDefinition("EndDate");
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
        /**
         * Name defining the point in time period. If the startDate is not supplied. The startDate takes precedence.
         */
        public static final FieldDefinition PERIOD = new FieldDefinition("Period");
        /**
         * Unit of Measure for the periodic adjustment. Periodic adjustment can be a percentage or a fixed amount.
         */
        public static final FieldDefinition PERIODICADJUSTMENTTYPE = new FieldDefinition("PeriodicAdjustmentType");
        /**
         * This is a periodic adjustment to be applied to the point in time value. A negative number means in increase to the value.
         */
        public static final FieldDefinition PERIODICADJUSTMENTVALUE = new FieldDefinition("PeriodicAdjustmentValue");
        /**
         * The point in time type
         */
        public static final FieldDefinition PITTYPECODE = new FieldDefinition("PitTypeCode");
        /**
         * The point in time value
         */
        public static final FieldDefinition PITVALUE = new FieldDefinition("PitValue");
        /**
         * The start of the point in time period.
         */
        public static final FieldDefinition STARTDATE = new FieldDefinition("StartDate");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link PointInTime.PredicateBuilder} 
     */
    public interface PointInTimeFieldPredicateFactory<T extends PointInTime>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereCalendar();
        public FieldPredicate<T,DateTime> whereEndDate();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> wherePeriod();
        public FieldPredicate<T,String> wherePeriodicAdjustmentType();
        public FieldPredicate<T,Float> wherePeriodicAdjustmentValue();
        public FieldPredicate<T,String> wherePitTypeCode();
        public FieldPredicate<T,Float> wherePitValue();
        public FieldPredicate<T,DateTime> whereStartDate();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link PointInTime}
         */
        public static PointInTime newInstance() {
            return MessageLineProxy.newInstance(PointInTime.class);
        }
        
        /**
         * Clone a instance of {@link PointInTime}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static PointInTime clone(PointInTime dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(PointInTime.class, dataToClone);
        }
		
        /**
         * Use this method to create a field based predicate on {@link PointInTime}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;PointInTime&gt; pred = PointInTime
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
        public static PointInTimeFieldPredicateFactory<PointInTime> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(PointInTime.class, PointInTimeFieldPredicateFactory.class);
        }
    }
 }