/**
 * @BusinessEntity.java@
 *
 * Created on Thu Oct 16 14:09:07 PDT 2014
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
 
package com.test.selenium.scplatform.messages.businessEntity;
 
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
 * <b>SPEC:</b> http://confluence.dev.scplatform.local/display/PUBT/Business+Entity
 *
 */
@Generated("IrisCodeGenerator")
public interface BusinessEntity extends MessageLine {

    /**
     * The Business Entity Alternates are used to provide alternate names for a given business entity. Each alternate name is specified within the Business Entity element using the AlternateName element.
     */
    @Size(max=255)
    @Metadata(index=23, displayName="Alternates_AlternateName")
    @FlexCDMData({"*","*.down"})
    public String getAlternates_AlternateName();
    /**
     * The Business Entity Alternates are used to provide alternate names for a given business entity. Each alternate name is specified within the Business Entity element using the AlternateName element.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=23, displayName="Alternates_AlternateName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAlternates_AlternateName(String value);
    


    /**
     * From Date for altentative business entitiy
     */
    @Metadata(index=24, displayName="Alternates_EffectiveFromDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getAlternates_EffectiveFromDate();
    /**
     * From Date for altentative business entitiy
     * Annotations:
     * <ul>
     * <li>@Metadata(index=24, displayName="Alternates_EffectiveFromDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAlternates_EffectiveFromDate(DateTime value);


    /**
     * To Date for altentative business entitiy
     */
    @Metadata(index=25, displayName="Alternates_EffectiveToDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getAlternates_EffectiveToDate();
    /**
     * To Date for altentative business entitiy
     * Annotations:
     * <ul>
     * <li>@Metadata(index=25, displayName="Alternates_EffectiveToDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAlternates_EffectiveToDate(DateTime value);


    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
     * A=Add
     * C=Changed
     * D=Delete
     * U=Unchanged
     */
    @Size(max=64)
    @RestrictToStrings({"A","C","D","U"})
    @Metadata(index=26, displayName="Alternates_OperationCode")
    @FlexCDMData({"*","*.down"})
    public String getAlternates_OperationCode();
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
     * <li>@Metadata(index=26, displayName="Alternates_OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAlternates_OperationCode(String value);


    /**
     * Unique identifier for this business
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=0, displayName="BusinessEntity")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntity();
    /**
     * Unique identifier for this business
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="BusinessEntity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntity(String value);


    /**
     * external reference for this business
     */
    @Size(max=255)
    @Metadata(index=3, displayName="BusinessEntityExternalId")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntityExternalId();
    /**
     * external reference for this business
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=3, displayName="BusinessEntityExternalId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntityExternalId(String value);


    /**
     * Name of the business
     */
    @Size(max=255)
    @Metadata(index=1, displayName="BusinessEntityName")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntityName();
    /**
     * Name of the business
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=1, displayName="BusinessEntityName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntityName(String value);


    /**
     * Reference to the type of business.
     */
    @Size(max=64)
    @RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})
    @Metadata(index=4, displayName="BusinessEntityType")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntityType();
    /**
     * Reference to the type of business.
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})</li>
     * <li>@Metadata(index=4, displayName="BusinessEntityType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntityType(String value);


    /**
     * Reference to a contact for this business
     */
    @Size(max=255)
    @Metadata(index=5, displayName="ContactName")
    @FlexCDMData({"*","*.down"})
    public String getContactName();
    /**
     * Reference to a contact for this business
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=5, displayName="ContactName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setContactName(String value);


    /**
     * The contacts unique identifier
     */
    @Size(max=255)
    @Metadata(index=6, displayName="ContactUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getContactUniqueId();
    /**
     * The contacts unique identifier
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=6, displayName="ContactUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setContactUniqueId(String value);


    /**
     * The Currency can be used to set a collection of potential currencies for this BusinessEntity. These are considered possible currencies for use by that business. Specific sites may have a currency default that implies that site uses a specific currency.
     */
    @NotNull
    @Size(max=3)
    @Metadata(index=11, displayName="Currency_CurrencyCode")
    @FlexCDMData({"*","*.down"})
    public String getCurrency_CurrencyCode();
    /**
     * The Currency can be used to set a collection of potential currencies for this BusinessEntity. These are considered possible currencies for use by that business. Specific sites may have a currency default that implies that site uses a specific currency.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=3)</li>
     * <li>@Metadata(index=11, displayName="Currency_CurrencyCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCurrency_CurrencyCode(String value);


    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
     * A=Add
     * C=Changed
     * D=Delete
     * U=Unchanged
     */
    @Size(max=64)
    @RestrictToStrings({"A","C","D","U"})
    @Metadata(index=12, displayName="Currency_OperationCode")
    @FlexCDMData({"*","*.down"})
    public String getCurrency_OperationCode();
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
     * <li>@Metadata(index=12, displayName="Currency_OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCurrency_OperationCode(String value);


    /**
     * The system of record for the business entity (i.e. The E2open Customer)
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=7, displayName="DataSource")
    @FlexCDMData({"*","*.down"})
    public String getDataSource();
    /**
     * The system of record for the business entity (i.e. The E2open Customer)
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=7, displayName="DataSource")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDataSource(String value);


    /**
     * Optional description
     */
    @Size(max=1024)
    @Metadata(index=2, displayName="Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * Optional description
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
    @Metadata(index=8, displayName="EffectiveFromDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveFromDate();
    /**
     * From Date
     * Annotations:
     * <ul>
     * <li>@Metadata(index=8, displayName="EffectiveFromDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveFromDate(DateTime value);


    /**
     * To Date
     */
    @Metadata(index=9, displayName="EffectiveToDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveToDate();
    /**
     * To Date
     * Annotations:
     * <ul>
     * <li>@Metadata(index=9, displayName="EffectiveToDate")</li>
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
    @Metadata(index=10, displayName="OperationCode")
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
     * <li>@Metadata(index=10, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    /**
     * reference to a contact for this site
     */
    @Size(max=255)
    @Metadata(index=17, displayName="Site_ContactName")
    @FlexCDMData({"*","*.down"})
    public String getSite_ContactName();
    /**
     * reference to a contact for this site
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=17, displayName="Site_ContactName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite_ContactName(String value);


    /**
     * The site contacts unique ID
     */
    @Size(max=255)
    @Metadata(index=19, displayName="Site_ContactUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getSite_ContactUniqueId();
    /**
     * The site contacts unique ID
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=19, displayName="Site_ContactUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite_ContactUniqueId(String value);


    /**
     * The default currency code for the location
     */
    @Size(max=3)
    @Metadata(index=18, displayName="Site_DefaultCurrency")
    @FlexCDMData({"*","*.down"})
    public String getSite_DefaultCurrency();
    /**
     * The default currency code for the location
     * Annotations:
     * <ul>
     * <li>@Size(max=3)</li>
     * <li>@Metadata(index=18, displayName="Site_DefaultCurrency")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite_DefaultCurrency(String value);


    /**
     * Description of the site
     */
    @Size(max=1024)
    @Metadata(index=14, displayName="Site_Description")
    @FlexCDMData({"*","*.down"})
    public String getSite_Description();
    /**
     * Description of the site
     * Annotations:
     * <ul>
     * <li>@Size(max=1024)</li>
     * <li>@Metadata(index=14, displayName="Site_Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite_Description(String value);


    /**
     * From Date for Site
     */
    @Metadata(index=20, displayName="Site_EffectiveFromDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getSite_EffectiveFromDate();
    /**
     * From Date for Site
     * Annotations:
     * <ul>
     * <li>@Metadata(index=20, displayName="Site_EffectiveFromDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite_EffectiveFromDate(DateTime value);


    /**
     * To Date for Site
     */
    @Metadata(index=21, displayName="Site_EffectiveToDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getSite_EffectiveToDate();
    /**
     * To Date for Site
     * Annotations:
     * <ul>
     * <li>@Metadata(index=21, displayName="Site_EffectiveToDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite_EffectiveToDate(DateTime value);


    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
     * A=Add
     * C=Changed
     * D=Delete
     * U=Unchanged
     */
    @Size(max=64)
    @RestrictToStrings({"A","C","D","U"})
    @Metadata(index=22, displayName="Site_OperationCode")
    @FlexCDMData({"*","*.down"})
    public String getSite_OperationCode();
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
     * <li>@Metadata(index=22, displayName="Site_OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite_OperationCode(String value);


    /**
     * The identifier of a parent site if this is not a top level site
     */
    @Size(max=255)
    @Metadata(index=15, displayName="Site_ParentSite")
    @FlexCDMData({"*","*.down"})
    public String getSite_ParentSite();
    /**
     * The identifier of a parent site if this is not a top level site
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=15, displayName="Site_ParentSite")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite_ParentSite(String value);


    /**
     * Identifier for the site
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=13, displayName="Site_Site")
    @FlexCDMData({"*","*.down"})
    public String getSite_Site();
    /**
     * Identifier for the site
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=13, displayName="Site_Site")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite_Site(String value);


    /**
     * code describing the site region type, for instance GLOBAL, REGION, SITE, FFC, WAREHOUSE
     */
    @Size(max=255)
    @Metadata(index=16, displayName="Site_SiteType")
    @FlexCDMData({"*","*.down"})
    public String getSite_SiteType();
    /**
     * code describing the site region type, for instance GLOBAL, REGION, SITE, FFC, WAREHOUSE
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=16, displayName="Site_SiteType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite_SiteType(String value);

    
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
         * The Business Entity Alternates are used to provide alternate names for a given business entity. Each alternate name is specified within the Business Entity element using the AlternateName element.
         */
        public static final FieldDefinition ALTERNATES_ALTERNATENAME = new FieldDefinition("Alternates_AlternateName");
        /**
         * From Date for altentative business entitiy
         */
        public static final FieldDefinition ALTERNATES_EFFECTIVEFROMDATE = new FieldDefinition("Alternates_EffectiveFromDate");
        /**
         * To Date for altentative business entitiy
         */
        public static final FieldDefinition ALTERNATES_EFFECTIVETODATE = new FieldDefinition("Alternates_EffectiveToDate");
        /**
         * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
         * A=Add
         * C=Changed
         * D=Delete
         * U=Unchanged
         */
        public static final FieldDefinition ALTERNATES_OPERATIONCODE = new FieldDefinition("Alternates_OperationCode");
        /**
         * Unique identifier for this business
         */
        public static final FieldDefinition BUSINESSENTITY = new FieldDefinition("BusinessEntity");
        /**
         * external reference for this business
         */
        public static final FieldDefinition BUSINESSENTITYEXTERNALID = new FieldDefinition("BusinessentityExternalId");
        /**
         * Name of the business
         */
        public static final FieldDefinition BUSINESSENTITYNAME = new FieldDefinition("BusinessEntityName");
        /**
         * Reference to the type of business.
         */
        public static final FieldDefinition BUSINESSENTITYTYPE = new FieldDefinition("BusinessEntityType");
        /**
         * Reference to a contact for this business
         */
        public static final FieldDefinition CONTACTNAME = new FieldDefinition("ContactName");
        /**
         * The contacts unique identifier
         */
        public static final FieldDefinition CONTACTUNIQUEID = new FieldDefinition("ContactUniqueId");
        /**
         * The Currency can be used to set a collection of potential currencies for this BusinessEntity. These are considered possible currencies for use by that business. Specific sites may have a currency default that implies that site uses a specific currency.
         */
        public static final FieldDefinition CURRENCY_CURRENCYCODE = new FieldDefinition("Currency_CurrencyCode");
        /**
         * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
         * A=Add
         * C=Changed
         * D=Delete
         * U=Unchanged
         */
        public static final FieldDefinition CURRENCY_OPERATIONCODE = new FieldDefinition("Currency_OperationCode");
        /**
         * The system of record for the business entity (i.e. The E2open Customer)
         */
        public static final FieldDefinition DATASOURCE = new FieldDefinition("DataSource");
        /**
         * Optional description
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
         * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
         * A=Add
         * C=Changed
         * D=Delete
         * U=Unchanged
         */
        public static final FieldDefinition OPERATIONCODE = new FieldDefinition("OperationCode");
        /**
         * reference to a contact for this site
         */
        public static final FieldDefinition SITE_CONTACTNAME = new FieldDefinition("Site_ContactName");
        /**
         * The site contacts unique ID
         */
        public static final FieldDefinition SITE_CONTACTUNIQUEID = new FieldDefinition("Site_ContactUniqueId");
        /**
         * The default currency code for the location
         */
        public static final FieldDefinition SITE_DEFAULTCURRENCY = new FieldDefinition("Site_DefaultCurrency");
        /**
         * Description of the site
         */
        public static final FieldDefinition SITE_DESCRIPTION = new FieldDefinition("Site_Description");
        /**
         * From Date for Site
         */
        public static final FieldDefinition SITE_EFFECTIVEFROMDATE = new FieldDefinition("Site_EffectiveFromDate");
        /**
         * To Date for Site
         */
        public static final FieldDefinition SITE_EFFECTIVETODATE = new FieldDefinition("Site_EffectiveToDate");
        /**
         * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
         * A=Add
         * C=Changed
         * D=Delete
         * U=Unchanged
         */
        public static final FieldDefinition SITE_OPERATIONCODE = new FieldDefinition("Site_OperationCode");
        /**
         * The identifier of a parent site if this is not a top level site
         */
        public static final FieldDefinition SITE_PARENTSITE = new FieldDefinition("Site_ParentSite");
        /**
         * Identifier for the site
         */
        public static final FieldDefinition SITE_SITE = new FieldDefinition("Site_Site");
        /**
         * code describing the site region type, for instance GLOBAL, REGION, SITE, FFC, WAREHOUSE
         */
        public static final FieldDefinition SITE_SITETYPE = new FieldDefinition("Site_SiteType");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link BusinessEntity.PredicateBuilder} 
     */
    public interface BusinessEntityFieldPredicateFactory<T extends BusinessEntity>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereAlternates_AlternateName();
        public FieldPredicate<T,DateTime> whereAlternates_EffectiveFromDate();
        public FieldPredicate<T,DateTime> whereAlternates_EffectiveToDate();
        public FieldPredicate<T,String> whereAlternates_OperationCode();
        public FieldPredicate<T,String> whereBusinessEntity();
        public FieldPredicate<T,String> whereBusinessEntityExternalId();
        public FieldPredicate<T,String> whereBusinessEntityName();
        public FieldPredicate<T,String> whereBusinessEntityType();
        public FieldPredicate<T,String> whereContactnName();
        public FieldPredicate<T,String> whereContactUniqueId();
        public FieldPredicate<T,String> whereCurrency_CurrencyCode();
        public FieldPredicate<T,String> whereCurrency_OperationCode();
        public FieldPredicate<T,String> whereDataSource();
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,DateTime> whereEffectiveFromDate();
        public FieldPredicate<T,DateTime> whereEffectiveToDate();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> whereSite_ContactName();
        public FieldPredicate<T,String> whereSite_ContactUniqueId();
        public FieldPredicate<T,String> whereSite_DefaultCurrency();
        public FieldPredicate<T,String> whereSite_Description();
        public FieldPredicate<T,DateTime> whereSite_EffectiveFromDate();
        public FieldPredicate<T,DateTime> whereSite_EffectiveToDate();
        public FieldPredicate<T,String> whereSite_OperationCode();
        public FieldPredicate<T,String> whereSite_ParentSite();
        public FieldPredicate<T,String> whereSite_Site();
        public FieldPredicate<T,String> whereSite_SiteType();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link BusinessEntity}
         */
        public static BusinessEntity newInstance() {
            return MessageLineProxy.newInstance(BusinessEntity.class);
        }
        
        /**
         * clone a instance of {@link BusinessEntity}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static BusinessEntity clone(BusinessEntity dataToClone) throws FieldNotFoundException, InvalidValueException {
        	return MessageLineProxy.clone(BusinessEntity.class, dataToClone);
        }
        
        /**
         * Use this method to create a field based predicate on {@link BusinessEntity}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;BusinessEntity&gt; pred = BusinessEntity
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
        public static BusinessEntityFieldPredicateFactory<BusinessEntity> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(BusinessEntity.class, BusinessEntityFieldPredicateFactory.class);
        }
    }
 }