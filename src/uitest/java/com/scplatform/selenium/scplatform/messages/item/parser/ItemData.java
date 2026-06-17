/**
 * @ItemData.java@
 *
 * Created on Tue Nov 04 07:45:23 PST 2014
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
 
package com.test.selenium.scplatform.messages.item.parser;
 
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
 
@Generated("IrisCodeGenerator")
public interface ItemData extends MessageLine {

    @NotNull
    @Metadata(index=3, displayName="BomLevel")
    @FlexCDMData({"*","*.down"})
    public int getBomLevel();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=3, displayName="BomLevel")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBomLevel(int value);


    @NotNull
    @Size(max=255)
    @Metadata(index=2, displayName="BomName")
    @FlexCDMData({"*","*.down"})
    public String getBomName();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=2, displayName="BomName")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBomName(String value);


    @NotNull
    @Size(max=255)
    @RestrictToStrings({"DirectMaterial","IndirectMaterial","Subassembly","PhantomSubassembly","EndProduct","Kit","Setup","AsNeeded","Reference","Nontangible","Other"})
    @Metadata(index=1, displayName="BomType")
    @FlexCDMData({"*","*.down"})
    public String getBomType();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@RestrictToStrings({"DirectMaterial","IndirectMaterial","Subassembly","PhantomSubassembly","EndProduct","Kit","Setup","AsNeeded","Reference","Nontangible","Other"})</li>
     * <li>@Metadata(index=1, displayName="BomType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBomType(String value);


    @NotNull
    @Size(max=255)
    @RestrictToStrings({"ItemAndBOM","BOMOnly","ItemOnly","DoNotCreate"})
    @Metadata(index=0, displayName="BuildAction")
    @FlexCDMData({"*","*.down"})
    public String getBuildAction();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@RestrictToStrings({"ItemAndBOM","BOMOnly","ItemOnly","DoNotCreate"})</li>
     * <li>@Metadata(index=0, displayName="BuildAction")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBuildAction(String value);


    @NotNull
    @Size(max=255)
    @Metadata(index=13, displayName="CommodityCode")
    @FlexCDMData({"*","*.down"})
    public String getCommodityCode();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=13, displayName="CommodityCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCommodityCode(String value);


    @NotNull
    @Size(max=255)
    @Metadata(index=16, displayName="CostElementType")
    @FlexCDMData({"*","*.down"})
    public String getCostElementType();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=16, displayName="CostElementType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostElementType(String value);


    @NotNull
    @Size(max=255)
    @RestrictToStrings({"BUY","MARKET","LIST","ODMBUY","SERVICE","EMQUOTE","SELL"})
    @Metadata(index=14, displayName="CostType")
    @FlexCDMData({"*","*.down"})
    public String getCostType();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@RestrictToStrings({"BUY","MARKET","LIST","ODMBUY","SERVICE","EMQUOTE","SELL"})</li>
     * <li>@Metadata(index=14, displayName="CostType")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostType(String value);


    @NotNull
    @Size(max=255)
    @Metadata(index=8, displayName="Description")
    @FlexCDMData({"*","*.down"})
    public String getDescription();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=8, displayName="Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDescription(String value);


    @NotNull
    @Size(max=255)
    @Metadata(index=5, displayName="ItemNumber")
    @FlexCDMData({"*","*.down"})
    public String getItemNumber();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=5, displayName="ItemNumber")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemNumber(String value);


    @Size(max=255)
    @Metadata(index=4, displayName="ParentBOM")
    @FlexCDMData({"*","*.down"})
    public String getParentBOM();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=4, displayName="ParentBOM")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setParentBOM(String value);


    @NotNull
    @Size(max=126)
    @Metadata(index=10, displayName="Price")
    @FlexCDMData({"*","*.down"})
    public float getPrice();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=10, displayName="Price")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPrice(float value);


    @NotNull
    @Size(max=255)
    @RestrictToStrings({"Consumption Based","Volume Based","Evergreen"})
    @Metadata(index=15, displayName="PricingScenario")
    @FlexCDMData({"*","*.down"})
    public String getPricingScenario();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@RestrictToStrings({"Consumption Based","Volume Based","Evergreen"})</li>
     * <li>@Metadata(index=15, displayName="PricingScenario")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPricingScenario(String value);


    @NotNull
    @Size(max=126)
    @Metadata(index=9, displayName="Quantity")
    @FlexCDMData({"*","*.down"})
    public float getQuantity();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=9, displayName="Quantity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setQuantity(float value);


    @NotNull
    @Size(max=255)
    @RestrictToStrings({"PerAssembly","PerSetup","AsNeeded","Shrinkage","Other"})
    @Metadata(index=11, displayName="QuantityTypeCode")
    @FlexCDMData({"*","*.down"})
    public String getQuantityTypeCode();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@RestrictToStrings({"PerAssembly","PerSetup","AsNeeded","Shrinkage","Other"})</li>
     * <li>@Metadata(index=11, displayName="QuantityTypeCode")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setQuantityTypeCode(String value);


    @NotNull
    @Size(max=255)
    @Metadata(index=12, displayName="Revision")
    @FlexCDMData({"*","*.down"})
    public String getRevision();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=12, displayName="Revision")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setRevision(String value);


    @NotNull
    @Size(max=255)
    @Metadata(index=7, displayName="Suppliers")
    @FlexCDMData({"*","*.down"})
    public String getSuppliers();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=7, displayName="Suppliers")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSuppliers(String value);

    
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
        public static final FieldDefinition BOMLEVEL = new FieldDefinition("BomLevel");
        public static final FieldDefinition BOMNAME = new FieldDefinition("BomName");
        public static final FieldDefinition BOMTYPE = new FieldDefinition("BomType");
        public static final FieldDefinition BUILDACTION = new FieldDefinition("BuildAction");
        public static final FieldDefinition COMMODITYCODE = new FieldDefinition("CommodityCode");
        public static final FieldDefinition COSTELEMENTTYPE = new FieldDefinition("CostElementType");
        public static final FieldDefinition COSTTYPE = new FieldDefinition("CostType");
        public static final FieldDefinition DESCRIPTION = new FieldDefinition("Description");
        public static final FieldDefinition ITEMNUMBER = new FieldDefinition("ItemNumber");
        public static final FieldDefinition PARENTBOM = new FieldDefinition("ParentBOM");
        public static final FieldDefinition PRICE = new FieldDefinition("Price");
        public static final FieldDefinition PRICINGSCENARIO = new FieldDefinition("PricingScenario");
        public static final FieldDefinition QUANTITY = new FieldDefinition("Quantity");
        public static final FieldDefinition QUANTITYTYPECODE = new FieldDefinition("QuantityTypeCode");
        public static final FieldDefinition REVISION = new FieldDefinition("Revision");
        public static final FieldDefinition SUPPLIERS = new FieldDefinition("Suppliers");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link ItemData.PredicateBuilder} 
     */
    public interface ItemDataFieldPredicateFactory<T extends ItemData>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,Integer> whereBomLevel();
        public FieldPredicate<T,String> whereBomName();
        public FieldPredicate<T,String> whereBomType();
        public FieldPredicate<T,String> whereBuildAction();
        public FieldPredicate<T,String> whereCommodityCode();
        public FieldPredicate<T,String> whereCostElementType();
        public FieldPredicate<T,String> whereCostType();
        public FieldPredicate<T,String> whereDescription();
        public FieldPredicate<T,String> whereItemNumber();
        public FieldPredicate<T,String> whereParentBOM();
        public FieldPredicate<T,Float> wherePrice();
        public FieldPredicate<T,String> wherePricingScenario();
        public FieldPredicate<T,Float> whereQuantity();
        public FieldPredicate<T,String> whereQuantityTypeCode();
        public FieldPredicate<T,String> whereRevision();
        public FieldPredicate<T,String> whereSuppliers();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link ItemData}
         */
        public static ItemData newInstance() {
            return MessageLineProxy.newInstance(ItemData.class);
        }
        
        /**
         * Clone a instance of {@link ItemData}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static ItemData clone(ItemData dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(ItemData.class, dataToClone);
        }
        
        /**
         * Use this method to create a field based predicate on {@link ItemData}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;ItemData&gt; pred = ItemData
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
        public static ItemDataFieldPredicateFactory<ItemData> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(ItemData.class, ItemDataFieldPredicateFactory.class);
        }
    }
 }