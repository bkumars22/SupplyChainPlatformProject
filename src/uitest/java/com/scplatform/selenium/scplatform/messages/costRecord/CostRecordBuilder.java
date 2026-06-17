/**
 * @CostRecordBuilder.java@
 *
 * Created on Thu Oct 16 07:50:36 PDT 2014
 *
 *      Copyright (c) 2014 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.messages.costRecord;

import java.util.List;

import com.scplatform.qa.iris.factory.DefaultMessageFactory;
import com.scplatform.qa.iris.factory.MessageLineEnricher;
import com.test.selenium.common.JLog;
import com.test.selenium.common.RandomUtils;
import com.test.selenium.scplatform.messages.sourcingLane.SourcingLane;
import com.test.selenium.scplatform.messages.sourcingLane.subClasses.CostRecordValue;
import com.test.selenium.scplatform.utilities.DatabaseUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Used to build default {@link CostRecord} message data.  
 *
 * <br><br>
 * Chained Call Example
 * <pre>
 * CostRecordBuilder<CostRecord> builder = 
 * 				new CostRecordBuilder<CostRecord>(CostRecord.class, sourcingLaneData);
 * Iterable<CostRecord> data = builder.build();
 * </pre>
 * 
 *
 */
public class CostRecordBuilder<T extends CostRecord> extends DefaultMessageFactory<T> {
    protected final static long defaultNumMessages = 1;
    protected List<SourcingLane> sourcingLaneList;
    
    protected CostRecordBuilder(Class<T> messageClazz, long numMessages) {
        super(messageClazz, numMessages);
        this.setMessageLineEnricher(new CostRecordEnricher());
    }

    public CostRecordBuilder(Class<T> messageClazz, Iterable<SourcingLane> sourcingLaneData) {
        this(messageClazz, Iterables.size(sourcingLaneData));
        this.sourcingLaneList = Lists.newArrayList(sourcingLaneData);
    }

    protected class CostRecordEnricher implements MessageLineEnricher<T> {

