/**
 * @Responsibility.java@
 *
 * Created on Tue Oct 21 09:11:54 PDT 2014
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
 * 
 *
 */
@Generated("IrisCodeGenerator")
public interface Responsibility extends MessageLine {

    /**
     * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
     * A=Add
     * C=Changed
     * D=Delete
     * U=Unchanged
     */
    @Size(max=64)
    @RestrictToStrings({"A","C","D","U"})
    @Metadata(index=2, displayName="OperationCode")
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
     * <li>@Metadata(index=2, displayName="OperationCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOperationCode(String value);


    /**
     * The responsibility of the item
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=0, displayName="Responsibility")
    @FlexCDMData({"*","*.down"})
    public String getResponsibility();
    /**
     * The responsibility of the item
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="Responsibility")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setResponsibility(String value);


    /**
     * User ID of the person who is responsible
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=1, displayName="ResponsibilityUserId")
    @FlexCDMData({"*","*.down"})
    public String getResponsibilityUserId();
    /**
     * User ID of the person who is responsible
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=1, displayName="ResponsibilityUserId")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setResponsibilityUserId(String value);

    
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
         * Operation being performed on this element. If not specified, assumption is to add it if not found or update if found.
         * A=Add
         * C=Changed
         * D=Delete
         * U=Unchanged
         */
        public static final FieldDefinition OPERATIONCODE = new FieldDefinition("OperationCode");
        /**
         * The responsibility of the item
         */
        public static final FieldDefinition RESPONSIBILITY = new FieldDefinition("Responsibility");
        /**
         * User ID of the person who is responsible
         */
        public static final FieldDefinition RESPONSIBILITYUSERID = new FieldDefinition("ResponsibilityUserId");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link Responsibility.PredicateBuilder} 
     */
    public interface ResponsibilityFieldPredicateFactory<T extends Responsibility>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,String> whereOperationCode();
        public FieldPredicate<T,String> whereResponsibility();
        public FieldPredicate<T,String> whereResponsibilityUserId();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link Responsibility}
         */
        public static Responsibility newInstance() {
            return MessageLineProxy.newInstance(Responsibility.class);
        }
        
        /**
         * Clone a instance of {@link Responsibility}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static Responsibility clone(Responsibility dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(Responsibility.class, dataToClone);
        }
		
        /**
         * Use this method to create a field based predicate on {@link Responsibility}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;Responsibility&gt; pred = Responsibility
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
        public static ResponsibilityFieldPredicateFactory<Responsibility> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(Responsibility.class, ResponsibilityFieldPredicateFactory.class);
        }
    }
 }