/**
 * @CostRecord.java@
 *
 * Created on Fri Oct 24 09:06:44 PDT 2014
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
 
package com.test.selenium.scplatform.messages.costRecord;
 
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
public interface CostRecord extends MessageLine {

    @Size(max=126)
    @Metadata(index=31, displayName="Air %")
    @FlexCDMData({"*","*.down"})
    public float getAIR_Percent();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=31, displayName="Air %")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAIR_Percent(float value);


    @Size(max=126)
    @Metadata(index=30, displayName="Air value")
    @FlexCDMData({"*","*.down"})
    public float getAIR_Value();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=30, displayName="Air value")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAIR_Value(float value);


    @Size(max=126)
    @Metadata(index=39, displayName="AMORTIZATION")
    @FlexCDMData({"*","*.down"})
    public float getAMORTIZATION();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=39, displayName="AMORTIZATION")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setAMORTIZATION(float value);


    /**
     * The name of the company that uses this part number to identify its parts. If the part is a Dell part number, then Whose Part Number is Dell, regardless of where we source the part.
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=2, displayName="Item Business Entity")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntity();
    /**
     * The name of the company that uses this part number to identify its parts. If the part is a Dell part number, then Whose Part Number is Dell, regardless of where we source the part.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=2, displayName="Item Business Entity")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntity(String value);


    /**
     * The type of the business entity that uses this part number to identify its parts. If the part is a Dell part number, then business Entity type is Enterprise. If business entity is not Dell then use Supplier
     */
    @NotNull
    @Size(max=64)
    @RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})
    @Metadata(index=3, displayName="Item Business Entity Type")
    @FlexCDMData({"*","*.down"})
    public String getBusinessEntityType();
    /**
     * The type of the business entity that uses this part number to identify its parts. If the part is a Dell part number, then business Entity type is Enterprise. If business entity is not Dell then use Supplier
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})</li>
     * <li>@Metadata(index=3, displayName="Item Business Entity Type")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setBusinessEntityType(String value);


    @Size(max=126)
    @Metadata(index=42, displayName="CAPEX")
    @FlexCDMData({"*","*.down"})
    public float getCAPEX();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=42, displayName="CAPEX")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCAPEX(float value);


    @Size(max=255)
    @Metadata(index=45, displayName="Comment")
    @FlexCDMData({"*","*.down"})
    public String getComment();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=45, displayName="Comment")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setComment(String value);


    /**
     * business that provides the cost. Used when the quoted cost is not from the business supplying the item
     */
    @Size(max=255)
    @Metadata(index=9, displayName="Cost Provider")
    @FlexCDMData({"*","*.down"})
    public String getCostBusinessEntity();
    /**
     * business that provides the cost. Used when the quoted cost is not from the business supplying the item
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=9, displayName="Cost Provider")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostBusinessEntity(String value);


    @Size(max=64)
    @RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})
    @Metadata(index=10, displayName="Cost Provider Entity Type")
    @FlexCDMData({"*","*.down"})
    public String getCostBusinessEntityType();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"ENTERPRISE","SUPPLIER","MANUFACTURER"})</li>
     * <li>@Metadata(index=10, displayName="Cost Provider Entity Type")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostBusinessEntityType(String value);


    @NotNull
    @Size(max=64)
    @RestrictToStrings({"BUY","MARKET","LIST","ODMBUY","SERVICE","EMQUOTE","SELL"})
    @Metadata(index=17, displayName="Cost Type")
    @FlexCDMData({"*","*.down"})
    public String getCostType();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"BUY","MARKET","LIST","ODMBUY","SERVICE","EMQUOTE","SELL"})</li>
     * <li>@Metadata(index=17, displayName="Cost Type")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCostType(String value);


    /**
     * Currency for this supplier at this location
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=12, displayName="Currency")
    @FlexCDMData({"*","*.down"})
    public String getCurrencyCode();
    /**
     * Currency for this supplier at this location
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=12, displayName="Currency")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setCurrencyCode(String value);


    /**
     * Defines the time delta in days between a part being purchased (that is, the date of the cost and rebate records) and the date of the forecast. The offset accommodates manufacturing and transportation cycle times.
     */
    @Size(max=10)
    @Metadata(index=11, displayName="Offset in days")
    @FlexCDMData({"*","*.down"})
    public int getDateOffset();
    /**
     * Defines the time delta in days between a part being purchased (that is, the date of the cost and rebate records) and the date of the forecast. The offset accommodates manufacturing and transportation cycle times.
     * Annotations:
     * <ul>
     * <li>@Size(max=10)</li>
     * <li>@Metadata(index=11, displayName="Offset in days")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setDateOffset(int value);


    /**
     * From Date
     */
    @NotNull
    @Metadata(index=13, displayName="Start Date")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveFromDate();
    /**
     * From Date
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Metadata(index=13, displayName="Start Date")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveFromDate(DateTime value);


    /**
     * To Date
     */
    @Metadata(index=14, displayName="End Date")
    @FlexCDMData({"*","*.down"})
    public DateTime getEffectiveToDate();
    /**
     * To Date
     * Annotations:
     * <ul>
     * <li>@Metadata(index=14, displayName="End Date")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEffectiveToDate(DateTime value);


    @NotNull
    @Size(max=1)
    @RestrictToStrings({"Y","N"})
    @Metadata(index=15, displayName="End Dates Required")
    @FlexCDMData({"*","*.down"})
    public String getEndDatesRequired();
    /**
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=1)</li>
     * <li>@RestrictToStrings({"Y","N"})</li>
     * <li>@Metadata(index=15, displayName="End Dates Required")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setEndDatesRequired(String value);


    /**
     * The supplier's business entity
     */
    @Size(max=255)
    @Metadata(index=5, displayName="Supplier")
    @FlexCDMData({"*","*.down"})
    public String getFromBusinessEntity();
    /**
     * The supplier's business entity
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=5, displayName="Supplier")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setFromBusinessEntity(String value);


    /**
     * The supplier's type. Should be SUPPLIER.
     */
    @Size(max=64)
    @RestrictToStrings({"SUPPLIER"})
    @Metadata(index=6, displayName="Supplier Entity Type")
    @FlexCDMData({"*","*.down"})
    public String getFromBusinessEntityType();
    /**
     * The supplier's type. Should be SUPPLIER.
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"SUPPLIER"})</li>
     * <li>@Metadata(index=6, displayName="Supplier Entity Type")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setFromBusinessEntityType(String value);


    /**
     * If the pricing scenario allows for ranges, enter the beginning value of the range in this column.<br>
     * The cost records pricing scenario determines whether the cost record can have ranges. For example, a pricing scenario may be based on volume. In this case, a supplier has different prices based on how many items are ordered. The cost record shows a list of ranges with different prices for each range.<br>
     * Which pricing scenarios support ranges is determined when MTCM is first configured. For more information,  see the information on defining pricing scenarios in the MTCM Implementation Reference Guide.
     */
    @Size(max=126)
    @Metadata(index=19, displayName="From Range")
    @FlexCDMData({"*","*.down"})
    public float getFromRange();
    /**
     * If the pricing scenario allows for ranges, enter the beginning value of the range in this column.<br>
     * The cost records pricing scenario determines whether the cost record can have ranges. For example, a pricing scenario may be based on volume. In this case, a supplier has different prices based on how many items are ordered. The cost record shows a list of ranges with different prices for each range.<br>
     * Which pricing scenarios support ranges is determined when MTCM is first configured. For more information,  see the information on defining pricing scenarios in the MTCM Implementation Reference Guide.
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=19, displayName="From Range")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setFromRange(float value);


    /**
     * The supplier's site
     */
    @Size(max=255)
    @Metadata(index=7, displayName="Source Site")
    @FlexCDMData({"*","*.down"})
    public String getFromSite();
    /**
     * The supplier's site
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=7, displayName="Source Site")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setFromSite(String value);


    @Size(max=126)
    @Metadata(index=36, displayName="INBOUND FREIGHT")
    @FlexCDMData({"*","*.down"})
    public float getINFREIGHT();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=36, displayName="INBOUND FREIGHT")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setINFREIGHT(float value);


    /**
     * If a cost record has ranges, you can make a range active, which means the cost record uses that range.<br>
     * Enter true to make this range active. Enter false to make the range inactive.<br>
     * By default, the cost record uses the lowest range.
     */
    @Size(max=64)
    @RestrictToStrings({"true","false"})
    @Metadata(index=21, displayName="Is Active")
    @FlexCDMData({"*","*.down"})
    public String getIsActive();
    /**
     * If a cost record has ranges, you can make a range active, which means the cost record uses that range.<br>
     * Enter true to make this range active. Enter false to make the range inactive.<br>
     * By default, the cost record uses the lowest range.
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"true","false"})</li>
     * <li>@Metadata(index=21, displayName="Is Active")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setIsActive(String value);


    /**
     * cost record description
     */
    @Size(max=1024)
    @Metadata(index=1, displayName="Item Description")
    @FlexCDMData({"*","*.down"})
    public String getItemDescription();
    /**
     * cost record description
     * Annotations:
     * <ul>
     * <li>@Size(max=1024)</li>
     * <li>@Metadata(index=1, displayName="Item Description")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemDescription(String value);


    /**
     * The part number receiving the pricing. This can be a Dell part number, an EM's part, or a Manufacturer's part.
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=0, displayName="Item")
    @FlexCDMData({"*","*.down"})
    public String getItemIdentifier();
    /**
     * The part number receiving the pricing. This can be a Dell part number, an EM's part, or a Manufacturer's part.
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=0, displayName="Item")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setItemIdentifier(String value);


    @Size(max=126)
    @Metadata(index=35, displayName="Land %")
    @FlexCDMData({"*","*.down"})
    public float getLAND_Percent();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=35, displayName="Land %")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLAND_Percent(float value);


    @Size(max=126)
    @Metadata(index=34, displayName="Land Value")
    @FlexCDMData({"*","*.down"})
    public float getLAND_Value();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=34, displayName="Land Value")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLAND_Value(float value);


    /**
     * Type of sourcing lane: pre-production, production, EOL
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=4, displayName="Product State")
    @FlexCDMData({"*","*.down"})
    public String getLifeCycleCode();
    /**
     * Type of sourcing lane: pre-production, production, EOL
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=4, displayName="Product State")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setLifeCycleCode(String value);


    @Size(max=126)
    @Metadata(index=23, displayName="MATERIAL")
    @FlexCDMData({"*","*.down"})
    public float getMATERIAL();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=23, displayName="MATERIAL")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setMATERIAL(float value);


    @Size(max=126)
    @Metadata(index=24, displayName="MVA")
    @FlexCDMData({"*","*.down"})
    public float getMVA();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=24, displayName="MVA")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setMVA(float value);


    @Size(max=126)
    @Metadata(index=41, displayName="NPI TOOLING")
    @FlexCDMData({"*","*.down"})
    public float getNPITOOLING();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=41, displayName="NPI TOOLING")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setNPITOOLING(float value);


    @Size(max=126)
    @Metadata(index=40, displayName="OPEX/NR")
    @FlexCDMData({"*","*.down"})
    public float getOPEXNRE();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=40, displayName="OPEX/NR")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOPEXNRE(float value);


    @Size(max=126)
    @Metadata(index=37, displayName="OUTBOUND FREIGHT")
    @FlexCDMData({"*","*.down"})
    public float getOUTFREIGHT();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=37, displayName="OUTBOUND FREIGHT")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setOUTFREIGHT(float value);


    @Size(max=126)
    @Metadata(index=25, displayName="PROFIT MARGIN")
    @FlexCDMData({"*","*.down"})
    public float getPROFITMARGIN();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=25, displayName="PROFIT MARGIN")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPROFITMARGIN(float value);


    /**
     * The scenario used for costing. Examples include Volume Based, Consumption Based, and Evergreen.<br>
     * The pricing scenarios are defined when MTCM is first configured. See the information on defining pricing scenarios in the MTCM Implementation Reference Guide.<br>
     * http://confluence.dev.scplatform.local/display/MTCM/Defining+Pricing+Scenarios
     */
    @Size(max=64)
    @RestrictToStrings({"Consumption Based","Volume Based","Evergreen"})
    @Metadata(index=18, displayName="Pricing Scenario")
    @FlexCDMData({"*","*.down"})
    public String getPricingScenario();
    /**
     * The scenario used for costing. Examples include Volume Based, Consumption Based, and Evergreen.<br>
     * The pricing scenarios are defined when MTCM is first configured. See the information on defining pricing scenarios in the MTCM Implementation Reference Guide.<br>
     * http://confluence.dev.scplatform.local/display/MTCM/Defining+Pricing+Scenarios
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@RestrictToStrings({"Consumption Based","Volume Based","Evergreen"})</li>
     * <li>@Metadata(index=18, displayName="Pricing Scenario")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setPricingScenario(String value);


    @Size(max=255)
    @Metadata(index=43, displayName="PRODUCTION Responsibility")
    @FlexCDMData({"*","*.down"})
    public String getProductionResponsibility();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=43, displayName="PRODUCTION Responsibility")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setProductionResponsibility(String value);


    @Size(max=126)
    @Metadata(index=38, displayName="ROYALTY")
    @FlexCDMData({"*","*.down"})
    public float getROYALTY();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=38, displayName="ROYALTY")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setROYALTY(float value);


    @Size(max=255)
    @Metadata(index=46, displayName="Reason Code")
    @FlexCDMData({"*","*.down"})
    public String getReasonCode();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=46, displayName="Reason Code")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setReasonCode(String value);


    @Size(max=126)
    @Metadata(index=33, displayName="Sea %")
    @FlexCDMData({"*","*.down"})
    public float getSEA_Percent();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=33, displayName="Sea %")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSEA_Percent(float value);


    @Size(max=126)
    @Metadata(index=32, displayName="Sea Value")
    @FlexCDMData({"*","*.down"})
    public float getSEA_Value();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=32, displayName="Sea Value")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSEA_Value(float value);


    @Size(max=126)
    @Metadata(index=26, displayName="SGA")
    @FlexCDMData({"*","*.down"})
    public float getSGA();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=26, displayName="SGA")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSGA(float value);


    @Size(max=255)
    @Metadata(index=44, displayName="SERVICE Responsibility")
    @FlexCDMData({"*","*.down"})
    public String getServiceResponsibility();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=44, displayName="SERVICE Responsibility")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setServiceResponsibility(String value);


    /**
     * The site where the item is being costed
     */
    @NotNull
    @Size(max=255)
    @Metadata(index=8, displayName="Destination Site")
    @FlexCDMData({"*","*.down"})
    public String getSite();
    /**
     * The site where the item is being costed
     * Annotations:
     * <ul>
     * <li>@NotNull</li>
     * <li>@Size(max=255)</li>
     * <li>@Metadata(index=8, displayName="Destination Site")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setSite(String value);


    @Size(max=64)
    @Metadata(index=16, displayName="Cost Status")
    @FlexCDMData({"*","*.down"})
    public String getStatus();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=64)</li>
     * <li>@Metadata(index=16, displayName="Cost Status")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setStatus(String value);


    @Size(max=126)
    @Metadata(index=28, displayName="TAXES")
    @FlexCDMData({"*","*.down"})
    public float getTAXES();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=28, displayName="TAXES")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setTAXES(float value);


    @Size(max=126)
    @Metadata(index=29, displayName="Blended Transportation")
    @FlexCDMData({"*","*.down"})
    public float getTRANSPORTATION();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=29, displayName="Blended Transportation")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setTRANSPORTATION(float value);


    /**
     * If the pricing scenario allows for ranges, enter the end value of the range. You can leave this column empty if you want the range to be limitless.
     */
    @Size(max=126)
    @Metadata(index=20, displayName="To Range")
    @FlexCDMData({"*","*.down"})
    public float getToRange();
    /**
     * If the pricing scenario allows for ranges, enter the end value of the range. You can leave this column empty if you want the range to be limitless.
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=20, displayName="To Range")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setToRange(float value);


    @Size(max=126)
    @Metadata(index=22, displayName="Total Price")
    @FlexCDMData({"*","*.down"})
    public float getTotalPrice();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=22, displayName="Total Price")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setTotalPrice(float value);


    @Size(max=126)
    @Metadata(index=27, displayName="VAT")
    @FlexCDMData({"*","*.down"})
    public float getVAT();
    /**
     * Annotations:
     * <ul>
     * <li>@Size(max=126)</li>
     * <li>@Metadata(index=27, displayName="VAT")</li>
     * <li>@FlexCDMData({"*","*.down"})</li>
     * </ul>
     */
    public void setVAT(float value);

    
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
        public static final FieldDefinition AIR_PERCENT = new FieldDefinition("AIR_Percent");
        public static final FieldDefinition AIR_VALUE = new FieldDefinition("AIR_Value");
        public static final FieldDefinition AMORTIZATION = new FieldDefinition("AMORTIZATION");
        /**
         * The name of the company that uses this part number to identify its parts. If the part is a Dell part number, then Whose Part Number is Dell, regardless of where we source the part.
         */
        public static final FieldDefinition BUSINESSENTITY = new FieldDefinition("BusinessEntity");
        /**
         * The type of the business entity that uses this part number to identify its parts. If the part is a Dell part number, then business Entity type is Enterprise. If business entity is not Dell then use Supplier
         */
        public static final FieldDefinition BUSINESSENTITYTYPE = new FieldDefinition("BusinessEntityType");
        public static final FieldDefinition CAPEX = new FieldDefinition("CAPEX");
        public static final FieldDefinition COMMENT = new FieldDefinition("Comment");
        /**
         * business that provides the cost. Used when the quoted cost is not from the business supplying the item
         */
        public static final FieldDefinition COSTBUSINESSENTITY = new FieldDefinition("CostBusinessEntity");
        public static final FieldDefinition COSTBUSINESSENTITYTYPE = new FieldDefinition("CostBusinessEntityType");
        public static final FieldDefinition COSTTYPE = new FieldDefinition("CostType");
        /**
         * Currency for this supplier at this location
         */
        public static final FieldDefinition CURRENCYCODE = new FieldDefinition("CurrencyCode");
        /**
         * Defines the time delta in days between a part being purchased (that is, the date of the cost and rebate records) and the date of the forecast. The offset accommodates manufacturing and transportation cycle times.
         */
        public static final FieldDefinition DATEOFFSET = new FieldDefinition("DateOffset");
        /**
         * From Date
         */
        public static final FieldDefinition EFFECTIVEFROMDATE = new FieldDefinition("EffectiveFromDate");
        /**
         * To Date
         */
        public static final FieldDefinition EFFECTIVETODATE = new FieldDefinition("EffectiveToDate");
        public static final FieldDefinition ENDDATESREQUIRED = new FieldDefinition("EndDatesRequired");
        /**
         * The supplier's business entity
         */
        public static final FieldDefinition FROMBUSINESSENTITY = new FieldDefinition("FromBusinessEntity");
        /**
         * The supplier's type. Should be SUPPLIER.
         */
        public static final FieldDefinition FROMBUSINESSENTITYTYPE = new FieldDefinition("FromBusinessEntityType");
        /**
         * If the pricing scenario allows for ranges, enter the beginning value of the range in this column.<br>
         * The cost records pricing scenario determines whether the cost record can have ranges. For example, a pricing scenario may be based on volume. In this case, a supplier has different prices based on how many items are ordered. The cost record shows a list of ranges with different prices for each range.<br>
         * Which pricing scenarios support ranges is determined when MTCM is first configured. For more information,  see the information on defining pricing scenarios in the MTCM Implementation Reference Guide.
         */
        public static final FieldDefinition FROMRANGE = new FieldDefinition("FromRange");
        /**
         * The supplier's site
         */
        public static final FieldDefinition FROMSITE = new FieldDefinition("FromSite");
        public static final FieldDefinition INFREIGHT = new FieldDefinition("INFREIGHT");
        /**
         * If a cost record has ranges, you can make a range active, which means the cost record uses that range.<br>
         * Enter true to make this range active. Enter false to make the range inactive.<br>
         * By default, the cost record uses the lowest range.
         */
        public static final FieldDefinition ISACTIVE = new FieldDefinition("IsActive");
        /**
         * cost record description
         */
        public static final FieldDefinition ITEMDESCRIPTION = new FieldDefinition("ItemDescription");
        /**
         * The part number receiving the pricing. This can be a Dell part number, an EM's part, or a Manufacturer's part.
         */
        public static final FieldDefinition ITEMIDENTIFIER = new FieldDefinition("ItemIdentifier");
        public static final FieldDefinition LAND_PERCENT = new FieldDefinition("LAND_Percent");
        public static final FieldDefinition LAND_VALUE = new FieldDefinition("LAND_Value");
        /**
         * Type of sourcing lane: pre-production, production, EOL
         */
        public static final FieldDefinition LIFECYCLECODE = new FieldDefinition("LifeCycleCode");
        public static final FieldDefinition MATERIAL = new FieldDefinition("MATERIAL");
        public static final FieldDefinition MVA = new FieldDefinition("MVA");
        public static final FieldDefinition NPITOOLING = new FieldDefinition("NPITOOLING");
        public static final FieldDefinition OPEXNRE = new FieldDefinition("OPEXNRE");
        public static final FieldDefinition OUTFREIGHT = new FieldDefinition("OUTFREIGHT");
        public static final FieldDefinition PROFITMARGIN = new FieldDefinition("PROFITMARGIN");
        /**
         * The scenario used for costing. Examples include Volume Based, Consumption Based, and Evergreen.<br>
         * The pricing scenarios are defined when MTCM is first configured. See the information on defining pricing scenarios in the MTCM Implementation Reference Guide.<br>
         * http://confluence.dev.scplatform.local/display/MTCM/Defining+Pricing+Scenarios
         */
        public static final FieldDefinition PRICINGSCENARIO = new FieldDefinition("PricingScenario");
        public static final FieldDefinition PRODUCTIONRESPONSIBILITY = new FieldDefinition("ProductionResponsibility");
        public static final FieldDefinition ROYALTY = new FieldDefinition("ROYALTY");
        public static final FieldDefinition REASONCODE = new FieldDefinition("ReasonCode");
        public static final FieldDefinition SEA_PERCENT = new FieldDefinition("SEA_Percent");
        public static final FieldDefinition SEA_VALUE = new FieldDefinition("SEA_Value");
        public static final FieldDefinition SGA = new FieldDefinition("SGA");
        public static final FieldDefinition SERVICERESPONSIBILITY = new FieldDefinition("ServiceResponsibility");
        /**
         * The site where the item is being costed
         */
        public static final FieldDefinition SITE = new FieldDefinition("Site");
        public static final FieldDefinition STATUS = new FieldDefinition("Status");
        public static final FieldDefinition TAXES = new FieldDefinition("TAXES");
        public static final FieldDefinition TRANSPORTATION = new FieldDefinition("TRANSPORTATION");
        /**
         * If the pricing scenario allows for ranges, enter the end value of the range. You can leave this column empty if you want the range to be limitless.
         */
        public static final FieldDefinition TORANGE = new FieldDefinition("ToRange");
        public static final FieldDefinition TOTALPRICE = new FieldDefinition("TotalPrice");
        public static final FieldDefinition VAT = new FieldDefinition("VAT");
    }
    
    /**
     * This interface solely is used to help build field based predicates.
     * See {@link CostRecord.PredicateBuilder} 
     */
    public interface CostRecordFieldPredicateFactory<T extends CostRecord>  extends FieldPredicateFactory<T> {
        public FieldPredicate<T,Float> whereAIR_Percent();
        public FieldPredicate<T,Float> whereAIR_Value();
        public FieldPredicate<T,Float> whereAMORTIZATION();
        public FieldPredicate<T,String> whereBusinessEntity();
        public FieldPredicate<T,String> whereBusinessEntityType();
        public FieldPredicate<T,Float> whereCAPEX();
        public FieldPredicate<T,String> whereComment();
        public FieldPredicate<T,String> whereCostBusinessEntity();
        public FieldPredicate<T,String> whereCostBusinessEntityType();
        public FieldPredicate<T,String> whereCostType();
        public FieldPredicate<T,String> whereCurrencyCode();
        public FieldPredicate<T,Integer> whereDateOffset();
        public FieldPredicate<T,DateTime> whereEffectiveFromDate();
        public FieldPredicate<T,DateTime> whereEffectiveToDate();
        public FieldPredicate<T,String> whereEndDatesRequired();
        public FieldPredicate<T,String> whereFromBusinessEntity();
        public FieldPredicate<T,String> whereFromBusinessEntityType();
        public FieldPredicate<T,Float> whereFromRange();
        public FieldPredicate<T,String> whereFromSite();
        public FieldPredicate<T,Float> whereINFREIGHT();
        public FieldPredicate<T,String> whereIsActive();
        public FieldPredicate<T,String> whereItemDescription();
        public FieldPredicate<T,String> whereItemIdentifier();
        public FieldPredicate<T,Float> whereLAND_Percent();
        public FieldPredicate<T,Float> whereLAND_Value();
        public FieldPredicate<T,String> whereLifeCycleCode();
        public FieldPredicate<T,Float> whereMATERIAL();
        public FieldPredicate<T,Float> whereMVA();
        public FieldPredicate<T,Float> whereNPITOOLING();
        public FieldPredicate<T,Float> whereOPEXNRE();
        public FieldPredicate<T,Float> whereOUTFREIGHT();
        public FieldPredicate<T,Float> wherePROFITMARGIN();
        public FieldPredicate<T,String> wherePricingScenario();
        public FieldPredicate<T,String> whereProductionResponsibility();
        public FieldPredicate<T,Float> whereROYALTY();
        public FieldPredicate<T,String> whereReasonCode();
        public FieldPredicate<T,Float> whereSEA_Percent();
        public FieldPredicate<T,Float> whereSEA_Value();
        public FieldPredicate<T,Float> whereSGA();
        public FieldPredicate<T,String> whereServiceResponsibility();
        public FieldPredicate<T,String> whereSite();
        public FieldPredicate<T,String> whereStatus();
        public FieldPredicate<T,Float> whereTAXES();
        public FieldPredicate<T,Float> whereTRANSPORTATION();
        public FieldPredicate<T,Float> whereToRange();
        public FieldPredicate<T,Float> whereTotalPrice();
        public FieldPredicate<T,Float> whereVAT();
    }
    
    /**
     * This is a convenient class to create instances of this message line and also Predicates
     * on Fields of this message
     */
    public static class Factory {
        
        /**
         * Create a new instance of {@link CostRecord}
         */
        public static CostRecord newInstance() {
            return MessageLineProxy.newInstance(CostRecord.class);
        }
        
        /**
         * Clone a instance of {@link CostRecord}
         * @throws InvalidValueException 
         * @throws FieldNotFoundException 
         */
        public static CostRecord clone(CostRecord dataToClone) throws FieldNotFoundException, InvalidValueException {
            return MessageLineProxy.clone(CostRecord.class, dataToClone);
        }
		
        
        /**
         * Use this method to create a field based predicate on {@link CostRecord}.
         * <br/>
         * <br/>
         * Sample Code
         * <pre>
         * <code>
         * Predicate&lt;CostRecord&gt; pred = CostRecord
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
        public static CostRecordFieldPredicateFactory<CostRecord> newFieldPredicate() {
            return MessageLinePredicateBuilderProxy.createMessageLinePredicate(CostRecord.class, CostRecordFieldPredicateFactory.class);
        }
    }
 }