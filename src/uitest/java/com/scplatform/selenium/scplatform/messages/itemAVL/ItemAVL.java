/**
 * @ItemAVL.java@
 *
 * Created on Thu Oct 16 11:55:57 PDT 2014
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
 
package com.test.selenium.scplatform.messages.itemAVL;
 
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
public interface ItemAVL extends MessageLine {

    /**
     * This models what business entity this item is for.  In a typical system there will be Supplier, Manufacturer, and Customer items and each is mapped to a specific business.
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=1, displayName="Item Business Entity")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntity();
    /**
     * This models what business entity this item is for.  In a typical system there will be Supplier, Manufacturer, and Customer items and each is mapped to a specific business.
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
     * Reference to the type of business.  Standard types are Supplier, Manufacturer, and Customer.  Other types can be modeled as needed.
     */
    @NotNull
    @Size(max=255)
    @RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})
    @Metadata(index=2, displayName="Item Business Entity Type")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntityType();
    /**
     * Reference to the type of business.  Standard types are Supplier, Manufacturer, and Customer.  Other types can be modeled as needed.
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
     * Commodity code for this item
     */
    @Size(max=255)
    @Metadata(index=11, displayName="Commodity Code")
    @FlexCDMData({"*","*.down"})
    public String getCommodityCode();
    /**
     * Commodity code for this item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=11, displayName="Commodity Code")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCommodityCode(String value);


    /**
     * Optional contact information
     */
    @Size(max=255)
    @Metadata(index=16, displayName="Contact Name")
    @FlexCDMData({"*","*.down"})
    public String getContactName();
    /**
     * Optional contact information
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=16, displayName="Contact Name")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setContactName(String value);


    /**
     * Optional Description of the item.
     */
    @Size(max=1024)
    @Metadata(index=7, displayName="Item Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * Optional Description of the item.
     * Annotations:
     * <ul>
     * <li>@Size(max=1024)</li>
     * <li>@Metadata(index=7, displayName="Item Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDescription(String value);


    /**
     * Optional From Date.
     */
    @Metadata(index=17, displayName="Start Date")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveFromDate();
    /**
     * Optional From Date.
     * Annotations:
     * <ul>
     * <li>@Metadata(index=17, displayName="Start Date")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveFromDate(DateTime value);


    /**
     * Optional To Date.
     */
    @Metadata(index=18, displayName="End Date")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveToDate();
    /**
     * Optional To Date.
     * Annotations:
     * <ul>
     * <li>@Metadata(index=18, displayName="End Date")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveToDate(DateTime value);


    /**
     * Optional classification name
     */
    @Size(max=255)
    @Metadata(index=10, displayName="Item Classification")
    @FlexCDMData({"*","*.down"})
    public String getItemClassification();
    /**
     * Optional classification name
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=10, displayName="Item Classification")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemClassification(String value);


    /**
     * Reference to the Item.
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=0, displayName="Item Number")
    @FlexCDMData({"*","*.down"})
    public String getItemIdentifier();
    /**
     * Reference to the Item.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="Item Number")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemIdentifier(String value);


    /**
     * Make or Buy decision
     */
    @Size(max=255)
    @RestrictToStrings({"Make","Buy"})
    @Metadata(index=14, displayName="MakeBuy")
    @FlexCDMData({"*","*.down"})
    public String getMakeBuy();
    /**
     * Make or Buy decision
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@RestrictToStrings({"Make","Buy"})</li>
     * <li>@Metadata(index=14, displayName="MakeBuy")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setMakeBuy(String value);


    /**
     * The group responsible for managing the item.
     */
    @Size(max=255)
    @Metadata(index=12, displayName="Managed By")
    @FlexCDMData({"*","*.down"})
    public String getManagedBy();
    /**
     * The group responsible for managing the item.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=12, displayName="Managed By")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setManagedBy(String value);


    /**
     * Optional owner identification
     */
    @Size(max=255)
    @Metadata(index=15, displayName="Owner Name")
    @FlexCDMData({"*","*.down"})
    public String getOwnerName();
    /**
     * Optional owner identification
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=15, displayName="Owner Name")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOwnerName(String value);


    /**
     * Unit of measure for item (gallons, inches, etc.)
     */
    @Size(max=255)
    @Metadata(index=13, displayName="Product UOM")
    @FlexCDMData({"*","*.down"})
    public String getProductUnitOfMeasureCode();
    /**
     * Unit of measure for item (gallons, inches, etc.)
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=13, displayName="Product UOM")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setProductUnitOfMeasureCode(String value);


    /**
     * The revision of the item
     */
    @Size(max=255)
    @Metadata(index=8, displayName="Revision")
    @FlexCDMData({"*","*.down"})
    public String getRevision();
    /**
     * The revision of the item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=8, displayName="Revision")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setRevision(String value);


    /**
     * This models what vendor business entity this vendor's item is for.  In a typical system there will be Supplier, Manufacturer, and Customer items and each is mapped to a specific business.
     */
    @Size(max=255)
    @Metadata(index=4, displayName="Supplier")
    @FlexCDMData({"*","*.down"})
    public String getVendorBusinessEntity();
    /**
     * This models what vendor business entity this vendor's item is for.  In a typical system there will be Supplier, Manufacturer, and Customer items and each is mapped to a specific business.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=4, displayName="Supplier")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVendorBusinessEntity(String value);


    /**
     * Reference to the type of  vendor business.  Standard types are Supplier, Manufacturer, and Customer.  Other types can be modeled as needed.
     */
    @Size(max=255)
    @RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})
    @Metadata(index=5, displayName="Supplier Entity Type")
    @FlexCDMData({"*","*.down"})
    public String getVendorBusinessEntityType();
    /**
     * Reference to the type of  vendor business.  Standard types are Supplier, Manufacturer, and Customer.  Other types can be modeled as needed.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})</li>
     * <li>@Metadata(index=5, displayName="Supplier Entity Type")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVendorBusinessEntityType(String value);


    /**
     * Reference to the vendor's Item.
     */
    @Size(max=255)
    @Metadata(index=3, displayName="Supplier Item")
    @FlexCDMData({"*","*.down"})
    public String getVendorItemIdentifier();
    /**
     * Reference to the vendor's Item.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=3, displayName="Supplier Item")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVendorItemIdentifier(String value);


    /**
     * A site for the Vendor Business Entity.  The vendor item can be site specific.
     */
    @Size(max=255)
    @Metadata(index=6, displayName="Supplier Site")
    @FlexCDMData({"*","*.down"})
    public String getVendorSite();
    /**
     * A site for the Vendor Business Entity.  The vendor item can be site specific.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=6, displayName="Supplier Site")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVendorSite(String value);


    /**
     * The version of the item
     */
    @Size(max=255)
    @Metadata(index=9, displayName="Version")
    @FlexCDMData({"*","*.down"})
    public String getVersion();
    /**
     * The version of the item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=9, displayName="Version")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVersion(String value);

    /**
     * The Platform of the item
     * <br>This is not part of the spec.  Adding here for verification.
     */
    @Size(max=255)
    @Metadata(index=9, displayName="Platform")
    @FlexCDMData({"*","*.down"})
    public String getVerification_Platform();
    /**
     * The Platform of the item
     * <br>This is not part of the spec.  Adding here for verification.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=9, displayName="Platform")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVerification_Platform(String value);
    
    /**
     * The ProductFamily of the item
     * <br>This is not part of the spec.  Adding here for verification.
     */
    @Size(max=255)
    @Metadata(index=9, displayName="ProductFamily")
    @FlexCDMData({"*","*.down"})
    public String getVerification_ProductFamily();
    /**
     * The ProductFamily of the item
     * <br>This is not part of the spec.  Adding here for verification.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=9, displayName="ProductFamily")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVerification_ProductFamily(String value);
    
    /**
     * The Responsibility of the item
     * <br>This is not part of the spec.  Adding here for verification.
     */
    @Size(max=255)
    @Metadata(index=9, displayName="Responsibility")
    @FlexCDMData({"*","*.down"})
    public String getVerification_Responsibility();
    /**
     * The Responsibility of the item
     * <br>This is not part of the spec.  Adding here for verification.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=9, displayName="Responsibility")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVerification_Responsibility(String value);
    
    
    
    
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
         * This models what business entity this item is for.  In a typical system there will be Supplier, Manufacturer, and Customer items and each is mapped to a specific business.
         */
        public static final FieldDefinition BUSINESSENTITY = new FieldDefinition("BusinessEntity");
        /**
         * Reference to the type of business.  Standard types are Supplier, Manufacturer, and Customer.  Other types can be modeled as needed.
         */
        public static final FieldDefinition BUSINESSENTITYTYPE = new FieldDefinition("BusinessEntityType");
        /**
         * Commodity code for this item
         */
        public static final FieldDefinition COMMODITYCODE = new FieldDefinition("CommodityCode");
        /**
         * Optional contact information
         */
        public static final FieldDefinition CONTACTNAME = new FieldDefinition("ContactName");
        /**
         * Optional Description of the item.
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
         * Optional classification name
         */
        public static final FieldDefinition ITEMCLASSIFICATION = new FieldDefinition("ItemClassification");
        /**
         * Reference to the Item.
         */
        public static final FieldDefinition ITEMIDENTIFIER = new FieldDefinition("ItemIdentifier");
        /**
         * Make or Buy decision
         */
        public static final FieldDefinition MAKEBUY = new FieldDefinition("MakeBuy");
        /**
         * The group responsible for managing the item.
         */
        public static final FieldDefinition MANAGEDBY = new FieldDefinition("ManagedBy");
        /**
         * Optional owner identification
         */
        public static final FieldDefinition OWNERNAME = new FieldDefinition("OwnerName");
        /**
         * Unit of measure for item (gallons, inches, etc.)
         */
        public static final FieldDefinition PRODUCTUNITOFMEASURECODE = new FieldDefinition("ProductUnitOfMeasureCode");
        /**
         * The revision of the item
         */
        public static final FieldDefinition REVISION = new FieldDefinition("Revision");
        /**
         * This models what vendor business entity this vendor's item is for.  In a typical system there will be Supplier, Manufacturer, and Customer items and each is mapped to a specific business.
         */
        public static final FieldDefinition VENDORBUSINESSENTITY = new FieldDefinition("VendorBusinessEntity");
        /**
         * Reference to the type of  vendor business.  Standard types are Supplier, Manufacturer, and Customer.  Other types can be modeled as needed.
         */
        public static final FieldDefinition VENDORBUSINESSENTITYTYPE = new FieldDefinition("VendorBusinessEntityType");
        /**
         * Reference to the vendor's Item.
         */
        public static final FieldDefinition VENDORITEMIDENTIFIER = new FieldDefinition("VendorItemIdentifier");
        /**
         * A site for the Vendor Business Entity.  The vendor item can be site specific.
         */
        public static final FieldDefinition VENDORSITE = new FieldDefinition("VendorSite");
        /**
         * The version of the item
         */
        public static final FieldDefinition VERSION = new FieldDefinition("Version");
        
        /**
         * The Platform of the item
         * <br>This is not part of the spec.  Adding here for verification.
         */
        public static final FieldDefinition VERIFICATION_PLATFORM = new FieldDefinition("Verification_Platform");
        
        /**
         * The ProductFamily of the item
         * <br>This is not part of the spec.  Adding here for verification.
         */
        public static final FieldDefinition VERIFICATION_PRODUCTFAMILY = new FieldDefinition("Verification_ProductFamily");
        
        /**
         * The Responsibility of the item
         * <br>This is not part of the spec.  Adding here for verification.
         */
        public static final FieldDefinition VERIFICATION_RESPONSIBILITY = new FieldDefinition("Verification_Responsibility");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link ItemAVL.PredicateBuilder} 
     */
    public interface ItemAVLFieldPredicateFactory<T extends ItemAVL>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereBusinessEntity();
        public FieldPredicate<T,String> whereBusinessEntityType();
        public FieldPredicate<T,String> whereCommodityCode();
        public FieldPredicate<T,String> whereContactName();
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,DateTime> whereEffectiveFromDate();
        public FieldPredicate<T,DateTime> whereEffectiveToDate();
        public FieldPredicate<T,String> whereItemClassification();
        public FieldPredicate<T,String> whereItemIdentifier();
        public FieldPredicate<T,String> whereMakeBuy();
        public FieldPredicate<T,String> whereManagedBy();
        public FieldPredicate<T,String> whereOwnerName();
        public FieldPredicate<T,String> whereProductUnitOfMeasureCode();
        public FieldPredicate<T,String> whereRevision();
        public FieldPredicate<T,String> whereVendorBusinessEntity();
        public FieldPredicate<T,String> whereVendorBusinessEntityType();
        public FieldPredicate<T,String> whereVendorItemIdentifier();
        public FieldPredicate<T,String> whereVendorSite();
        public FieldPredicate<T,String> whereVersion();
        public FieldPredicate<T,String> whereVerification_Platform();
        public FieldPredicate<T,String> whereVerification_ProductFamily();
        public FieldPredicate<T,String> whereVerification_Responsibility();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link ItemAVL}
         */
        public static ItemAVL newInstance() {
            return MessageLineProxy.newInstance(ItemAVL.class);
        }
        
        /**
         * Clone a instance of {@link ItemAVL}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static ItemAVL clone(ItemAVL dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(ItemAVL.class, dataToClone);
        }
		
        /**
         * Use this method to create a field based predicate on {@link ItemAVL}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;ItemAVL&gt; pred = ItemAVL
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
        public static ItemAVLFieldPredicateFactory<ItemAVL> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(ItemAVL.class, ItemAVLFieldPredicateFactory.class);
        }
    }
 }