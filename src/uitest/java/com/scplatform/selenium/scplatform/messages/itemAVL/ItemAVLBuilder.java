/**
 * @ItemAVLBuilder.java@
 *
 * Created on Thu Oct 16 11:55:57 PDT 2014
 *
 *      Copyright (c) 2014 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.messages.itemAVL;

import java.util.List;

import com.scplatform.qa.iris.factory.DefaultMessageFactory;
import com.scplatform.qa.iris.factory.MessageLineEnricher;
import com.test.selenium.scplatform.messages.item.Item;
import com.google.common.collect.Lists;

/**
 *
 * Used to build default {@link ItemAVL} message data.  
 *
 * <br><br>
 * Chained Call Example
 * <pre>
 * ItemAVLBuilder<ItemAVL> builder = 
 * 				new ItemAVLBuilder<ItemAVL>(ItemAVL.class, itemData);
 * Iterable<ItemAVL> data = builder.build();
 * </pre>
 * 
 *
 */
public class ItemAVLBuilder<T extends ItemAVL> extends DefaultMessageFactory<T> {
    protected final static long defaultNumMessages = 1;
    protected List<Item> itemList;
    protected List<ItemAVL> itemAVLData;
    
    protected ItemAVLBuilder(Class<T> messageClazz, long numMessages) {
        super(messageClazz, numMessages);
        this.setMessageLineEnricher(new ItemAVLEnricher());
    }

    public ItemAVLBuilder(Class<T> messageClazz, Iterable<Item> itemData) {
        this(messageClazz, 1);
        this.itemList = Lists.newArrayList(itemData);
    }

	@Override
    public Iterable<T> build() {
		ItemAVLGenerator<ItemAVL> generator = new ItemAVLGenerator<ItemAVL>();
		itemAVLData = generator.build(itemList);
		
     	setNumMessages(itemAVLData.size());
        return super.build();
	}
    
    protected class ItemAVLEnricher implements MessageLineEnricher<T> {

    	/**
    	 * Sets the data for a single line.  
    	 * 
    	 * @param messageLine	The message line
    	 * @param lineNumber	The line number
    	 * @return
    	 */
        @SuppressWarnings("unchecked")
		@Override
        public T enrichMessageLine(T messageLine, long lineNumber) {
        	messageLine = (T) itemAVLData.get((int) lineNumber);
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
    public ItemAVLBuilder<T> withLines(long lines) {
        setNumMessages(lines);
        return this;
    }
    

}
