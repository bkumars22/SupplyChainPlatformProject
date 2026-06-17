/**
 * @SourcingLane.java@
 *
 * Created on Wed Oct 22 07:08:11 PDT 2014
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
 
package com.test.selenium.scplatform.messages.sourcingLane;
 
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
import com.test.selenium.scplatform.messages.sourcingLane.subClasses.CostRecord;
 
/**
 * <b>SPEC:</b> http://confluence.dev.scplatform.local/display/PUBT/Sourcing+Lane
 */
@Generated("IrisCodeGenerator")
public interface SourcingLane extends MessageLine {

    /**
     * The items business
     */
    @Size(max=255)
    @Metadata(index=8, displayName="BusinessEntity")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntity();
    /**
     * The items business
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=8, displayName="BusinessEntity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntity(String value);


    /**
     * he type of business. Can be ENTERPRISE or SUPPLIER.
     */
    @Size(max=64)
    @RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})
    @Metadata(index=9, displayName="BusinessEntityType")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntityType();
    /**
     * he type of business. Can be ENTERPRISE or SUPPLIER.
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})</li>
     * <li>@Metadata(index=9, displayName="BusinessEntityType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntityType(String value);


    /**
     * User comments
     */
    @Size(max=255)
    @Metadata(index=3, displayName="Comment")
    @FlexCDMData({"*","*.down"})
    public String getComment();
    /**
     * User comments
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=3, displayName="Comment")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setComment(String value);


    /**
     * Cost Record is a Sub Structure<br>
     * The following rules apply to outbound notification of cost changes:
     * <UL>
     * <LI> Whenever an approved cost record changes
     * <LI> Whenever an approved cost record is closed---this message signals that the pricing 
     *      should be removed or marked inactive in the receiving system
     * </UL>
     */
    @Metadata(index=23, displayName="CostRecord")
    @FlexCDMData({"*","*.down"})
    public List<CostRecord> getCostRecord();
    /**
     * Cost Record is a Sub Structure<br>
     * The following rules apply to outbound notification of cost changes:
     * <UL>
     * <LI> Whenever an approved cost record changes
     * <LI> Whenever an approved cost record is closed---this message signals that the pricing 
     *      should be removed or marked inactive in the receiving system
     * </UL>
     * Annotations:
     * <ul>
     * <li>@Metadata(index=23, displayName="CostRecord")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostRecord(List<CostRecord> value);


    /**
     * Currency for this supplier at this location
     */
    @Size(max=255)
    @Metadata(index=16, displayName="CurrencyCode")
    @FlexCDMData({"*","*.down"})
    public String getCurrencyCode();
    /**
     * Currency for this supplier at this location
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=16, displayName="CurrencyCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCurrencyCode(String value);


    /**
     * Defines the time delta in days between a part being purchased (that is, the date of the cost and rebate records) and the date of the forecast. The offset accommodates manufacturing and transportation cycle times.
     */
    @Metadata(index=15, displayName="DateOffset")
    @FlexCDMData({"*","*.down"})
    public int getDateOffset();
    /**
     * Defines the time delta in days between a part being purchased (that is, the date of the cost and rebate records) and the date of the forecast. The offset accommodates manufacturing and transportation cycle times.
     * Annotations:
     * <ul>
     * <li>@Metadata(index=15, displayName="DateOffset")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDateOffset(int value);


    @Size(max=1024)
    @Metadata(index=2, displayName="Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=1024)</li>
     * <li>@Metadata(index=2, displayName="Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDescription(String value);


    /**
     * The supplier
     */
    @Size(max=255)
    @Metadata(index=11, displayName="FromBusinessEntity")
    @FlexCDMData({"*","*.down"})
    public String getFromBusinessEntity();
    /**
     * The supplier
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=11, displayName="FromBusinessEntity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setFromBusinessEntity(String value);


    /**
     * The suppliers type. Should be SUPPLIER.
     */
    @Size(max=64)
    @RestrictToStrings({"SUPPLIER"})
    @Metadata(index=12, displayName="FromBusinessEntityType")
    @FlexCDMData({"*","*.down"})
    public String getFromBusinessEntityType();
    /**
     * The suppliers type. Should be SUPPLIER.
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"SUPPLIER"})</li>
     * <li>@Metadata(index=12, displayName="FromBusinessEntityType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setFromBusinessEntityType(String value);


    /**
     * The suppliers site
     */
    @Size(max=255)
    @Metadata(index=13, displayName="FromSite")
    @FlexCDMData({"*","*.down"})
    public String getFromSite();
    /**
     * The suppliers site
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=13, displayName="FromSite")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setFromSite(String value);


    /**
     * The part number
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=4, displayName="ItemIdentifier")
    @FlexCDMData({"*","*.down"})
    public String getItemIdentifier();
    /**
     * The part number
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=4, displayName="ItemIdentifier")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemIdentifier(String value);


    /**
     * The revision of the item
     */
    @Size(max=255)
    @Metadata(index=6, displayName="ItemRevision")
    @FlexCDMData({"*","*.down"})
    public String getItemRevision();
    /**
     * The revision of the item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=6, displayName="ItemRevision")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemRevision(String value);


    /**
     * The external reference number or other part identifier
     */
    @Size(max=255)
    @Metadata(index=5, displayName="ItemUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getItemUniqueId();
    /**
     * The external reference number or other part identifier
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=5, displayName="ItemUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemUniqueId(String value);


    /**
     * The version of the item
     */
    @Size(max=255)
    @Metadata(index=7, displayName="ItemVersion")
    @FlexCDMData({"*","*.down"})
    public String getItemVersion();
    /**
     * The version of the item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=7, displayName="ItemVersion")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemVersion(String value);


    /**
     * Userid that performed the last state change of this record or null if no state
     */
    @Size(max=255)
    @Metadata(index=19, displayName="LastStateChangeBy")
    @FlexCDMData({"*","*.down"})
    public String getLastStateChangeBy();
    /**
     * Userid that performed the last state change of this record or null if no state
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=19, displayName="LastStateChangeBy")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLastStateChangeBy(String value);


    /**
     * Date of last state change
     */
    @Metadata(index=20, displayName="LastStateChangeOn")
    @FlexCDMData({"*","*.down"})
    public DateTime getLastStateChangeOn();
    /**
     * Date of last state change
     * Annotations:
     * <ul>
     * <li>@Metadata(index=20, displayName="LastStateChangeOn")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLastStateChangeOn(DateTime value);


    /**
     * Date of last update
     */
    @NotNull
    @Metadata(index=21, displayName="LastUpdateDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getLastUpdateDate();
    /**
     * Date of last update
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=21, displayName="LastUpdateDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLastUpdateDate(DateTime value);


    /**
     * Type of sourcing lane: pre-production, production, EOL.
     */
    @Size(max=255)
    @Metadata(index=14, displayName="LifeCycleCode")
    @FlexCDMData({"*","*.down"})
    public String getLifeCycleCode();
    /**
     * Type of sourcing lane: pre-production, production, EOL.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=14, displayName="LifeCycleCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLifeCycleCode(String value);


    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.<br>
     * A=Add<br>
     * C=Changed<br>
     * D=Delete<br>
     * U=Unchanged<br>
     */
    @Size(max=64)
    @RestrictToStrings({"A","C","D","U"})
    @Metadata(index=22, displayName="OperationCode")
    @FlexCDMData({"*","*.down"})
    public String getOperationCode();
    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.<br>
     * A=Add<br>
     * C=Changed<br>
     * D=Delete<br>
     * U=Unchanged<br>
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"A","C","D","U"})</li>
     * <li>@Metadata(index=22, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    /**
     * owner identification
     */
    @Size(max=255)
    @Metadata(index=17, displayName="OwnerName")
    @FlexCDMData({"*","*.down"})
    public String getOwnerName();
    /**
     * owner identification
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=17, displayName="OwnerName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOwnerName(String value);


    @Size(max=255)
    @Metadata(index=10, displayName="Site")
    @FlexCDMData({"*","*.down"})
    public String getSite();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=10, displayName="Site")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite(String value);


    /**
     * external ID
     */
    @Size(max=255)
    @Metadata(index=1, displayName="SourcingLaneExternalId")
    @FlexCDMData({"*","*.down"})
    public String getSourcingLaneExternalId();
    /**
     * external ID
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=1, displayName="SourcingLaneExternalId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSourcingLaneExternalId(String value);


    /**
     * The sourcing lane identifier
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=0, displayName="SourcingLaneIdentifier")
    @FlexCDMData({"*","*.down"})
    public String getSourcingLaneIdentifier();
    /**
     * The sourcing lane identifier
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="SourcingLaneIdentifier")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSourcingLaneIdentifier(String value);


    /**
     * Current state of the record
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=18, displayName="State")
    @FlexCDMData({"*","*.down"})
    public String getState();
    /**
     * Current state of the record
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=18, displayName="State")</li>
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
         * The items business
         */
        public static final FieldDefinition BUSINESSENTITY = new FieldDefinition("BusinessEntity");
        /**
         * he type of business. Can be ENTERPRISE or SUPPLIER.
         */
        public static final FieldDefinition BUSINESSENTITYTYPE = new FieldDefinition("BusinessEntityType");
        /**
         * User comments
         */
        public static final FieldDefinition COMMENT = new FieldDefinition("Comment");
        /**
         * Cost Record is a Sub Structure<br>
         * The following rules apply to outbound notification of cost changes:
         * <UL>
         * <LI> Whenever an approved cost record changes
         * <LI> Whenever an approved cost record is closed---this message signals that the pricing 
         *      should be removed or marked inactive in the receiving system
         * </UL>
         */
        public static final FieldDefinition COSTRECORD = new FieldDefinition("CostRecord");
        /**
         * Currency for this supplier at this location
         */
        public static final FieldDefinition CURRENCYCODE = new FieldDefinition("CurrencyCode");
        /**
         * Defines the time delta in days between a part being purchased (that is, the date of the cost and rebate records) and the date of the forecast. The offset accommodates manufacturing and transportation cycle times.
         */
        public static final FieldDefinition DATEOFFSET = new FieldDefinition("DateOffset");
        public static final FieldDefinition DESCRIPTION = new FieldDefinition("Description");
        /**
         * The supplier
         */
        public static final FieldDefinition FROMBUSINESSENTITY = new FieldDefinition("FromBusinessEntity");
        /**
         * The suppliers type. Should be SUPPLIER.
         */
        public static final FieldDefinition FROMBUSINESSENTITYTYPE = new FieldDefinition("FromBusinessEntityType");
        /**
         * The suppliers site
         */
        public static final FieldDefinition FROMSITE = new FieldDefinition("FromSite");
        /**
         * The part number
         */
        public static final FieldDefinition ITEMIDENTIFIER = new FieldDefinition("ItemIdentifier");
        /**
         * The revision of the item
         */
        public static final FieldDefinition ITEMREVISION = new FieldDefinition("ItemRevision");
        /**
         * The external reference number or other part identifier
         */
        public static final FieldDefinition ITEMUNIQUEID = new FieldDefinition("ItemUniqueId");
        /**
         * The version of the item
         */
        public static final FieldDefinition ITEMVERSION = new FieldDefinition("ItemVersion");
        /**
         * Userid that performed the last state change of this record or null if no state
         */
        public static final FieldDefinition LASTSTATECHANGEBY = new FieldDefinition("LastStateChangeBy");
        /**
         * Date of last state change
         */
        public static final FieldDefinition LASTSTATECHANGEON = new FieldDefinition("LastStateChangeOn");
        /**
         * Date of last update
         */
        public static final FieldDefinition LASTUPDATEDATE = new FieldDefinition("LastUpdateDate");
        /**
         * Type of sourcing lane: pre-production, production, EOL.
         */
        public static final FieldDefinition LIFECYCLECODE = new FieldDefinition("LifeCycleCode");
        /**
         * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.<br>
         * A=Add<br>
         * C=Changed<br>
         * D=Delete<br>
         * U=Unchanged<br>
         */
        public static final FieldDefinition OPERATIONCODE = new FieldDefinition("OperationCode");
        /**
         * owner identification
         */
        public static final FieldDefinition OWNERNAME = new FieldDefinition("OwnerName");
        public static final FieldDefinition SITE = new FieldDefinition("Site");
        /**
         * external ID
         */
        public static final FieldDefinition SOURCINGLANEEXTERNALID = new FieldDefinition("SourcingLaneExternalId");
        /**
         * The sourcing lane identifier
         */
        public static final FieldDefinition SOURCINGLANEIDENTIFIER = new FieldDefinition("SourcingLaneIdentifier");
        /**
         * Current state of the record
         */
        public static final FieldDefinition STATE = new FieldDefinition("State");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link SourcingLane.PredicateBuilder} 
     */
    public interface SourcingLaneFieldPredicateFactory<T extends SourcingLane>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereBusinessEntity();
        public FieldPredicate<T,String> whereBusinessEntityType();
        public FieldPredicate<T,String> whereComment();
        public FieldPredicate<T,String> whereCostRecord();
        public FieldPredicate<T,String> whereCurrencyCode();
        public FieldPredicate<T,Integer> whereDateOffset();
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,String> whereFromBusinessEntity();
        public FieldPredicate<T,String> whereFromBusinessEntityType();
        public FieldPredicate<T,String> whereFromSite();
        public FieldPredicate<T,String> whereItemIdentifier();
        public FieldPredicate<T,String> whereItemRevision();
        public FieldPredicate<T,String> whereItemUniqueId();
        public FieldPredicate<T,String> whereItemVersion();
        public FieldPredicate<T,String> whereLastStateChangeBy();
        public FieldPredicate<T,DateTime> whereLastStateChangeOn();
        public FieldPredicate<T,DateTime> whereLastUpdateDate();
        public FieldPredicate<T,String> whereLifeCycleCode();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> whereOwnerName();
        public FieldPredicate<T,String> whereSite();
        public FieldPredicate<T,String> whereSourcingLaneExternalId();
        public FieldPredicate<T,String> whereSourcingLaneIdentifier();
        public FieldPredicate<T,String> whereState();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link SourcingLane}
         */
        public static SourcingLane newInstance() {
            return MessageLineProxy.newInstance(SourcingLane.class);
        }
        
        /**
         * Clone a instance of {@link SourcingLane}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static SourcingLane clone(SourcingLane dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(SourcingLane.class, dataToClone);
        }
		
        /**
         * Use this method to create a field based predicate on {@link SourcingLane}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;SourcingLane&gt; pred = SourcingLane
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
        public static SourcingLaneFieldPredicateFactory<SourcingLane> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(SourcingLane.class, SourcingLaneFieldPredicateFactory.class);
        }
    }
 }