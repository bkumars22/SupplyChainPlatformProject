/**
 * @ItemPlatformBuilder.java@
 *
 * Created on Thu Oct 16 12:45:02 PDT 2014
 *
 *      Copyright (c) 2014 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.messages.itemPlatform;

import java.util.List;

import com.scplatform.qa.iris.factory.DefaultMessageFactory;
import com.scplatform.qa.iris.factory.MessageLineEnricher;
import com.test.selenium.scplatform.messages.itemAVL.ItemAVL;
import com.google.common.collect.Lists;

/**
 * Used to build default {@link ItemPlatform} message data.  
 *
 * <br><br>
 * Chained Call Example
 * <pre>
 * ItemPlatformBuilder<ItemPlatform> builder = 
 * 				new ItemPlatformBuilder<ItemPlatform>(ItemPlatform.class, itemAVLData, platformName, platformDescription);
 * Iterable<ItemPlatform> data = builder.build();
 * </pre>
 * 
 *
 */
public class ItemPlatformBuilder<T extends ItemPlatform> extends DefaultMessageFactory<T> {
    protected final static long defaultNumMessages = 1;
    protected List<ItemAVL> itemAVLList;
    protected String platformName;
    protected String platformDescription;
    protected Class<T> messageClazz;
    protected List<T> itemPlatformData;
    
    protected ItemPlatformBuilder(Class<T> messageClazz, long numMessages) {
        super(messageClazz, numMessages);
        this.messageClazz = messageClazz;
        this.setMessageLineEnricher(new ItemPlatformEnricher());
    }

    public ItemPlatformBuilder(Class<T> messageClazz, Iterable<ItemAVL> itemAVLData, String platformName, String platformDescription) {
        this(messageClazz, defaultNumMessages);
        this.itemAVLList = Lists.newArrayList(itemAVLData);
        this.platformName = platformName;
        this.platformDescription = platformDescription;
    }

	@Override
    public Iterable<T> build() {
		ItemPlatformGenerator<T> generator = new ItemPlatformGenerator<T>(messageClazz, defaultNumMessages);
		generator.itemAVLList = this.itemAVLList;
		generator.messageClazz = this.messageClazz;
		generator.platformDescription = this.platformDescription;
		generator.platformName = this.platformName;
		
		itemPlatformData = generator.build();
		
		setNumMessages(itemPlatformData.size());
		return super.build();
	}
	
    protected class ItemPlatformEnricher implements MessageLineEnricher<T> {

    	/**
    	 * Sets the data for a single line.  
    	 * 
    	 * @param messageLine	The message line
    	 * @param lineNumber	The line number
    	 * @return
    	 */
        @Override
        public T enrichMessageLine(T messageLine, long lineNumber) {
        	messageLine = (T) itemPlatformData.get((int) lineNumber);
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
    public ItemPlatformBuilder<T> withLines(long lines) {
        setNumMessages(lines);
        return this;
    }
    

}
