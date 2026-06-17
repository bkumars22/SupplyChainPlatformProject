/**
 * @Contact.java@
 *
 * Created on Thu Oct 16 14:14:28 PDT 2014
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
 
package com.test.selenium.scplatform.messages.contact;
 
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
public interface Contact extends MessageLine {

    @Size(max=255)
    @Metadata(index=3, displayName="AddressLine1")
    @FlexCDMData({"*","*.down"})
    public String getAddressLine1();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=3, displayName="AddressLine1")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAddressLine1(String value);


    @Size(max=255)
    @Metadata(index=4, displayName="AddressLine2")
    @FlexCDMData({"*","*.down"})
    public String getAddressLine2();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=4, displayName="AddressLine2")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAddressLine2(String value);


    @Size(max=255)
    @Metadata(index=5, displayName="AddressLine3")
    @FlexCDMData({"*","*.down"})
    public String getAddressLine3();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=5, displayName="AddressLine3")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAddressLine3(String value);


    @NotNull
    @Size(max=255)
    @Metadata(index=15, displayName="BusinessEntity")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntity();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=15, displayName="BusinessEntity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntity(String value);


    @Size(max=64)
    @RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})
    @Metadata(index=16, displayName="BusinessEntityType")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntityType();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})</li>
     * <li>@Metadata(index=16, displayName="BusinessEntityType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntityType(String value);


    @Size(max=255)
    @Metadata(index=17, displayName="BusinessName")
    @FlexCDMData({"*","*.down"})
    public String getBusinessName();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=17, displayName="BusinessName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessName(String value);


    @Size(max=255)
    @Metadata(index=6, displayName="CityName")
    @FlexCDMData({"*","*.down"})
    public String getCityName();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=6, displayName="CityName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCityName(String value);


    @Size(max=255)
    @Metadata(index=1, displayName="ContactId")
    @FlexCDMData({"*","*.down"})
    public String getContactId();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=1, displayName="ContactId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setContactId(String value);


    @NotNull
    @Size(max=255)
    @Metadata(index=0, displayName="ContactName")
    @FlexCDMData({"*","*.down"})
    public String getContactName();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="ContactName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setContactName(String value);


    @NotNull
    @Size(max=255)
    @Metadata(index=2, displayName="ContactUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getContactUniqueId();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=2, displayName="ContactUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setContactUniqueId(String value);


    @Size(max=255)
    @Metadata(index=9, displayName="CountryName")
    @FlexCDMData({"*","*.down"})
    public String getCountryName();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=9, displayName="CountryName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCountryName(String value);


    @NotNull
    @Size(max=255)
    @Metadata(index=21, displayName="DataSource")
    @FlexCDMData({"*","*.down"})
    public String getDataSource();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=21, displayName="DataSource")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDataSource(String value);


    @Size(max=255)
    @Metadata(index=14, displayName="Department")
    @FlexCDMData({"*","*.down"})
    public String getDepartment();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=14, displayName="Department")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDepartment(String value);


    @Metadata(index=22, displayName="EffectiveFromDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveFromDate();
    /**
     * Annotations:
     * <ul>
     * <li>@Metadata(index=22, displayName="EffectiveFromDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveFromDate(DateTime value);


    @Metadata(index=23, displayName="EffectiveToDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveToDate();
    /**
     * Annotations:
     * <ul>
     * <li>@Metadata(index=23, displayName="EffectiveToDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveToDate(DateTime value);


    @Size(max=255)
    @Metadata(index=18, displayName="EmailAddress")
    @FlexCDMData({"*","*.down"})
    public String getEmailAddress();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=18, displayName="EmailAddress")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEmailAddress(String value);


    @Size(max=255)
    @Metadata(index=13, displayName="FascimileNumber")
    @FlexCDMData({"*","*.down"})
    public String getFascimileNumber();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=13, displayName="FascimileNumber")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setFascimileNumber(String value);


    @Size(max=255)
    @Metadata(index=12, displayName="GlobalCountryCode")
    @FlexCDMData({"*","*.down"})
    public String getGlobalCountryCode();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=12, displayName="GlobalCountryCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setGlobalCountryCode(String value);


    @Size(max=255)
    @Metadata(index=11, displayName="NationalPostalCode")
    @FlexCDMData({"*","*.down"})
    public String getNationalPostalCode();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=11, displayName="NationalPostalCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setNationalPostalCode(String value);


    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
     * A=Add
     * C=Changed
     * D=Delete
     * U=Unchanged
     */
    @Size(max=64)
    @RestrictToStrings({"A","C","D","U"})
    @Metadata(index=24, displayName="OperationCode")
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
     * <li>@Metadata(index=24, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    @Size(max=255)
    @Metadata(index=19, displayName="PartnerClassificationCode")
    @FlexCDMData({"*","*.down"})
    public String getPartnerClassificationCode();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=19, displayName="PartnerClassificationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPartnerClassificationCode(String value);


    @Size(max=255)
    @Metadata(index=7, displayName="RegionName")
    @FlexCDMData({"*","*.down"})
    public String getRegionName();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=7, displayName="RegionName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setRegionName(String value);


    @Size(max=255)
    @Metadata(index=20, displayName="Role")
    @FlexCDMData({"*","*.down"})
    public String getRole();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=20, displayName="Role")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setRole(String value);


    @Size(max=255)
    @Metadata(index=8, displayName="StateName")
    @FlexCDMData({"*","*.down"})
    public String getStateName();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=8, displayName="StateName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setStateName(String value);


    @Size(max=255)
    @Metadata(index=10, displayName="TelephoneNumber")
    @FlexCDMData({"*","*.down"})
    public String getTelephoneNumber();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=10, displayName="TelephoneNumber")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setTelephoneNumber(String value);

    
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
        public static final FieldDefinition ADDRESSLINE1 = new FieldDefinition("AddressLine1");
        public static final FieldDefinition ADDRESSLINE2 = new FieldDefinition("AddressLine2");
        public static final FieldDefinition ADDRESSLINE3 = new FieldDefinition("AddressLine3");
        public static final FieldDefinition BUSINESSENTITY = new FieldDefinition("BusinessEntity");
        public static final FieldDefinition BUSINESSENTITYTYPE = new FieldDefinition("BusinessEntityType");
        public static final FieldDefinition BUSINESSNAME = new FieldDefinition("BusinessName");
        public static final FieldDefinition CITYNAME = new FieldDefinition("CityName");
        public static final FieldDefinition CONTACTID = new FieldDefinition("ContactId");
        public static final FieldDefinition CONTACTNAME = new FieldDefinition("ContactName");
        public static final FieldDefinition CONTACTUNIQUEID = new FieldDefinition("ContactUniqueId");
        public static final FieldDefinition COUNTRYNAME = new FieldDefinition("CountryName");
        public static final FieldDefinition DATASOURCE = new FieldDefinition("DataSource");
        public static final FieldDefinition DEPARTMENT = new FieldDefinition("Department");
        public static final FieldDefinition EFFECTIVEFROMDATE = new FieldDefinition("EffectiveFromDate");
        public static final FieldDefinition EFFECTIVETODATE = new FieldDefinition("EffectiveToDate");
        public static final FieldDefinition EMAILADDRESS = new FieldDefinition("EmailAddress");
        public static final FieldDefinition FASCIMILENUMBER = new FieldDefinition("FascimileNumber");
        public static final FieldDefinition GLOBALCOUNTRYCODE = new FieldDefinition("GlobalCountryCode");
        public static final FieldDefinition NATIONALPOSTALCODE = new FieldDefinition("NationalPostalCode");
        /**
         * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
         * A=Add
         * C=Changed
         * D=Delete
         * U=Unchanged
         */
        public static final FieldDefinition OPERATIONCODE = new FieldDefinition("OperationCode");
        public static final FieldDefinition PARTNERCLASSIFICATIONCODE = new FieldDefinition("PartnerClassificationCode");
        public static final FieldDefinition REGIONNAME = new FieldDefinition("RegionName");
        public static final FieldDefinition ROLE = new FieldDefinition("Role");
        public static final FieldDefinition STATENAME = new FieldDefinition("StateName");
        public static final FieldDefinition TELEPHONENUMBER = new FieldDefinition("TelephoneNumber");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link Contact.PredicateBuilder} 
     */
    public interface ContactFieldPredicateFactory<T extends Contact>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereAddressLine1();
        public FieldPredicate<T,String> whereAddressLine2();
        public FieldPredicate<T,String> whereAddressLine3();
        public FieldPredicate<T,String> whereBusinessEntity();
        public FieldPredicate<T,String> whereBusinessEntityType();
        public FieldPredicate<T,String> whereBusinessName();
        public FieldPredicate<T,String> whereCityName();
        public FieldPredicate<T,String> whereContactId();
        public FieldPredicate<T,String> whereContactName();
        public FieldPredicate<T,String> whereContactUniqueId();
        public FieldPredicate<T,String> whereCountryName();
        public FieldPredicate<T,String> whereDataSource();
        public FieldPredicate<T,String> whereDepartment();
        public FieldPredicate<T,DateTime> whereEffectiveFromDate();
        public FieldPredicate<T,DateTime> whereEffectiveToDate();
        public FieldPredicate<T,String> whereEmailAddress();
        public FieldPredicate<T,String> whereFascimileNumber();
        public FieldPredicate<T,String> whereGlobalCountryCode();
        public FieldPredicate<T,String> whereNationalPostalCode();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> wherePartnerClassificationCode();
        public FieldPredicate<T,String> whereRegionName();
        public FieldPredicate<T,String> whereRole();
        public FieldPredicate<T,String> whereStateName();
        public FieldPredicate<T,String> whereTelephoneNumber();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link Contact}
         */
        public static Contact newInstance() {
            return MessageLineProxy.newInstance(Contact.class);
        }
        
        /**
         * Clone a instance of {@link Contact}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static Contact clone(Contact dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(Contact.class, dataToClone);
        }
        
        /**
         * Use this method to create a field based predicate on {@link Contact}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;Contact&gt; pred = Contact
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
        public static ContactFieldPredicateFactory<Contact> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(Contact.class, ContactFieldPredicateFactory.class);
        }
    }
 }