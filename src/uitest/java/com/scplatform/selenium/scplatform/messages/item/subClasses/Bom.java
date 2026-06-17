/**
 * @Bom.java@
 *
 * Created on Tue Oct 21 09:09:55 PDT 2014
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
public interface Bom extends MessageLine {

    /**
     * The type of material for the item
     */
    @Size(max=64)
    @RestrictToStrings({"DirectMaterial","IndirectMaterial","Subassembly","PhantomSubassembly","EndProduct","Kit","Setup","AsNeeded","Reference","Nontangible","Other"})
    @Metadata(index=9, displayName="BillOfMaterialTypeCode")
    @FlexCDMData({"*","*.down"})
    public String getBillOfMaterialTypeCode();
    /**
     * The type of material for the item
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"DirectMaterial","IndirectMaterial","Subassembly","PhantomSubassembly","EndProduct","Kit","Setup","AsNeeded","Reference","Nontangible","Other"})</li>
     * <li>@Metadata(index=9, displayName="BillOfMaterialTypeCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBillOfMaterialTypeCode(String value);


    /**
     * If the billOfMaterialTypeCode attribute is set to Other, use this attribute to provide a more descriptive value.
     */
    @Size(max=255)
    @Metadata(index=10, displayName="BillOfMaterialTypeCodeOther")
    @FlexCDMData({"*","*.down"})
    public String getBillOfMaterialTypeCodeOther();
    /**
     * If the billOfMaterialTypeCode attribute is set to Other, use this attribute to provide a more descriptive value.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=10, displayName="BillOfMaterialTypeCodeOther")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBillOfMaterialTypeCodeOther(String value);


    /**
     * BomLine (Bill of Material Line Attributes) is a Sub Structure
     */
    @Metadata(index=19, displayName="BomLine")
    @FlexCDMData({"*","*.down"})
    public List<BomLine> getBomLine();
    /**
     * BomLine (Bill of Material Line Attributes) is a Sub Structure
     * Annotations:
     * <ul>
     * <li>@Metadata(index=19, displayName="BomLine")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBomLine(List<BomLine> value);


    /**
     * Name of the BOM
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=0, displayName="BomName")
    @FlexCDMData({"*","*.down"})
    public String getBomName();
    /**
     * Name of the BOM
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="BomName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBomName(String value);


    /**
     * The revision of the BOM. If not set it defaults to the revision of the item
     */
    @Size(max=255)
    @Metadata(index=1, displayName="BomRevision")
    @FlexCDMData({"*","*.down"})
    public String getBomRevision();
    /**
     * The revision of the BOM. If not set it defaults to the revision of the item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=1, displayName="BomRevision")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBomRevision(String value);


    /**
     * The date the revision was releases. Defaults to the revision release date for the item.
     */
    @Metadata(index=3, displayName="BomRevisionReleaseDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getBomRevisionReleaseDate();
    /**
     * The date the revision was releases. Defaults to the revision release date for the item.
     * Annotations:
     * <ul>
     * <li>@Metadata(index=3, displayName="BomRevisionReleaseDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBomRevisionReleaseDate(DateTime value);


    /**
     * The version of the BOM. If not set it defaults to the version of the item.
     */
    @Size(max=255)
    @Metadata(index=2, displayName="BomVersion")
    @FlexCDMData({"*","*.down"})
    public String getBomVersion();
    /**
     * The version of the BOM. If not set it defaults to the version of the item.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=2, displayName="BomVersion")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBomVersion(String value);


    /**
     * The date the version was released. If not set it defaults to the version release date of the item.
     */
    @Metadata(index=4, displayName="BomVersionReleaseDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getBomVersionReleaseDate();
    /**
     * The date the version was released. If not set it defaults to the version release date of the item.
     * Annotations:
     * <ul>
     * <li>@Metadata(index=4, displayName="BomVersionReleaseDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBomVersionReleaseDate(DateTime value);


    /**
     * Description of the BOM
     */
    @Size(max=1024)
    @Metadata(index=5, displayName="Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * Description of the BOM
     * Annotations:
     * <ul>
     * <li>@Size(max=1024)</li>
     * <li>@Metadata(index=5, displayName="Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDescription(String value);


    /**
     * Effective From Date
     */
    @Metadata(index=11, displayName="EffectiveFromDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveFromDate();
    /**
     * Effective From Date
     * Annotations:
     * <ul>
     * <li>@Metadata(index=11, displayName="EffectiveFromDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveFromDate(DateTime value);


    /**
     * Effective To Date
     */
    @Metadata(index=12, displayName="EffectiveToDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveToDate();
    /**
     * Effective To Date
     * Annotations:
     * <ul>
     * <li>@Metadata(index=12, displayName="EffectiveToDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveToDate(DateTime value);


    /**
     * A flag indicating whether the BOM is a repairs BOM.
     */
    @Size(max=64)
    @RestrictToStrings({"true","false"})
    @Metadata(index=18, displayName="IsRepairs")
    @FlexCDMData({"*","*.down"})
    public String getIsRepairs();
    /**
     * A flag indicating whether the BOM is a repairs BOM.
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"true","false"})</li>
     * <li>@Metadata(index=18, displayName="IsRepairs")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setIsRepairs(String value);


    /**
     * Last changed by
     */
    @Size(max=255)
    @Metadata(index=15, displayName="LastStateChangeBy")
    @FlexCDMData({"*","*.down"})
    public String getLastStateChangeBy();
    /**
     * Last changed by
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=15, displayName="LastStateChangeBy")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLastStateChangeBy(String value);


    /**
     * The date change when the state was changed
     */
    @Metadata(index=16, displayName="LastStateChangeOn")
    @FlexCDMData({"*","*.down"})
    public DateTime getLastStateChangeOn();
    /**
     * The date change when the state was changed
     * Annotations:
     * <ul>
     * <li>@Metadata(index=16, displayName="LastStateChangeOn")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLastStateChangeOn(DateTime value);


    /**
     * The date last change was made
     */
    @NotNull
    @Metadata(index=17, displayName="LastUpdateDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getLastUpdateDate();
    /**
     * The date last change was made
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=17, displayName="LastUpdateDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLastUpdateDate(DateTime value);


    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
     * A=Add
     * C=Changed
     * D=Delete
     * U=Unchanged
     */
    @Size(max=64)
    @RestrictToStrings({"A","C","D","U"})
    @Metadata(index=13, displayName="OperationCode")
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
     * <li>@Metadata(index=13, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    /**
     * Refers to contactUniqueIdentifier attribute of the Contact element for the owner or responsible party for the Item
     */
    @Size(max=255)
    @Metadata(index=7, displayName="OwnerContactUniqueIdentifier")
    @FlexCDMData({"*","*.down"})
    public String getOwnerContactUniqueIdentifier();
    /**
     * Refers to contactUniqueIdentifier attribute of the Contact element for the owner or responsible party for the Item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=7, displayName="OwnerContactUniqueIdentifier")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOwnerContactUniqueIdentifier(String value);


    /**
     * Owner
     */
    @Size(max=255)
    @Metadata(index=6, displayName="OwnerName")
    @FlexCDMData({"*","*.down"})
    public String getOwnerName();
    /**
     * Owner
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=6, displayName="OwnerName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOwnerName(String value);


    /**
     * A site for the Business Entity
     */
    @Size(max=255)
    @Metadata(index=8, displayName="Site")
    @FlexCDMData({"*","*.down"})
    public String getSite();
    /**
     * A site for the Business Entity
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=8, displayName="Site")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite(String value);


    /**
     * The state of the BOM. "APPROVED" and "CLOSED"
     */
    @NotNull
    @Size(max=64)
    @RestrictToStrings({"APPROVED","CLOSED"})
    @Metadata(index=14, displayName="State")
    @FlexCDMData({"*","*.down"})
    public String getState();
    /**
     * The state of the BOM. "APPROVED" and "CLOSED"
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"APPROVED","CLOSED"})</li>
     * <li>@Metadata(index=14, displayName="State")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setState(String value);

    
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
         * The type of material for the item
         */
        public static final FieldDefinition BILLOFMATERIALTYPECODE = new FieldDefinition("BillOfMaterialTypeCode");
        /**
         * If the billOfMaterialTypeCode attribute is set to Other, use this attribute to provide a more descriptive value.
         */
        public static final FieldDefinition BILLOFMATERIALTYPECODEOTHER = new FieldDefinition("BillOfMaterialTypeCodeOther");
        /**
         * BomLine (Bill of Material Line Attributes) is a Sub Structure
         */
        public static final FieldDefinition BOMLINE = new FieldDefinition("BomLine");
        /**
         * Name of the BOM
         */
        public static final FieldDefinition BOMNAME = new FieldDefinition("BomName");
        /**
         * The revision of the BOM. If not set it defaults to the revision of the item
         */
        public static final FieldDefinition BOMREVISION = new FieldDefinition("BomRevision");
        /**
         * The date the revision was releases. Defaults to the revision release date for the item.
         */
        public static final FieldDefinition BOMREVISIONRELEASEDATE = new FieldDefinition("BomRevisionReleaseDate");
        /**
         * The version of the BOM. If not set it defaults to the version of the item.
         */
        public static final FieldDefinition BOMVERSION = new FieldDefinition("BomVersion");
        /**
         * The date the version was released. If not set it defaults to the version release date of the item.
         */
        public static final FieldDefinition BOMVERSIONRELEASEDATE = new FieldDefinition("BomVersionReleaseDate");
        /**
         * Description of the BOM
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
         * A flag indicating whether the BOM is a repairs BOM.
         */
        public static final FieldDefinition ISREPAIRS = new FieldDefinition("IsRepairs");
        /**
         * Last changed by
         */
        public static final FieldDefinition LASTSTATECHANGEBY = new FieldDefinition("LastStateChangeBy");
        /**
         * The date change when the state was changed
         */
        public static final FieldDefinition LASTSTATECHANGEON = new FieldDefinition("LastStateChangeOn");
        /**
         * The date last change was made
         */
        public static final FieldDefinition LASTUPDATEDATE = new FieldDefinition("LastUpdateDate");
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
        public static final FieldDefinition OWNERCONTACTUNIQUEIDENTIFIER = new FieldDefinition("OwnerContactUniqueIdentifier");
        /**
         * Owner
         */
        public static final FieldDefinition OWNERNAME = new FieldDefinition("OwnerName");
        /**
         * A site for the Business Entity
         */
        public static final FieldDefinition SITE = new FieldDefinition("Site");
        /**
         * The state of the BOM. "APPROVED" and "CLOSED"
         */
        public static final FieldDefinition STATE = new FieldDefinition("State");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link Bom.PredicateBuilder} 
     */
    public interface BomFieldPredicateFactory<T extends Bom>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereBillOfMaterialTypeCode();
        public FieldPredicate<T,String> whereBillOfMaterialTypeCodeOther();
        public FieldPredicate<T,String> whereBomLine();
        public FieldPredicate<T,String> whereBomName();
        public FieldPredicate<T,String> whereBomRevision();
        public FieldPredicate<T,DateTime> whereBomRevisionReleaseDate();
        public FieldPredicate<T,String> whereBomVersion();
        public FieldPredicate<T,DateTime> whereBomVersionReleaseDate();
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,DateTime> whereEffectiveFromDate();
        public FieldPredicate<T,DateTime> whereEffectiveToDate();
        public FieldPredicate<T,String> whereIsRepairs();
        public FieldPredicate<T,String> whereLastStateChangeBy();
        public FieldPredicate<T,DateTime> whereLastStateChangeOn();
        public FieldPredicate<T,DateTime> whereLastUpdateDate();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> whereOwnerContactUniqueIdentifier();
        public FieldPredicate<T,String> whereOwnerName();
        public FieldPredicate<T,String> whereSite();
        public FieldPredicate<T,String> whereState();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link Bom}
         */
        public static Bom newInstance() {
            return MessageLineProxy.newInstance(Bom.class);
        }
        
        /**
         * Clone a instance of {@link Bom}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static Bom clone(Bom dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(Bom.class, dataToClone);
        }
		
        /**
         * Use this method to create a field based predicate on {@link Bom}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;Bom&gt; pred = Bom
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
        public static BomFieldPredicateFactory<Bom> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(Bom.class, BomFieldPredicateFactory.class);
        }
    }
 }