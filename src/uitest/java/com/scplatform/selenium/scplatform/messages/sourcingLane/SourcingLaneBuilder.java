/**
 * @SourcingLaneBuilder.java@
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
package com.test.selenium.scplatform.messages.sourcingLane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scplatform.qa.iris.factory.DefaultMessageFactory;
import com.scplatform.qa.iris.factory.MessageLineEnricher;
import com.test.selenium.common.Partner;
import com.test.selenium.scplatform.messages.calendar.Calendar;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.messages.item.parser.ItemData;
import com.test.selenium.scplatform.messages.sourcingLane.subClasses.CostRecord;
import com.test.selenium.scplatform.messages.sourcingLane.subClasses.CostRecordRange;
import com.test.selenium.scplatform.messages.sourcingLane.subClasses.CostRecordValue;
import com.test.selenium.scplatform.messages.sourcingLane.subClasses.CostValueDetail;
import com.test.selenium.scplatform.utilities.MessageIO;
import com.google.common.collect.Lists;

/**
 * <b>SPEC:</b> http://confluence.dev.scplatform.local/display/PUBT/Sourcing+Lane
 *
 * Used to build default {@link SourcingLane} message data.  
 *
 * <br><br>
 * Chained Call Example
 * <pre>
 * SourcingLaneBuilder<SourcingLane> builder = 
 * 				new SourcingLaneBuilder<SourcingLane>(SourcingLane.class, item, calendar, allCompanies);
 * Iterable<SourcingLane> data = builder.build();
 * </pre>
 * 
 *
 */
public class SourcingLaneBuilder<T extends SourcingLane> extends DefaultMessageFactory<T> {
    protected final static long defaultNumMessages = 1;
    protected List<Item> itemList;
    protected List<Partner> allCompanies;
    protected List<T> sourcingLaneData;
    protected Iterable<Calendar> calendar;
    
    protected SourcingLaneBuilder(Class<T> messageClazz, long numMessages) {
        super(messageClazz, numMessages);
        this.setMessageLineEnricher(new SourcingLaneEnricher());
    }

    public SourcingLaneBuilder(
    		Class<T> messageClazz, 
    		Iterable<Item> item, 
    		Iterable<Calendar> calendar, 
    		List<Partner> allCompanies) {
    	
        this(messageClazz, defaultNumMessages);
        
        this.itemList = Lists.newArrayList(item);
        this.allCompanies = allCompanies;
        this.calendar = calendar;
    }

    
    @SuppressWarnings("unchecked")
	@Override
    public Iterable<T> build() {
    	MessageIO<ItemData> itemDataMessageIO = new MessageIO<ItemData>(ItemData.class);
    	List<ItemData> itemData = itemDataMessageIO.load();
    	
    	SourcingLaneGenerator generator = new SourcingLaneGenerator();
    	sourcingLaneData = generator.build(this.itemList, itemData, this.calendar, this.allCompanies);
    	
     	setNumMessages(sourcingLaneData.size());
        return super.build();
    }
    
    
    protected class SourcingLaneEnricher implements MessageLineEnricher<T> {

    	/**
    	 * Sets the data for a single line.  
    	 * 
    	 * @param messageLine	The message line
    	 * @param lineNumber	The line number
    	 * @return
    	 */
        @Override
        public T enrichMessageLine(T messageLine, long lineNumber) {
        	messageLine = (T) sourcingLaneData.get((int) lineNumber);
            return messageLine;
        }
        
    }
    
   
	
    
    
    
    
    //===========================================
    // CHAINED CALLS
    //===========================================
    

//    public SourcingLaneBuilder<T> withBuildAction(String buildAction) {
//    	forceBuildActions = buildAction;
//        return this;
//    }
    
	public static Map<String, Class> getSubClasses() {
		Map<String, Class> listClasses = new HashMap<String, Class>();
		listClasses.put("CostRecord", CostRecord.class);
		listClasses.put("CostRecordRange", CostRecordRange.class);
		listClasses.put("CostRecordValue", CostRecordValue.class);
		listClasses.put("CostValueDetail", CostValueDetail.class);
		return listClasses;
	}
    
    


}
