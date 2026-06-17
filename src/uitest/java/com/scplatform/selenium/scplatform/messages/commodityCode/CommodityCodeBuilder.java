/**
 * @CommodityCodeBuilder.java@
 *
 * Created on Thu Oct 16 14:13:17 PDT 2014
 *
 *      Copyright (c) 2014 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.messages.commodityCode;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.scplatform.qa.iris.factory.DefaultMessageFactory;
import com.scplatform.qa.iris.factory.MessageLineEnricher;

/**
 * Used to build default {@link CommodityCode} message data.  
 *
 * Default Data:
 * <UL>
 * <LI> 
 * </UL>
 * <br><br>
 * Chained Call Example
 * <pre>
 * CommodityCodeBuilder<CommodityCode> builder = 
 * 				new CommodityCodeBuilder<CommodityCode>(CommodityCode.class, commodityCodes);
 * Iterable<CommodityCode> data = builder.build();
 * </pre>
 * 
 * <b>SPEC:</b> http://confluence.dev.scplatform.local/display/PUBT/Commodity+Code
 */
public class CommodityCodeBuilder<T extends CommodityCode> extends DefaultMessageFactory<T> {
    protected final static long defaultNumMessages = 1;
    protected List<CommodityCodeModel> commodityCodes;
    
    protected CommodityCodeBuilder(Class<T> messageClazz, long numMessages) {
        super(messageClazz, numMessages);
        this.setMessageLineEnricher(new CommodityCodeEnricher());
    }

    /**
     * @param messageClazz
     * 		The CommodityCode Message class, typically BusinessEntity.class, but can be any class that extends it.
     * 
     * @param commodityCodes
     * 		List of {@link CommodityCodeModel}.  The data is built from this list.
     */
    public CommodityCodeBuilder(Class<T> messageClazz, List<CommodityCodeModel> commodityCodes) {
        this(messageClazz, commodityCodes.size());
        
        this.commodityCodes = commodityCodes;
    }

    
    protected class CommodityCodeEnricher implements MessageLineEnricher<T> {

    	/**
    	 * Sets the data for a single line.  ADD DETAILS ON SPECIFICATIONS
    	 * 
    	 * @param messageLine	The message line
    	 * @param lineNumber	The line number
    	 * @return
    	 */
    	public T enrichMessageLine(T messageLine, long lineNumber) {
    		CommodityCodeModel model = commodityCodes.get((int) lineNumber);

    		messageLine.setCommodityCode(model.getCommodityCode());
    		messageLine.setCommodityCodeName(model.getCommodityCode());
    		messageLine.setCommodityUniqueId(model.getCommodityCode());
    		messageLine.setDescription(model.getCommodityCodeDescription());
    		if (StringUtils.isNotEmpty(model.getParentCommodityCode())) {
    			messageLine.setParentCommodityCode(model.getParentCommodityCode());
    			messageLine.setParentCommodityUniqueId(model.getParentCommodityCode());
    		}
    		
    		messageLine.setManagedBy(model.getManagedBy());
    		
    		if (StringUtils.isNotEmpty(model.getOperationCode())) {
    			messageLine.setOperationCode(model.getOperationCode());
    		} 
    		
    		return messageLine;
    	}
        
    }
    
   
	
    
    
    
    
    //===========================================
    // CHAINED CALLS
    //===========================================
    protected String operationCode = "C";
    
    
    
    /**
	 * Used to set {@link CommodityCode#setOperationCode(String)}<br>
	 * Default: C
     */
//    public CommodityCodeBuilder<T> withOperationCode(String value){
//    	this.operationCode = value;
//    	return this;
//    }

}
