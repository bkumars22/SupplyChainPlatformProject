/**
 * @SupplierAllocation.java@
 *
 * Created on Wed Oct 22 12:34:06 PDT 2014
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
 
package com.test.selenium.scplatform.messages.supplierAllocation;
 
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
public interface SupplierAllocation extends MessageLine {

    /**
     * The percentage of allocated forecast.
     */
    @Size(max=126)
    @Metadata(index=11, displayName="Allocation")
    @FlexCDMData({"*","*.down"})
    public float getAllocation();
    /**
     * The percentage of allocated forecast.
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=11, displayName="Allocation")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAllocation(float value);


    @Size(max=255)
    @Metadata(index=29, displayName="Comment")
    @FlexCDMData({"*","*.down"})
    public String getComment();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=29, displayName="Comment")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setComment(String value);


    /**
     * The business entity
     */
    @Size(max=255)
    @Metadata(index=15, displayName="ContextBusinessEntity")
    @FlexCDMData({"*","*.down"})
    public String getContextBusinessEntity();
    /**
     * The business entity
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=15, displayName="ContextBusinessEntity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setContextBusinessEntity(String value);


    /**
     * Reference to the type of business. Can be ENTERPRISE or SUPPLIER.
     */
    @Size(max=255)
    @RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})
    @Metadata(index=16, displayName="ContextBusinessEntityType")
    @FlexCDMData({"*","*.down"})
    public String getContextBusinessEntityType();
    /**
     * Reference to the type of business. Can be ENTERPRISE or SUPPLIER.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})</li>
     * <li>@Metadata(index=16, displayName="ContextBusinessEntityType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setContextBusinessEntityType(String value);


    /**
     * The part number
     */
    @Size(max=255)
    @Metadata(index=17, displayName="ContextItemIdentifier")
    @FlexCDMData({"*","*.down"})
    public String getContextItemIdentifier();
    /**
     * The part number
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=17, displayName="ContextItemIdentifier")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setContextItemIdentifier(String value);


    @Size(max=255)
    @Metadata(index=18, displayName="ContextPlatformName")
    @FlexCDMData({"*","*.down"})
    public String getContextPlatformName();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=18, displayName="ContextPlatformName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setContextPlatformName(String value);


    /**
     * The platform type (typically unset)
     */
    @Size(max=255)
    @Metadata(index=19, displayName="ContextPlatformType")
    @FlexCDMData({"*","*.down"})
    public String getContextPlatformType();
    /**
     * The platform type (typically unset)
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=19, displayName="ContextPlatformType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setContextPlatformType(String value);


    /**
     * The business entity
     */
    @Size(max=255)
    @RestrictToStrings({"Item","Platform","All"})
    @Metadata(index=14, displayName="ContextType")
    @FlexCDMData({"*","*.down"})
    public String getContextType();
    /**
     * The business entity
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@RestrictToStrings({"Item","Platform","All"})</li>
     * <li>@Metadata(index=14, displayName="ContextType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setContextType(String value);


    /**
     * The revision of the item
     */
    @Size(max=255)
    @Metadata(index=22, displayName="ItemRevision")
    @FlexCDMData({"*","*.down"})
    public String getItemRevision();
    /**
     * The revision of the item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=22, displayName="ItemRevision")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemRevision(String value);


    /**
     * The customer's business entity.
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=3, displayName="Customer Business Entity")
    @FlexCDMData({"*","*.down"})
    public String getCustomerBusinessEntity();
    /**
     * The customer's business entity.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=3, displayName="Customer Business Entity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCustomerBusinessEntity(String value);


    /**
     * Reference to the type of business.  Should be 'Enterpriser'.
     */
    @NotNull
    @Size(max=255)
    @RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})
    @Metadata(index=4, displayName="Customer Business Entity Type")
    @FlexCDMData({"*","*.down"})
    public String getCustomerBusinessEntityType();
    /**
     * Reference to the type of business.  Should be 'Enterpriser'.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})</li>
     * <li>@Metadata(index=4, displayName="Customer Business Entity Type")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCustomerBusinessEntityType(String value);


    /**
     * The customer's part description. One of the CustomerItemGroupIdentifier or the CustomerItemIdentifier is required.
     */
    @Size(max=1024)
    @Metadata(index=2, displayName="Customer Item Description")
    @FlexCDMData({"*","*.down"})
    public String getCustomerItemDescription();
    /**
     * The customer's part description. One of the CustomerItemGroupIdentifier or the CustomerItemIdentifier is required.
     * Annotations:
     * <ul>
     * <li>@Size(max=1024)</li>
     * <li>@Metadata(index=2, displayName="Customer Item Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCustomerItemDescription(String value);


    /**
     * The customer's item group (i.e. CFG). One of the CustomerItemGroupIdentifier or the CustomerItemIdentifier is required.
     */
    @Size(max=255)
    @Metadata(index=0, displayName="Customer Item Group Identifier")
    @FlexCDMData({"*","*.down"})
    public String getCustomerItemGroupIdentifier();
    /**
     * The customer's item group (i.e. CFG). One of the CustomerItemGroupIdentifier or the CustomerItemIdentifier is required.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="Customer Item Group Identifier")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCustomerItemGroupIdentifier(String value);


    /**
     * The customer's part number. One of the CustomerItemGroupIdentifier or the CustomerItemIdentifier is required.
     */
    @Size(max=255)
    @Metadata(index=1, displayName="Customer Item Identifier")
    @FlexCDMData({"*","*.down"})
    public String getCustomerItemIdentifier();
    /**
     * The customer's part number. One of the CustomerItemGroupIdentifier or the CustomerItemIdentifier is required.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=1, displayName="Customer Item Identifier")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCustomerItemIdentifier(String value);


    /**
     * The customer's site.
     */
    @Size(max=255)
    @Metadata(index=5, displayName="Customer Site")
    @FlexCDMData({"*","*.down"})
    public String getCustomerSite();
    /**
     * The customer's site.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=5, displayName="Customer Site")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCustomerSite(String value);


    @Size(max=255)
    @Metadata(index=31, displayName="DataSource")
    public String getDataSource();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=31, displayName="DataSource")</li>
     * </ul>
     */
    public void setDataSource(String value);


    /**
     * Optional Description of the platform.
     */
    @NotNull
    @Size(max=1024)
    @Metadata(index=10, displayName="Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * Optional Description of the platform.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=1024)</li>
     * <li>@Metadata(index=10, displayName="Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDescription(String value);


    /**
     * From Date.
     */
    @NotNull
    @Metadata(index=12, displayName="Effective From Date")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveFromDate();
    /**
     * From Date.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=12, displayName="Effective From Date")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveFromDate(DateTime value);


    /**
     * To Date.
     */
    @NotNull
    @Metadata(index=13, displayName="Effective To Date")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveToDate();
    /**
     * To Date.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=13, displayName="Effective To Date")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveToDate(DateTime value);


    /**
     * The item group revision
     */
    @Size(max=255)
    @Metadata(index=25, displayName="ItemGroupRevision")
    @FlexCDMData({"*","*.down"})
    public String getItemGroupRevision();
    /**
     * The item group revision
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=25, displayName="ItemGroupRevision")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemGroupRevision(String value);


    /**
     * The item group version
     */
    @Size(max=255)
    @Metadata(index=24, displayName="ItemGroupVersion")
    @FlexCDMData({"*","*.down"})
    public String getItemGroupVersion();
    /**
     * The item group version
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=24, displayName="ItemGroupVersion")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemGroupVersion(String value);


    /**
     * The external reference number or other part identifier
     */
    @Size(max=255)
    @Metadata(index=20, displayName="ItemUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getItemUniqueId();
    /**
     * The external reference number or other part identifier
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=20, displayName="ItemUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemUniqueId(String value);


    /**
     * The version of the item
     */
    @Size(max=255)
    @Metadata(index=23, displayName="ItemVersion")
    @FlexCDMData({"*","*.down"})
    public String getItemVersion();
    /**
     * The version of the item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=23, displayName="ItemVersion")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemVersion(String value);


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
    @Metadata(index=30, displayName="OperationCode")
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
     * <li>@Metadata(index=30, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    /**
     * This supplier's business entity.
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=7, displayName="Supplier Business Entity")
    @FlexCDMData({"*","*.down"})
    public String getSupplierBusinessEntity();
    /**
     * This supplier's business entity.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=7, displayName="Supplier Business Entity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSupplierBusinessEntity(String value);


    /**
     * Reference to the type of business.  Should be 'Supplier'.
     */
    @NotNull
    @Size(max=255)
    @RestrictToStrings({"SUPPLIER"})
    @Metadata(index=8, displayName="Supplier Business Entity Type")
    @FlexCDMData({"*","*.down"})
    public String getSupplierBusinessEntityType();
    /**
     * Reference to the type of business.  Should be 'Supplier'.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@RestrictToStrings({"SUPPLIER"})</li>
     * <li>@Metadata(index=8, displayName="Supplier Business Entity Type")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSupplierBusinessEntityType(String value);


    /**
     * The supplier's part number.
     */
    @Size(max=255)
    @Metadata(index=6, displayName="Supplier Item Identifier")
    @FlexCDMData({"*","*.down"})
    public String getSupplierItemIdentifier();
    /**
     * The supplier's part number.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=6, displayName="Supplier Item Identifier")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSupplierItemIdentifier(String value);


    /**
     * The revision of the suppliers item
     */
    @Size(max=255)
    @Metadata(index=27, displayName="SupplierItemRevision")
    @FlexCDMData({"*","*.down"})
    public String getSupplierItemRevision();
    /**
     * The revision of the suppliers item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=27, displayName="SupplierItemRevision")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSupplierItemRevision(String value);


    /**
     * The suppliers external reference number or other part identifier
     */
    @Size(max=255)
    @Metadata(index=26, displayName="SupplierItemUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getSupplierItemUniqueId();
    /**
     * The suppliers external reference number or other part identifier
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=26, displayName="SupplierItemUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSupplierItemUniqueId(String value);


    /**
     * The version of the suppliers item
     */
    @Size(max=255)
    @Metadata(index=28, displayName="SupplierItemVersion")
    @FlexCDMData({"*","*.down"})
    public String getSupplierItemVersion();
    /**
     * The version of the suppliers item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=28, displayName="SupplierItemVersion")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSupplierItemVersion(String value);


    /**
     * The supplier's site.
     */
    @Size(max=255)
    @Metadata(index=9, displayName="Supplier Site")
    @FlexCDMData({"*","*.down"})
    public String getSupplierSite();
    /**
     * The supplier's site.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=9, displayName="Supplier Site")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSupplierSite(String value);

    
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
         * The percentage of allocated forecast.
         */
        public static final FieldDefinition ALLOCATION = new FieldDefinition("Allocation");
        public static final FieldDefinition COMMENT = new FieldDefinition("Comment");
        /**
         * The business entity
         */
        public static final FieldDefinition CONTEXTBUSINESSENTITY = new FieldDefinition("ContextBusinessEntity");
        /**
         * Reference to the type of business. Can be ENTERPRISE or SUPPLIER.
         */
        public static final FieldDefinition CONTEXTBUSINESSENTITYTYPE = new FieldDefinition("ContextBusinessEntityType");
        /**
         * The part number
         */
        public static final FieldDefinition CONTEXTITEMIDENTIFIER = new FieldDefinition("ContextItemIdentifier");
        public static final FieldDefinition CONTEXTPLATFORMNAME = new FieldDefinition("ContextPlatformName");
        /**
         * The platform type (typically unset)
         */
        public static final FieldDefinition CONTEXTPLATFORMTYPE = new FieldDefinition("ContextPlatformType");
        /**
         * The business entity
         */
        public static final FieldDefinition CONTEXTTYPE = new FieldDefinition("ContextType");
        /**
         * The revision of the item
         */
        public static final FieldDefinition ITEMREVISION = new FieldDefinition("ItemRevision");
        /**
         * The customer's business entity.
         */
        public static final FieldDefinition CUSTOMERBUSINESSENTITY = new FieldDefinition("CustomerBusinessEntity");
        /**
         * Reference to the type of business.  Should be 'Enterpriser'.
         */
        public static final FieldDefinition CUSTOMERBUSINESSENTITYTYPE = new FieldDefinition("CustomerBusinessEntityType");
        /**
         * The customer's part description. One of the CustomerItemGroupIdentifier or the CustomerItemIdentifier is required.
         */
        public static final FieldDefinition CUSTOMERITEMDESCRIPTION = new FieldDefinition("CustomerItemDescription");
        /**
         * The customer's item group (i.e. CFG). One of the CustomerItemGroupIdentifier or the CustomerItemIdentifier is required.
         */
        public static final FieldDefinition CUSTOMERITEMGROUPIDENTIFIER = new FieldDefinition("CustomerItemGroupIdentifier");
        /**
         * The customer's part number. One of the CustomerItemGroupIdentifier or the CustomerItemIdentifier is required.
         */
        public static final FieldDefinition CUSTOMERITEMIDENTIFIER = new FieldDefinition("CustomerItemIdentifier");
        /**
         * The customer's site.
         */
        public static final FieldDefinition CUSTOMERSITE = new FieldDefinition("CustomerSite");
        public static final FieldDefinition DATASOURCE = new FieldDefinition("DataSource");
        /**
         * Optional Description of the platform.
         */
        public static final FieldDefinition DESCRIPTION = new FieldDefinition("Description");
        /**
         * From Date.
         */
        public static final FieldDefinition EFFECTIVEFROMDATE = new FieldDefinition("EffectiveFromDate");
        /**
         * To Date.
         */
        public static final FieldDefinition EFFECTIVETODATE = new FieldDefinition("EffectiveToDate");
        /**
         * The item group revision
         */
        public static final FieldDefinition ITEMGROUPREVISION = new FieldDefinition("ItemGroupRevision");
        /**
         * The item group version
         */
        public static final FieldDefinition ITEMGROUPVERSION = new FieldDefinition("ItemGroupVersion");
        /**
         * The external reference number or other part identifier
         */
        public static final FieldDefinition ITEMUNIQUEID = new FieldDefinition("ItemUniqueId");
        /**
         * The version of the item
         */
        public static final FieldDefinition ITEMVERSION = new FieldDefinition("ItemVersion");
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
         * This supplier's business entity.
         */
        public static final FieldDefinition SUPPLIERBUSINESSENTITY = new FieldDefinition("SupplierBusinessEntity");
        /**
         * Reference to the type of business.  Should be 'Supplier'.
         */
        public static final FieldDefinition SUPPLIERBUSINESSENTITYTYPE = new FieldDefinition("SupplierBusinessEntityType");
        /**
         * The supplier's part number.
         */
        public static final FieldDefinition SUPPLIERITEMIDENTIFIER = new FieldDefinition("SupplierItemIdentifier");
        /**
         * The revision of the suppliers item
         */
        public static final FieldDefinition SUPPLIERITEMREVISION = new FieldDefinition("SupplierItemRevision");
        /**
         * The suppliers external reference number or other part identifier
         */
        public static final FieldDefinition SUPPLIERITEMUNIQUEID = new FieldDefinition("SupplierItemUniqueId");
        /**
         * The version of the suppliers item
         */
        public static final FieldDefinition SUPPLIERITEMVERSION = new FieldDefinition("SupplierItemVersion");
        /**
         * The supplier's site.
         */
        public static final FieldDefinition SUPPLIERSITE = new FieldDefinition("SupplierSite");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link SupplierAllocation.PredicateBuilder} 
     */
    public interface SupplierAllocationFieldPredicateFactory<T extends SupplierAllocation>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,Float> whereAllocation();
        public FieldPredicate<T,String> whereComment();
        public FieldPredicate<T,String> whereContextBusinessEntity();
        public FieldPredicate<T,String> whereContextBusinessEntityType();
        public FieldPredicate<T,String> whereContextItemIdentifier();
        public FieldPredicate<T,String> whereContextPlatformName();
        public FieldPredicate<T,String> whereContextPlatformType();
        public FieldPredicate<T,String> whereContextType();
        public FieldPredicate<T,String> whereItemRevision();
        public FieldPredicate<T,String> whereCustomerBusinessEntity();
        public FieldPredicate<T,String> whereCustomerBusinessEntityType();
        public FieldPredicate<T,String> whereCustomerItemDescription();
        public FieldPredicate<T,String> whereCustomerItemGroupIdentifier();
        public FieldPredicate<T,String> whereCustomerItemIdentifier();
        public FieldPredicate<T,String> whereCustomerSite();
        public FieldPredicate<T,String> whereDataSource();
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,DateTime> whereEffectiveFromDate();
        public FieldPredicate<T,DateTime> whereEffectiveToDate();
        public FieldPredicate<T,String> whereItemGroupRevision();
        public FieldPredicate<T,String> whereItemGroupVersion();
        public FieldPredicate<T,String> whereItemUniqueId();
        public FieldPredicate<T,String> whereItemVersion();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> whereSupplierBusinessEntity();
        public FieldPredicate<T,String> whereSupplierBusinessEntityType();
        public FieldPredicate<T,String> whereSupplierItemIdentifier();
        public FieldPredicate<T,String> whereSupplierItemRevision();
        public FieldPredicate<T,String> whereSupplierItemUniqueId();
        public FieldPredicate<T,String> whereSupplierItemVersion();
        public FieldPredicate<T,String> whereSupplierSite();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link SupplierAllocation}
         */
        public static SupplierAllocation newInstance() {
            return MessageLineProxy.newInstance(SupplierAllocation.class);
        }
        
        /**
         * Clone a instance of {@link SupplierAllocation}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static SupplierAllocation clone(SupplierAllocation dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(SupplierAllocation.class, dataToClone);
        }
		
        /**
         * Use this method to create a field based predicate on {@link SupplierAllocation}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;SupplierAllocation&gt; pred = SupplierAllocation
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
        public static SupplierAllocationFieldPredicateFactory<SupplierAllocation> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(SupplierAllocation.class, SupplierAllocationFieldPredicateFactory.class);
        }
    }
 }