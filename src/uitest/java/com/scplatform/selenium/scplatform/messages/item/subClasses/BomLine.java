/**
 * @BomLine.java@
 *
 * Created on Tue Oct 21 09:09:41 PDT 2014
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
 * <b>SPEC:</b> http://confluence.dev.scplatform.local/display/PUBT/Item
 * 
 *
 */
@Generated("IrisCodeGenerator")
public interface BomLine extends MessageLine {

    /**
     * Approved Manufacturer List Item is a Sub Structure
     */
    @Metadata(index=20, displayName="ApprovedManufacturerListItem")
    @FlexCDMData({"*","*.down"})
    public List<ApprovedManufacturerListItem> getApprovedManufacturerListItem();
    /**
     * Approved Manufacturer List Item is a Sub Structure
     * Annotations:
     * <ul>
     * <li>@Metadata(index=20, displayName="ApprovedManufacturerListItem")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setApprovedManufacturerListItem(List<ApprovedManufacturerListItem> value);


    /**
     * Approved Vendor List Item is a Sub Structure
     */
    @Metadata(index=19, displayName="ApprovedVendorListItem")
    @FlexCDMData({"*","*.down"})
    public List<ApprovedVendorListItem> getApprovedVendorListItem();
    /**
     * Approved Vendor List Item is a Sub Structure
     * Annotations:
     * <ul>
     * <li>@Metadata(index=19, displayName="ApprovedVendorListItem")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setApprovedVendorListItem(List<ApprovedVendorListItem> value);


    /**
     * The type of material for the item
     */
    @Size(max=64)
    @RestrictToStrings({"DirectMaterial","IndirectMaterial","Subassembly","PhantomSubassembly","EndProduct","Kit","Setup","AsNeeded","Reference","Nontangible","Other"})
    @Metadata(index=8, displayName="BillOfMaterialTypeCode")
    @FlexCDMData({"*","*.down"})
    public String getBillOfMaterialTypeCode();
    /**
     * The type of material for the item
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"DirectMaterial","IndirectMaterial","Subassembly","PhantomSubassembly","EndProduct","Kit","Setup","AsNeeded","Reference","Nontangible","Other"})</li>
     * <li>@Metadata(index=8, displayName="BillOfMaterialTypeCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBillOfMaterialTypeCode(String value);


    /**
     * If the billOfMaterialTypeCode attribute is set to Other, use this attribute to provide a more descriptive value.
     */
    @Size(max=255)
    @Metadata(index=9, displayName="BillOfMaterialTypeCodeOther")
    @FlexCDMData({"*","*.down"})
    public String getBillOfMaterialTypeCodeOther();
    /**
     * If the billOfMaterialTypeCode attribute is set to Other, use this attribute to provide a more descriptive value.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=9, displayName="BillOfMaterialTypeCodeOther")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBillOfMaterialTypeCodeOther(String value);


    /**
     * The business entity of the BOM Line item. If no business entity is defined, the business entity associated with the BOM is used.
     */
    @Size(max=255)
    @Metadata(index=4, displayName="BusinessEntity")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntity();
    /**
     * The business entity of the BOM Line item. If no business entity is defined, the business entity associated with the BOM is used.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=4, displayName="BusinessEntity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntity(String value);


    /**
     * The type of business
     */
    @NotNull
    @Size(max=64)
    @RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})
    @Metadata(index=5, displayName="BusinessEntityType")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntityType();
    /**
     * The type of business
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})</li>
     * <li>@Metadata(index=5, displayName="BusinessEntityType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntityType(String value);


    /**
     * description of the BOM Line
     */
    @Size(max=255)
    @Metadata(index=14, displayName="Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * description of the BOM Line
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=14, displayName="Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDescription(String value);


    /**
     * From Date
     */
    @Metadata(index=16, displayName="EffectiveFromDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveFromDate();
    /**
     * From Date
     * Annotations:
     * <ul>
     * <li>@Metadata(index=16, displayName="EffectiveFromDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveFromDate(DateTime value);


    /**
     * to Date
     */
    @Metadata(index=17, displayName="EffectiveToDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveToDate();
    /**
     * to Date
     * Annotations:
     * <ul>
     * <li>@Metadata(index=17, displayName="EffectiveToDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveToDate(DateTime value);


//    /**
//     * Serial number required for item (default is false)
//     */
//    @Size(max=64)
//    @RestrictToStrings({"true","false"})
//    @Metadata(index=7, displayName="IsSerializationRequired")
//    @FlexCDMData({"*","*.down"})
//    public String getIsSerializationRequired();
//    /**
//     * Serial number required for item (default is false)
//     * Annotations:
//     * <ul>
//     * <li>@Size(max=64)</li>
//     * <li>@RestrictToStrings({"true","false"})</li>
//     * <li>@Metadata(index=7, displayName="IsSerializationRequired")</li>
//     * <li>@FlexCDMData({"*","*.down"})</li>
//     * </ul>
//     */
//    public void setIsSerializationRequired(String value);


    /**
     * Reference to the item this BOM Line represents
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=0, displayName="ItemIdentifier")
    @FlexCDMData({"*","*.down"})
    public String getItemIdentifier();
    /**
     * Reference to the item this BOM Line represents
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
     * Number used in this BOM
     */
    @Size(max=126)
    @Metadata(index=11, displayName="ItemQuantity")
    @FlexCDMData({"*","*.down"})
    public float getItemQuantity();
    /**
     * Number used in this BOM
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=11, displayName="ItemQuantity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemQuantity(float value);


    /**
     * The revision of the item
     */
    @Size(max=255)
    @Metadata(index=2, displayName="ItemRevision")
    @FlexCDMData({"*","*.down"})
    public String getItemRevision();
    /**
     * The revision of the item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=2, displayName="ItemRevision")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemRevision(String value);


    /**
     * Reference to the unique id for the part in the data source
     */
    @Size(max=255)
    @Metadata(index=1, displayName="ItemUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getItemUniqueId();
    /**
     * Reference to the unique id for the part in the data source
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=1, displayName="ItemUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemUniqueId(String value);


    /**
     * The version of the item
     */
    @Size(max=255)
    @Metadata(index=3, displayName="ItemVersion")
    @FlexCDMData({"*","*.down"})
    public String getItemVersion();
    /**
     * The version of the item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=3, displayName="ItemVersion")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemVersion(String value);


    /**
     * The group responsible for managing the item
     */
    @Size(max=255)
    @Metadata(index=6, displayName="ManagedBy")
    @FlexCDMData({"*","*.down"})
    public String getManagedBy();
    /**
     * The group responsible for managing the item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=6, displayName="ManagedBy")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setManagedBy(String value);


    /**
     * Notes related to the BOM Line
     */
    @Size(max=255)
    @Metadata(index=10, displayName="Notes")
    @FlexCDMData({"*","*.down"})
    public String getNotes();
    /**
     * Notes related to the BOM Line
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=10, displayName="Notes")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setNotes(String value);


    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
     * A=Add
     * C=Changed
     * D=Delete
     * U=Unchanged
     */
    @Size(max=64)
    @RestrictToStrings({"A","C","D","U"})
    @Metadata(index=18, displayName="OperationCode")
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
     * <li>@Metadata(index=18, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    /**
     * Product type code
     */
    @Size(max=64)
    @RestrictToStrings({"PerAssembly","PerSetup","AsNeeded","Shrinkage","Other"})
    @Metadata(index=12, displayName="ProductQuantityTypeCode")
    @FlexCDMData({"*","*.down"})
    public String getProductQuantityTypeCode();
    /**
     * Product type code
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"PerAssembly","PerSetup","AsNeeded","Shrinkage","Other"})</li>
     * <li>@Metadata(index=12, displayName="ProductQuantityTypeCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setProductQuantityTypeCode(String value);


    /**
     * If the productQuantityTypeCode attribute is set to Other, use this attribute to provide a more descriptive value.
     */
    @Size(max=255)
    @Metadata(index=13, displayName="ProductQuantityTypeCodeOther")
    @FlexCDMData({"*","*.down"})
    public String getProductQuantityTypeCodeOther();
    /**
     * If the productQuantityTypeCode attribute is set to Other, use this attribute to provide a more descriptive value.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=13, displayName="ProductQuantityTypeCodeOther")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setProductQuantityTypeCodeOther(String value);


    /**
     * sequence for this line. If present, it is used to order the lines within the BOM.
     */
    @Metadata(index=15, displayName="ProprietarySequenceIdentifier")
    @FlexCDMData({"*","*.down"})
    public int getProprietarySequenceIdentifier();
    /**
     * sequence for this line. If present, it is used to order the lines within the BOM.
     * Annotations:
     * <ul>
     * <li>@Metadata(index=15, displayName="ProprietarySequenceIdentifier")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setProprietarySequenceIdentifier(int value);

    
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
         * Approved Vendor List Item is a Sub Structure
         */
        public static final FieldDefinition APPROVEDMANUFACTURERLISTITEM = new FieldDefinition("ApprovedManufacturerListItem");
        /**
         * Approved Vendor List Item is a Sub Structure
         */
        public static final FieldDefinition APPROVEDVENDORLISTITEM = new FieldDefinition("ApprovedVendorListItem");
        /**
         * The type of material for the item
         */
        public static final FieldDefinition BILLOFMATERIALTYPECODE = new FieldDefinition("BillOfMaterialTypeCode");
        /**
         * If the billOfMaterialTypeCode attribute is set to Other, use this attribute to provide a more descriptive value.
         */
        public static final FieldDefinition BILLOFMATERIALTYPECODEOTHER = new FieldDefinition("BillOfMaterialTypeCodeOther");
        /**
         * The business entity of the BOM Line item. If no business entity is defined, the business entity associated with the BOM is used.
         */
        public static final FieldDefinition BUSINESSENTITY = new FieldDefinition("BusinessEntity");
        /**
         * The type of business
         */
        public static final FieldDefinition BUSINESSENTITYTYPE = new FieldDefinition("BusinessEntityType");
        /**
         * description of the BOM Line
         */
        public static final FieldDefinition DESCRIPTION = new FieldDefinition("Description");
        /**
         * From Date
         */
        public static final FieldDefinition EFFECTIVEFROMDATE = new FieldDefinition("EffectiveFromDate");
        /**
         * to Date
         */
        public static final FieldDefinition EFFECTIVETODATE = new FieldDefinition("EffectiveToDate");
        /**
         * Serial number required for item (default is false)
         */
//        public static final FieldDefinition ISSERIALIZATIONREQUIRED = new FieldDefinition("IsSerializationRequired");
        /**
         * Reference to the item this BOM Line represents
         */
        public static final FieldDefinition ITEMIDENTIFIER = new FieldDefinition("ItemIdentifier");
        /**
         * Number used in this BOM
         */
        public static final FieldDefinition ITEMQUANTITY = new FieldDefinition("ItemQuantity");
        /**
         * The revision of the item
         */
        public static final FieldDefinition ITEMREVISION = new FieldDefinition("ItemRevision");
        /**
         * Reference to the unique id for the part in the data source
         */
        public static final FieldDefinition ITEMUNIQUEID = new FieldDefinition("ItemUniqueId");
        /**
         * The version of the item
         */
        public static final FieldDefinition ITEMVERSION = new FieldDefinition("ItemVersion");
        /**
         * The group responsible for managing the item
         */
        public static final FieldDefinition MANAGEDBY = new FieldDefinition("ManagedBy");
        /**
         * Notes related to the BOM Line
         */
        public static final FieldDefinition NOTES = new FieldDefinition("Notes");
        /**
         * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
         * A=Add
         * C=Changed
         * D=Delete
         * U=Unchanged
         */
        public static final FieldDefinition OPERATIONCODE = new FieldDefinition("OperationCode");
        /**
         * Product type code
         */
        public static final FieldDefinition PRODUCTQUANTITYTYPECODE = new FieldDefinition("ProductQuantityTypeCode");
        /**
         * If the productQuantityTypeCode attribute is set to Other, use this attribute to provide a more descriptive value.
         */
        public static final FieldDefinition PRODUCTQUANTITYTYPECODEOTHER = new FieldDefinition("ProductQuantityTypeCodeOther");
        /**
         * sequence for this line. If present, it is used to order the lines within the BOM.
         */
        public static final FieldDefinition PROPRIETARYSEQUENCEIDENTIFIER = new FieldDefinition("ProprietarySequenceIdentifier");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link BomLine.PredicateBuilder} 
     */
    public interface BomLineFieldPredicateFactory<T extends BomLine>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereApprovedManufacturerListItem();
        public FieldPredicate<T,String> whereApprovedVendorListItem();
        public FieldPredicate<T,String> whereBillOfMaterialTypeCode();
        public FieldPredicate<T,String> whereBillOfMaterialTypeCodeOther();
        public FieldPredicate<T,String> whereBusinessEntity();
        public FieldPredicate<T,String> whereBusinessEntityType();
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,DateTime> whereEffectiveFromDate();
        public FieldPredicate<T,DateTime> whereEffectiveToDate();
//        public FieldPredicate<T,String> whereIsSerializationRequired();
        public FieldPredicate<T,String> whereItemIdentifier();
        public FieldPredicate<T,Float> whereItemQuantity();
        public FieldPredicate<T,String> whereItemRevision();
        public FieldPredicate<T,String> whereItemUniqueId();
        public FieldPredicate<T,String> whereItemVersion();
        public FieldPredicate<T,String> whereManagedBy();
        public FieldPredicate<T,String> whereNotes();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> whereProductQuantityTypeCode();
        public FieldPredicate<T,String> whereProductQuantityTypeCodeOther();
        public FieldPredicate<T,Integer> whereProprietarySequenceIdentifier();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link BomLine}
         */
        public static BomLine newInstance() {
            return MessageLineProxy.newInstance(BomLine.class);
        }
        
        /**
         * Clone a instance of {@link BomLine}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static BomLine clone(BomLine dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(BomLine.class, dataToClone);
        }
		
        /**
         * Use this method to create a field based predicate on {@link BomLine}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;BomLine&gt; pred = BomLine
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
        public static BomLineFieldPredicateFactory<BomLine> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(BomLine.class, BomLineFieldPredicateFactory.class);
        }
    }
 }