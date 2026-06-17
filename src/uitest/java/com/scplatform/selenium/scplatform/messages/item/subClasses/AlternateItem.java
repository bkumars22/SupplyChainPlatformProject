/**
 * @AlternateItem.java@
 *
 * Created on Tue Oct 21 09:08:46 PDT 2014
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
import javax.validation.constraints.Size;

import org.joda.time.DateTime;

import com.scplatform.qa.iris.model.FieldDefinition;
import com.scplatform.qa.iris.model.MessageLine;
import com.scplatform.qa.iris.model.annotations.FlexCDMData;
import com.scplatform.qa.iris.model.annotations.Metadata;
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
public interface AlternateItem extends MessageLine {

    /**
     * An alternate part number
     */
    @Size(max=255)
    @Metadata(index=0, displayName="AltItemIdentifier")
    @FlexCDMData({"*","*.down"})
    public String getAltItemIdentifier();
    /**
     * An alternate part number
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="AltItemIdentifier")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAltItemIdentifier(String value);


    /**
     * The alternate external reference number or other part identifier
     */
    @Size(max=255)
    @Metadata(index=1, displayName="AltItemUniqueId")
    @FlexCDMData({"*","*.down"})
    public String getAltItemUniqueId();
    /**
     * The alternate external reference number or other part identifier
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=1, displayName="AltItemUniqueId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAltItemUniqueId(String value);


    /**
     * The revision of the alternate item
     */
    @Size(max=255)
    @Metadata(index=2, displayName="AltRevision")
    @FlexCDMData({"*","*.down"})
    public String getAltRevision();
    /**
     * The revision of the alternate item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=2, displayName="AltRevision")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAltRevision(String value);


    /**
     * The version of the alternate item
     */
    @Size(max=255)
    @Metadata(index=3, displayName="AltVersion")
    @FlexCDMData({"*","*.down"})
    public String getAltVersion();
    /**
     * The version of the alternate item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=3, displayName="AltVersion")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAltVersion(String value);


    /**
     * Indicates if the alternate is the preferred alternate
     */
    @Size(max=255)
    @Metadata(index=4, displayName="PreferredStatusCode")
    @FlexCDMData({"*","*.down"})
    public String getPreferredStatusCode();
    /**
     * Indicates if the alternate is the preferred alternate
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=4, displayName="PreferredStatusCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPreferredStatusCode(String value);


    /**
     * The end data for the preferred alternate
     */
    @Metadata(index=6, displayName="PreferredStatusEndDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getPreferredStatusEndDate();
    /**
     * The end data for the preferred alternate
     * Annotations:
     * <ul>
     * <li>@Metadata(index=6, displayName="PreferredStatusEndDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPreferredStatusEndDate(DateTime value);


    /**
     * The start date for the preferred alternate
     */
    @Metadata(index=5, displayName="PreferredStatusStartDate")
    @FlexCDMData({"*","*.down"})
    public DateTime getPreferredStatusStartDate();
    /**
     * The start date for the preferred alternate
     * Annotations:
     * <ul>
     * <li>@Metadata(index=5, displayName="PreferredStatusStartDate")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPreferredStatusStartDate(DateTime value);

    
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
         * An alternate part number
         */
        public static final FieldDefinition ALTITEMIDENTIFIER = new FieldDefinition("AltItemIdentifier");
        /**
         * The alternate external reference number or other part identifier
         */
        public static final FieldDefinition ALTITEMUNIQUEID = new FieldDefinition("AltItemUniqueId");
        /**
         * The revision of the alternate item
         */
        public static final FieldDefinition ALTREVISION = new FieldDefinition("AltRevision");
        /**
         * The version of the alternate item
         */
        public static final FieldDefinition ALTVERSION = new FieldDefinition("AltVersion");
        /**
         * Indicates if the alternate is the preferred alternate
         */
        public static final FieldDefinition PREFERREDSTATUSCODE = new FieldDefinition("PreferredStatusCode");
        /**
         * The end data for the preferred alternate
         */
        public static final FieldDefinition PREFERREDSTATUSENDDATE = new FieldDefinition("PreferredStatusEndDate");
        /**
         * The start date for the preferred alternate
         */
        public static final FieldDefinition PREFERREDSTATUSSTARTDATE = new FieldDefinition("PreferredStatusStartDate");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link AlternateItem.PredicateBuilder} 
     */
    public interface AlternateItemFieldPredicateFactory<T extends AlternateItem>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereAltItemIdentifier();
        public FieldPredicate<T,String> whereAltItemUniqueId();
        public FieldPredicate<T,String> whereAltRevision();
        public FieldPredicate<T,String> whereAltVersion();
        public FieldPredicate<T,String> wherePreferredStatusCode();
        public FieldPredicate<T,DateTime> wherePreferredStatusEndDate();
        public FieldPredicate<T,DateTime> wherePreferredStatusStartDate();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link AlternateItem}
         */
        public static AlternateItem newInstance() {
            return MessageLineProxy.newInstance(AlternateItem.class);
        }
        
        /**
         * Clone a instance of {@link AlternateItem}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static AlternateItem clone(AlternateItem dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(AlternateItem.class, dataToClone);
        }
		
        
        /**
         * Use this method to create a field based predicate on {@link AlternateItem}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;AlternateItem&gt; pred = AlternateItem
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
        public static AlternateItemFieldPredicateFactory<AlternateItem> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(AlternateItem.class, AlternateItemFieldPredicateFactory.class);
        }
    }
 }