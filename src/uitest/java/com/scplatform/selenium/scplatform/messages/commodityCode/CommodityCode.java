/**
 * @CommodityCode.java@
 *
 * Created on Thu Oct 16 14:13:17 PDT 2014
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
 
package com.test.selenium.scplatform.messages.commodityCode;
 
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
 
@Generated("IrisCodeGenerator")
public interface CommodityCode extends MessageLine {

    /**
     * The commodity code, must be unique
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=0, displayName="CommodityCode")
    @FlexCDMData({"*","*.down"})
    public String getCommodityCode();
    /**
     * The commodity code, must be unique
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="CommodityCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCommodityCode(String value);


    /**
     * The commodity code name. This value can be change and is displayed on the UI.
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=1, displayName="CommodityCodeName")
    @FlexCDMData({"*","*.down"})
    public String getCommodityCodeName();
    /**
     * The commodity code name. This value can be change and is displayed on the UI.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=1, displayName="CommodityCodeName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCommodityCodeName(String value);


    @NotNull
    @Size(max=255)
    @Metadata(index=2, displayName="CommodityUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getCommodityUniqueId();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=2, displayName="CommodityUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCommodityUniqueId(String value);


    @Size(max=1024)
    @Metadata(index=3, displayName="Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=1024)</li>
     * <li>@Metadata(index=3, displayName="Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDescription(String value);


    /**
     * The group responsible for managing the commodity
     */
    @Size(max=255)
    @Metadata(index=6, displayName="ManagedBy")
    @FlexCDMData({"*","*.down"})
    public String getManagedBy();
    /**
     * The group responsible for managing the commodity
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=6, displayName="ManagedBy")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setManagedBy(String value);


    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
     * A=Add
     * C=Changed
     * D=Delete
     * U=Unchanged
     */
    @Size(max=64)
    @RestrictToStrings({"A","C","D","U"})
    @Metadata(index=7, displayName="OperationCode")
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
     * <li>@Metadata(index=7, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    /**
     * The parent commodity code if nested commodity. Note that using nested commodities requires that parentCommodityCode is unique.
     */
    @Size(max=255)
    @Metadata(index=4, displayName="ParentCommodityCode")
    @FlexCDMData({"*","*.down"})
    public String getParentCommodityCode();
    /**
     * The parent commodity code if nested commodity. Note that using nested commodities requires that parentCommodityCode is unique.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=4, displayName="ParentCommodityCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setParentCommodityCode(String value);


    /**
     * This is matched to the commodityUniqueId of the parent
     */
    @Size(max=255)
    @Metadata(index=5, displayName="ParentCommodityUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getParentCommodityUniqueId();
    /**
     * This is matched to the commodityUniqueId of the parent
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=5, displayName="ParentCommodityUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setParentCommodityUniqueId(String value);

    
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
         * The commodity code, must be unique
         */
        public static final FieldDefinition COMMODITYCODE = new FieldDefinition("CommodityCode");
        /**
         * The commodity code name. This value can be change and is displayed on the UI.
         */
        public static final FieldDefinition COMMODITYCODENAME = new FieldDefinition("CommodityCodeName");
        public static final FieldDefinition COMMODITYUNIQUEID = new FieldDefinition("CommodityUniqueId");
        public static final FieldDefinition DESCRIPTION = new FieldDefinition("Description");
        /**
         * The group responsible for managing the commodity
         */
        public static final FieldDefinition MANAGEDBY = new FieldDefinition("ManagedBy");
        /**
         * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
         * A=Add
         * C=Changed
         * D=Delete
         * U=Unchanged
         */
        public static final FieldDefinition OPERATIONCODE = new FieldDefinition("OperationCode");
        /**
         * The parent commodity code if nested commodity. Note that using nested commodities requires that parentCommodityCode is unique.
         */
        public static final FieldDefinition PARENTCOMMODITYCODE = new FieldDefinition("ParentCommodityCode");
        /**
         * This is matched to the commodityUniqueId of the parent
         */
        public static final FieldDefinition PARENTCOMMODITYUNIQUEID = new FieldDefinition("ParentCommodityUniqueId");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link CommodityCode.PredicateBuilder} 
     */
    public interface CommodityCodeFieldPredicateFactory<T extends CommodityCode>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereCommodityCode();
        public FieldPredicate<T,String> whereCommodityCodeName();
        public FieldPredicate<T,String> whereCommodityUniqueId();
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,String> whereManagedBy();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> whereParentCommodityCode();
        public FieldPredicate<T,String> whereParentCommodityUniqueId();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link CommodityCode}
         */
        public static CommodityCode newInstance() {
            return MessageLineProxy.newInstance(CommodityCode.class);
        }
        
        /**
         * Clone a instance of {@link CommodityCode}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static CommodityCode clone(CommodityCode dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(CommodityCode.class, dataToClone);
        }
        
        /**
         * Use this method to create a field based predicate on {@link CommodityCode}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;CommodityCode&gt; pred = CommodityCode
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
        public static CommodityCodeFieldPredicateFactory<CommodityCode> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(CommodityCode.class, CommodityCodeFieldPredicateFactory.class);
        }
    }
 }