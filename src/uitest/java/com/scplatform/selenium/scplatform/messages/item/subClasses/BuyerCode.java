/**
 * @BuyerCode.java@
 *
 * Created on Tue Oct 21 09:10:09 PDT 2014
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
 * <br>
 * Item buyer codes are used to designate what buyer code this item has been assigned to. 
 * The code can be site-specific, in which case the site name must be specified. 
 */
@Generated("IrisCodeGenerator")
public interface BuyerCode extends MessageLine {

    /**
     * Buyer Code
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=0, displayName="BuyerCode")
    @FlexCDMData({"*","*.down"})
    public String getBuyerCode();
    /**
     * Buyer Code
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="BuyerCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBuyerCode(String value);


    /**
     * User id for the buyer code. If set, the system attempts to associate the buyer code with the user in the system.
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=1, displayName="BuyerCodeUserid")
    @FlexCDMData({"*","*.down"})
    public String getBuyerCodeUserid();
    /**
     * User id for the buyer code. If set, the system attempts to associate the buyer code with the user in the system.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=1, displayName="BuyerCodeUserid")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBuyerCodeUserid(String value);


    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
     * A=Add
     * C=Changed
     * D=Delete
     * U=Unchanged
     */
    @Size(max=64)
    @RestrictToStrings({"A","C","D","U"})
    @Metadata(index=3, displayName="OperationCode")
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
     * <li>@Metadata(index=3, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    /**
     * If specified, it must match one of the sites already stored in the system.
     */
    @Size(max=255)
    @Metadata(index=2, displayName="Site")
    @FlexCDMData({"*","*.down"})
    public String getSite();
    /**
     * If specified, it must match one of the sites already stored in the system.
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=2, displayName="Site")</li>
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
         * Buyer Code
         */
        public static final FieldDefinition BUYERCODE = new FieldDefinition("BuyerCode");
        /**
         * User id for the buyer code. If set, the system attempts to associate the buyer code with the user in the system.
         */
        public static final FieldDefinition BUYERCODEUSERID = new FieldDefinition("BuyerCodeUserid");
        /**
         * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
         * A=Add
         * C=Changed
         * D=Delete
         * U=Unchanged
         */
        public static final FieldDefinition OPERATIONCODE = new FieldDefinition("OperationCode");
        /**
         * If specified, it must match one of the sites already stored in the system.
         */
        public static final FieldDefinition SITE = new FieldDefinition("Site");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link BuyerCode.PredicateBuilder} 
     */
    public interface BuyerCodeFieldPredicateFactory<T extends BuyerCode>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereBuyerCode();
        public FieldPredicate<T,String> whereBuyerCodeUserid();
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> whereSite();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link BuyerCode}
         */
        public static BuyerCode newInstance() {
            return MessageLineProxy.newInstance(BuyerCode.class);
        }
        
        /**
         * Clone a instance of {@link BuyerCode}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static BuyerCode clone(BuyerCode dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(BuyerCode.class, dataToClone);
        }
		
        
        /**
         * Use this method to create a field based predicate on {@link BuyerCode}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;BuyerCode&gt; pred = BuyerCode
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
        public static BuyerCodeFieldPredicateFactory<BuyerCode> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(BuyerCode.class, BuyerCodeFieldPredicateFactory.class);
        }
    }
 }