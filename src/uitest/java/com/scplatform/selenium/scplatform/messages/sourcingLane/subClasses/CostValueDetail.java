/**
 * @CostValueDetail.java@
 *
 * Created on Wed Oct 22 07:10:20 PDT 2014
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
 
import javax.annotation.processing.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.scplatform.qa.iris.model.FieldDefinition;
import com.scplatform.qa.iris.model.MessageLine;
import com.scplatform.qa.iris.model.annotations.FlexCDMData;
import com.scplatform.qa.iris.model.annotations.Metadata;
import com.scplatform.qa.iris.model.exceptions.FieldNotFoundException;
import com.scplatform.qa.iris.model.exceptions.InvalidValueException;
import com.scplatform.qa.iris.model.proxy.MessageLineProxy;
import com.scplatform.qa.iris.predicates.FieldPredicate;
import com.scplatform.qa.iris.predicates.FieldPredicateFactory;
import com.scplatform.qa.iris.predicates.proxy.MessageLinePredicateBuilderProxy;
 
/**
 * Cost Record Detail is a Sub Structure<br>
 * The Cost Value Detail is used to provide detailed costing information about a Cost Record, 
 * an example of which might be a freight or transportation Cost Record. The detail might include 
 * air, land, and sea transportation cost details. The Cost Value Detail is a child of the Cost Record<br>
 * 
 * <b>SPEC:</b> http://confluence.dev.scplatform.local/display/PUBT/Sourcing+Lane
 */
@Generated("IrisCodeGenerator")
public interface CostValueDetail extends MessageLine {

    /**
     * The blend or percentage of the Cost Recordï¿½s cost value
     */
    @NotNull
    @Size(max=126)
    @Metadata(index=2, displayName="CostValueBlend")
    @FlexCDMData({"*","*.down"})
    public float getCostValueBlend();
    /**
     * The blend or percentage of the Cost Recordï¿½s cost value
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=2, displayName="CostValueBlend")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostValueBlend(float value);


    /**
     * The name of the cost value detail
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=0, displayName="CostValueName")
    @FlexCDMData({"*","*.down"})
    public String getCostValueName();
    /**
     * The name of the cost value detail
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="CostValueName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostValueName(String value);


    /**
     * The name of the cost value detail
     */
    @NotNull
    @Size(max=126)
    @Metadata(index=1, displayName="CostValueValue")
    @FlexCDMData({"*","*.down"})
    public float getCostValueValue();
    /**
     * The name of the cost value detail
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=1, displayName="CostValueValue")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostValueValue(float value);

    
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
         * The blend or percentage of the Cost Recordï¿½s cost value
         */
        public static final FieldDefinition COSTVALUEBLEND = new FieldDefinition("CostValueBlend");
        /**
         * The name of the cost value detail
         */
        public static final FieldDefinition COSTVALUENAME = new FieldDefinition("CostValueName");
        /**
         * The name of the cost value detail
         */
        public static final FieldDefinition COSTVALUEVALUE = new FieldDefinition("CostValueValue");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link CostValueDetail.PredicateBuilder} 
     */
    public interface CostValueDetailFieldPredicateFactory<T extends CostValueDetail>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,Float> whereCostValueBlend();
        public FieldPredicate<T,String> whereCostValueName();
        public FieldPredicate<T,Float> whereCostValueValue();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link CostValueDetail}
         */
        public static CostValueDetail newInstance() {
            return MessageLineProxy.newInstance(CostValueDetail.class);
        }
        
        /**
         * Clone a instance of {@link CostValueDetail}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static CostValueDetail clone(CostValueDetail dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(CostValueDetail.class, dataToClone);
        }
		
        /**
         * Use this method to create a field based predicate on {@link CostValueDetail}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;CostValueDetail&gt; pred = CostValueDetail
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
        public static CostValueDetailFieldPredicateFactory<CostValueDetail> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(CostValueDetail.class, CostValueDetailFieldPredicateFactory.class);
        }
    }
 }