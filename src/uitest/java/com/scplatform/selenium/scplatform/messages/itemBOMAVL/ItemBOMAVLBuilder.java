/**
 * @ItemBOMAVLBuilder.java@
 *
 * Created on Thu Oct 16 12:21:35 PDT 2014
 *
 *      Copyright (c) 2014 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.messages.itemBOMAVL;

import java.util.List;

import com.scplatform.qa.iris.factory.DefaultMessageFactory;
import com.scplatform.qa.iris.factory.MessageLineEnricher;
import com.test.selenium.common.Partner;
import com.test.selenium.scplatform.messages.item.Item;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Used to build default {@link ItemBOMAVL} message data.  
 *
 * <br><br>
 * Chained Call Example
 * <pre>
 * ItemBOMAVLBuilder<ItemBOMAVL> builder = 
 * 				new ItemBOMAVLBuilder<ItemBOMAVL>(ItemBOMAVL.class, itemData);
 * Iterable<ItemBOMAVL> data = builder.build();
 * </pre>
 * 
 *
 */
public class ItemBOMAVLBuilder<T extends ItemBOMAVL> extends DefaultMessageFactory<T> {
    protected final static long defaultNumMessages = 1;
    protected List<Item> itemList;
    protected Partner enterprise;
    protected Class<T> messageClazz;
    protected List<T> itemBOMData;
    
    protected ItemBOMAVLBuilder(Class<T> messageClazz, long numMessages) {
        super(messageClazz, numMessages);
        this.messageClazz = messageClazz;
        this.setMessageLineEnricher(new ItemBOMAVLEnricher());
    }

    public ItemBOMAVLBuilder(Class<T> messageClazz, Iterable<Item> itemData, Partner enterprisePartner) {
        this(messageClazz, Iterables.size(itemData));
        this.itemList = Lists.newArrayList(itemData);
        this.enterprise = enterprisePartner;
    }

	@Override
    public Iterable<T> build() {
		ItemBOMAVLGenerator<T> generator = new ItemBOMAVLGenerator<T>(messageClazz, defaultNumMessages);
		generator.enterprise = this.enterprise;
		generator.itemBOMData = this.itemBOMData;
		generator.itemList = this.itemList;
		generator.messageClazz = this.messageClazz;
		
		itemBOMData = generator.build();
		
		setNumMessages(itemBOMData.size());
		return super.build();
	}
	
    protected class ItemBOMAVLEnricher implements MessageLineEnricher<T> {

    	/**
    	 * Sets the data for a single line. 
    	 * 
    	 * @param messageLine	The message line
    	 * @param lineNumber	The line number
    	 * @return
    	 */
        @Override
        public T enrichMessageLine(T messageLine, long lineNumber) {
        	messageLine = (T) itemBOMData.get((int) lineNumber);
            return messageLine;
        }
        
    }
    
   
	
    
    
    
    
    //===========================================
    // CHAINED CALLS
    //===========================================
    

    

}
