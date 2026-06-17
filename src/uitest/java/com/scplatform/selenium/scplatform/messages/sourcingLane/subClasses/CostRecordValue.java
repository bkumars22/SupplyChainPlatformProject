/**
 * @CostRecordValue.java@
 *
 * Created on Wed Oct 22 07:10:06 PDT 2014
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
 * Cost Record Value is a Sub Structure<br>
 * A cost record value is a child of both the cost record and cost record range. 
 * he cost record value contains the cost value for each element of a cost record.<br>
 * 
 * <b>SPEC:</b> http://confluence.dev.scplatform.local/display/PUBT/Sourcing+Lane
 */
@Generated("IrisCodeGenerator")
public interface CostRecordValue extends MessageLine {

    /**
     * Type of cost element depends on the cost type
     */
    @NotNull
    @Size(max=64)
    @RestrictToStrings({"MATERIAL","MVA","PROFITMARGIN","SGA","VAT","TAXES","TRANSPORTATION","INFREIGHT","OUTFREIGHT","ROYALTY","AMORTIZATION","OPEXNRE","NPITOOLING","CAPEX"})
    @Metadata(index=0, displayName="CostElementType")
    @FlexCDMData({"*","*.down"})
    public String getCostElementType();
    /**
     * Type of cost element depends on the cost type
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"MATERIAL","MVA","PROFITMARGIN","SGA","VAT","TAXES","TRANSPORTATION","INFREIGHT","OUTFREIGHT","ROYALTY","AMORTIZATION","OPEXNRE","NPITOOLING","CAPEX"})</li>
     * <li>@Metadata(index=0, displayName="CostElementType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostElementType(String value);


    /**
     * Unit of measure for this cost record. The default is each
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=2, displayName="CostUnitofMeasureCode")
    @FlexCDMData({"*","*.down"})
    public String getCostUnitofMeasureCode();
    /**
     * Unit of measure for this cost record. The default is each
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=2, displayName="CostUnitofMeasureCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostUnitofMeasureCode(String value);


    /**
     * Cost Value. Can be a simple value, a blended value, linked value, or a tiered value.
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=1, displayName="CostValue")
    @FlexCDMData({"*","*.down"})
    public String getCostValue();
    /**
     * Cost Value. Can be a simple value, a blended value, linked value, or a tiered value.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=1, displayName="CostValue")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostValue(String value);


    /**
     * Cost Record Detail is a Sub Structure.<br>
     * The Cost Value Detail is used to provide detailed costing information about a Cost Record, 
     * an example of which might be a freight or transportation Cost Record. The detail might include 
     * air, land, and sea transportation cost details. The Cost Value Detail is a child of the Cost Record<br>
     */
    @Metadata(index=3, displayName="CostValueDetail")
    @FlexCDMData({"*","*.down"})
    public List<CostValueDetail> getCostValueDetail();
    /**
     * Cost Record Detail is a Sub Structure.<br>
     * The Cost Value Detail is used to provide detailed costing information about a Cost Record, 
     * an example of which might be a freight or transportation Cost Record. The detail might include 
     * air, land, and sea transportation cost details. The Cost Value Detail is a child of the Cost Record<br>
     * Annotations:
     * <ul>
     * <li>@Metadata(index=3, displayName="CostValueDetail")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostValueDetail(List<CostValueDetail> value);

    
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
         * Type of cost element depends on the cost type
         */
        public static final FieldDefinition COSTELEMENTTYPE = new FieldDefinition("CostElementType");
        /**
         * Unit of measure for this cost record. The default is each
         */
        public static final FieldDefinition COSTUNITOFMEASURECODE = new FieldDefinition("CostUnitofMeasureCode");
        /**
         * Cost Value. Can be a simple value, a blended value, linked value, or a tiered value.
         */
        public static final FieldDefinition COSTVALUE = new FieldDefinition("CostValue");
        /**
         * Cost Record Detail is a Sub Structure.<br>
         * The Cost Value Detail is used to provide detailed costing information about a Cost Record, 
         * an example of which might be a freight or transportation Cost Record. The detail might include 
         * air, land, and sea transportation cost details. The Cost Value Detail is a child of the Cost Record<br>
         */
        public static final FieldDefinition COSTVALUEDETAIL = new FieldDefinition("CostValueDetail");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link CostRecordValue.PredicateBuilder} 
     */
    public interface CostRecordValueFieldPredicateFactory<T extends CostRecordValue>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereCostElementType();
        public FieldPredicate<T,String> whereCostUnitofMeasureCode();
        public FieldPredicate<T,String> whereCostValue();
        public FieldPredicate<T,String> whereCostValueDetail();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link CostRecordValue}
         */
        public static CostRecordValue newInstance() {
            return MessageLineProxy.newInstance(CostRecordValue.class);
        }
        
        /**
         * Clone a instance of {@link CostRecordValue}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static CostRecordValue clone(CostRecordValue dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(CostRecordValue.class, dataToClone);
        }
		
        /**
         * Use this method to create a field based predicate on {@link CostRecordValue}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;CostRecordValue&gt; pred = CostRecordValue
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
        public static CostRecordValueFieldPredicateFactory<CostRecordValue> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(CostRecordValue.class, CostRecordValueFieldPredicateFactory.class);
        }
    }
 }