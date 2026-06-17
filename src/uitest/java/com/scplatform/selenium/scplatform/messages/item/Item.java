/**
 * @Item.java@
 *
 * Created on Tue Oct 21 09:06:46 PDT 2014
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
 
package com.test.selenium.scplatform.messages.item;
 
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
import com.test.selenium.scplatform.messages.item.subClasses.AlternateItem;
import com.test.selenium.scplatform.messages.item.subClasses.ApprovedManufacturerListItem;
import com.test.selenium.scplatform.messages.item.subClasses.ApprovedVendorListItem;
import com.test.selenium.scplatform.messages.item.subClasses.Bom;
import com.test.selenium.scplatform.messages.item.subClasses.BuyerCode;
import com.test.selenium.scplatform.messages.item.subClasses.ItemPlatform;
import com.test.selenium.scplatform.messages.item.subClasses.Responsibility;
 
/**
 * <b>SPEC:</b> http://confluence.dev.scplatform.local/display/PUBT/Item
 * 
 *
 */
@Generated("IrisCodeGenerator")
public interface Item extends MessageLine {

    /**
     * Alternate Item is a Sub Structure
     */
    @Metadata(index=32, displayName="AlternateItem")
    @FlexCDMData({"*","*.down"})
    public List<AlternateItem> getAlternateItem();
    /**
     * Alternate Item is a Sub Structure
     * Annotations:
     * <ul>
     * <li>@Metadata(index=32, displayName="AlternateItem")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAlternateItem(List<AlternateItem> value);


    /**
     * Approved Vendor List Item is a Sub Structure
     */
    @Metadata(index=34, displayName="ApprovedManufacturerListItem")
    @FlexCDMData({"*","*.down"})
    public List<ApprovedManufacturerListItem> getApprovedManufacturerListItem();
    /**
     * Approved Vendor List Item is a Sub Structure
     * Annotations:
     * <ul>
     * <li>@Metadata(index=34, displayName="ApprovedManufacturerListItem")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setApprovedManufacturerListItem(List<ApprovedManufacturerListItem> value);


    /**
     * Approved Vendor List Item is a Sub Structure
     */
    @Metadata(index=33, displayName="ApprovedVendorListItem")
    @FlexCDMData({"*","*.down"})
    public List<ApprovedVendorListItem> getApprovedVendorListItem();
    /**
     * Approved Vendor List Item is a Sub Structure
     * Annotations:
     * <ul>
     * <li>@Metadata(index=33, displayName="ApprovedVendorListItem")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setApprovedVendorListItem(List<ApprovedVendorListItem> value);


    /**
     * BOM (Bill of Material Element) is a Sub Structure
     */
    @Metadata(index=31, displayName="Bom")
    @FlexCDMData({"*","*.down"})
    public List<Bom> getBom();
    /**
     * BOM (Bill of Material Element) is a Sub Structure
     * Annotations:
     * <ul>
     * <li>@Metadata(index=31, displayName="Bom")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBom(List<Bom> value);


    /**
     * This models what business entity this part is for. In a typical system, there is customer, supplier, and manufacturer items and each is mapped to a specific business.
     */
    @Size(max=255)
    @Metadata(index=24, displayName="BusinessEntity")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntity();
    /**
     * This models what business entity this part is for. In a typical system, there is customer, supplier, and manufacturer items and each is mapped to a specific business.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=24, displayName="BusinessEntity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntity(String value);


    /**
     * Reference to the type of business. Can be ENTERPRISE or SUPPLIER.
     */
    @NotNull
    @Size(max=64)
    @RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})
    @Metadata(index=25, displayName="BusinessEntityType")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntityType();
    /**
     * Reference to the type of business. Can be ENTERPRISE or SUPPLIER.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})</li>
     * <li>@Metadata(index=25, displayName="BusinessEntityType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntityType(String value);


    /**
     * Item Buyer Codes is a Sub Structure. Item buyer codes are used to designate what buyer code this item has been assigned to. The code can be site-specific, in which case the site name must be specified.
     */
    @Metadata(index=36, displayName="BuyerCode")
    @FlexCDMData({"*","*.down"})
    public List<BuyerCode> getBuyerCode();
    /**
     * Item Buyer Codes is a Sub Structure. Item buyer codes are used to designate what buyer code this item has been assigned to. The code can be site-specific, in which case the site name must be specified.
     * Annotations:
     * <ul>
     * <li>@Metadata(index=36, displayName="BuyerCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBuyerCode(List<BuyerCode> value);


    /**
     * Commodity code for this item
     */
    @Size(max=255)
    @Metadata(index=12, displayName="CommodityCode")
    @FlexCDMData({"*","*.down"})
    public String getCommodityCode();
    /**
     * Commodity code for this item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=12, displayName="CommodityCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCommodityCode(String value);


    /**
     * contact information
     */
    @Size(max=255)
    @Metadata(index=21, displayName="ContactName")
    @FlexCDMData({"*","*.down"})
    public String getContactName();
    /**
     * contact information
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=21, displayName="ContactName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setContactName(String value);


    /**
     * The contacts unique identifier
     */
    @Size(max=255)
    @Metadata(index=22, displayName="ContactUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getContactUniqueId();
    /**
     * The contacts unique identifier
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=22, displayName="ContactUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setContactUniqueId(String value);


    /**
     * The system of record for the item
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=26, displayName="DataSource")
    @FlexCDMData({"*","*.down"})
    public String getDataSource();
    /**
     * The system of record for the item
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=26, displayName="DataSource")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDataSource(String value);


    /**
     * description of the part
     */
    @Size(max=1024)
    @Metadata(index=2, displayName="Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * description of the part
     * Annotations:
     * <ul>
     * <li>@Size(max=1024)</li>
     * <li>@Metadata(index=2, displayName="Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDescription(String value);


    /**
     * From Date
     */
    @Metadata(index=27, displayName="EffectiveFromDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveFromDate();
    /**
     * From Date
     * Annotations:
     * <ul>
     * <li>@Metadata(index=27, displayName="EffectiveFromDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveFromDate(DateTime value);


    /**
     * To Date
     */
    @Metadata(index=28, displayName="EffectiveToDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveToDate();
    /**
     * To Date
     * Annotations:
     * <ul>
     * <li>@Metadata(index=28, displayName="EffectiveToDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveToDate(DateTime value);


    /**
     * Certification required (default is false)
     */
    @Size(max=64)
    @RestrictToStrings({"true","false"})
    @Metadata(index=18, displayName="IsCertificationRequired")
    @FlexCDMData({"*","*.down"})
    public String getIsCertificationRequired();
    /**
     * Certification required (default is false)
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"true","false"})</li>
     * <li>@Metadata(index=18, displayName="IsCertificationRequired")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setIsCertificationRequired(String value);


    /**
     * Serial number required for item (default is false)
     */
    @Size(max=64)
    @RestrictToStrings({"true","false"})
    @Metadata(index=17, displayName="IsSerializationRequired")
    @FlexCDMData({"*","*.down"})
    public String getIsSerializationRequired();
    /**
     * Serial number required for item (default is false)
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"true","false"})</li>
     * <li>@Metadata(index=17, displayName="IsSerializationRequired")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setIsSerializationRequired(String value);


    /**
     * Is item a top level item
     */
    @Size(max=64)
    @RestrictToStrings({"true","false"})
    @Metadata(index=23, displayName="IsTopLevel")
    @FlexCDMData({"*","*.down"})
    public String getIsTopLevel();
    /**
     * Is item a top level item
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"true","false"})</li>
     * <li>@Metadata(index=23, displayName="IsTopLevel")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setIsTopLevel(String value);


    /**
     * classification name
     */
    @Size(max=255)
    @Metadata(index=10, displayName="ItemClassification")
    @FlexCDMData({"*","*.down"})
    public String getItemClassification();
    /**
     * classification name
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=10, displayName="ItemClassification")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemClassification(String value);


    /**
     * The part number
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=0, displayName="ItemIdentifier")
    @FlexCDMData({"*","*.down"})
    public String getItemIdentifier();
    /**
     * The part number
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="ItemIdentifier")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemIdentifier(String value);


    /**
     * Item Part Type
     */
    @Size(max=255)
    @Metadata(index=9, displayName="ItemPartType")
    @FlexCDMData({"*","*.down"})
    public String getItemPartType();
    /**
     * Item Part Type
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=9, displayName="ItemPartType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemPartType(String value);


    /**
     * Item Platform is a Sub Structure. Item platform is used to designate what platform this item has been assigned to and can be assigned to more than one platform.
     */
    @Metadata(index=35, displayName="ItemPlatform")
    @FlexCDMData({"*","*.down"})
    public List<ItemPlatform> getItemPlatform();
    /**
     * Item Platform is a Sub Structure. Item platform is used to designate what platform this item has been assigned to and can be assigned to more than one platform.
     * Annotations:
     * <ul>
     * <li>@Metadata(index=35, displayName="ItemPlatform")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemPlatform(List<ItemPlatform> value);


    /**
     * External reference number or other part identifier
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=1, displayName="ItemUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getItemUniqueId();
    /**
     * External reference number or other part identifier
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=1, displayName="ItemUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemUniqueId(String value);


    /**
     * Last time the item owner was changed
     */
    @Metadata(index=30, displayName="LastUpdateDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getLastUpdateDate();
    /**
     * Last time the item owner was changed
     * Annotations:
     * <ul>
     * <li>@Metadata(index=30, displayName="LastUpdateDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLastUpdateDate(DateTime value);


    /**
     * Not used in upload, but used for some automation internal value for the level of the item
     */
    @Size(max=64)
    @Metadata(index=38, displayName="Level")
    @FlexCDMData({"*","*.down"})
    public String getLevel();
    /**
     * Not used in upload, but used for some automation internal value for the level of the item
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@Metadata(index=38, displayName="Level")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLevel(String value);


    /**
     * lifecycle code. Refer to the SDD for specific codes defined.
     */
    @Size(max=255)
    @Metadata(index=7, displayName="LifeCycleCode")
    @FlexCDMData({"*","*.down"})
    public String getLifeCycleCode();
    /**
     * lifecycle code. Refer to the SDD for specific codes defined.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=7, displayName="LifeCycleCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLifeCycleCode(String value);


    /**
     * If the lifeCycleCode is set to Other , provides additional information.
     */
    @Size(max=255)
    @Metadata(index=8, displayName="LifeCycleCodeOther")
    @FlexCDMData({"*","*.down"})
    public String getLifeCycleCodeOther();
    /**
     * If the lifeCycleCode is set to Other , provides additional information.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=8, displayName="LifeCycleCodeOther")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLifeCycleCodeOther(String value);


    /**
     * Make or Buy decision
     */
    @Size(max=255)
    @RestrictToStrings({"Make","Buy"})
    @Metadata(index=15, displayName="MakeBuy")
    @FlexCDMData({"*","*.down"})
    public String getMakeBuy();
    /**
     * Make or Buy decision
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@RestrictToStrings({"Make","Buy"})</li>
     * <li>@Metadata(index=15, displayName="MakeBuy")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setMakeBuy(String value);


    /**
     * If the makeBuy attribute is set to Other, provides a more descriptive value
     */
    @Size(max=255)
    @Metadata(index=16, displayName="MakeBuyOther")
    @FlexCDMData({"*","*.down"})
    public String getMakeBuyOther();
    /**
     * If the makeBuy attribute is set to Other, provides a more descriptive value
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=16, displayName="MakeBuyOther")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setMakeBuyOther(String value);


    /**
     * The group responsible for managing the item
     */
    @Size(max=255)
    @Metadata(index=13, displayName="ManagedBy")
    @FlexCDMData({"*","*.down"})
    public String getManagedBy();
    /**
     * The group responsible for managing the item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=13, displayName="ManagedBy")</li>
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
    @Metadata(index=29, displayName="OperationCode")
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
     * <li>@Metadata(index=29, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    /**
     * Refers to contactUniqueIdentifier attribute of the Contact element for the owner or responsible party for the Item
     */
    @Size(max=255)
    @Metadata(index=20, displayName="OwnerContactUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getOwnerContactUniqueId();
    /**
     * Refers to contactUniqueIdentifier attribute of the Contact element for the owner or responsible party for the Item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=20, displayName="OwnerContactUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOwnerContactUniqueId(String value);


    /**
     * owner identification
     */
    @Size(max=255)
    @Metadata(index=19, displayName="OwnerName")
    @FlexCDMData({"*","*.down"})
    public String getOwnerName();
    /**
     * owner identification
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=19, displayName="OwnerName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOwnerName(String value);


    /**
     * nit of measure for item (gallons, inches, and so on)
     */
    @Size(max=255)
    @Metadata(index=14, displayName="ProductUnitOfMeasureCode")
    @FlexCDMData({"*","*.down"})
    public String getProductUnitOfMeasureCode();
    /**
     * nit of measure for item (gallons, inches, and so on)
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=14, displayName="ProductUnitOfMeasureCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setProductUnitOfMeasureCode(String value);


    /**
     * Product line(s) to which the item belongs
     */
    @Size(max=255)
    @Metadata(index=11, displayName="ProprietaryProductFamily")
    @FlexCDMData({"*","*.down"})
    public String getProprietaryProductFamily();
    /**
     * Product line(s) to which the item belongs
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=11, displayName="ProprietaryProductFamily")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setProprietaryProductFamily(String value);


    /**
     * Responsibility is a Sub Structure
     */
    @Metadata(index=37, displayName="Responsibility")
    @FlexCDMData({"*","*.down"})
    public List<Responsibility> getResponsibility();
    /**
     * Responsibility is a Sub Structure
     * Annotations:
     * <ul>
     * <li>@Metadata(index=37, displayName="Responsibility")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setResponsibility(List<Responsibility> value);


    /**
     * The revision of the item
     */
    @Size(max=255)
    @Metadata(index=3, displayName="Revision")
    @FlexCDMData({"*","*.down"})
    public String getRevision();
    /**
     * The revision of the item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=3, displayName="Revision")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setRevision(String value);


    /**
     * The date the revision was released
     */
    @Metadata(index=5, displayName="RevisionReleaseDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getRevisionReleaseDate();
    /**
     * The date the revision was released
     * Annotations:
     * <ul>
     * <li>@Metadata(index=5, displayName="RevisionReleaseDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setRevisionReleaseDate(DateTime value);


    /**
     * The version of the item
     */
    @Size(max=255)
    @Metadata(index=4, displayName="Version")
    @FlexCDMData({"*","*.down"})
    public String getVersion();
    /**
     * The version of the item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=4, displayName="Version")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVersion(String value);


    /**
     * The date the version was released
     */
    @Metadata(index=6, displayName="VersionReleaseDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getVersionReleaseDate();
    /**
     * The date the version was released
     * Annotations:
     * <ul>
     * <li>@Metadata(index=6, displayName="VersionReleaseDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVersionReleaseDate(DateTime value);

    
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
         * Alternate Item is a Sub Structure
         */
        public static final FieldDefinition ALTERNATEITEM = new FieldDefinition("AlternateItem");
        /**
         * Approved Vendor List Item is a Sub Structure
         */
        public static final FieldDefinition APPROVEDMANUFACTURERLISTITEM = new FieldDefinition("ApprovedManufacturerListItem");
        /**
         * Approved Vendor List Item is a Sub Structure
         */
        public static final FieldDefinition APPROVEDVENDORLISTITEM = new FieldDefinition("ApprovedVendorListItem");
        /**
         * BOM (Bill of Material Element) is a Sub Structure
         */
        public static final FieldDefinition BOM = new FieldDefinition("Bom");
        /**
         * This models what business entity this part is for. In a typical system, there is customer, supplier, and manufacturer items and each is mapped to a specific business.
         */
        public static final FieldDefinition BUSINESSENTITY = new FieldDefinition("BusinessEntity");
        /**
         * Reference to the type of business. Can be ENTERPRISE or SUPPLIER.
         */
        public static final FieldDefinition BUSINESSENTITYTYPE = new FieldDefinition("BusinessEntityType");
        /**
         * Item Buyer Codes is a Sub Structure. Item buyer codes are used to designate what buyer code this item has been assigned to. The code can be site-specific, in which case the site name must be specified.
         */
        public static final FieldDefinition BUYERCODE = new FieldDefinition("BuyerCode");
        /**
         * Commodity code for this item
         */
        public static final FieldDefinition COMMODITYCODE = new FieldDefinition("CommodityCode");
        /**
         * contact information
         */
        public static final FieldDefinition CONTACTNAME = new FieldDefinition("ContactName");
        /**
         * The contacts unique identifier
         */
        public static final FieldDefinition CONTACTUNIQUEID = new FieldDefinition("ContactUniqueId");
        /**
         * The system of record for the item
         */
        public static final FieldDefinition DATASOURCE = new FieldDefinition("DataSource");
        /**
         * description of the part
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
         * Certification required (default is false)
         */
        public static final FieldDefinition ISCERTIFICATIONREQUIRED = new FieldDefinition("IsCertificationRequired");
        /**
         * Serial number required for item (default is false)
         */
        public static final FieldDefinition ISSERIALIZATIONREQUIRED = new FieldDefinition("IsSerializationRequired");
        /**
         * Is item a top level item
         */
        public static final FieldDefinition ISTOPLEVEL = new FieldDefinition("IsTopLevel");
        /**
         * classification name
         */
        public static final FieldDefinition ITEMCLASSIFICATION = new FieldDefinition("ItemClassification");
        /**
         * The part number
         */
        public static final FieldDefinition ITEMIDENTIFIER = new FieldDefinition("ItemIdentifier");
        /**
         * Item Part Type
         */
        public static final FieldDefinition ITEMPARTTYPE = new FieldDefinition("ItemPartType");
        /**
         * Item Platform is a Sub Structure. Item platform is used to designate what platform this item has been assigned to and can be assigned to more than one platform.
         */
        public static final FieldDefinition ITEMPLATFORM = new FieldDefinition("ItemPlatform");
        /**
         * External reference number or other part identifier
         */
        public static final FieldDefinition ITEMUNIQUEID = new FieldDefinition("ItemUniqueId");
        /**
         * Last time the item owner was changed
         */
        public static final FieldDefinition LASTUPDATEDATE = new FieldDefinition("LastUpdateDate");
        /**
         * Not used in upload, but used for some automation internal value for the level of the item
         */
        public static final FieldDefinition LEVEL = new FieldDefinition("Level");
        /**
         * lifecycle code. Refer to the SDD for specific codes defined.
         */
        public static final FieldDefinition LIFECYCLECODE = new FieldDefinition("LifeCycleCode");
        /**
         * If the lifeCycleCode is set to Other , provides additional information.
         */
        public static final FieldDefinition LIFECYCLECODEOTHER = new FieldDefinition("LifeCycleCodeOther");
        /**
         * Make or Buy decision
         */
        public static final FieldDefinition MAKEBUY = new FieldDefinition("MakeBuy");
        /**
         * If the makeBuy attribute is set to Other, provides a more descriptive value
         */
        public static final FieldDefinition MAKEBUYOTHER = new FieldDefinition("MakeBuyOther");
        /**
         * The group responsible for managing the item
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
         * Refers to contactUniqueIdentifier attribute of the Contact element for the owner or responsible party for the Item
         */
        public static final FieldDefinition OWNERCONTACTUNIQUEID = new FieldDefinition("OwnerContactUniqueId");
        /**
         * owner identification
         */
        public static final FieldDefinition OWNERNAME = new FieldDefinition("OwnerName");
        /**
         * nit of measure for item (gallons, inches, and so on)
         */
        public static final FieldDefinition PRODUCTUNITOFMEASURECODE = new FieldDefinition("ProductUnitOfMeasureCode");
        /**
         * Product line(s) to which the item belongs
         */
        public static final FieldDefinition PROPRIETARYPRODUCTFAMILY = new FieldDefinition("ProprietaryProductFamily");
        /**
         * Responsibility is a Sub Structure
         */
        public static final FieldDefinition RESPONSIBILITY = new FieldDefinition("Responsibility");
        /**
         * The revision of the item
         */
        public static final FieldDefinition REVISION = new FieldDefinition("Revision");
        /**
         * The date the revision was released
         */
        public static final FieldDefinition REVISIONRELEASEDATE = new FieldDefinition("RevisionReleaseDate");
        /**
         * The version of the item
         */
        public static final FieldDefinition VERSION = new FieldDefinition("Version");
        /**
         * The date the version was released
         */
        public static final FieldDefinition VERSIONRELEASEDATE = new FieldDefinition("VersionReleaseDate");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link Item.PredicateBuilder} 
     */
    public interface ItemFieldPredicateFactory<T extends Item>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereAlternateItem();
        public FieldPredicate<T,String> whereApprovedManufacturerListItem();
        public FieldPredicate<T,String> whereApprovedVendorListItem();
        public FieldPredicate<T,String> whereBom();
        public FieldPredicate<T,String> whereBusinessEntity();
        public FieldPredicate<T,String> whereBusinessEntityType();
        public FieldPredicate<T,String> whereBuyerCode();
        public FieldPredicate<T,String> whereCommodityCode();
        public FieldPredicate<T,String> whereContactName();
        public FieldPredicate<T,String> whereContactUniqueId();
        public FieldPredicate<T,String> whereDataSource();
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,DateTime> whereEffectiveFromDate();
        public FieldPredicate<T,DateTime> whereEffectiveToDate();
        public FieldPredicate<T,String> whereIsCertificationRequired();
        public FieldPredicate<T,String> whereIsSerializationRequired();
        public FieldPredicate<T,String> whereIsTopLevel();
        public FieldPredicate<T,String> whereItemClassification();
        public FieldPredicate<T,String> whereItemIdentifier();
        public FieldPredicate<T,String> whereItemPartType();
        public FieldPredicate<T,String> whereItemPlatform();
        public FieldPredicate<T,String> whereItemUniqueId();
        public FieldPredicate<T,DateTime> whereLastUpdateDate();
        public FieldPredicate<T,String> whereLevel();
        public FieldPredicate<T,String> whereLifeCycleCode();
        public FieldPredicate<T,String> whereLifeCycleCodeOther();
        public FieldPredicate<T,String> whereMakeBuy();
        public FieldPredicate<T,String> whereMakeBuyOther();
        public FieldPredicate<T,String> whereManagedBy();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> whereOwnerContactUniqueId();
        public FieldPredicate<T,String> whereOwnerName();
        public FieldPredicate<T,String> whereProductUnitOfMeasureCode();
        public FieldPredicate<T,String> whereProprietaryProductFamily();
        public FieldPredicate<T,String> whereResponsibility();
        public FieldPredicate<T,String> whereRevision();
        public FieldPredicate<T,DateTime> whereRevisionReleaseDate();
        public FieldPredicate<T,String> whereVersion();
        public FieldPredicate<T,DateTime> whereVersionReleaseDate();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link Item}
         */
        public static Item newInstance() {
            return MessageLineProxy.newInstance(Item.class);
        }
        
        /**
         * Clone a instance of {@link Item}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static Item clone(Item dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(Item.class, dataToClone);
        }
		
        /**
         * Use this method to create a field based predicate on {@link Item}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;Item&gt; pred = Item
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
        public static ItemFieldPredicateFactory<Item> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(Item.class, ItemFieldPredicateFactory.class);
        }
    }
 }