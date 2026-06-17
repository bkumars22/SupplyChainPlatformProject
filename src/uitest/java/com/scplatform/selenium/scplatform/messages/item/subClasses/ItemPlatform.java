/**
 * @ItemPlatform.java@
 *
 * Created on Tue Oct 21 09:10:22 PDT 2014
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
 
package com.test.selenium.scplatform.messages.item.subClasses;
 
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
 * <b>SPEC:</b> http://confluence.dev.scplatform.local/display/PUBT/Item
 * <br>
 * Item platform is used to designate what platform this item has been 
 * assigned to and can be assigned to more than one platform.
 */
@Generated("IrisCodeGenerator")
public interface ItemPlatform extends MessageLine {

    /**
     * description
     */
    @Size(max=1024)
    @Metadata(index=1, displayName="Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * description
     * Annotations:
     * <ul>
     * <li>@Size(max=1024)</li>
     * <li>@Metadata(index=1, displayName="Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDescription(String value);


    /**
     * Effective From Date
     */
    @Metadata(index=3, displayName="EffectiveFromDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveFromDate();
    /**
     * Effective From Date
     * Annotations:
     * <ul>
     * <li>@Metadata(index=3, displayName="EffectiveFromDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveFromDate(DateTime value);


    /**
     * Effective To Date
     */
    @Metadata(index=4, displayName="EffectiveToDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveToDate();
    /**
     * Effective To Date
     * Annotations:
     * <ul>
     * <li>@Metadata(index=4, displayName="EffectiveToDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveToDate(DateTime value);


    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
     * A=Add
     * C=Changed
     * D=Delete
     * U=Unchanged
     */
    @Size(max=64)
    @RestrictToStrings({"A","C","D","U"})
    @Metadata(index=5, displayName="OperationCode")
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
     * <li>@Metadata(index=5, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    /**
     * Platform that this item is qualified for
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=0, displayName="PlatformName")
    @FlexCDMData({"*","*.down"})
    public String getPlatformName();
    /**
     * Platform that this item is qualified for
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="PlatformName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPlatformName(String value);


    /**
     * type
     */
    @Size(max=32)
    @Metadata(index=2, displayName="PlatformType")
    @FlexCDMData({"*","*.down"})
    public String getPlatformType();
    /**
     * type
     * Annotations:
     * <ul>
     * <li>@Size(max=32)</li>
     * <li>@Metadata(index=2, displayName="PlatformType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPlatformType(String value);

    
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
         * description
         */
        public static final FieldDefinition DESCRIPTION = new FieldDefinition("Description");
        /**
         * Effective From Date
         */
        public static final FieldDefinition EFFECTIVEFROMDATE = new FieldDefinition("EffectiveFromDate");
        /**
         * Effective To Date
         */
        public static final FieldDefinition EFFECTIVETODATE = new FieldDefinition("EffectiveToDate");
        /**
         * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
         * A=Add
         * C=Changed
         * D=Delete
         * U=Unchanged
         */
        public static final FieldDefinition OPERATIONCODE = new FieldDefinition("OperationCode");
        /**
         * Platform that this item is qualified for
         */
        public static final FieldDefinition PLATFORMNAME = new FieldDefinition("PlatformName");
        /**
         * type
         */
        public static final FieldDefinition PLATFORMTYPE = new FieldDefinition("PlatformType");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link ItemPlatform.PredicateBuilder} 
     */
    public interface ItemPlatformFieldPredicateFactory<T extends ItemPlatform>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,DateTime> whereEffectiveFromDate();
        public FieldPredicate<T,DateTime> whereEffectiveToDate();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> wherePlatformName();
        public FieldPredicate<T,String> wherePlatformType();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link ItemPlatform}
         */
        public static ItemPlatform newInstance() {
            return MessageLineProxy.newInstance(ItemPlatform.class);
        }
        
        /**
         * Clone a instance of {@link ItemPlatform}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static ItemPlatform clone(ItemPlatform dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(ItemPlatform.class, dataToClone);
        }
		
        /**
         * Use this method to create a field based predicate on {@link ItemPlatform}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;ItemPlatform&gt; pred = ItemPlatform
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
        public static ItemPlatformFieldPredicateFactory<ItemPlatform> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(ItemPlatform.class, ItemPlatformFieldPredicateFactory.class);
        }
    }
 }