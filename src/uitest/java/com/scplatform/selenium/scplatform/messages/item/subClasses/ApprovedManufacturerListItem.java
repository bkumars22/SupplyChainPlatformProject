/**
 * @ApprovedManufacturerListItem.java@
 *
 * Created on Tue Oct 21 09:09:09 PDT 2014
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
 * 
 *
 */
@Generated("IrisCodeGenerator")
public interface ApprovedManufacturerListItem extends MessageLine {

    /**
     * The manufacturers reference notes
     */
    @Size(max=255)
    @Metadata(index=1, displayName="Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * The manufacturers reference notes
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=1, displayName="Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDescription(String value);


    /**
     * The business entity that manufactured the part
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=2, displayName="ManufacturerBusinessEntity")
    @FlexCDMData({"*","*.down"})
    public String getManufacturerBusinessEntity();
    /**
     * The business entity that manufactured the part
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=2, displayName="ManufacturerBusinessEntity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setManufacturerBusinessEntity(String value);


    /**
     * Reference to the type of business. Should be MANUFACTURER
     */
    @NotNull
    @Size(max=64)
    @RestrictToStrings({"MANUFACTURER"})
    @Metadata(index=3, displayName="ManufacturerBusinessEntityType")
    @FlexCDMData({"*","*.down"})
    public String getManufacturerBusinessEntityType();
    /**
     * Reference to the type of business. Should be MANUFACTURER
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"MANUFACTURER"})</li>
     * <li>@Metadata(index=3, displayName="ManufacturerBusinessEntityType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setManufacturerBusinessEntityType(String value);


    /**
     * Reference to the manufacturers contact name
     */
    @Size(max=255)
    @Metadata(index=8, displayName="ManufacturerContactName")
    @FlexCDMData({"*","*.down"})
    public String getManufacturerContactName();
    /**
     * Reference to the manufacturers contact name
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=8, displayName="ManufacturerContactName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setManufacturerContactName(String value);


    /**
     * Reference to the manufacturers contact unique id
     */
    @Size(max=255)
    @Metadata(index=9, displayName="ManufacturerContactUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getManufacturerContactUniqueId();
    /**
     * Reference to the manufacturers contact unique id
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=9, displayName="ManufacturerContactUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setManufacturerContactUniqueId(String value);


    /**
     * The manufacturers part number
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=4, displayName="ManufacturerItemIdentifier")
    @FlexCDMData({"*","*.down"})
    public String getManufacturerItemIdentifier();
    /**
     * The manufacturers part number
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=4, displayName="ManufacturerItemIdentifier")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setManufacturerItemIdentifier(String value);


    /**
     * The manufacturers external reference number or other part identifier
     */
    @Size(max=255)
    @Metadata(index=5, displayName="ManufacturerItemUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getManufacturerItemUniqueId();
    /**
     * The manufacturers external reference number or other part identifier
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=5, displayName="ManufacturerItemUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setManufacturerItemUniqueId(String value);


    /**
     * The manufacturers item revision
     */
    @Size(max=255)
    @Metadata(index=6, displayName="ManufacturerRevision")
    @FlexCDMData({"*","*.down"})
    public String getManufacturerRevision();
    /**
     * The manufacturers item revision
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=6, displayName="ManufacturerRevision")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setManufacturerRevision(String value);


    /**
     * The manufacturers item version
     */
    @Size(max=255)
    @Metadata(index=7, displayName="ManufacturerVersion")
    @FlexCDMData({"*","*.down"})
    public String getManufacturerVersion();
    /**
     * The manufacturers item version
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=7, displayName="ManufacturerVersion")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setManufacturerVersion(String value);


    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
     * A=Add
     * C=Changed
     * D=Delete
     * U=Unchanged
     */
    @Size(max=64)
    @RestrictToStrings({"A","C","D","U"})
    @Metadata(index=15, displayName="OperationCode")
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
     * <li>@Metadata(index=15, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    /**
     * Manufacturers part status
     */
    @Size(max=64)
    @RestrictToStrings({"Approved","QualityHold","UnderQualification","Unqualified","Disqualified","Nonpreferred","Conditional","Other"})
    @Metadata(index=10, displayName="PartStatusCode")
    @FlexCDMData({"*","*.down"})
    public String getPartStatusCode();
    /**
     * Manufacturers part status
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"Approved","QualityHold","UnderQualification","Unqualified","Disqualified","Nonpreferred","Conditional","Other"})</li>
     * <li>@Metadata(index=10, displayName="PartStatusCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPartStatusCode(String value);


    /**
     * If the partStatusCode is set to Other, use this attribute to provide a more descriptive value.
     */
    @Size(max=255)
    @Metadata(index=11, displayName="PartStatusCodeOther")
    @FlexCDMData({"*","*.down"})
    public String getPartStatusCodeOther();
    /**
     * If the partStatusCode is set to Other, use this attribute to provide a more descriptive value.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=11, displayName="PartStatusCodeOther")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPartStatusCodeOther(String value);


    /**
     * Preferred status code
     */
    @Size(max=255)
    @Metadata(index=12, displayName="PreferredStatusCode")
    @FlexCDMData({"*","*.down"})
    public String getPreferredStatusCode();
    /**
     * Preferred status code
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=12, displayName="PreferredStatusCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPreferredStatusCode(String value);


    /**
     * The end date if the manufacturer is preferred
     */
    @Metadata(index=14, displayName="PreferredStatusEndDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getPreferredStatusEndDate();
    /**
     * The end date if the manufacturer is preferred
     * Annotations:
     * <ul>
     * <li>@Metadata(index=14, displayName="PreferredStatusEndDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPreferredStatusEndDate(DateTime value);


    /**
     * The start date if the manufacturer is preferred
     */
    @Metadata(index=13, displayName="PreferredStatusStartDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getPreferredStatusStartDate();
    /**
     * The start date if the manufacturer is preferred
     * Annotations:
     * <ul>
     * <li>@Metadata(index=13, displayName="PreferredStatusStartDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPreferredStatusStartDate(DateTime value);


    /**
     * A site for the manufacturer
     */
    @Size(max=255)
    @Metadata(index=0, displayName="Site")
    @FlexCDMData({"*","*.down"})
    public String getSite();
    /**
     * A site for the manufacturer
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="Site")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite(String value);

    
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
         * The manufacturers reference notes
         */
        public static final FieldDefinition DESCRIPTION = new FieldDefinition("Description");
        /**
         * The business entity that manufactured the part
         */
        public static final FieldDefinition MANUFACTURERBUSINESSENTITY = new FieldDefinition("ManufacturerBusinessEntity");
        /**
         * Reference to the type of business. Should be MANUFACTURER
         */
        public static final FieldDefinition MANUFACTURERBUSINESSENTITYTYPE = new FieldDefinition("ManufacturerBusinessEntityType");
        /**
         * Reference to the manufacturers contact name
         */
        public static final FieldDefinition MANUFACTURERCONTACTNAME = new FieldDefinition("ManufacturerContactName");
        /**
         * Reference to the manufacturers contact unique id
         */
        public static final FieldDefinition MANUFACTURERCONTACTUNIQUEID = new FieldDefinition("ManufacturerContactUniqueId");
        /**
         * The manufacturers part number
         */
        public static final FieldDefinition MANUFACTURERITEMIDENTIFIER = new FieldDefinition("ManufacturerItemIdentifier");
        /**
         * The manufacturers external reference number or other part identifier
         */
        public static final FieldDefinition MANUFACTURERITEMUNIQUEID = new FieldDefinition("ManufacturerItemUniqueId");
        /**
         * The manufacturers item revision
         */
        public static final FieldDefinition MANUFACTURERREVISION = new FieldDefinition("ManufacturerRevision");
        /**
         * The manufacturers item version
         */
        public static final FieldDefinition MANUFACTURERVERSION = new FieldDefinition("ManufacturerVersion");
        /**
         * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
         * A=Add
         * C=Changed
         * D=Delete
         * U=Unchanged
         */
        public static final FieldDefinition OPERATIONCODE = new FieldDefinition("OperationCode");
        /**
         * Manufacturers part status
         */
        public static final FieldDefinition PARTSTATUSCODE = new FieldDefinition("PartStatusCode");
        /**
         * If the partStatusCode is set to Other, use this attribute to provide a more descriptive value.
         */
        public static final FieldDefinition PARTSTATUSCODEOTHER = new FieldDefinition("PartStatusCodeOther");
        /**
         * Preferred status code
         */
        public static final FieldDefinition PREFERREDSTATUSCODE = new FieldDefinition("PreferredStatusCode");
        /**
         * The end date if the manufacturer is preferred
         */
        public static final FieldDefinition PREFERREDSTATUSENDDATE = new FieldDefinition("PreferredStatusEndDate");
        /**
         * The start date if the manufacturer is preferred
         */
        public static final FieldDefinition PREFERREDSTATUSSTARTDATE = new FieldDefinition("PreferredStatusStartDate");
        /**
         * A site for the manufacturer
         */
        public static final FieldDefinition SITE = new FieldDefinition("Site");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link ApprovedManufacturerListItem.PredicateBuilder} 
     */
    public interface ApprovedManufacturerListItemFieldPredicateFactory<T extends ApprovedManufacturerListItem>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,String> whereManufacturerBusinessEntity();
        public FieldPredicate<T,String> whereManufacturerBusinessEntityType();
        public FieldPredicate<T,String> whereManufacturerContactName();
        public FieldPredicate<T,String> whereManufacturerContactUniqueId();
        public FieldPredicate<T,String> whereManufacturerItemIdentifier();
        public FieldPredicate<T,String> whereManufacturerItemUniqueId();
        public FieldPredicate<T,String> whereManufacturerRevision();
        public FieldPredicate<T,String> whereManufacturerVersion();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> wherePartStatusCode();
        public FieldPredicate<T,String> wherePartStatusCodeOther();
        public FieldPredicate<T,String> wherePreferredStatusCode();
        public FieldPredicate<T,DateTime> wherePreferredStatusEndDate();
        public FieldPredicate<T,DateTime> wherePreferredStatusStartDate();
        public FieldPredicate<T,String> whereSite();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link ApprovedManufacturerListItem}
         */
        public static ApprovedManufacturerListItem newInstance() {
            return MessageLineProxy.newInstance(ApprovedManufacturerListItem.class);
        }
        
        /**
         * Clone a instance of {@link ApprovedManufacturerListItem}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static ApprovedManufacturerListItem clone(ApprovedManufacturerListItem dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(ApprovedManufacturerListItem.class, dataToClone);
        }
		
        /**
         * Use this method to create a field based predicate on {@link ApprovedManufacturerListItem}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;ApprovedManufacturerListItem&gt; pred = ApprovedManufacturerListItem
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
        public static ApprovedManufacturerListItemFieldPredicateFactory<ApprovedManufacturerListItem> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(ApprovedManufacturerListItem.class, ApprovedManufacturerListItemFieldPredicateFactory.class);
        }
    }
 }