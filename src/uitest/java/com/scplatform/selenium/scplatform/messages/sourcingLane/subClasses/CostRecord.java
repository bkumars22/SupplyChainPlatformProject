/**
 * @CostRecord.java@
 *
 * Created on Wed Oct 22 07:09:25 PDT 2014
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
 
package com.test.selenium.scplatform.messages.sourcingLane.subClasses;
 
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

/**
 * Cost Record is a Sub Structure<br>
 * The following rules apply to outbound notification of cost changes:
 * <UL>
 * <LI> Whenever an approved cost record changes
 * <LI> Whenever an approved cost record is closed---this message signals that the pricing 
 *      should be removed or marked inactive in the receiving system
 * </UL>
 * <br>
 * <b>SPEC:</b> http://confluence.dev.scplatform.local/display/PUBT/Sourcing+Lane
 */
@Generated("IrisCodeGenerator")
public interface CostRecord extends MessageLine {

    @Size(max=255)
    @Metadata(index=2, displayName="Comment")
    @FlexCDMData({"*","*.down"})
    public String getComment();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=2, displayName="Comment")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setComment(String value);


    /**
     * The business entity providing the cost record
     */
    @Size(max=255)
    @Metadata(index=4, displayName="CostProviderBusinessEntity")
    @FlexCDMData({"*","*.down"})
    public String getCostProviderBusinessEntity();
    /**
     * The business entity providing the cost record
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=4, displayName="CostProviderBusinessEntity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostProviderBusinessEntity(String value);


    /**
     * The providers type of business
     */
    @Size(max=64)
    @RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})
    @Metadata(index=5, displayName="CostProviderBusinessEntityType")
    @FlexCDMData({"*","*.down"})
    public String getCostProviderBusinessEntityType();
    /**
     * The providers type of business
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})</li>
     * <li>@Metadata(index=5, displayName="CostProviderBusinessEntityType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostProviderBusinessEntityType(String value);


    /**
     * external ID
     */
    @Size(max=255)
    @Metadata(index=0, displayName="CostRecordExternalId")
    @FlexCDMData({"*","*.down"})
    public String getCostRecordExternalId();
    /**
     * external ID
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="CostRecordExternalId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostRecordExternalId(String value);


    /**
     * Cost Record Range is a Sub Structure<br>
     * The cost record range contains tiers of ranges that contain cost values<br>
     */
    @Metadata(index=14, displayName="CostRecordRange")
    @FlexCDMData({"*","*.down"})
    public List<CostRecordRange> getCostRecordRange();
    /**
     * Cost Record Range is a Sub Structure<br>
     * The cost record range contains tiers of ranges that contain cost values<br>
     * Annotations:
     * <ul>
     * <li>@Metadata(index=14, displayName="CostRecordRange")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostRecordRange(List<CostRecordRange> value);


    /**
     * Cost Record Value is a Sub Structure<br>
     * A cost record value is a child of both the cost record and cost record range.
     * The cost record value contains the cost value for each element of a cost record.<br>
     */
    @Metadata(index=15, displayName="CostRecordValue")
    @FlexCDMData({"*","*.down"})
    public List<CostRecordValue> getCostRecordValue();
    /**
     * Cost Record Value is a Sub Structure<br>
     * A cost record value is a child of both the cost record and cost record range.
     * The cost record value contains the cost value for each element of a cost record.<br>
     * Annotations:
     * <ul>
     * <li>@Metadata(index=15, displayName="CostRecordValue")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostRecordValue(List<CostRecordValue> value);


    /**
     * Type of cost record
     */
    @NotNull
    @Size(max=64)
    @RestrictToStrings({"BUY","MARKET","LIST","ODMBUY","SERVICE","EMQUOTE","SELL"})
    @Metadata(index=3, displayName="CostType")
    @FlexCDMData({"*","*.down"})
    public String getCostType();
    /**
     * Type of cost record
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"BUY","MARKET","LIST","ODMBUY","SERVICE","EMQUOTE","SELL"})</li>
     * <li>@Metadata(index=3, displayName="CostType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostType(String value);


    /**
     * cost record description
     */
    @Size(max=1024)
    @Metadata(index=1, displayName="Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * cost record description
     * Annotations:
     * <ul>
     * <li>@Size(max=1024)</li>
     * <li>@Metadata(index=1, displayName="Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDescription(String value);


    /**
     * From Date
     */
    @Metadata(index=6, displayName="EffectiveFromDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveFromDate();
    /**
     * From Date
     * Annotations:
     * <ul>
     * <li>@Metadata(index=6, displayName="EffectiveFromDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveFromDate(DateTime value);


    /**
     * To Date
     */
    @Metadata(index=7, displayName="EffectiveToDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveToDate();
    /**
     * To Date
     * Annotations:
     * <ul>
     * <li>@Metadata(index=7, displayName="EffectiveToDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveToDate(DateTime value);


    /**
     * Userid that performed the last state change of this record or null if no state
     */
    @Size(max=255)
    @Metadata(index=9, displayName="LastStateChangeBy")
    @FlexCDMData({"*","*.down"})
    public String getLastStateChangeBy();
    /**
     * Userid that performed the last state change of this record or null if no state
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=9, displayName="LastStateChangeBy")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLastStateChangeBy(String value);


    /**
     * Date of last state change
     */
    @Metadata(index=10, displayName="LastStateChangeOn")
    @FlexCDMData({"*","*.down"})
    public DateTime getLastStateChangeOn();
    /**
     * Date of last state change
     * Annotations:
     * <ul>
     * <li>@Metadata(index=10, displayName="LastStateChangeOn")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLastStateChangeOn(DateTime value);


    /**
     * Date of last state change
     */
    @NotNull
    @Metadata(index=11, displayName="LastUpdateDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getLastUpdateDate();
    /**
     * Date of last state change
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=11, displayName="LastUpdateDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLastUpdateDate(DateTime value);


    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.<br>
     * A=Add<br>
     * C=Changed<br>
     * D=Delete<br>
     * U=Unchanged<br>
     */
    @Size(max=64)
    @RestrictToStrings({"A","C","D","U"})
    @Metadata(index=12, displayName="OperationCode")
    @FlexCDMData({"*","*.down"})
    public String getOperationCode();
    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.<br>
     * A=Add<br>
     * C=Changed<br>
     * D=Delete<br>
     * U=Unchanged<br>
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"A","C","D","U"})</li>
     * <li>@Metadata(index=12, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    /**
     * The scenario used for costing.
     */
    @Size(max=64)
    @RestrictToStrings({"Consumption Based","Volume Based","Evergreen"})
    @Metadata(index=13, displayName="PricingScenario")
    @FlexCDMData({"*","*.down"})
    public String getPricingScenario();
    /**
     * The scenario used for costing.
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"Consumption Based","Volume Based","Evergreen"})</li>
     * <li>@Metadata(index=13, displayName="PricingScenario")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPricingScenario(String value);


    @Size(max=255)
    @Metadata(index=16, displayName="ReasonCode")
    @FlexCDMData({"*","*.down"})
    public String getReasonCode();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=16, displayName="ReasonCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setReasonCode(String value);


    /**
     * Current state of the record
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=8, displayName="State")
    @FlexCDMData({"*","*.down"})
    public String getState();
    /**
     * Current state of the record
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=8, displayName="State")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setState(String value);

    
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
        public static final FieldDefinition COMMENT = new FieldDefinition("Comment");
        /**
         * The business entity providing the cost record
         */
        public static final FieldDefinition COSTPROVIDERBUSINESSENTITY = new FieldDefinition("CostProviderBusinessEntity");
        /**
         * The providers type of business
         */
        public static final FieldDefinition COSTPROVIDERBUSINESSENTITYTYPE = new FieldDefinition("CostProviderBusinessEntityType");
        /**
         * external ID
         */
        public static final FieldDefinition COSTRECORDEXTERNALID = new FieldDefinition("CostRecordExternalId");
        /**
         * Cost Record Range is a Sub Structure<br>
         * The cost record range contains tiers of ranges that contain cost values<br>
         */
        public static final FieldDefinition COSTRECORDRANGE = new FieldDefinition("CostRecordRange");
        /**
         * Cost Record Value is a Sub Structure<br>
         * A cost record value is a child of both the cost record and cost record range.
         * The cost record value contains the cost value for each element of a cost record.<br>
         */
        public static final FieldDefinition COSTRECORDVALUE = new FieldDefinition("CostRecordValue");
        /**
         * Type of cost record
         */
        public static final FieldDefinition COSTTYPE = new FieldDefinition("CostType");
        /**
         * cost record description
         */
        public static final FieldDefinition DESCRIPTION = new FieldDefinition("Description");
        /**
         * From Date
         */
        public static final FieldDefinition EFFECTIVEFROMDATE = new FieldDefinition("EffectiveFromDate");
        /**
         * To Date
         */
        public static final FieldDefinition EFFECTIVETODATE = new FieldDefinition("EffectiveToDate");
        /**
         * Userid that performed the last state change of this record or null if no state
         */
        public static final FieldDefinition LASTSTATECHANGEBY = new FieldDefinition("LastStateChangeBy");
        /**
         * Date of last state change
         */
        public static final FieldDefinition LASTSTATECHANGEON = new FieldDefinition("LastStateChangeOn");
        /**
         * Date of last state change
         */
        public static final FieldDefinition LASTUPDATEDATE = new FieldDefinition("LastUpdateDate");
        /**
         * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.<br>
         * A=Add<br>
         * C=Changed<br>
         * D=Delete<br>
         * U=Unchanged<br>
         */
        public static final FieldDefinition OPERATIONCODE = new FieldDefinition("OperationCode");
        /**
         * The scenario used for costing.
         */
        public static final FieldDefinition PRICINGSCENARIO = new FieldDefinition("PricingScenario");
        public static final FieldDefinition REASONCODE = new FieldDefinition("ReasonCode");
        /**
         * Current state of the record
         */
        public static final FieldDefinition STATE = new FieldDefinition("State");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link CostRecord.PredicateBuilder} 
     */
    public interface CostRecordFieldPredicateFactory<T extends CostRecord>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereComment();
        public FieldPredicate<T,String> whereCostProviderBusinessEntity();
        public FieldPredicate<T,String> whereCostProviderBusinessEntityType();
        public FieldPredicate<T,String> whereCostRecordExternalId();
        public FieldPredicate<T,String> whereCostRecordRange();
        public FieldPredicate<T,String> whereCostRecordValue();
        public FieldPredicate<T,String> whereCostType();
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,DateTime> whereEffectiveFromDate();
        public FieldPredicate<T,DateTime> whereEffectiveToDate();
        public FieldPredicate<T,String> whereLastStateChangeBy();
        public FieldPredicate<T,DateTime> whereLastStateChangeOn();
        public FieldPredicate<T,DateTime> whereLastUpdateDate();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> wherePricingScenario();
        public FieldPredicate<T,String> whereReasonCode();
        public FieldPredicate<T,String> whereState();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link CostRecord}
         */
        public static CostRecord newInstance() {
            return MessageLineProxy.newInstance(CostRecord.class);
        }
        
        /**
         * Clone a instance of {@link CostRecord}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static CostRecord clone(CostRecord dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(CostRecord.class, dataToClone);
        }
		
        
        /**
         * Use this method to create a field based predicate on {@link CostRecord}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;CostRecord&gt; pred = CostRecord
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
        public static CostRecordFieldPredicateFactory<CostRecord> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(CostRecord.class, CostRecordFieldPredicateFactory.class);
        }
    }
 }