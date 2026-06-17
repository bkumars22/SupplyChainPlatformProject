/**
 * @CostRecordRange.java@
 *
 * Created on Wed Oct 22 07:09:45 PDT 2014
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
 * Cost Record Range is a Sub Structure<br>
 * The cost record range contains tiers of ranges that contain cost values<br>
 * 
 * <b>SPEC:</b> http://confluence.dev.scplatform.local/display/PUBT/Sourcing+Lane
 */
@Generated("IrisCodeGenerator")
public interface CostRecordRange extends MessageLine {

    /**
     * Cost Record Value is a Sub Structure<br>
     * A cost record value is a child of both the cost record and cost record range.
     * The cost record value contains the cost value for each element of a cost record.<br>
     */
    @Metadata(index=3, displayName="CostRecordValue")
    @FlexCDMData({"*","*.down"})
    public List<CostRecordValue> getCostRecordValue();
    /**
     * Cost Record Value is a Sub Structure<br>
     * A cost record value is a child of both the cost record and cost record range.
     * The cost record value contains the cost value for each element of a cost record.<br>
     * Annotations:
     * <ul>
     * <li>@Metadata(index=3, displayName="CostRecordValue")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostRecordValue(List<CostRecordValue> value);


    /**
     * The starting value for the tier.
     */
    @NotNull
    @Size(max=126)
    @Metadata(index=0, displayName="FromRange")
    @FlexCDMData({"*","*.down"})
    public float getFromRange();
    /**
     * The starting value for the tier.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=0, displayName="FromRange")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setFromRange(float value);


    /**
     * Identifies whether the tier is active
     */
    @Size(max=64)
    @RestrictToStrings({"true","false"})
    @Metadata(index=2, displayName="IsActive")
    @FlexCDMData({"*","*.down"})
    public String getIsActive();
    /**
     * Identifies whether the tier is active
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"true","false"})</li>
     * <li>@Metadata(index=2, displayName="IsActive")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setIsActive(String value);


    /**
     * The ending value for the tier
     */
    @Size(max=126)
    @Metadata(index=1, displayName="ToRange")
    @FlexCDMData({"*","*.down"})
    public float getToRange();
    /**
     * The ending value for the tier
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=1, displayName="ToRange")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setToRange(float value);

    
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
         * Cost Record Value is a Sub Structure<br>
         * A cost record value is a child of both the cost record and cost record range.
         * The cost record value contains the cost value for each element of a cost record.<br>
         */
        public static final FieldDefinition COSTRECORDVALUE = new FieldDefinition("CostRecordValue");
        /**
         * The starting value for the tier.
         */
        public static final FieldDefinition FROMRANGE = new FieldDefinition("FromRange");
        /**
         * Identifies whether the tier is active
         */
        public static final FieldDefinition ISACTIVE = new FieldDefinition("IsActive");
        /**
         * The ending value for the tier
         */
        public static final FieldDefinition TORANGE = new FieldDefinition("ToRange");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link CostRecordRange.PredicateBuilder} 
     */
    public interface CostRecordRangeFieldPredicateFactory<T extends CostRecordRange>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereCostRecordValue();
        public FieldPredicate<T,Float> whereFromRange();
        public FieldPredicate<T,String> whereIsActive();
        public FieldPredicate<T,Float> whereToRange();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link CostRecordRange}
         */
        public static CostRecordRange newInstance() {
            return MessageLineProxy.newInstance(CostRecordRange.class);
        }
        
        /**
         * Clone a instance of {@link CostRecordRange}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static CostRecordRange clone(CostRecordRange dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(CostRecordRange.class, dataToClone);
        }
		
        /**
         * Use this method to create a field based predicate on {@link CostRecordRange}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;CostRecordRange&gt; pred = CostRecordRange
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
        public static CostRecordRangeFieldPredicateFactory<CostRecordRange> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(CostRecordRange.class, CostRecordRangeFieldPredicateFactory.class);
        }
    }
 }