    	/**
    	 * Sets the data for a single line. 
    	 * 
    	 * @param messageLine	The message line
    	 * @param lineNumber	The line number
    	 * @return
    	 */
        public T enrichMessageLine(T messageLine, long lineNumber) {
        	SourcingLane sourcingLane = sourcingLaneList.get((int) lineNumber);
        	
        	messageLine.setItemIdentifier(sourcingLane.getItemIdentifier());
        	messageLine.setBusinessEntity(sourcingLane.getBusinessEntity());
        	messageLine.setItemDescription(sourcingLane.getDescription());
        	messageLine.setBusinessEntityType("ENTERPRISE");
        	messageLine.setLifeCycleCode("PRODUCTION");
        	messageLine.setFromBusinessEntity(sourcingLane.getFromBusinessEntity());
        	messageLine.setFromBusinessEntityType(sourcingLane.getFromBusinessEntityType());
        	messageLine.setFromSite(sourcingLane.getFromSite());
        	messageLine.setSite(DatabaseUtils.getSiteDescription(sourcingLane.getSite()));	
        	messageLine.setCostBusinessEntity(sourcingLane.getCostRecord().get(0).getCostProviderBusinessEntity());
        	messageLine.setCostBusinessEntityType(sourcingLane.getCostRecord().get(0).getCostProviderBusinessEntityType());
        	messageLine.setDateOffset(sourcingLane.getDateOffset());
        	messageLine.setCurrencyCode(sourcingLane.getCurrencyCode());
        	messageLine.setEffectiveFromDate(sourcingLane.getCostRecord().get(0).getEffectiveFromDate());
        	messageLine.setEffectiveToDate(sourcingLane.getCostRecord().get(0).getEffectiveToDate());
        	messageLine.setEndDatesRequired((messageLine.getEffectiveFromDate() == null) ? "false" : "true");
        	messageLine.setCostType(sourcingLane.getCostRecord().get(0).getCostType());
        	messageLine.setTotalPrice(0f);
        	messageLine.setProductionResponsibility(sourcingLane.getOwnerName() + " (Production)");
        	messageLine.setServiceResponsibility(sourcingLane.getOwnerName() + " (Service)");
        	messageLine.setComment(sourcingLane.getCostRecord().get(0).getComment());
        	messageLine.setReasonCode(sourcingLane.getCostRecord().get(0).getReasonCode());
        	messageLine.setStatus("PENDING");
        	
    		if (messageLine.getFromBusinessEntity().equals(messageLine.getBusinessEntity()))	{
    			messageLine.setFromBusinessEntityType(messageLine.getBusinessEntityType());
    		}
        	
    		String costElementType;
    		
    		for (int index = 0; index < sourcingLane.getCostRecord().size(); index++){
    			List<CostRecordValue> costRecordValue = null;
    			
    			if (sourcingLane.getCostRecord().get(index).getCostRecordValue() != null)	{
    				costRecordValue = sourcingLane.getCostRecord().get(index).getCostRecordValue();
    				messageLine.setPricingScenario(sourcingLane.getCostRecord().get(index).getPricingScenario());
    			} else if (sourcingLane.getCostRecord().get(index).getCostRecordRange() != null)	{
    				if (sourcingLane.getCostRecord().get(index).getCostRecordRange().get(0).getCostRecordValue() != null)	{
    					costRecordValue = sourcingLane.getCostRecord().get(index).getCostRecordRange().get(0).getCostRecordValue();
    					
    					messageLine.setPricingScenario(sourcingLane.getCostRecord().get(0).getPricingScenario());
    					messageLine.setFromRange(sourcingLane.getCostRecord().get(0).getCostRecordRange().get(0).getFromRange());
    					messageLine.setToRange(sourcingLane.getCostRecord().get(0).getCostRecordRange().get(0).getToRange()); 
    					messageLine.setIsActive(sourcingLane.getCostRecord().get(0).getCostRecordRange().get(0).getIsActive());
    				}

    			}
    			
    			
    			if (costRecordValue != null)	{
    				for (int x = 0; x < costRecordValue.size(); x++){
    					costElementType = costRecordValue.get(x).getCostElementType();
    					
    					if (costElementType.equals("MATERIAL"))	{
    						messageLine.setMATERIAL(Float.parseFloat(costRecordValue.get(x).getCostValue()));
    						messageLine.setTotalPrice(messageLine.getTotalPrice() + messageLine.getMATERIAL());
    					} else if (costElementType.equals("MVA"))	{
    						messageLine.setMVA(Float.parseFloat(costRecordValue.get(x).getCostValue()));
    						messageLine.setTotalPrice(messageLine.getTotalPrice() + messageLine.getMVA());
    					} else if (costElementType.equals("PROFITMARGIN"))	{
    						messageLine.setPROFITMARGIN(Float.parseFloat(costRecordValue.get(x).getCostValue()));
    						messageLine.setTotalPrice(messageLine.getTotalPrice() + messageLine.getPROFITMARGIN());
    					} else if (costElementType.equals("SGA"))	{
    						messageLine.setSGA(Float.parseFloat(costRecordValue.get(x).getCostValue()));
    						messageLine.setTotalPrice(messageLine.getTotalPrice() + messageLine.getSGA());
    					} else if (costElementType.equals("VAT"))	{
    						messageLine.setVAT(Float.parseFloat(costRecordValue.get(x).getCostValue()));
    						messageLine.setTotalPrice(messageLine.getTotalPrice() + messageLine.getVAT());
    					} else if (costElementType.equals("TAXES"))	{
    						messageLine.setTAXES(Float.parseFloat(costRecordValue.get(x).getCostValue()));
    						messageLine.setTotalPrice(messageLine.getTotalPrice() + messageLine.getTAXES());
    					} else if (costElementType.equals("OPEXNRE"))	{
    						messageLine.setOPEXNRE(Float.parseFloat(costRecordValue.get(x).getCostValue()));
    						messageLine.setTotalPrice(messageLine.getTotalPrice() + messageLine.getOPEXNRE());
    					} else if (costElementType.equals("INFREIGHT"))	{
    						messageLine.setINFREIGHT(Float.parseFloat(costRecordValue.get(x).getCostValue()));
    						messageLine.setTotalPrice(messageLine.getTotalPrice() + messageLine.getINFREIGHT());
    					} else if (costElementType.equals("OUTFREIGHT"))	{
    						messageLine.setOUTFREIGHT(Float.parseFloat(costRecordValue.get(x).getCostValue()));
    						messageLine.setTotalPrice(messageLine.getTotalPrice() + messageLine.getOUTFREIGHT());
    					} else if (costElementType.equals("ROYALTY"))	{
    						messageLine.setROYALTY(Float.parseFloat(costRecordValue.get(x).getCostValue()));
    						messageLine.setTotalPrice(messageLine.getTotalPrice() + messageLine.getROYALTY());
    					} else if (costElementType.equals("AMORTIZATION"))	{
    						messageLine.setAMORTIZATION(Float.parseFloat(costRecordValue.get(x).getCostValue()));
    						messageLine.setTotalPrice(messageLine.getTotalPrice() + messageLine.getAMORTIZATION());
    					} else if (costElementType.equals("NPITOOLING"))	{
    						messageLine.setNPITOOLING(Float.parseFloat(costRecordValue.get(x).getCostValue()));
    						messageLine.setTotalPrice(messageLine.getTotalPrice() + messageLine.getNPITOOLING());
    					} else if (costElementType.equals("CAPEX"))	{
    						messageLine.setCAPEX(Float.parseFloat(costRecordValue.get(x).getCostValue()));
    						messageLine.setTotalPrice(messageLine.getTotalPrice() + messageLine.getCAPEX());
    					} else if (costElementType.equals("TRANSPORTATION"))	{
							float baseCost = Float.parseFloat(costRecordValue.get(x).getCostValue());
							
							messageLine.setSEA_Value(baseCost);
							messageLine.setAIR_Value(baseCost);
							messageLine.setLAND_Value(baseCost);

							messageLine.setSEA_Percent(RandomUtils.rand(5, 40));
							messageLine.setAIR_Percent(RandomUtils.rand(5, 40));
							messageLine.setLAND_Percent(100 - messageLine.getSEA_Percent() - messageLine.getAIR_Percent());

							messageLine.setTRANSPORTATION(baseCost);
							messageLine.setTotalPrice(messageLine.getTotalPrice() + messageLine.getTRANSPORTATION());

    					}
 
    				}

    			} else	{
    				JLog.warning (this.getClass().getSimpleName() + ".getBaseData() Unable to find any Cost Record Data for Item " + messageLine.getItemIdentifier());
    			}

    			break;		// only do one loop
    			
    		}
    		
        	return messageLine;
        }
        
    }
    
   
	
    
    
    
    
    //===========================================
    // CHAINED CALLS
    //===========================================
    
    /**
     * Sets the line count. ADD DETAILS ON WHAT EACH LINE WILL DO
     * 
     * @param lines	The number of lines to create.
     * @return
     * 
     */
    public CostRecordBuilder<T> withLines(long lines) {
        setNumMessages(lines);
        return this;
    }
    

}
