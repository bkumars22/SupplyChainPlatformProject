/**
 * @ApprovedVendorListItem.java@
 *
 * Created on Tue Oct 21 09:09:26 PDT 2014
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
public interface ApprovedVendorListItem extends MessageLine {

    /**
     * The business entitys reference notes
     */
    @Size(max=1024)
    @Metadata(index=1, displayName="Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * The business entitys reference notes
     * Annotations:
     * <ul>
     * <li>@Size(max=1024)</li>
     * <li>@Metadata(index=1, displayName="Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDescription(String value);


    /**
     * The last time the AVL was updated
     */
    @Metadata(index=16, displayName="LastUpdateDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getLastUpdateDate();
    /**
     * The last time the AVL was updated
     * Annotations:
     * <ul>
     * <li>@Metadata(index=16, displayName="LastUpdateDate")</li>
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
     * Vendors part status
     */
    @Size(max=64)
    @RestrictToStrings({"Approved","QualityHold","UnderQualification","Unqualified","Disqualified","Nonpreferred","Conditional","Other"})
    @Metadata(index=10, displayName="PartStatusCode")
    @FlexCDMData({"*","*.down"})
    public String getPartStatusCode();
    /**
     * Vendors part status
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
     * The end date if the vendor is preferred
     */
    @Metadata(index=14, displayName="PreferredStatusEndDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getPreferredStatusEndDate();
    /**
     * The end date if the vendor is preferred
     * Annotations:
     * <ul>
     * <li>@Metadata(index=14, displayName="PreferredStatusEndDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPreferredStatusEndDate(DateTime value);


    /**
     * The start date if the vendor is preferred
     */
    @Metadata(index=13, displayName="PreferredStatusStartDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getPreferredStatusStartDate();
    /**
     * The start date if the vendor is preferred
     * Annotations:
     * <ul>
     * <li>@Metadata(index=13, displayName="PreferredStatusStartDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPreferredStatusStartDate(DateTime value);


    /**
     * A site for the vendor.  Use Site description here.
     */
    @Size(max=255)
    @Metadata(index=0, displayName="Site")
    @FlexCDMData({"*","*.down"})
    public String getSite();
    /**
     * A site for the vendor.  Use Site description here.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="Site")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite(String value);


    /**
     * The site value is the site description. When loading, the site name is shown on the UI.  This field is to hold the site name value for verification.
     */
    @Size(max=255)
    @Metadata(index=17, displayName="SiteName")
    @FlexCDMData({"*","*.down"})
    public String getSiteName();
    /**
     * The site value is the site description. When loading, the site name is shown on the UI.  This field is to hold the site name value for verification.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=17, displayName="SiteName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSiteName(String value);


    /**
     * The business entity supplying the part
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=2, displayName="VendorBusinessEntity")
    @FlexCDMData({"*","*.down"})
    public String getVendorBusinessEntity();
    /**
     * The business entity supplying the part
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=2, displayName="VendorBusinessEntity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVendorBusinessEntity(String value);


    /**
     * Reference to the type of business. Should be VENDOR or SUPPLIER.
     */
    @NotNull
    @Size(max=64)
    @RestrictToStrings({"SUPPLIER"})
    @Metadata(index=3, displayName="VendorBusinessEntityType")
    @FlexCDMData({"*","*.down"})
    public String getVendorBusinessEntityType();
    /**
     * Reference to the type of business. Should be VENDOR or SUPPLIER.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"SUPPLIER"})</li>
     * <li>@Metadata(index=3, displayName="VendorBusinessEntityType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVendorBusinessEntityType(String value);


    /**
     * Reference to the vendors contact name
     */
    @Size(max=255)
    @Metadata(index=8, displayName="VendorContactName")
    @FlexCDMData({"*","*.down"})
    public String getVendorContactName();
    /**
     * Reference to the vendors contact name
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=8, displayName="VendorContactName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVendorContactName(String value);


    /**
     * Reference to the vendors contact unique id
     */
    @Size(max=255)
    @Metadata(index=9, displayName="VendorContactUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getVendorContactUniqueId();
    /**
     * Reference to the vendors contact unique id
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=9, displayName="VendorContactUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVendorContactUniqueId(String value);


    /**
     * The vendors external reference number or other part identifier
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=4, displayName="VendorItemIdentifier")
    @FlexCDMData({"*","*.down"})
    public String getVendorItemIdentifier();
    /**
     * The vendors external reference number or other part identifier
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=4, displayName="VendorItemIdentifier")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVendorItemIdentifier(String value);


    /**
     * The vendors external reference number or other part identifier
     */
    @Size(max=255)
    @Metadata(index=5, displayName="VendorItemUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getVendorItemUniqueId();
    /**
     * The vendors external reference number or other part identifier
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=5, displayName="VendorItemUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVendorItemUniqueId(String value);


    /**
     * The vendors item revision
     */
    @Size(max=255)
    @Metadata(index=6, displayName="VendorRevision")
    @FlexCDMData({"*","*.down"})
    public String getVendorRevision();
    /**
     * The vendors item revision
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=6, displayName="VendorRevision")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVendorRevision(String value);


    /**
     * The vendors item version
     */
    @Size(max=255)
    @Metadata(index=7, displayName="VendorVersion")
    @FlexCDMData({"*","*.down"})
    public String getVendorVersion();
    /**
     * The vendors item version
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=7, displayName="VendorVersion")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVendorVersion(String value);

    
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
         * The business entitys reference notes
         */
        public static final FieldDefinition DESCRIPTION = new FieldDefinition("Description");
        /**
         * The last time the AVL was updated
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
         * Vendors part status
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
         * The end date if the vendor is preferred
         */
        public static final FieldDefinition PREFERREDSTATUSENDDATE = new FieldDefinition("PreferredStatusEndDate");
        /**
         * The start date if the vendor is preferred
         */
        public static final FieldDefinition PREFERREDSTATUSSTARTDATE = new FieldDefinition("PreferredStatusStartDate");
        /**
         * A site for the vendor.  Use Site description here.
         */
        public static final FieldDefinition SITE = new FieldDefinition("Site");
        /**
         * The site value is the site description. When loading, the site name is shown on the UI.  This field is to hold the site name value for verification.
         */
        public static final FieldDefinition SITENAME = new FieldDefinition("SiteName");
        /**
         * The business entity supplying the part
         */
        public static final FieldDefinition VENDORBUSINESSENTITY = new FieldDefinition("VendorBusinessEntity");
        /**
         * Reference to the type of business. Should be VENDOR or SUPPLIER.
         */
        public static final FieldDefinition VENDORBUSINESSENTITYTYPE = new FieldDefinition("VendorBusinessEntityType");
        /**
         * Reference to the vendors contact name
         */
        public static final FieldDefinition VENDORCONTACTNAME = new FieldDefinition("VendorContactName");
        /**
         * Reference to the vendors contact unique id
         */
        public static final FieldDefinition VENDORCONTACTUNIQUEID = new FieldDefinition("VendorContactUniqueId");
        /**
         * The vendors external reference number or other part identifier
         */
        public static final FieldDefinition VENDORITEMIDENTIFIER = new FieldDefinition("VendorItemIdentifier");
        /**
         * The vendors external reference number or other part identifier
         */
        public static final FieldDefinition VENDORITEMUNIQUEID = new FieldDefinition("VendorItemUniqueId");
        /**
         * The vendors item revision
         */
        public static final FieldDefinition VENDORREVISION = new FieldDefinition("VendorRevision");
        /**
         * The vendors item version
         */
        public static final FieldDefinition VENDORVERSION = new FieldDefinition("VendorVersion");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link ApprovedVendorListItem.PredicateBuilder} 
     */
    public interface ApprovedVendorListItemFieldPredicateFactory<T extends ApprovedVendorListItem>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,DateTime> whereLastUpdateDate();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> wherePartStatusCode();
        public FieldPredicate<T,String> wherePartStatusCodeOther();
        public FieldPredicate<T,String> wherePreferredStatusCode();
        public FieldPredicate<T,DateTime> wherePreferredStatusEndDate();
        public FieldPredicate<T,DateTime> wherePreferredStatusStartDate();
        public FieldPredicate<T,String> whereSite();
        public FieldPredicate<T,String> whereSiteName();
        public FieldPredicate<T,String> whereVendorBusinessEntity();
        public FieldPredicate<T,String> whereVendorBusinessEntityType();
        public FieldPredicate<T,String> whereVendorContactName();
        public FieldPredicate<T,String> whereVendorContactUniqueId();
        public FieldPredicate<T,String> whereVendorItemIdentifier();
        public FieldPredicate<T,String> whereVendorItemUniqueId();
        public FieldPredicate<T,String> whereVendorRevision();
        public FieldPredicate<T,String> whereVendorVersion();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link ApprovedVendorListItem}
         */
        public static ApprovedVendorListItem newInstance() {
            return MessageLineProxy.newInstance(ApprovedVendorListItem.class);
        }
        
        /**
         * Clone a instance of {@link ApprovedVendorListItem}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static ApprovedVendorListItem clone(ApprovedVendorListItem dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(ApprovedVendorListItem.class, dataToClone);
        }
		
        
        /**
         * Use this method to create a field based predicate on {@link ApprovedVendorListItem}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;ApprovedVendorListItem&gt; pred = ApprovedVendorListItem
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
        public static ApprovedVendorListItemFieldPredicateFactory<ApprovedVendorListItem> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(ApprovedVendorListItem.class, ApprovedVendorListItemFieldPredicateFactory.class);
        }
    }
 }