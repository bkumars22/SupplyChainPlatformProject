/**
 * @ItemPlatform.java@
 *
 * Created on Thu Oct 16 12:45:02 PDT 2014
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
 
package com.test.selenium.scplatform.messages.itemPlatform;
 
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
public interface ItemPlatform extends MessageLine {

    /**
     * This models what business entity this part is for.  In a typical system there will be customer, supplier, and manufacturer items and each is mapped to a specific business.
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=1, displayName="Item Business Entity")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntity();
    /**
     * This models what business entity this part is for.  In a typical system there will be customer, supplier, and manufacturer items and each is mapped to a specific business.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=1, displayName="Item Business Entity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntity(String value);


    /**
     * Reference to the type of business.  Standard types are Supplier, Manufacturer, and Enterprise.  Other types can be modeled as needed.
     */
    @NotNull
    @Size(max=255)
    @RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})
    @Metadata(index=2, displayName="Item Business Entity Type")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntityType();
    /**
     * Reference to the type of business.  Standard types are Supplier, Manufacturer, and Enterprise.  Other types can be modeled as needed.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})</li>
     * <li>@Metadata(index=2, displayName="Item Business Entity Type")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntityType(String value);


    /**
     * Optional Description of the platform.
     */
    @Size(max=1024)
    @Metadata(index=4, displayName="Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * Optional Description of the platform.
     * Annotations:
     * <ul>
     * <li>@Size(max=1024)</li>
     * <li>@Metadata(index=4, displayName="Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDescription(String value);


    /**
     * Optional From Date.
     */
    @Metadata(index=5, displayName="Start Date")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveFromDate();
    /**
     * Optional From Date.
     * Annotations:
     * <ul>
     * <li>@Metadata(index=5, displayName="Start Date")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveFromDate(DateTime value);


    /**
     * Optional To Date.
     */
    @Metadata(index=6, displayName="End Date")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveToDate();
    /**
     * Optional To Date.
     * Annotations:
     * <ul>
     * <li>@Metadata(index=6, displayName="End Date")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveToDate(DateTime value);


    /**
     * The part number.
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=0, displayName="Item")
    @FlexCDMData({"*","*.down"})
    public String getItemIdentifier();
    /**
     * The part number.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="Item")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemIdentifier(String value);


    /**
     * Platform that this item is qualified for
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=3, displayName="Platform Name")
    @FlexCDMData({"*","*.down"})
    public String getPlatformName();
    /**
     * Platform that this item is qualified for
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=3, displayName="Platform Name")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPlatformName(String value);

    
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
         * This models what business entity this part is for.  In a typical system there will be customer, supplier, and manufacturer items and each is mapped to a specific business.
         */
        public static final FieldDefinition BUSINESSENTITY = new FieldDefinition("BusinessEntity");
        /**
         * Reference to the type of business.  Standard types are Supplier, Manufacturer, and Enterprise.  Other types can be modeled as needed.
         */
        public static final FieldDefinition BUSINESSENTITYTYPE = new FieldDefinition("BusinessEntityType");
        /**
         * Optional Description of the platform.
         */
        public static final FieldDefinition DESCRIPTION = new FieldDefinition("Description");
        /**
         * Optional From Date.
         */
        public static final FieldDefinition EFFECTIVEFROMDATE = new FieldDefinition("EffectiveFromDate");
        /**
         * Optional To Date.
         */
        public static final FieldDefinition EFFECTIVETODATE = new FieldDefinition("EffectiveToDate");
        /**
         * The part number.
         */
        public static final FieldDefinition ITEMIDENTIFIER = new FieldDefinition("ItemIdentifier");
        /**
         * Platform that this item is qualified for
         */
        public static final FieldDefinition PLATFORMNAME = new FieldDefinition("PlatformName");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link ItemPlatform.PredicateBuilder} 
     */
    public interface ItemPlatformFieldPredicateFactory<T extends ItemPlatform>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereBusinessEntity();
        public FieldPredicate<T,String> whereBusinessEntityType();
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,DateTime> whereEffectiveFromDate();
        public FieldPredicate<T,DateTime> whereEffectiveToDate();
        public FieldPredicate<T,String> whereItemIdentifier();
        public FieldPredicate<T,String> wherePlatformName();
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