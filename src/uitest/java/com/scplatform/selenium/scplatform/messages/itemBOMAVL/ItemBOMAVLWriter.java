/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.itemBOMAVL;

import com.scplatform.qa.e2Messages.utilities.MessageWriter;

/**
 * @author dgenrich
 *
 * @param <T>	ItemBOMAVL or any class that extends it
 * 
 * @see MessageWriter
 */
public class ItemBOMAVLWriter<T extends ItemBOMAVL> extends MessageWriter<T> {

	/**
	 * @param messageClazz	
	 * 		The ItemBOMAVL Message class, typically ItemBOMAVL.class, but can be any class that extends it.
	 * @param messageLines
	 * 		The message data, typically from {@link ItemBOMAVLBuilder}
	 */
	public ItemBOMAVLWriter(Class<T> messageClazz, Iterable<T> messageLines) {
		super(messageClazz, messageLines);
	}

}
