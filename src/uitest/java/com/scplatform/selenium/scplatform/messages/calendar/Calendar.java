/**
 * @Calendar.java@
 *
 * Created on Mon Oct 20 12:15:33 PDT 2014
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
 
package com.test.selenium.scplatform.messages.calendar;
 
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
 
@Generated("IrisCodeGenerator")
public interface Calendar extends MessageLine {

    /**
     * Type of calendar 454,445,544 or calendar
     * 454=Defines the type of calendar 454-3 month quarter divided into 4 weeks, 5 weeks, and 4 weeks.
     * 445=Defines the type of calendar 445-3 month quarter divided into 4 weeks, 4 weeks, and 5 weeks.
     * 544=Defines the type of calendar 544-3 month quarter divided into 5 weeks, 4 weeks, and 4 weeks.
     */
    @Size(max=64)
    @RestrictToStrings({"454","445","554"})
    @Metadata(index=3, displayName="CalendarType")
    @FlexCDMData({"*","*.down"})
    public String getCalendarType();
    /**
     * Type of calendar 454,445,544 or calendar
     * 454=Defines the type of calendar 454-3 month quarter divided into 4 weeks, 5 weeks, and 4 weeks.
     * 445=Defines the type of calendar 445-3 month quarter divided into 4 weeks, 4 weeks, and 5 weeks.
     * 544=Defines the type of calendar 544-3 month quarter divided into 5 weeks, 4 weeks, and 4 weeks.
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"454","445","554"})</li>
     * <li>@Metadata(index=3, displayName="CalendarType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCalendarType(String value);


    @Size(max=1024)
    @Metadata(index=1, displayName="Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=1024)</li>
     * <li>@Metadata(index=1, displayName="Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDescription(String value);


    /**
     * End of the month
     */
    @NotNull
    @Metadata(index=12, displayName="Month_EndDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getMonth_EndDate();
    /**
     * End of the month
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=12, displayName="Month_EndDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setMonth_EndDate(DateTime value);


    /**
     * The fiscal month within the fiscal year
     */
    @NotNull
    @Metadata(index=10, displayName="Month_FiscalMonth")
    @FlexCDMData({"*","*.down"})
    public int getMonth_FiscalMonth();
    /**
     * The fiscal month within the fiscal year
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=10, displayName="Month_FiscalMonth")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setMonth_FiscalMonth(int value);


    /**
     * The month name
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=9, displayName="Month_Name")
    @FlexCDMData({"*","*.down"})
    public String getMonth_Name();
    /**
     * The month name
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=9, displayName="Month_Name")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setMonth_Name(String value);


    /**
     * Start of the month
     */
    @NotNull
    @Metadata(index=11, displayName="Month_StartDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getMonth_StartDate();
    /**
     * Start of the month
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=11, displayName="Month_StartDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setMonth_StartDate(DateTime value);


    /**
     * The calendar name
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=0, displayName="Name")
    @FlexCDMData({"*","*.down"})
    public String getName();
    /**
     * The calendar name
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="Name")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setName(String value);


    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
     * A=Add
     * C=Changed
     * D=Delete
     * U=Unchanged
     */
    @Size(max=64)
    @RestrictToStrings({"A","C","D","U"})
    @Metadata(index=4, displayName="OperationCode")
    @FlexCDMData({"*","*.down"})
    public String getOperationCode();
    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
     * A=Add
     * C=Changed
     * D=Delete
     * U=Unchanged
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"A","C","D","U"})</li>
     * <li>@Metadata(index=4, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    /**
     * End of the quarter
     */
    @NotNull
    @Metadata(index=8, displayName="Quarter_EndDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getQuarter_EndDate();
    /**
     * End of the quarter
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=8, displayName="Quarter_EndDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setQuarter_EndDate(DateTime value);


    /**
     * The fiscal quarter within the year
     */
    @NotNull
    @Metadata(index=6, displayName="Quarter_FiscalQuarter")
    @FlexCDMData({"*","*.down"})
    public int getQuarter_FiscalQuarter();
    /**
     * The fiscal quarter within the year
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=6, displayName="Quarter_FiscalQuarter")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setQuarter_FiscalQuarter(int value);


    /**
     * Name of the quarter
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=5, displayName="Quarter_Name")
    @FlexCDMData({"*","*.down"})
    public String getQuarter_Name();
    /**
     * Name of the quarter
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=5, displayName="Quarter_Name")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setQuarter_Name(String value);


    /**
     * Start of the quarter
     */
    @NotNull
    @Metadata(index=7, displayName="Quarter_StartDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getQuarter_StartDate();
    /**
     * Start of the quarter
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=7, displayName="Quarter_StartDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setQuarter_StartDate(DateTime value);


    /**
     * Date this calendar starts
     */
    @NotNull
    @Metadata(index=2, displayName="StartDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getStartDate();
    /**
     * Date this calendar starts
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=2, displayName="StartDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setStartDate(DateTime value);


    /**
     * End of the week
     */
    @NotNull
    @Metadata(index=16, displayName="Week_EndDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getWeek_EndDate();
    /**
     * End of the week
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=16, displayName="Week_EndDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setWeek_EndDate(DateTime value);


    /**
     * The fiscal week within the fiscal year
     */
    @NotNull
    @Metadata(index=14, displayName="Week_FiscalWeek")
    @FlexCDMData({"*","*.down"})
    public int getWeek_FiscalWeek();
    /**
     * The fiscal week within the fiscal year
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=14, displayName="Week_FiscalWeek")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setWeek_FiscalWeek(int value);


    /**
     * The week name
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=13, displayName="Week_Name")
    @FlexCDMData({"*","*.down"})
    public String getWeek_Name();
    /**
     * The week name
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=13, displayName="Week_Name")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setWeek_Name(String value);


    /**
     * Start of the week
     */
    @NotNull
    @Metadata(index=15, displayName="Week_StartDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getWeek_StartDate();
    /**
     * Start of the week
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=15, displayName="Week_StartDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setWeek_StartDate(DateTime value);

    
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
         * Type of calendar 454,445,544 or calendar
         * 454=Defines the type of calendar 454-3 month quarter divided into 4 weeks, 5 weeks, and 4 weeks.
         * 445=Defines the type of calendar 445-3 month quarter divided into 4 weeks, 4 weeks, and 5 weeks.
         * 544=Defines the type of calendar 544-3 month quarter divided into 5 weeks, 4 weeks, and 4 weeks.
         */
        public static final FieldDefinition CALENDARTYPE = new FieldDefinition("CalendarType");
        public static final FieldDefinition DESCRIPTION = new FieldDefinition("Description");
        /**
         * End of the month
         */
        public static final FieldDefinition MONTH_ENDDATE = new FieldDefinition("Month_EndDate");
        /**
         * The fiscal month within the fiscal year
         */
        public static final FieldDefinition MONTH_FISCALMONTH = new FieldDefinition("Month_FiscalMonth");
        /**
         * The month name
         */
        public static final FieldDefinition MONTH_NAME = new FieldDefinition("Month_Name");
        /**
         * Start of the month
         */
        public static final FieldDefinition MONTH_STARTDATE = new FieldDefinition("Month_StartDate");
        /**
         * The calendar name
         */
        public static final FieldDefinition NAME = new FieldDefinition("Name");
        /**
         * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
         * A=Add
         * C=Changed
         * D=Delete
         * U=Unchanged
         */
        public static final FieldDefinition OPERATIONCODE = new FieldDefinition("OperationCode");
        /**
         * End of the quarter
         */
        public static final FieldDefinition QUARTER_ENDDATE = new FieldDefinition("Quarter_EndDate");
        /**
         * The fiscal quarter within the year
         */
        public static final FieldDefinition QUARTER_FISCALQUARTER = new FieldDefinition("Quarter_FiscalQuarter");
        /**
         * Name of the quarter
         */
        public static final FieldDefinition QUARTER_NAME = new FieldDefinition("Quarter_Name");
        /**
         * Start of the quarter
         */
        public static final FieldDefinition QUARTER_STARTDATE = new FieldDefinition("Quarter_StartDate");
        /**
         * Date this calendar starts
         */
        public static final FieldDefinition STARTDATE = new FieldDefinition("StartDate");
        /**
         * End of the week
         */
        public static final FieldDefinition WEEK_ENDDATE = new FieldDefinition("Week_EndDate");
        /**
         * The fiscal week within the fiscal year
         */
        public static final FieldDefinition WEEK_FISCALWEEK = new FieldDefinition("Week_FiscalWeek");
        /**
         * The week name
         */
        public static final FieldDefinition WEEK_NAME = new FieldDefinition("Week_Name");
        /**
         * Start of the week
         */
        public static final FieldDefinition WEEK_STARTDATE = new FieldDefinition("Week_StartDate");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link Calendar.PredicateBuilder} 
     */
    public interface CalendarFieldPredicateFactory<T extends Calendar>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereCalendarType();
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,DateTime> whereMonth_EndDate();
        public FieldPredicate<T,Integer> whereMonth_FiscalMonth();
        public FieldPredicate<T,String> whereMonth_Name();
        public FieldPredicate<T,DateTime> whereMonth_StartDate();
        public FieldPredicate<T,String> whereName();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,DateTime> whereQuarter_EndDate();
        public FieldPredicate<T,Integer> whereQuarter_FiscalQuarter();
        public FieldPredicate<T,String> whereQuarter_Name();
        public FieldPredicate<T,DateTime> whereQuarter_StartDate();
        public FieldPredicate<T,DateTime> whereStartDate();
        public FieldPredicate<T,DateTime> whereWeek_EndDate();
        public FieldPredicate<T,Integer> whereWeek_FiscalWeek();
        public FieldPredicate<T,String> whereWeek_Name();
        public FieldPredicate<T,DateTime> whereWeek_StartDate();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link Calendar}
         */
        public static Calendar newInstance() {
            return MessageLineProxy.newInstance(Calendar.class);
        }
        
        /**
         * Clone a instance of {@link Calendar}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static Calendar clone(Calendar dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(Calendar.class, dataToClone);
        }
        /**
         * Use this method to create a field based predicate on {@link Calendar}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;Calendar&gt; pred = Calendar
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
        public static CalendarFieldPredicateFactory<Calendar> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(Calendar.class, CalendarFieldPredicateFactory.class);
        }
    }
 }